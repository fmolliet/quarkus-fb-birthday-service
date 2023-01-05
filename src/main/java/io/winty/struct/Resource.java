package io.winty.struct;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("/birthday")
public class Resource {
    
    public static final SecureRandom random = new SecureRandom();
    
    private static final Logger log = Logger.getLogger(Resource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Birthday> list() {
        return Birthday.findAll().list();
    }
    
    @GET
    @Path("/{snowflake}")
    @Produces(MediaType.APPLICATION_JSON)
    public Birthday get(@PathParam("snowflake") String snowflake) {
        return Birthday.findBySnowflake(snowflake);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
        @Transactional
    public Response create(CreateDTO request) throws URISyntaxException {
        log.info(request);
        random.nextBytes(new byte[8]);
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
}