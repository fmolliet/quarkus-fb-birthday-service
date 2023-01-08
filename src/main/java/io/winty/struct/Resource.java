package io.winty.struct;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse.Status;

import lombok.extern.jbosslog.JBossLog;

@Path("/birthday")
@JBossLog
public class Resource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Birthday> list() {
        return Birthday.findAll().list();
    }
    
    @GET
    @Path("/today")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Birthday> today() {
        return Birthday.findTodayBirthDays();
    }
    
    @GET
    @Path("/{snowflake}")
    @Produces(MediaType.APPLICATION_JSON)
    public Birthday get(@PathParam("snowflake") String snowflake) {
        log.info("BUSCANDO: " + snowflake);
        return Birthday.findBySnowflake(snowflake);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
        @Transactional
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
    public Response delete(  @PathParam("snowflake") String snowflake ){
        log.info("DELETANDO: " + snowflake);
        
        Birthday birthday = Birthday.findBySnowflake(snowflake);
        
        if ( birthday == null ){
            throw new NotFoundException("Usuário não encontrado na base.");
        }
        
        birthday.delete();
        
        return Response.noContent().build();
    }
}