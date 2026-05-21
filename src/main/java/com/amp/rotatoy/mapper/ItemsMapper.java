package com.amp.rotatoy.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapping;

import com.amp.rotatoy.dto.ItemsDto;
import com.amp.rotatoy.model.Items;

// For large-scale or production apps, 
//consider using MapStruct to automate mapping with compile-time safety.
//instead of mapper class


@Mapper(componentModel = "spring")
public interface ItemsMapper {

    Items toEntity(ItemsDto itemsDto);

    ItemsDto toDto(Items items);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateExceptNulls(ItemsDto dto, @MappingTarget Items entity);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateForDelete(ItemsDto dto, @MappingTarget Items entity);

    @Mapping(target = "id", ignore = true)
    void updateItemFromDto(ItemsDto dto, @MappingTarget Items items);

    
}