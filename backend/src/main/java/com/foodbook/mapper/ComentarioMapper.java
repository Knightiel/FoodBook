package com.foodbook.mapper;

import com.foodbook.dto.response.ComentarioResponse;
import com.foodbook.entity.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface ComentarioMapper {

    @Mapping(target = "autor", source = "usuario")
    @Mapping(target = "receitaId", source = "receita.id")
    ComentarioResponse toResponse(Comentario comentario);
}
