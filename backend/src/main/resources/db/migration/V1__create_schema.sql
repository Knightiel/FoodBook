-- =============================================================
-- FoodBook — V1 Schema inicial
-- PostgreSQL 16
-- =============================================================

-- -------------------------------------------------------------
-- EXTENSÕES
-- -------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- -------------------------------------------------------------
-- CATEGORIA
-- -------------------------------------------------------------
CREATE TABLE categoria (
    id      BIGSERIAL    PRIMARY KEY,
    nome    VARCHAR(80)  NOT NULL UNIQUE,
    icone   VARCHAR(100)
);

CREATE INDEX idx_categoria_nome ON categoria (nome);

-- -------------------------------------------------------------
-- USUARIO
-- -------------------------------------------------------------
CREATE TABLE usuario (
    id           BIGSERIAL    PRIMARY KEY,
    nome         VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    senha        VARCHAR(255) NOT NULL,
    foto_url     VARCHAR(500),
    criado_em    TIMESTAMP    NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_usuario_email ON usuario (email);

-- -------------------------------------------------------------
-- RECEITA
-- -------------------------------------------------------------
CREATE TABLE receita (
    id             BIGSERIAL    PRIMARY KEY,
    titulo         VARCHAR(200) NOT NULL,
    descricao      TEXT,
    modo_preparo   TEXT         NOT NULL,
    tempo_preparo  INTEGER      NOT NULL CHECK (tempo_preparo > 0),  -- minutos
    porcoes        INTEGER      NOT NULL CHECK (porcoes > 0),
    imagem_url     VARCHAR(500),
    categoria_id   BIGINT       NOT NULL REFERENCES categoria(id) ON DELETE RESTRICT,
    usuario_id     BIGINT       NOT NULL REFERENCES usuario(id)   ON DELETE CASCADE,
    origem_externa BOOLEAN      NOT NULL DEFAULT FALSE,
    criado_em      TIMESTAMP    NOT NULL DEFAULT NOW(),
    atualizado_em  TIMESTAMP    NOT NULL DEFAULT NOW(),
    busca_ts       TSVECTOR
);

CREATE INDEX idx_receita_usuario     ON receita (usuario_id);
CREATE INDEX idx_receita_categoria   ON receita (categoria_id);
CREATE INDEX idx_receita_criado_em   ON receita (criado_em DESC);
CREATE INDEX idx_receita_busca_ts    ON receita USING GIN (busca_ts);
CREATE INDEX idx_receita_titulo_trgm ON receita USING GIN (titulo gin_trgm_ops);

-- Manter busca_ts atualizado automaticamente
CREATE OR REPLACE FUNCTION receita_busca_ts_trigger() RETURNS TRIGGER AS $$
BEGIN
    NEW.busca_ts := to_tsvector('portuguese',
        unaccent(COALESCE(NEW.titulo, '')) || ' ' ||
        unaccent(COALESCE(NEW.descricao, ''))
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_receita_busca_ts
    BEFORE INSERT OR UPDATE ON receita
    FOR EACH ROW EXECUTE FUNCTION receita_busca_ts_trigger();

-- -------------------------------------------------------------
-- INGREDIENTE
-- -------------------------------------------------------------
CREATE TABLE ingrediente (
    id             BIGSERIAL   PRIMARY KEY,
    nome           VARCHAR(150) NOT NULL UNIQUE,
    unidade_padrao VARCHAR(20)
);

CREATE UNIQUE INDEX idx_ingrediente_nome ON ingrediente (lower(unaccent(nome)));

-- -------------------------------------------------------------
-- RECEITA_INGREDIENTE
-- -------------------------------------------------------------
CREATE TABLE receita_ingrediente (
    receita_id    BIGINT         NOT NULL REFERENCES receita(id)     ON DELETE CASCADE,
    ingrediente_id BIGINT        NOT NULL REFERENCES ingrediente(id) ON DELETE RESTRICT,
    quantidade    NUMERIC(10, 3) NOT NULL CHECK (quantidade > 0),
    unidade       VARCHAR(30)    NOT NULL,
    observacao    VARCHAR(200),
    PRIMARY KEY (receita_id, ingrediente_id)
);

CREATE INDEX idx_ri_ingrediente ON receita_ingrediente (ingrediente_id);

-- -------------------------------------------------------------
-- COMENTARIO
-- -------------------------------------------------------------
CREATE TABLE comentario (
    id         BIGSERIAL PRIMARY KEY,
    texto      TEXT      NOT NULL,
    usuario_id BIGINT    NOT NULL REFERENCES usuario(id)  ON DELETE CASCADE,
    receita_id BIGINT    NOT NULL REFERENCES receita(id)  ON DELETE CASCADE,
    criado_em  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comentario_receita ON comentario (receita_id, criado_em DESC);
CREATE INDEX idx_comentario_usuario ON comentario (usuario_id);

-- -------------------------------------------------------------
-- CURTIDA
-- -------------------------------------------------------------
CREATE TABLE curtida (
    usuario_id BIGINT    NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    receita_id BIGINT    NOT NULL REFERENCES receita(id) ON DELETE CASCADE,
    criado_em  TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_id, receita_id)
);

CREATE INDEX idx_curtida_receita ON curtida (receita_id);

-- -------------------------------------------------------------
-- FAVORITO
-- -------------------------------------------------------------
CREATE TABLE favorito (
    usuario_id BIGINT    NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    receita_id BIGINT    NOT NULL REFERENCES receita(id) ON DELETE CASCADE,
    criado_em  TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_id, receita_id)
);

CREATE INDEX idx_favorito_usuario ON favorito (usuario_id, criado_em DESC);

-- -------------------------------------------------------------
-- LISTA_COMPRA
-- -------------------------------------------------------------
CREATE TABLE lista_compra (
    id         BIGSERIAL    PRIMARY KEY,
    nome       VARCHAR(200) NOT NULL,
    usuario_id BIGINT       NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    criado_em  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_lista_compra_usuario ON lista_compra (usuario_id, criado_em DESC);

-- -------------------------------------------------------------
-- ITEM_LISTA_COMPRA
-- -------------------------------------------------------------
CREATE TABLE item_lista_compra (
    id              BIGSERIAL      PRIMARY KEY,
    lista_compra_id BIGINT         NOT NULL REFERENCES lista_compra(id) ON DELETE CASCADE,
    ingrediente_id  BIGINT         NOT NULL REFERENCES ingrediente(id)  ON DELETE RESTRICT,
    quantidade      NUMERIC(10, 3) NOT NULL CHECK (quantidade > 0),
    unidade         VARCHAR(30)    NOT NULL,
    comprado        BOOLEAN        NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_item_lista ON item_lista_compra (lista_compra_id);
CREATE UNIQUE INDEX idx_item_lista_ingrediente ON item_lista_compra (lista_compra_id, ingrediente_id);
