package com.projekt.wyporzyczalnia.services;

import com.projekt.wyporzyczalnia.dao.BookRepository;
import com.projekt.wyporzyczalnia.dao.LoanRepository;
import com.projekt.wyporzyczalnia.dao.MemberRepository;
import com.projekt.wyporzyczalnia.dto.loan.LoanCreateRequestDto;
import com.projekt.wyporzyczalnia.dto.loan.LoanPageResponseDto;
import com.projekt.wyporzyczalnia.dto.loan.LoanResponseDto;
import com.projekt.wyporzyczalnia.dto.loan.LoanUpdateRequestDto;
import com.projekt.wyporzyczalnia.exceptions.BusinessRuleViolationException;
import com.projekt.wyporzyczalnia.exceptions.ResourceNotFoundException;
import com.projekt.wyporzyczalnia.mapper.LoanMapper;
import com.projekt.wyporzyczalnia.models.Book;
import com.projekt.wyporzyczalnia.models.Loan;
import com.projekt.wyporzyczalnia.models.LoanStatus;
import com.projekt.wyporzyczalnia.models.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final EnumSet<LoanStatus> ACTIVE_LOAN_STATUSES = EnumSet.of(LoanStatus.REQUESTED, LoanStatus.ACTIVE);

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final LoanMapper loanMapper;

    @Transactional(readOnly = true)
    public LoanPageResponseDto getMemberLoans(UUID memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Czytelnik o id %s nie istnieje".formatted(memberId)));
        Page<Loan> page = loanRepository.findAllByMemberId(member.getId(), pageable);
        return loanMapper.toPageResponse(page);
    }

    @Transactional(readOnly = true)
    public LoanResponseDto getMemberLoan(UUID memberId, UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .filter(existing -> existing.getMember().getId().equals(memberId))
                .orElseThrow(() -> new ResourceNotFoundException("Wypożyczenie o id %s dla czytelnika %s nie istnieje".formatted(loanId, memberId)));
        return loanMapper.toResponse(loan);
    }

    @Transactional
    public LoanResponseDto createLoan(UUID memberId, LoanCreateRequestDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Czytelnik o id %s nie istnieje".formatted(memberId)));
        if (Boolean.FALSE.equals(member.getActive())) {
            throw new BusinessRuleViolationException("Nieaktywny czytelnik nie może wypożyczać książek");
        }
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Książka o id %s nie istnieje".formatted(request.getBookId())));
        if (book.getAvailableCopies() <= 0) {
            throw new BusinessRuleViolationException("Brak dostępnych egzemplarzy do wypożyczenia");
        }
        boolean alreadyLoaned = loanRepository.existsByMemberIdAndBookIdAndStatusIn(member.getId(), book.getId(), ACTIVE_LOAN_STATUSES);
        if (alreadyLoaned) {
            throw new BusinessRuleViolationException("Czytelnik posiada już aktywne wypożyczenie tej książki");
        }
        Loan loan = Loan.builder()
                .book(book)
                .member(member)
                .loanDate(LocalDate.now())
                .dueDate(request.getDueDate())
                .status(LoanStatus.ACTIVE)
                .build();
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Loan saved = loanRepository.save(loan);
        return loanMapper.toResponse(saved);
    }

    @Transactional
    public LoanResponseDto updateLoan(UUID memberId, UUID loanId, LoanUpdateRequestDto request) {
        Loan loan = loanRepository.findById(loanId)
                .filter(existing -> existing.getMember().getId().equals(memberId))
                .orElseThrow(() -> new ResourceNotFoundException("Wypożyczenie o id %s dla czytelnika %s nie istnieje".formatted(loanId, memberId)));
        boolean wasReturned = loan.getReturnDate() != null;
        if (request.getDueDate() != null && request.getDueDate().isBefore(loan.getLoanDate())) {
            throw new BusinessRuleViolationException("Nowy termin zwrotu nie może być wcześniejszy niż data wypożyczenia");
        }
        if (request.getReturnDate() != null && request.getReturnDate().isBefore(loan.getLoanDate())) {
            throw new BusinessRuleViolationException("Data zwrotu nie może być wcześniejsza niż data wypożyczenia");
        }
        if (request.getStatus() != null) {
            validateStatusTransition(loan.getStatus(), request.getStatus());
            loan.setStatus(request.getStatus());
        }
        if (request.getReturnDate() != null) {
            loan.setReturnDate(request.getReturnDate());
            if (loan.getStatus() == LoanStatus.ACTIVE) {
                loan.setStatus(LoanStatus.RETURNED);
            }
            if (!wasReturned && loan.getBook().getAvailableCopies() < loan.getBook().getTotalCopies()) {
                loan.getBook().setAvailableCopies(loan.getBook().getAvailableCopies() + 1);
            }
        }
        loanMapper.updateLoanFromDto(request, loan);
        Loan updated = loanRepository.save(loan);
        return loanMapper.toResponse(updated);
    }

    @Transactional
    public void deleteLoan(UUID memberId, UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .filter(existing -> existing.getMember().getId().equals(memberId))
                .orElseThrow(() -> new ResourceNotFoundException("Wypożyczenie o id %s dla czytelnika %s nie istnieje".formatted(loanId, memberId)));
        if (ACTIVE_LOAN_STATUSES.contains(loan.getStatus())) {
            int restored = Math.min(loan.getBook().getAvailableCopies() + 1, loan.getBook().getTotalCopies());
            loan.getBook().setAvailableCopies(restored);
        }
        loanRepository.delete(loan);
    }

    private void validateStatusTransition(LoanStatus from, LoanStatus to) {
        if (from == LoanStatus.RETURNED && to != LoanStatus.RETURNED) {
            throw new BusinessRuleViolationException("Zwrot wypożyczenia jest operacją nieodwracalną");
        }
        if (from == LoanStatus.OVERDUE && to == LoanStatus.REQUESTED) {
            throw new BusinessRuleViolationException("Nie można przywrócić wypożyczenia do statusu REQUESTED");
        }
    }
}
