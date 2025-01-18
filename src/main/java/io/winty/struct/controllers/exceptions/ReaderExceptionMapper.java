package io.winty.struct.controllers.exceptions;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * ReaderExceptionMapper
 */
@Provider
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