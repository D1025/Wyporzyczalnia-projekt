package com.projekt.wyporzyczalnia.mapper;

import com.projekt.wyporzyczalnia.dto.book.BookCreateRequestDto;
import com.projekt.wyporzyczalnia.dto.book.BookPageResponseDto;
import com.projekt.wyporzyczalnia.dto.book.BookResponseDto;
import com.projekt.wyporzyczalnia.dto.book.BookUpdateRequestDto;
import com.projekt.wyporzyczalnia.dto.common.PageMetadataDto;
import com.projekt.wyporzyczalnia.models.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookCreateRequestDto dto);

    BookResponseDto toResponse(Book entity);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        void updateBookFromDto(BookUpdateRequestDto dto, @MappingTarget Book entity);

    default BookPageResponseDto toPageResponse(Page<Book> page) {
        return BookPageResponseDto.builder()
                .metadata(PageMetadataDto.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .books(page.getContent().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
