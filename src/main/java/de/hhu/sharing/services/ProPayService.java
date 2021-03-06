package de.hhu.sharing.services;

import com.google.gson.Gson;
import de.hhu.sharing.model.LendableItem;
import de.hhu.sharing.model.NotFoundException;
import de.hhu.sharing.model.SellableItem;
import de.hhu.sharing.model.User;
import de.hhu.sharing.propay.Account;
import de.hhu.sharing.propay.TransactionPurchase;
import de.hhu.sharing.propay.TransactionRental;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class ProPayService {

    @Autowired
    private TransactionRentalService transRenService;

    private RestTemplate rt = new RestTemplate();
    private String URL = "http://propay:8888/";

    public Account getAccount(User user) {
        String URL = this.URL + "account/" + user.getUsername() + "/";
        this.callURL(URL, "GET", 3);
        String jsonAccount = rt.getForObject(URL, String.class);
        return new Gson().fromJson(jsonAccount, Account.class);
    }

    public boolean enoughCredit(User user, LendableItem lendableItem, LocalDate startdate, LocalDate enddate){
        int days = (int) DAYS.between(startdate, enddate) + 1;
        int rent = lendableItem.getRental() * days;
        int amount = this.getAccount(user).getAmount();
        return amount >= (rent + lendableItem.getDeposit() + getDepositSum(this.getAccount(user)));
    }

    public boolean enoughCredit(User user, SellableItem sellableItem){
        int price = sellableItem.getPrice();
        int amount = this.getAccount(user).getAmount();
        return amount >= (price + getDepositSum(this.getAccount(user)));
    }

    public void rechargeCredit(User user, int amount) {
        String URL = this.URL + "account/" + user.getUsername() + "?amount=" + amount;
        this.callURL(URL, "POST", 3);
    }

    public void initiateTransactionRental(TransactionRental transRen) {
        String URL = this.URL
                + "account/" + transRen.getSender().getUsername()
                + "/transfer/" + transRen.getReceiver().getUsername()
                + "?amount=" + transRen.getWholeRent();
        this.callURL(URL, "POST", 3);
        URL = this.URL
                + "reservation/reserve/" + transRen.getSender().getUsername()
                + "/" + transRen.getReceiver().getUsername()
                + "?amount=" + transRen.getDeposit();
        this.callURL(URL, "POST", 3);
        Account account = this.getAccount(transRen.getSender());
        transRen.setId(account.getLastReservationId());
    }

    public void initiateTransactionPurchase(TransactionPurchase transPur) {
        String URL = this.URL
                + "account/" + transPur.getSender().getUsername()
                + "/transfer/" + transPur.getReceiver().getUsername()
                + "?amount=" + transPur.getPrice();
        this.callURL(URL, "POST", 3);
    }

    public void releaseDeposit(User sender, TransactionRental transRen) {
        String URL = this.URL + "reservation/release/" + sender.getUsername() + "?reservationId=" + transRen.getId();
        this.callURL(URL, "POST", 3);
        transRenService.setDepositRevoked(transRen,"Nein");
    }

    public void punishDeposit(User sender, TransactionRental transRen) {
        String URL = this.URL + "reservation/punish/" + sender.getUsername() + "?reservationId=" + transRen.getId();
        this.callURL(URL, "POST", 3);
        transRenService.setDepositRevoked(transRen,"Ja");
    }

    public int getDepositSum (Account account){
        int depositSum = 0;
        for(int i = 0; i < account.getReservations().size(); i++) {
            depositSum += account.getReservations().get(i).getAmount();
        }
        return depositSum;
    }

    public void callURL(String urlString, String method, int retries) {
        if(retries < 0){
            throw new NotFoundException("ProPay nicht erreichbar! (timeout)");
        }
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            connection.getResponseCode();
            connection.disconnect();
        } catch (SocketTimeoutException e) {
            retries -= 1;
            callURL(urlString, method, retries);
        } catch (IOException e) {
            throw new NotFoundException("ProPay nicht erreichbar!");
        }
    }
}