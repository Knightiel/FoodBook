package com.foodbook.mapper;

import com.foodbook.dto.response.ReceitaDetalheResponse;
import com.foodbook.dto.response.ReceitaIngredienteResponse;
import com.foodbook.dto.response.ReceitaResumoResponse;
import com.foodbook.entity.Receita;
import com.foodbook.entity.ReceitaIngrediente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoriaMapper.class, UsuarioMapper.class})
public interface ReceitaMapper {

    @Mapping(target = "autor", source = "usuario")
    @Mapping(target = "totalCurtidas", expression = "java(receita.getTotalCurtidas())")
    @Mapping(target = "totalComentarios", expression = "java(receita.getTotalComentarios())")
    @Mapping(target = "curtidoPorMim", ignore = true)
    @Mapping(target = "favoritadoPorMim", ignore = true)
    ReceitaResumoResponse toResumoResponse(Receita receita);

    @Mapping(target = "autor", source = "usuario")
    @Mapping(target = "totalCurtidas", expression = "java(receita.getTotalCurtidas())")
    @Mapping(target = "totalComentarios", expression = "java(receita.getTotalComentarios())")
    @Mapping(target = "curtidoPorMim", ignore = true)
    @Mapping(target = "favoritadoPorMim", ignore = true)
    ReceitaDetalheResponse toDetalheResponse(Receita receita);

    @Mapping(target = "ingredienteId", source = "ingrediente.id")
    @Mapping(target = "nomeIngrediente", source = "ingrediente.nome")
    ReceitaIngredienteResponse toIngredienteResponse(ReceitaIngrediente receitaIngrediente);
}
