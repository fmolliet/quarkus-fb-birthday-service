package io.winty.struct.models;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private String name;
    @Id
    private String snowflake;
    private int day;
    private int month;
    
    public static Birthday findBySnowflake( String snowflake){
        return find("snowflake = :snowflake", Parameters.with("snowflake", snowflake)).firstResult();
    }
    
    public static List<Birthday> findTodayBirthDays(){
        ZoneId zoneId = ZoneId.of("GMT-3");

        // Get the current date and time in GMT-3
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

        // Extract the LocalDate from the ZonedDateTime
        LocalDate today = zonedDateTime.toLocalDate();

        return find("day = :day AND month = :month",
            Parameters.with("day", today.getDayOfMonth()).and("month", today.getMonthValue())).list();
    }
    
    public static List<Birthday> findMonthBirthDays(){
        ZoneId zoneId = ZoneId.of("GMT-3");

        // Get the current date and time in GMT-3
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

        // Extract the LocalDate from the ZonedDateTime
        LocalDate today = zonedDateTime.toLocalDate();

        return find("month = :month",
            Parameters.with("month", today.getMonthValue())).list();
    }
    
    public static List<Birthday> listAllBirthDays(int page, int size) {
        PanacheQuery<Birthday> listCars = findAll();
        return listCars.page(Page.of(page, size)).list();
    }
    
    
}
