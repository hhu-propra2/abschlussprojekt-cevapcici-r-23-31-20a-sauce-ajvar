package de.hhu.sharing.web;

import de.hhu.sharing.model.BorrowingProcess;
import de.hhu.sharing.model.Conflict;
import de.hhu.sharing.model.Item;
import de.hhu.sharing.model.User;
import de.hhu.sharing.services.BorrowingProcessService;
import de.hhu.sharing.services.ConflictService;
import de.hhu.sharing.services.ItemService;
import de.hhu.sharing.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;

@Controller
public class ConflictController {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ConflictService conflictService;

    @Autowired
    private BorrowingProcessService borrowingProcessService;

    @GetMapping("/conflict")
    public String conflictPage(Model model, Principal p, @RequestParam("id") Long id){

        BorrowingProcess borrowingProcess = borrowingProcessService.getBorrowingProcess(id);
        model.addAttribute( "borrowingProcess", borrowingProcess);
        User borrower = userService.getBorrowerFromBorrowingProcessId(id);
        model.addAttribute("borrower", borrower);
        return "conflict";
    }

    @PostMapping("/saveConflict")
    public String saveConflict(@RequestParam("id") Long id, String problem){
        BorrowingProcess process = borrowingProcessService.getBorrowingProcess(id);
        Item item =  borrowingProcessService.getItemFromProcess(process);
        conflictService.create(problem, item, item.getLender(), userService.getBorrowerFromBorrowingProcessId(id));
        return "redirect:/account";
    }

    @GetMapping("/conflictView")
    public String conflictView(Model model){
        model.addAttribute("allConflicts", conflictService.getAll());
        return "conflictView";
    }

    @GetMapping("/conflictDetails")
    public String conflictDetails (Model model, @RequestParam("id") Long id){
        model.addAttribute("conflict", conflictService.get(id));
        return "conflictDetails";
    }
}
