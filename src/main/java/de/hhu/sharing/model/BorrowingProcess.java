package de.hhu.sharing.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class BorrowingProcess {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private LendableItem item;

    @Embedded
    private Period period;

    public BorrowingProcess(){
    }

    public BorrowingProcess(LendableItem item, Period period){
        this.item = item;
        this.period = period;
    }

}
