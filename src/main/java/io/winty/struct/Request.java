package io.winty.struct;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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
