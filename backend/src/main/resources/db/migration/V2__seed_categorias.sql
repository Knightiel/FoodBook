-- =============================================================
-- FoodBook — V2 Seeds: categorias padrão
-- =============================================================

INSERT INTO categoria (nome, icone) VALUES
    ('Massas',       'pasta'),
    ('Carnes',       'meat'),
    ('Bolos',        'cake'),
    ('Doces',        'candy'),
    ('Sobremesas',   'dessert'),
    ('Bebidas',      'drink'),
    ('Vegano',       'leaf'),
    ('Vegetariano',  'broccoli'),
    ('Saladas',      'salad'),
    ('Sopas',        'soup'),
    ('Lanches',      'sandwich'),
    ('Frutos do Mar','shrimp')
ON CONFLICT (nome) DO NOTHING;
