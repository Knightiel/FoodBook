package com.foodbook.mapper;

import com.foodbook.dto.response.UsuarioPerfilResponse;
import com.foodbook.dto.response.UsuarioResponse;
import com.foodbook.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario usuario);

    @Mapping(target = "totalReceitas", ignore = true)
    @Mapping(target = "totalCurtidasRecebidas", ignore = true)
    @Mapping(target = "totalFavoritos", ignore = true)
    UsuarioPerfilResponse toPerfilResponse(Usuario usuario);
}
