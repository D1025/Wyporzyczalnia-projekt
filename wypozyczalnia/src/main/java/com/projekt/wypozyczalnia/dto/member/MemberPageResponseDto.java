package com.projekt.wypozyczalnia.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.projekt.wypozyczalnia.dto.common.PageMetadataDto;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "MemberPage")
public class MemberPageResponseDto {

    @Singular("member")
    @JacksonXmlElementWrapper(localName = "members")
    @JacksonXmlProperty(localName = "Member")
    List<MemberResponseDto> members;

    @JacksonXmlProperty(localName = "metadata")
    PageMetadataDto metadata;
}
