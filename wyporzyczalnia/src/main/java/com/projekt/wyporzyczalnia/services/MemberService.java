package com.projekt.wyporzyczalnia.services;

import com.projekt.wyporzyczalnia.dao.LoanRepository;
import com.projekt.wyporzyczalnia.dao.MemberRepository;
import com.projekt.wyporzyczalnia.dto.member.MemberCreateRequestDto;
import com.projekt.wyporzyczalnia.dto.member.MemberPageResponseDto;
import com.projekt.wyporzyczalnia.dto.member.MemberResponseDto;
import com.projekt.wyporzyczalnia.dto.member.MemberUpdateRequestDto;
import com.projekt.wyporzyczalnia.exceptions.BusinessRuleViolationException;
import com.projekt.wyporzyczalnia.exceptions.ResourceNotFoundException;
import com.projekt.wyporzyczalnia.mapper.MemberMapper;
import com.projekt.wyporzyczalnia.models.LoanStatus;
import com.projekt.wyporzyczalnia.models.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final EnumSet<LoanStatus> ACTIVE_LOAN_STATUSES = EnumSet.of(LoanStatus.REQUESTED, LoanStatus.ACTIVE);

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final MemberMapper memberMapper;

    @Transactional(readOnly = true)
    public MemberPageResponseDto getMembers(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return memberMapper.toPageResponse(page);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto getMember(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Czytelnik o id %s nie istnieje".formatted(memberId)));
        return memberMapper.toResponse(member);
    }

    @Transactional
    public MemberResponseDto createMember(MemberCreateRequestDto request) {
        memberRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            throw new BusinessRuleViolationException("Czytelnik o podanym adresie e-mail już istnieje");
        });
        Member member = memberMapper.toEntity(request);
        Member saved = memberRepository.save(member);
        return memberMapper.toResponse(saved);
    }

    @Transactional
    public MemberResponseDto updateMember(UUID memberId, MemberUpdateRequestDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Czytelnik o id %s nie istnieje".formatted(memberId)));
        if (request.getEmail() != null && memberRepository.existsByEmailAndIdNot(request.getEmail(), memberId)) {
            throw new BusinessRuleViolationException("Inny czytelnik korzysta już z tego adresu e-mail");
        }
        memberMapper.updateMemberFromDto(request, member);
        Member updated = memberRepository.save(member);
        return memberMapper.toResponse(updated);
    }

    @Transactional
    public void deleteMember(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Czytelnik o id %s nie istnieje".formatted(memberId)));
        boolean hasActiveLoans = loanRepository.existsByMemberIdAndStatusIn(member.getId(), ACTIVE_LOAN_STATUSES);
        if (hasActiveLoans) {
            throw new BusinessRuleViolationException("Nie można usunąć czytelnika z aktywnymi wypożyczeniami");
        }
        memberRepository.delete(member);
    }
}
