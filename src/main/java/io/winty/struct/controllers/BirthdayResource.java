package io.winty.struct.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse.Status;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import io.winty.struct.models.Birthday;
import io.winty.struct.models.dto.BirthdayRequestDto;
import lombok.extern.jbosslog.JBossLog;

@Path("/birthday")
@JBossLog
public class BirthdayResource {
    
    private static final String NOT_FOUND_MESSAGE = "Usuário não encontrado na base.";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "birthdays") 
    public List<Birthday> list(
        @Min(0) 
        @Max(1000) 
        @QueryParam("page") int page, 
        @Min(0) 
        @Max(2000) 
        @QueryParam("size") int size) {
        log.info("LIST");
        if(size==0){size = 10;} 
        return Birthday.listAllBirthDays(page, size);
    }
    
    @GET
    @Path("/today")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Birthday> today() {
        log.info("TODAY");
        return Birthday.findTodayBirthDays();
    }
    
    @GET
    @Path("/month")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Birthday> month() {
        log.info("MONTH");
        return Birthday.findMonthBirthDays();
    }
    
    @GET
    @Path("/{snowflake}")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "birthdays") 
    public Birthday get(@PathParam("snowflake") String snowflake) {
        log.info("GET: " + snowflake);
        return Birthday.findBySnowflake(snowflake);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    @CacheInvalidateAll(cacheName = "birthdays") 
    @CacheInvalidateAll(cacheName = "birthdaysMonth") 
    public Response create(@Valid BirthdayRequestDto request) throws URISyntaxException {
        log.info("CREATE: " + request);
              
        if ( Birthday.findBySnowflake(request.snowflake) != null ){
            log.warn("Usuário já cadastrado.");
            throw new BadRequestException("Usuário já cadastrado.");
        }
        
        Birthday birthday = new Birthday();
        
        birthday.setDay(request.day);
        birthday.setMonth(request.month);
        birthday.setName(request.name);
        birthday.setSnowflake(request.snowflake);
        
        birthday.persist();
        
        return Response.created(
                new URI(String.format("/birthday/%s", birthday.getSnowflake())))
                .entity(birthday)
                .build();
    }
    
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    @CacheInvalidateAll(cacheName = "birthdays")
    public Response update(@Valid BirthdayRequestDto request){
        log.info("UPDATE: " + request);
        Birthday birthday = Birthday.findBySnowflake(request.snowflake);
        
        if ( birthday == null ){
            log.warn(NOT_FOUND_MESSAGE);
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        
        birthday.setDay(request.day);
        birthday.setMonth(request.month);
        birthday.setName(request.name);
        
        birthday.persist();
        
        return Response.ok(birthday).status(Status.ACCEPTED).build();
    }      
    
    @DELETE
    @Path("/{snowflake}")
    @Transactional
    @CacheInvalidateAll(cacheName = "birthdays")
    public Response delete(  @PathParam("snowflake") String snowflake ){
        log.info("DELETE: " + snowflake);
        
        Birthday birthday = Birthday.findBySnowflake(snowflake);
        
        if ( birthday == null ){
            log.warn(NOT_FOUND_MESSAGE);
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        
        birthday.delete();
        
        return Response.noContent().build();
    }
}