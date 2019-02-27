package de.hhu.sharing.ServiceTests;

import de.hhu.sharing.data.BorrowingProcessRepository;
import de.hhu.sharing.services.*;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BorrowingProcessServceTest {

    @Mock
    private BorrowingProcessRepository processes;

    @Mock
    private RequestService requestService;

    @Mock
    private LendableItemService lendableItemService;

    @Mock
    private UserService userService;

    @Mock
    private ProPayService proPayService;

    @Mock
    private  ConflictService conflictService;

    @InjectMocks
    private BorrowingProcessService BPService;

    @Before
    public void initialize(){
        MockitoAnnotations.initMocks(this);
    }

    private void testAccept(){

    }

}