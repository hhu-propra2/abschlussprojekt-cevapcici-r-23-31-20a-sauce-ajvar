package de.hhu.sharing.web;

import de.hhu.sharing.model.SellableItem;
import de.hhu.sharing.model.User;
import de.hhu.sharing.services.ProPayService;
import de.hhu.sharing.services.SellableItemService;
import de.hhu.sharing.services.TransactionPurchaseService;
import de.hhu.sharing.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class SellableItemController {

    @Autowired
    private SellableItemService sellableItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProPayService proService;

    @Autowired
    private TransactionPurchaseService transactionPurchaseService;

    @GetMapping("/sellableItemDetails")
    private String sellableItemDetails(Model model, @RequestParam("id") Long id, Principal p){
        SellableItem sellableItem = sellableItemService.get(id);
        model.addAttribute("user", userService.get(p.getName()));
        model.addAttribute("sellableItem", sellableItem);
        return ("sellableItemDetails");
    }

    @GetMapping("/newSellableItem")
    private String newSellableItem(Model model){
        model.addAttribute("sellableItem", new SellableItem());
        return("sellableItem");
    }

    @GetMapping("/editSellableItem")
    public String editSellableItem(Model model, @RequestParam("id") Long id, Principal p, RedirectAttributes redirectAttributes){
        User user = userService.get(p.getName());
        if(!sellableItemService.isOwner(id, user)){
            redirectAttributes.addFlashAttribute("errMessage","Keine Berechtigung!");
            return "redirect:/account";
        }
        model.addAttribute("sellableItem", sellableItemService.get(id));
        return "sellableItem";
    }

    @PostMapping("/saveSellableItem")
    private String saveSellableItem(Long id, @RequestParam("name") String name, @RequestParam("price") Integer price, @RequestParam("description") String description, @RequestParam("file") MultipartFile file , Principal p, RedirectAttributes redirectAttributes){
        User user = userService.get(p.getName());
        if(id == null){
            sellableItemService.create(name, description, price, user, file);
            redirectAttributes.addFlashAttribute("succMessage","Objekt erstellt.");
        }
        else {
            sellableItemService.edit(id, name, description, price, user ,file);
            redirectAttributes.addFlashAttribute("succMessage","Objekt bearbeitet.");
        }
        return "redirect:/account";
    }

    @GetMapping("/deleteSellableItem")
    public String deleteSellableItem(@RequestParam("id") Long id, Principal p, RedirectAttributes redirectAttributes){
        User user = userService.get(p.getName());
        if(!sellableItemService.isOwner(id, user)){
            redirectAttributes.addFlashAttribute("errMessage","Keine Berechtigung!");
            return "redirect:/account";
        }
        sellableItemService.delete(id);
        redirectAttributes.addFlashAttribute("succMessage","Objekt gelöscht.");
        return "redirect:/account";
    }

    @GetMapping("/buy")
    public String buy(@RequestParam("id") Long id, Principal p, RedirectAttributes redirectAttributes){
        User user = userService.get(p.getName());
        SellableItem sellableItem = sellableItemService.get(id);
        if(sellableItemService.isOwner(id, user)){
            redirectAttributes.addFlashAttribute("errMessage","Eigenes Objekt nicht an sich selbst verkaufbar.");
            return "redirect:/";
        }
        if(!proService.enoughCredit(user,sellableItem)){
            redirectAttributes.addFlashAttribute("errMessage","Nicht genug ProPray-Guthaben.");
            return "redirect:/";
        }
        transactionPurchaseService.createTransactionPurchase(sellableItem, user);
        sellableItemService.delete(id);
        redirectAttributes.addFlashAttribute("succMessage","Objekt erfolgreich gekauft.");
        return "redirect:/";
    }

}
