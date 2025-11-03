package com.projekt.wypozyczalnia.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.projekt.wypozyczalnia.dto.common.PageMetadataDto;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "BookPage")
public class BookPageResponseDto {

    @Singular("book")
    @JacksonXmlElementWrapper(localName = "books")
    @JacksonXmlProperty(localName = "Book")
    List<BookResponseDto> books;

    @JacksonXmlProperty(localName = "metadata")
    PageMetadataDto metadata;
}
