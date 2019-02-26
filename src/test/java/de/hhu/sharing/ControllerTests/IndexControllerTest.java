package de.hhu.sharing.ControllerTests;

import de.hhu.sharing.data.UserRepository;
import de.hhu.sharing.model.Address;
import de.hhu.sharing.model.LendableItem;
import de.hhu.sharing.model.User;
import de.hhu.sharing.services.ItemService;
import de.hhu.sharing.services.RequestService;
import de.hhu.sharing.services.UserService;
import de.hhu.sharing.storage.StorageService;
import de.hhu.sharing.web.IndexController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@WebMvcTest(IndexController.class)
public class IndexControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    StorageService storeService;

    @MockBean
    ItemService itemService;
    @MockBean
    UserService userService;
    @MockBean
    RequestService requestService;

    @MockBean
    UserRepository users;


    @WithMockUser
    @Test
    public void retrieveStatusIndex()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    public void retrieveStatusAccount()throws Exception{
        LocalDate date = LocalDate.of(2000,1,1);
        Address address = new Address("unistrase","duesseldorf", 40233);
        User user = new User("user","password", "role", "lastnmae", "forname", "email",date,address);

        Mockito.when(userService.get("user")).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/account"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    public void retrieveStatusSearch()throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/search?query=LendableItem"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    public void retrieveStatusMessages() throws Exception{
        LocalDate date = LocalDate.of(2000,1,1);
        Address address = new Address("unistrase","duesseldorf", 40233);
        User user = new User("user","password", "role", "lastnmae", "forname", "email",date,address);
        Mockito.when(userService.get("user")).thenReturn(user);
        List<LendableItem> lendableItemList = new ArrayList<>();
        Mockito.when(itemService.getAllIPosted(user)).thenReturn(lendableItemList);
        Mockito.when(itemService.getAllIRequested(user)).thenReturn(lendableItemList);
        mvc.perform(MockMvcRequestBuilders.get("/messages"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
