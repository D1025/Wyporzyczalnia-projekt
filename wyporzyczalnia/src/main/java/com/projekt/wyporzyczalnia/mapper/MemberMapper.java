package com.projekt.wypozyczalnia.mapper;

import com.projekt.wypozyczalnia.dto.common.PageMetadataDto;
import com.projekt.wypozyczalnia.dto.member.MemberCreateRequestDto;
import com.projekt.wypozyczalnia.dto.member.MemberPageResponseDto;
import com.projekt.wypozyczalnia.dto.member.MemberResponseDto;
import com.projekt.wypozyczalnia.dto.member.MemberUpdateRequestDto;
import com.projekt.wypozyczalnia.models.Member;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    Member toEntity(MemberCreateRequestDto dto);

    MemberResponseDto toResponse(Member entity);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        void updateMemberFromDto(MemberUpdateRequestDto dto, @MappingTarget Member entity);

    default MemberPageResponseDto toPageResponse(Page<Member> page) {
        return MemberPageResponseDto.builder()
                .metadata(PageMetadataDto.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .members(page.getContent().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
