package io.winty.struct.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "registrations")
public class Register extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    private UUID id;

    @Column(name = "applicantId", nullable = false)
    private String applicantId;

    @Column(name = "curatorId")
    private String curatorId;

    @Column(name = "finished", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean finished = false;
    private Boolean approved;

    private String messageId;
    private String birthday;
    private String specie; 
    private String source;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public static Register findUnfinishedByApplicantId( String applicantId){
        return find("applicantId = :applicantId AND finished = false", Parameters.with("applicantId", applicantId)).firstResult();
    }

    
}
