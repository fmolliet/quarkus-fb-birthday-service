package io.winty.struct;

import java.time.LocalDate;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "birthdays")
public class Birthday extends PanacheEntityBase {
    @Id
    private long id;
    private String name;
    private String snowflake;
    private int day;
    private int month;
    
    public static Birthday findBySnowflake( String snowflake){
        return find("snowflake", snowflake).firstResult();
    }
    
    public static List<Birthday> findTodayBirthDays(){
        LocalDate today = LocalDate.now();
        return find("{'day': :day, 'month': :month}",
            Parameters.with("day", today.getDayOfMonth()).and("month", today.getMonthValue())).list();
    }
    
    public static List<Birthday> findMonthBirthDays(){
        LocalDate today = LocalDate.now();
        return find("{'month': :month}",
            Parameters.with("month", today.getMonthValue())).list();
    }
    
    public static List<Birthday> listAllBirthDays(int page, int size) {
        PanacheQuery<Birthday> listCars = findAll();
        return listCars.page(Page.of(page, size)).list();
    }
    
    
}
