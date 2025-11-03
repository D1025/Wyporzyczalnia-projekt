package com.projekt.wypozyczalnia.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "Error")
public class ErrorResponseDto {

    OffsetDateTime timestamp;
    int status;
    String error;
    String message;
    String path;
}
