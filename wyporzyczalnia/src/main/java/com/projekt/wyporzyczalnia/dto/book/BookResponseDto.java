package com.projekt.wypozyczalnia.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "Book")
public class BookResponseDto {

    UUID id;
    String title;
    String author;
    String isbn;
    Integer publishedYear;
    String genre;
    String description;
    Integer totalCopies;
    Integer availableCopies;
}
