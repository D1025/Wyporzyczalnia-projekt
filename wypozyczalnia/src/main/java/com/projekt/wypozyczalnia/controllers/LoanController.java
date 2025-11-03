package com.projekt.wypozyczalnia.controllers;

import com.projekt.wypozyczalnia.dto.common.OperationStatusDto;
import com.projekt.wypozyczalnia.dto.loan.LoanCreateRequestDto;
import com.projekt.wypozyczalnia.dto.loan.LoanPageResponseDto;
import com.projekt.wypozyczalnia.dto.loan.LoanResponseDto;
import com.projekt.wypozyczalnia.dto.loan.LoanUpdateRequestDto;
import com.projekt.wypozyczalnia.services.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/members/{memberId}/loans", produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
})
@Validated
public class LoanController {

    private final LoanService loanService;

    @GetMapping
        public ResponseEntity<LoanPageResponseDto> getLoans(
                        @PathVariable UUID memberId,
                        @PageableDefault(size = 20, sort = "loanDate") Pageable pageable) {
                LoanPageResponseDto result = loanService.getMemberLoans(memberId, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/{loanId}")
    public ResponseEntity<LoanResponseDto> getLoan(
                        @PathVariable UUID memberId,
                        @PathVariable UUID loanId) {
                LoanResponseDto result = loanService.getMemberLoan(memberId, loanId);
        return ResponseEntity.ok(result);
    }

    @PostMapping(consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public ResponseEntity<LoanResponseDto> createLoan(
                        @PathVariable UUID memberId,
                        @Valid @RequestBody LoanCreateRequestDto request) {
                LoanResponseDto result = loanService.createLoan(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping(path = "/{loanId}", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public ResponseEntity<LoanResponseDto> updateLoan(
            @PathVariable UUID memberId,
            @PathVariable UUID loanId,
                        @Valid @RequestBody LoanUpdateRequestDto request) {
                LoanResponseDto result = loanService.updateLoan(memberId, loanId, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(path = "/{loanId}")
    public ResponseEntity<OperationStatusDto> deleteLoan(
            @PathVariable UUID memberId,
            @PathVariable UUID loanId) {
                loanService.deleteLoan(memberId, loanId);
        OperationStatusDto payload = OperationStatusDto.builder()
                .message("Wypożyczenie zostało anulowane")
                .build();
        return ResponseEntity.ok(payload);
    }
}
