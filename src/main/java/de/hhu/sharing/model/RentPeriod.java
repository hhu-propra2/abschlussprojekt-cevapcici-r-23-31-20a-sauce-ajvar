package de.hhu.sharing.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Embeddable
public class RentPeriod {

    private LocalDate startdate;
    private LocalDate enddate;

    public RentPeriod(){
    }

    public RentPeriod(LocalDate startdate, LocalDate enddate){
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public boolean overlapesWith(RentPeriod period) {
        return startdate.isEqual(period.getStartdate())
                || startdate.isEqual(period.getEnddate())
                || enddate.isEqual(period.getStartdate())
                || enddate.isEqual(period.getEnddate())
                || startdate.isBefore(period.getStartdate()) && enddate.isAfter(period.getStartdate())
                || startdate.isBefore(period.getEnddate()) && enddate.isAfter(period.getEnddate())
                || startdate.isAfter(period.getStartdate()) && enddate.isBefore(period.getEnddate());
    }
}