package com.projekt.wypozyczalnia.mapper;

import com.projekt.wypozyczalnia.dto.common.PageMetadataDto;
import com.projekt.wypozyczalnia.dto.loan.LoanPageResponseDto;
import com.projekt.wypozyczalnia.dto.loan.LoanResponseDto;
import com.projekt.wypozyczalnia.dto.loan.LoanUpdateRequestDto;
import com.projekt.wypozyczalnia.models.Loan;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "memberId", source = "member.id")
    LoanResponseDto toResponse(Loan entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLoanFromDto(LoanUpdateRequestDto dto, @MappingTarget Loan entity);

    default LoanPageResponseDto toPageResponse(Page<Loan> page) {
        return LoanPageResponseDto.builder()
                .metadata(PageMetadataDto.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .loans(page.getContent().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
