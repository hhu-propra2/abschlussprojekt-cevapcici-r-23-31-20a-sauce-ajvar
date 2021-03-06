package de.hhu.sharing.services;

import de.hhu.sharing.data.UserRepository;
import de.hhu.sharing.model.BorrowingProcess;
import de.hhu.sharing.model.NotFoundException;
import de.hhu.sharing.model.Period;
import de.hhu.sharing.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class UserService {

    @Autowired
    private UserRepository users;

    public User get(String username) {
        User user = this.users.findByUsername(username)
                .orElseThrow(
                        () -> new NotFoundException("Benutzer nicht gefunden!"));
        return user;
    }

    public User getBorrowerFromBorrowingProcessId(Long processId) {
        User user = this.users.findByBorrowed_id(processId)
                .orElseThrow(
                        () -> new NotFoundException("Benutzer nicht gefunden!"));
        return user;
    }

    public void removeProcessFromProcessLists(BorrowingProcess process) {
        User lender = process.getItem().getOwner();
        lender.removeFromLend(process);
        users.save(lender);
        User borrower = this.getBorrowerFromBorrowingProcessId(process.getId());
        borrower.removeFromBorrowed(process);
        users.save(borrower);
    }

    public boolean userIsInvolvedToProcess(User user, BorrowingProcess process) {
        User borrower = this.getBorrowerFromBorrowingProcessId(process.getId());
        User lender = process.getItem().getOwner();
        return user == borrower || user == lender;
    }

    public boolean userHasNotReturnedItems(User user){
        List<BorrowingProcess> allIBorrowed = user.getBorrowed();
        boolean iDidntReturnStuff = false;
        for(BorrowingProcess borrowed : allIBorrowed){
            Period period = borrowed.getPeriod();
            if(LocalDate.now().isAfter(period.getEnddate())){
                iDidntReturnStuff = true;
            }
        }
        return iDidntReturnStuff;
    }
}