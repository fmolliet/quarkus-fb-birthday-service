package io.winty.struct.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.RestResponse.Status;

import io.winty.struct.models.Register;
import io.winty.struct.models.dto.CreateRegisterDto;
import io.winty.struct.models.dto.UpdateRegisterDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.jbosslog.JBossLog;

@Path("/registration")
@JBossLog
public class RegistrationResource {

    private static final String NOT_FOUND_MESSAGE = "Usuário não encontrado na base.";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(@Valid CreateRegisterDto request) throws URISyntaxException {
        if (Register.findUnfinishedByApplicantId(request.applicantId()) != null) {
            log.warn("Já existe um registro em andamento para o usuário.");
            throw new BadRequestException("Já existe um registro em andamento para o usuário.");
        }

        log.info("Createing register for: " + request);

        Register register = new Register();

        register.setApplicantId(request.applicantId());
        register.setBirthday(request.birthday());
        register.setSpecie(request.specie());
        register.setSource(request.source());
        register.setMessageId(request.messageId());

        register.persist();

        return Response.created(
                new URI(String.format("/registration/%s", register.getId())))
                .entity(register)
                .build();
    }

    @GET
    @Path("/{applicantId}/open")
    @Produces(MediaType.APPLICATION_JSON)
    public Register getUnfinishedByApplicantId(@PathParam("applicantId") String applicantId) {
        log.info("Get Registration Openned for: " + applicantId);
        return Register.findUnfinishedByApplicantId(applicantId);
    }

    @GET
    @Path("/{registrationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Register getRegistration(@PathParam("registrationId") String registrationId) {
        log.info("Get registrationId for: " + registrationId);
        return Register.findById(UUID.fromString(registrationId));
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response finishRegistration(@RequestBody UpdateRegisterDto request){
        log.info("Updating registration for: " + request.applicantId());
        Register register = Register.findUnfinishedByApplicantId(request.applicantId());
        
        if ( register == null ){
            log.warn(NOT_FOUND_MESSAGE);
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        
        register.setCuratorId(request.curatorId());
        register.setFinished(true);
        register.setApproved(request.approved());
        
        register.persist();
        
        return Response.ok(register).status(Status.ACCEPTED).build();
    }     

    @DELETE
    @Path("/{registrationId}")
    @Transactional
    public Response delete(@PathParam("registrationId") String registrationId) {
        log.info("Deleting registration Id: " + registrationId);

        Register register = Register.findById(UUID.fromString(registrationId));

        if (register == null) {
            log.warn(NOT_FOUND_MESSAGE);
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }

        register.delete();

        return Response.noContent().build();
    }
}
