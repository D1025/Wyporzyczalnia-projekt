package com.projekt.wypozyczalnia.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JacksonXmlRootElement(localName = "PageMetadata")
public class PageMetadataDto {

    @JsonProperty("page")
    int page;

    @JsonProperty("size")
    int size;

    @JsonProperty("totalElements")
    long totalElements;

    @JsonProperty("totalPages")
    int totalPages;

    @JsonProperty("hasNext")
    boolean hasNext;

    @JsonProperty("hasPrevious")
    boolean hasPrevious;
}
