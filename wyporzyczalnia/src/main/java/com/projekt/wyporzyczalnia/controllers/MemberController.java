package com.projekt.wypozyczalnia.controllers;

import com.projekt.wypozyczalnia.dto.common.OperationStatusDto;
import com.projekt.wypozyczalnia.dto.member.MemberCreateRequestDto;
import com.projekt.wypozyczalnia.dto.member.MemberPageResponseDto;
import com.projekt.wypozyczalnia.dto.member.MemberResponseDto;
import com.projekt.wypozyczalnia.dto.member.MemberUpdateRequestDto;
import com.projekt.wypozyczalnia.services.MemberService;
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
@RequestMapping(value = "/api/members", produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
})
@Validated
public class MemberController {

    private final MemberService memberService;

    @GetMapping
        public ResponseEntity<MemberPageResponseDto> getMembers(
                        @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
                MemberPageResponseDto result = memberService.getMembers(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/{memberId}")
    public ResponseEntity<MemberResponseDto> getMember(
                        @PathVariable UUID memberId) {
                MemberResponseDto result = memberService.getMember(memberId);
        return ResponseEntity.ok(result);
    }

    @PostMapping(consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public ResponseEntity<MemberResponseDto> createMember(
                        @Valid @RequestBody MemberCreateRequestDto request) {
                MemberResponseDto result = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping(path = "/{memberId}", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public ResponseEntity<MemberResponseDto> updateMember(
            @PathVariable UUID memberId,
                        @Valid @RequestBody MemberUpdateRequestDto request) {
                MemberResponseDto result = memberService.updateMember(memberId, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(path = "/{memberId}")
    public ResponseEntity<OperationStatusDto> deleteMember(
            @PathVariable UUID memberId) {
                memberService.deleteMember(memberId);
        OperationStatusDto payload = OperationStatusDto.builder()
                .message("Czytelnik został usunięty")
                .build();
        return ResponseEntity.ok(payload);
    }
}
