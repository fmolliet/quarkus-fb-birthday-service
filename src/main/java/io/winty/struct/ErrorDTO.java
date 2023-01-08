package io.winty.struct;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

/**
 * ErrorDTO
 */
@Data
@RegisterForReflection
public class ErrorDTO {

    private String message;
}