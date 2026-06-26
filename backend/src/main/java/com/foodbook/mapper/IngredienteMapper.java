package com.foodbook.mapper;

import com.foodbook.dto.response.IngredienteResponse;
import com.foodbook.entity.Ingrediente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {

    IngredienteResponse toResponse(Ingrediente ingrediente);
}
