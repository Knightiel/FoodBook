package com.foodbook.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CurtidaId implements Serializable {

    private Long usuarioId;
    private Long receitaId;
}
