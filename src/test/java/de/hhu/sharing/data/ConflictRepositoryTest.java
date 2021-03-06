package de.hhu.sharing.data;

import de.hhu.sharing.model.*;
import de.hhu.sharing.storage.StorageService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(locations="classpath:test.properties")
public class ConflictRepositoryTest {

    @Autowired
    ConflictRepository conflictRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    LendableItemRepository itemRepo;

    @Autowired
    BorrowingProcessRepository bPRepo;

    @MockBean
    StorageService storageService;


    public User createUser(String username){
        LocalDate birthdate = LocalDate.of(2000,1,1);
        Address address = new Address("unistrase","duesseldorf", 40233);
        User user = new User(username,"password", "role", "lastnmae", "forname", "email",birthdate,address);
        userRepo.save(user);
        return userRepo.findByUsername(username).get();
    }

    public LendableItem createItem(String name, String description){
        User user = createUser("testman");
        LendableItem item = new LendableItem(name, description,1,1 ,user );
        itemRepo.save(item);
        return itemRepo.findById(item.getId()).get();
    }

    public BorrowingProcess createProcess(LendableItem item){
        BorrowingProcess borrowingProcess = new BorrowingProcess();
        borrowingProcess.setItem(item);
        bPRepo.save(borrowingProcess);
        return bPRepo.findById(borrowingProcess.getId()).get();
    }

    public BorrowingProcess createProcess(){
        BorrowingProcess borrowingProcess = new BorrowingProcess();
        bPRepo.save(borrowingProcess);
        return bPRepo.findById(borrowingProcess.getId()).get();
    }

    public Conflict createConflict(User lender, User borrower, BorrowingProcess process, String author){
        Message message = new Message(author, "cool");
        Conflict conflict = new Conflict(lender,borrower,process,message);
        conflictRepo.save(conflict);
        return conflictRepo.findById(conflict.getId()).get();
    }


    @Test
    public void testFindAll(){
        User user1 = createUser("testman1");
        User user2 = createUser("testman2");

        BorrowingProcess process = createProcess();

        createConflict(user1,user2,process, "testman1");
        createConflict(user2,user1,process, "testman2");

        Assertions.assertThat(conflictRepo.findAll().size()).isEqualTo(2);
    }

    @Test
    public void testFindAllByProcess_Item(){

        LendableItem item = createItem("testItem","cool");
        User user1 = createUser("testman1");
        User user2 = createUser("testman2");

        BorrowingProcess process1 = createProcess(item);
        BorrowingProcess process2 = createProcess(item);

        createConflict(user1,user2,process1, "testman1");
        createConflict(user2,user1,process2, "testman2");

        Assertions.assertThat(conflictRepo.findAllByProcess_Item(item).size()).isEqualTo(2);

    }

    @Test
    public void testFindByProcess(){

        User user1 = createUser("testman1");
        User user2 = createUser("testman2");

        BorrowingProcess process1 = createProcess();
        BorrowingProcess process2 = createProcess();

        Conflict conflict = createConflict(user1,user2,process1, "testman1");
        createConflict(user2,user1,process2, "testman2");

        Assertions.assertThat(conflictRepo.findByProcess(process1)).isEqualTo(conflict);

    }
}
