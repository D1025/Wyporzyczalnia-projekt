package com.projekt.wyporzyczalnia.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "Member")
public class MemberResponseDto {

    UUID id;
    String firstName;
    String lastName;
    String email;
    Boolean active;
}
