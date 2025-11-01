package com.projekt.wyporzyczalnia.controllers;

import com.projekt.wyporzyczalnia.dto.book.BookCreateRequestDto;
import com.projekt.wyporzyczalnia.dto.book.BookPageResponseDto;
import com.projekt.wyporzyczalnia.dto.book.BookResponseDto;
import com.projekt.wyporzyczalnia.dto.book.BookUpdateRequestDto;
import com.projekt.wyporzyczalnia.dto.common.OperationStatusDto;
import com.projekt.wyporzyczalnia.services.BookService;
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
@RequestMapping(value = "/api/books", produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
})
@Validated
public class BookController {

    private final BookService bookService;

    @GetMapping
        public ResponseEntity<BookPageResponseDto> getBooks(
                        @PageableDefault(size = 20, sort = "title") Pageable pageable) {
                BookPageResponseDto result = bookService.getBooks(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/{bookId}")
        public ResponseEntity<BookResponseDto> getBook(
                        @PathVariable UUID bookId) {
                BookResponseDto result = bookService.getBook(bookId);
        return ResponseEntity.ok(result);
    }

    @PostMapping(consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public ResponseEntity<BookResponseDto> createBook(
                        @Valid @RequestBody BookCreateRequestDto request) {
                BookResponseDto result = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping(path = "/{bookId}", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public ResponseEntity<BookResponseDto> updateBook(
            @PathVariable UUID bookId,
                        @Valid @RequestBody BookUpdateRequestDto request) {
                BookResponseDto result = bookService.updateBook(bookId, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(path = "/{bookId}")
    public ResponseEntity<OperationStatusDto> deleteBook(
            @PathVariable UUID bookId) {
                bookService.deleteBook(bookId);
        OperationStatusDto payload = OperationStatusDto.builder()
                .message("Książka została usunięta")
                .build();
        return ResponseEntity.ok(payload);
    }
}
