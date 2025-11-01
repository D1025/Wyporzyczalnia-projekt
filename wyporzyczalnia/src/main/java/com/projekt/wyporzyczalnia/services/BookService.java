package com.projekt.wyporzyczalnia.services;

import com.projekt.wyporzyczalnia.dao.BookRepository;
import com.projekt.wyporzyczalnia.dao.LoanRepository;
import com.projekt.wyporzyczalnia.dto.book.BookCreateRequestDto;
import com.projekt.wyporzyczalnia.dto.book.BookPageResponseDto;
import com.projekt.wyporzyczalnia.dto.book.BookResponseDto;
import com.projekt.wyporzyczalnia.dto.book.BookUpdateRequestDto;
import com.projekt.wyporzyczalnia.exceptions.BusinessRuleViolationException;
import com.projekt.wyporzyczalnia.exceptions.ResourceNotFoundException;
import com.projekt.wyporzyczalnia.mapper.BookMapper;
import com.projekt.wyporzyczalnia.models.Book;
import com.projekt.wyporzyczalnia.models.LoanStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private static final EnumSet<LoanStatus> ACTIVE_LOAN_STATUSES = EnumSet.of(LoanStatus.REQUESTED, LoanStatus.ACTIVE);

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    public BookPageResponseDto getBooks(Pageable pageable) {
        Page<Book> page = bookRepository.findAll(pageable);
        return bookMapper.toPageResponse(page);
    }

    @Transactional(readOnly = true)
    public BookResponseDto getBook(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Książka o id %s nie istnieje".formatted(bookId)));
        return bookMapper.toResponse(book);
    }

    @Transactional
    public BookResponseDto createBook(BookCreateRequestDto request) {
        validateCopies(request.getTotalCopies(), request.getAvailableCopies());
        bookRepository.findByIsbn(request.getIsbn()).ifPresent(existing -> {
            throw new BusinessRuleViolationException("Książka o numerze ISBN %s już istnieje".formatted(request.getIsbn()));
        });
        Book book = bookMapper.toEntity(request);
        Book saved = bookRepository.save(book);
        return bookMapper.toResponse(saved);
    }

    @Transactional
    public BookResponseDto updateBook(UUID bookId, BookUpdateRequestDto request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Książka o id %s nie istnieje".formatted(bookId)));
        if (request.getIsbn() != null && bookRepository.existsByIsbnAndIdNot(request.getIsbn(), bookId)) {
            throw new BusinessRuleViolationException("Inna książka z tym samym numerem ISBN już istnieje");
        }
        Integer totalCopies = request.getTotalCopies() != null ? request.getTotalCopies() : book.getTotalCopies();
        Integer availableCopies = request.getAvailableCopies() != null ? request.getAvailableCopies() : book.getAvailableCopies();
        validateCopies(totalCopies, availableCopies);
        bookMapper.updateBookFromDto(request, book);
        Book updated = bookRepository.save(book);
        return bookMapper.toResponse(updated);
    }

    @Transactional
    public void deleteBook(UUID bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Książka o id %s nie istnieje".formatted(bookId)));
        boolean hasActiveLoans = loanRepository.existsByBookIdAndStatusIn(book.getId(), ACTIVE_LOAN_STATUSES);
        if (hasActiveLoans) {
            throw new BusinessRuleViolationException("Nie można usunąć książki, która posiada aktywne wypożyczenia");
        }
        bookRepository.delete(book);
    }

    private void validateCopies(Integer totalCopies, Integer availableCopies) {
        if (availableCopies > totalCopies) {
            throw new BusinessRuleViolationException("Liczba dostępnych egzemplarzy nie może przekraczać liczby wszystkich egzemplarzy");
        }
    }
}
