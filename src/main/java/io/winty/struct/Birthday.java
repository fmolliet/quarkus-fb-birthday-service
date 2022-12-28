package io.winty.struct;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
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
}
