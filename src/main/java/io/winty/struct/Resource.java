package io.winty.struct;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse.Status;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import lombok.extern.jbosslog.JBossLog;

@Path("/birthday")
@JBossLog
public class Resource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "birthdays") 
    
    public List<Birthday> list(
        @Min(0) 
        @Max(1000) 
        @QueryParam("page") int page, 
        @Min(0) 
        @Max(50) 
        @QueryParam("size") int size) {
        log.info("LIST");
        if(size==0){size = 10;} 
        return Birthday.listAllBirthDays(page, size);
    }
    
    @GET
    @Path("/today")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "birthdays") 
    public List<Birthday> today() {
        log.info("TODAY");
        return Birthday.findTodayBirthDays();
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
    public Response create(@Valid Request request) throws URISyntaxException {
        log.info("CREATE: " + request);
              
        if ( Birthday.findBySnowflake(request.snowflake) != null ){
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
    public Response update(@Valid Request request){
        log.info("UPDATE: " + request);
        Birthday birthday = Birthday.findBySnowflake(request.snowflake);
        
        if ( birthday == null ){
            throw new NotFoundException("Usuário não encontrado na base.");
        }
        
        birthday.setDay(request.day);
        birthday.setMonth(request.month);
        birthday.setName(request.name);
        
        birthday.update();
        
        return Response.ok(birthday).status(Status.ACCEPTED).build();
    }      
    
    @DELETE
    @Path("/{snowflake}")
    @CacheInvalidateAll(cacheName = "birthdays")
    public Response delete(  @PathParam("snowflake") String snowflake ){
        log.info("DELETE: " + snowflake);
        
        Birthday birthday = Birthday.findBySnowflake(snowflake);
        
        if ( birthday == null ){
            throw new NotFoundException("Usuário não encontrado na base.");
        }
        
        birthday.delete();
        
        return Response.noContent().build();
    }
}