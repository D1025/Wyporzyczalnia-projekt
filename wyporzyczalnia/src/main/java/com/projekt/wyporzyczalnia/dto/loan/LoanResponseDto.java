package com.projekt.wypozyczalnia.dto.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.projekt.wypozyczalnia.models.LoanStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "Loan")
public class LoanResponseDto {

    UUID id;
    UUID bookId;
    UUID memberId;
    LocalDate loanDate;
    LocalDate dueDate;
    LocalDate returnDate;
    LoanStatus status;
}
