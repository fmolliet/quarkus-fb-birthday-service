package io.winty.struct;

import java.util.logging.Level;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.extern.java.Log;
import lombok.extern.jbosslog.JBossLog;

/**
 * ReaderExceptionMapper
 */
@Provider
@JBossLog
public class ReaderExceptionMapper implements ExceptionMapper<ClientErrorException> {

    @Override
    public Response toResponse(ClientErrorException exception) {
        
        ErrorDTO message = new ErrorDTO();
        
        if( exception.getClass() == BadRequestException.class ||
                exception.getClass() == NotFoundException.class ) {
            message.setMessage(exception.getMessage());        
                    
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(message)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        message.setMessage("Um erro interno ocorreu, por favor contate o adminstrador.");  

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(message)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}