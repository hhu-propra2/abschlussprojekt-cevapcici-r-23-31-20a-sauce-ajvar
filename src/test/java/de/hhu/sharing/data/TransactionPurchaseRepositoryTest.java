package de.hhu.sharing.data;
import de.hhu.sharing.data.TransactionPurchaseRepository;
import de.hhu.sharing.data.TransactionRentalRepository;
import de.hhu.sharing.data.UserRepository;
import de.hhu.sharing.model.Address;
import de.hhu.sharing.model.User;
import de.hhu.sharing.propay.TransactionPurchase;
import de.hhu.sharing.storage.StorageService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionPurchaseRepositoryTest {

    @Autowired
    TransactionPurchaseRepository transRepo;

    @Autowired
    UserRepository userRepo;

    @MockBean
    StorageService storageService;


    @Test
    public void testFindAll() {
        TransactionPurchase trans1 = new TransactionPurchase();
        TransactionPurchase trans2 = new TransactionPurchase();
        transRepo.save(trans1);
        transRepo.save(trans2);

        Assertions.assertThat(transRepo.findAll().size()).isEqualTo(2);
    }

    @Test
    public void testFindTargetTransactionPurchase() {
        Address sharedAdress = new Address("Gemeinsames", "Wohnhaus", 1234);
        User source = new User();
        User target = new User();
        source.setUsername("source");
        source.setAddress(sharedAdress);
        target.setUsername("target");
        target.setAddress(sharedAdress);

        userRepo.save(source);
        userRepo.save(target);

        source = userRepo.findByUsername("source").get();
        target = userRepo.findByUsername("target").get();

        TransactionPurchase trans3 = new TransactionPurchase();
        TransactionPurchase trans4 = new TransactionPurchase();
        TransactionPurchase trans5 = new TransactionPurchase();

        trans3.setSender(source);
        trans3.setReceiver(target);
        trans4.setSender(source);
        trans4.setReceiver(target);
        trans5.setSender(target);
        trans5.setReceiver(source);

        transRepo.save(trans3);
        transRepo.save(trans4);
        transRepo.save(trans5);

        Assertions.assertThat(transRepo.findAllByReceiver(target).size()).isEqualTo(2);
        Assertions.assertThat(transRepo.findAllByReceiver(target).contains(trans3)).isTrue();
        Assertions.assertThat(transRepo.findAllByReceiver(target).contains(trans4)).isTrue();
    }

    @Test
    public void testFindSourceTransactionPurchase() {
        Address sharedAdress = new Address("Gemeinsames", "Wohnhaus", 1234);
        User source = new User();
        User target = new User();
        source.setUsername("source");
        source.setAddress(sharedAdress);
        target.setUsername("target");
        target.setAddress(sharedAdress);

        userRepo.save(source);
        userRepo.save(target);

        source = userRepo.findByUsername("source").get();
        target = userRepo.findByUsername("target").get();

        TransactionPurchase trans6 = new TransactionPurchase();
        TransactionPurchase trans7 = new TransactionPurchase();
        TransactionPurchase trans8 = new TransactionPurchase();

        trans6.setSender(source);
        trans6.setReceiver(target);
        trans7.setSender(source);
        trans7.setReceiver(target);
        trans8.setSender(target);
        trans8.setReceiver(source);

        transRepo.save(trans6);
        transRepo.save(trans7);
        transRepo.save(trans8);

        Assertions.assertThat(transRepo.findAllBySender(source).size()).isEqualTo(2);
        Assertions.assertThat(transRepo.findAllBySender(source).contains(trans6)).isTrue();
        Assertions.assertThat(transRepo.findAllBySender(source).contains(trans7)).isTrue();
    }
}
