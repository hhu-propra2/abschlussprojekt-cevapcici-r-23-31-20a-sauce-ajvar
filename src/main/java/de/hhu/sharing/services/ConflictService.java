package de.hhu.sharing.services;

import de.hhu.sharing.data.ConflictRepository;
import de.hhu.sharing.model.BorrowingProcess;
import de.hhu.sharing.model.Conflict;
import de.hhu.sharing.model.Item;
import de.hhu.sharing.model.User;
import groovy.transform.AutoImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConflictService {

    @Autowired
    private ConflictRepository conflicts;

    @Autowired
    private BorrowingProcessService borrowingProcessService;

    public Conflict get(Long id){
        Conflict conflict = this.conflicts.findById(id)
                .orElseThrow(
                        ()-> new RuntimeException("Conflict not found"));
        return conflict;
    }



    public List<Conflict> getAll() {
        return conflicts.findAll();
    }

    public void create(String problem, Item item, User prosecuter, User accused) {
        Conflict conflict = new Conflict(problem, item, prosecuter, accused);
        conflicts.save(conflict);
    }
}
