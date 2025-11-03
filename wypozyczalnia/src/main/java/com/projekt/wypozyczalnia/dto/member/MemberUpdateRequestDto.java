package com.projekt.wypozyczalnia.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "MemberUpdateRequest")
public class MemberUpdateRequestDto {

    @Size(max = 128)
    private String firstName;

    @Size(max = 128)
    private String lastName;

    @Email
    private String email;

    private Boolean active;
}
