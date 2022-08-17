package com.sushi.api.dto;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

// @formatter:off
@Mapper(componentModel = "spring", 
nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, 
unmappedTargetPolicy = ReportingPolicy.IGNORE,  
nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
//@formatter:on
public interface EntityDTOMapper {
  
}
