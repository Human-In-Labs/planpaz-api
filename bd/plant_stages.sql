-- Limpeza prévia dos estágios de plantas
DELETE FROM public.plant_stage;

-- Inserção dinâmica dos estágios de crescimento para cada espécie da tabela 'plant'
-- 1. Germinação / Muda
INSERT INTO public.plant_stage (id, plant_id, name, "order", days, description, image)
SELECT 
    gen_random_uuid(),
    p.id,
    'Germinação / Muda',
    1,
    7,
    'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.',
    NULL
FROM public.plant p;

-- 2. Crescimento Vegetativo
INSERT INTO public.plant_stage (id, plant_id, name, "order", days, description, image)
SELECT 
    gen_random_uuid(),
    p.id,
    'Crescimento Vegetativo',
    2,
    21,
    'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.',
    NULL
FROM public.plant p;

-- 3. Floração / Maturação
INSERT INTO public.plant_stage (id, plant_id, name, "order", days, description, image)
SELECT 
    gen_random_uuid(),
    p.id,
    'Floração / Maturação',
    3,
    35,
    'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.',
    NULL
FROM public.plant p;

-- 4. Colheita / Fase Adulta
INSERT INTO public.plant_stage (id, plant_id, name, "order", days, description, image)
SELECT 
    gen_random_uuid(),
    p.id,
    'Colheita / Fase Adulta',
    4,
    50,
    'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.',
    NULL
FROM public.plant p;
