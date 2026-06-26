package com.foodbook.mapper;

import com.foodbook.dto.response.CategoriaResponse;
import com.foodbook.entity.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaResponse toResponse(Categoria categoria);
}
