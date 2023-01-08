package io.winty.struct;

import java.time.LocalDate;
import java.util.List;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.panache.common.Parameters;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MongoEntity(collection="birthdays")
@Getter
@Setter
@ToString
public class Birthday extends PanacheMongoEntity {
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
}
