package de.hhu.sharing.web;

import de.hhu.sharing.model.Address;
import de.hhu.sharing.model.Item;
import de.hhu.sharing.model.User;
import de.hhu.sharing.services.BorrowingProcessService;
import de.hhu.sharing.services.ItemService;
import de.hhu.sharing.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ItemController {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BorrowingProcessService processService;

    @GetMapping("/detailsItem")
    public String details(@RequestParam(name = "id") Long id, Model model){
        Item item = itemService.get(id);
        User user = item.getLender();
        Address address = user.getAddress();
        model.addAttribute("item", item);
        model.addAttribute("user", user);
        model.addAttribute("address", address);
        return "details";
    }

    @GetMapping("/newItem")
    public String newItem(Model model){
        model.addAttribute("item", new Item());
        return "item";
    }

    @GetMapping("/editItem")
    public String editItem(Model model, @RequestParam("id") Long id){
        model.addAttribute("item", itemService.get(id));
        return "item";
    }

    @PostMapping("/saveItem")
    public String saveItem(Long id, String name, String description, Integer rental, Integer deposit, Principal p, RedirectAttributes redirectAttributes){
//        if(!item.isAvailable()){
//            redirectAttributes.addFlashAttribute("notAvailable",true);
//            return "redirect:/account";
//        }
        User user = userService.get(p.getName());
        if(id == null){
            itemService.create(name, description, rental, deposit, user);
            redirectAttributes.addFlashAttribute("saved",true);
        }
        else {
            itemService.edit(id,name, description, rental, deposit, user);
            redirectAttributes.addFlashAttribute("edited",true);
        }
        return "redirect:/account";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("id") Long id, RedirectAttributes redirectAttributes){
//        if(!item.isAvailable()){
//            redirectAttributes.addFlashAttribute("notAvailable",true);
//            return "redirect:/account";
//        }
        itemService.delete(id);
        redirectAttributes.addFlashAttribute("deleted",true);
        return "redirect:/account";
    }

    @GetMapping("/returnItem")
    public String returnItem( @RequestParam("id") Long id, Principal p){
        User user = userService.get(p.getName());
        processService.returnItem(id, user);
        return "redirect:/account";
    }
}
