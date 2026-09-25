-- Limpeza prévia dos estágios de plantas
DELETE FROM public.plant_stage;

-- Inserção dos estágios de crescimento específicos por categoria (Ornamental, Comestível, Aromática)

-- 1. Estágio 1: Muda / Germinação
INSERT INTO public.plant_stage (id, plant_id, name, "order", days, description, image)
SELECT 
    gen_random_uuid(),
    p.id,
    CASE 
        WHEN p.type = 'ORNAMENTAL' THEN 'Muda / Estabelecimento'
        WHEN p.type = 'AROMATIC' THEN 'Muda / Enraizamento'
        ELSE 'Germinação / Muda'
    END,
    1,
    CASE 
        WHEN p.name LIKE '%Rabanete%' THEN 5
        WHEN p.name LIKE '%Alface%' THEN 7
        WHEN p.name LIKE '%Coentro%' THEN 10
        WHEN p.type = 'ORNAMENTAL' THEN 15
        ELSE 10
    END,
    CASE 
        WHEN p.type = 'ORNAMENTAL' THEN 'Fase inicial da planta com desenvolvimento das primeiras folhas, raízes e estruturas básicas. Requer atenção quanto à rega e luz.'
        WHEN p.type = 'AROMATIC' THEN 'Período inicial caracterizado pelo surgimento das primeiras folhas e fortalecimento do sistema radicular.'
        ELSE 'Fase inicial de germinação e estabelecimento da planta. O crescimento é voltado para a formação das primeiras folhas e raízes.'
    END,
    p.image
FROM public.plant p;

-- 2. Estágio 2: Crescimento Vegetativo
INSERT INTO public.plant_stage (id, plant_id, name, "order", days, description, image)
SELECT 
    gen_random_uuid(),
    p.id,
    'Crescimento Vegetativo',
    2,
    CASE 
        WHEN p.name LIKE '%Rabanete%' THEN 18
        WHEN p.name LIKE '%Alface%' THEN 25
        WHEN p.name LIKE '%Coentro%' THEN 35
        WHEN p.type = 'ORNAMENTAL' THEN 45
        ELSE 30
    END,
    CASE 
        WHEN p.type = 'ORNAMENTAL' THEN 'Período de desenvolvimento vegetativo com aumento de altura, quantidade de folhas e fortalecimento do sistema radicular.'
        WHEN p.type = 'AROMATIC' THEN 'Fase de expansão da parte vegetativa com aumento das folhas e intensificação gradual dos óleos aromáticos característicos.'
        ELSE 'Etapa de desenvolvimento vegetativo marcada pelo aumento da biomassa e expansão das folhas, caules e raízes.'
    END,
    p.image
FROM public.plant p;

-- 3. Estágio 3: Floração / Colheita
INSERT INTO public.plant_stage (id, plant_id, name, "order", days, description, image)
SELECT 
    gen_random_uuid(),
    p.id,
    CASE 
        WHEN p.type = 'ORNAMENTAL' THEN 'Floração / Maturidade'
        WHEN p.type = 'AROMATIC' THEN 'Ponto Aromático / Colheita'
        ELSE 'Maturação / Colheita'
    END,
    3,
    CASE 
        WHEN p.name LIKE '%Rabanete%' THEN 35
        WHEN p.name LIKE '%Alface%' THEN 50
        WHEN p.name LIKE '%Coentro%' THEN 75
        WHEN p.type = 'ORNAMENTAL' THEN 90
        ELSE 60
    END,
    CASE 
        WHEN p.type = 'ORNAMENTAL' THEN 'Estágio em que a planta atinge a maturidade e inicia a produção de flores, exibindo seu valor estético principal.'
        WHEN p.type = 'AROMATIC' THEN 'Estágio em que as folhas, flores ou ramos atingem o melhor potencial aromático e podem ser colhidos.'
        ELSE 'Fase em que a planta alcança o ponto ideal para consumo de suas folhas, frutos ou raízes.'
    END,
    p.image
FROM public.plant p;
