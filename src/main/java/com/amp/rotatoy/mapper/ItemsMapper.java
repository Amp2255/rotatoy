package com.amp.rotatoy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.amp.rotatoy.dto.ItemsDto;
import com.amp.rotatoy.model.Items;

// For large-scale or production apps, 
//consider using MapStruct to automate mapping with compile-time safety.
//instead of mapper class


@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemsMapper {

    Items toEntity(ItemsDto itemsDto);

    ItemsDto toDto(Items items);

    
    void updateItemFromDto(ItemsDto dto, @MappingTarget Items items);

    
}