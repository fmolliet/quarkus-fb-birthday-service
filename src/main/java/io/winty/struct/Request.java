package io.winty.struct;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.ToString;

/**
 * Classe de recebimento de dados
 * utilizando hibernate validator
 * https://quarkus.io/guides/validation
 */
@ToString
public class Request {
    @Max(value=30)
    @Min(value=1)
    public int day;
    @Max(value=12)
    @Min(value=1)
    public int month;
    @NotBlank
    @NotEmpty
    public String name;
    @NotEmpty
    @NotEmpty
    public String snowflake;
}
