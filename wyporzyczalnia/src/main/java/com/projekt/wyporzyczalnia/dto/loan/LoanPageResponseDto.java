package com.projekt.wyporzyczalnia.dto.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.projekt.wyporzyczalnia.dto.common.PageMetadataDto;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "LoanPage")
public class LoanPageResponseDto {

    @Singular("loan")
    @JacksonXmlElementWrapper(localName = "loans")
    @JacksonXmlProperty(localName = "Loan")
    List<LoanResponseDto> loans;

    @JacksonXmlProperty(localName = "metadata")
    PageMetadataDto metadata;
}
