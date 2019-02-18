package de.hhu.sharing.web;

import de.hhu.sharing.data.ItemRepository;
import de.hhu.sharing.data.RequestRepository;
import de.hhu.sharing.data.UserRepository;
import de.hhu.sharing.model.Item;
import de.hhu.sharing.model.Request;
import de.hhu.sharing.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RequestController {

    @Autowired
    private UserRepository users;

    @Autowired
    private ItemRepository items;

    @Autowired
    private RequestRepository requests;

    @GetMapping("/request")
    public String request(@RequestParam(name = "id") Long id, Model model, Principal p, RedirectAttributes redirectAttributes){
        final User user = this.users.findByUsername(p.getName())
                .orElseThrow(
                        () -> new RuntimeException("User not found!"));
        final Item item = this.items.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Item not found!"));
        if(item.getLender() == user){
            redirectAttributes.addFlashAttribute("ownItem",true);
            return "redirect:/";
        }
        model.addAttribute("item", item);
        return "request";
    }

    @PostMapping("/saveRequest")
    public String saveRequest(Long id, String startdate, String enddate, Principal p){
        final User user = this.users.findByUsername(p.getName())
                .orElseThrow(
                        () -> new RuntimeException("User not found!"));
        Item item = this.items.findById(id).orElseThrow(
                () -> new RuntimeException("Item not found!"));
        Request request = new Request(LocalDate.parse(startdate), LocalDate.parse(enddate), user);
        requests.save(request);
        item.addToRequests(request);
        items.save(item);
        return "redirect:/";
    }

    @GetMapping("/deleteRequest")
    public String deleteRequest(@RequestParam("requestId") Long requestId, @RequestParam("itemId") Long itemId){
        final Request request = this.requests.findById(requestId)
                .orElseThrow(
                        () -> new RuntimeException("Request not found!"));
        final Item item = this.items.findById(itemId)
                .orElseThrow(
                        () -> new RuntimeException("Item not found!"));
        List<Request> requestlist = item.getRequests();
        requestlist.remove(request);
        item.setRequests(requestlist);
        requests.delete(request);
        return "redirect:/messages";
    }

    @GetMapping("messages")
    public String messages(Model model, Principal p){
        final User user = this.users.findByUsername(p.getName())
                .orElseThrow(
                        () -> new RuntimeException("User not found!"));
        List<Item> allMyItems = this.items.findAllByLender(user);
        List<Request> allMyRequests = new ArrayList<Request>();
        for (Item item : allMyItems) {
            for ( Request newRequest : item.getRequests()){
                    allMyRequests.add(newRequest) ;
            }
        }
        List<Request> allIRequested = requests.findAllByRequester(user);
        List<Item> myRequestedItems = new ArrayList<>();
        for(Request re : allIRequested){
            myRequestedItems.add(items.findByRequests_id(re.getId()));
        }
        model.addAttribute("allMyItems", allMyItems);
        model.addAttribute("myRequestedItems", myRequestedItems);
        model.addAttribute("allIRequested", allIRequested);
        return "messages";
    }

    @GetMapping("accept")
    public String accept(@RequestParam("requestId") Long requestId, @RequestParam("itemId") Long itemId){
        final Request request = this.requests.findById(requestId)
                .orElseThrow(
                        () -> new RuntimeException("Request not found!"));
        final Item item = this.items.findById(itemId)
                .orElseThrow(
                        () -> new RuntimeException("Item not found!"));
        if(item.isAvailable()) {
            item.setAvailable(false);
            User requester = request.getRequester();
            requester.addToBorrowedItem(item);
            List<Request> requestlist = item.getRequests();
            requestlist.remove(request);
            item.setRequests(requestlist);
            requests.delete(request);
        }
        else{
            // Fehlermeldung, falls Objekt schon verliehen
        }

        return "redirect:/messages";
    }

    @GetMapping("/declineRequest")
    public String declineRequest(@RequestParam("requestId") Long requestId, @RequestParam("itemId") Long itemId ){
        final Item item = this.items.findById(itemId)
                .orElseThrow(
                        () -> new RuntimeException("Item not found!"));
        Request request = this.requests.findById(requestId)
                .orElseThrow(
                        () -> new RuntimeException("Request not found!"));
        List<Request> requestlist = item.getRequests();
        requestlist.remove(request);
        item.setRequests(requestlist);
        requests.delete(request);
        return "redirect:/messages";
    }
}
