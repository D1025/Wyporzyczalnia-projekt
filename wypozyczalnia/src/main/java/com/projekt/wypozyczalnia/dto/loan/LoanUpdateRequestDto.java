package com.projekt.wypozyczalnia.dto.loan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.projekt.wypozyczalnia.models.LoanStatus;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "LoanUpdateRequest")
public class LoanUpdateRequestDto {

    @FutureOrPresent
    private LocalDate dueDate;

    private LoanStatus status;

    private LocalDate returnDate;
}
