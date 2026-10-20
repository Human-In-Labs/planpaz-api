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
    CASE p.name
        WHEN 'Alface' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124697/planpaz/estagios/alface/1.png'
        WHEN 'Almeirão Amarelo' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526135/planpaz/estagios/almeirao_amarelo/1.jpg'
        WHEN 'Babosa' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125385/planpaz/estagios/babosa/1.png'
        WHEN 'Beterraba' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526135/planpaz/estagios/beterraba/1.jpg'
        WHEN 'Cacto Coroa de Frade' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125408/planpaz/estagios/cacto_coroa_de_frade/1.jpg'
        WHEN 'Capim Limão' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/capim_limao/1.jpg'
        WHEN 'Cebolinha' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/cebolinha/1.png'
        WHEN 'Coentro' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125432/planpaz/estagios/coentro/1.png'
        WHEN 'Colar de Pérolas' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125437/planpaz/estagios/colar_de_perolas/1.png'
        WHEN 'Couve' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526137/planpaz/estagios/couve/1.png'
        WHEN 'Erva Cidreira' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526137/planpaz/estagios/erva_cidreira/1.jpg'
        WHEN 'Erva-doce' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/erva_doce/1.jpg'
        WHEN 'Espada de São Jorge' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125446/planpaz/estagios/espada_de_sao_jorge/1.png'
        WHEN 'Hortelã' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/hortela/1.jpg'
        WHEN 'Jade' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125449/planpaz/estagios/jade/1.jpg'
        WHEN 'Jibóia' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125452/planpaz/estagios/jiboia/1.jpg'
        WHEN 'Manjericão' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/manjericao/1.jpg'
        WHEN 'Mini Coroa de Cristo' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125456/planpaz/estagios/mini_coroa_de_cristo/1.jpg'
        WHEN 'Morango' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/morango/1.jpg'
        WHEN 'Ora-pro-nóbis' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/ora_pro_nobis/1.jpg'
        WHEN 'Orquídea (Phalaenopsis)' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125458/planpaz/estagios/orquidea/1.jpg'
        WHEN 'Orégano' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/oregano/1.jpg'
        WHEN 'Pimenta Biquinho' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/pimenta_biquinho/1.jpg'
        WHEN 'Rabanete' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/rabanete/1.jpg'
        WHEN 'Rúcula' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/rucula/1.jpg'
        WHEN 'Salsinha' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/salsinha/1.jpg'
        WHEN 'Samambaia' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125461/planpaz/estagios/samambaia/1.png'
        WHEN 'Tomate Cereja' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125466/planpaz/estagios/tomate_cereja/1.png'
        WHEN 'Tomilho' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/tomilho/1.jpg'
        WHEN 'Violeta' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124730/planpaz/estagios/violeta/1.jpg'
        ELSE NULL
    END
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
    CASE p.name
        WHEN 'Alface' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124698/planpaz/estagios/alface/2.png'
        WHEN 'Almeirão Amarelo' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526135/planpaz/estagios/almeirao_amarelo/2.jpg'
        WHEN 'Babosa' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125391/planpaz/estagios/babosa/2.png'
        WHEN 'Beterraba' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526135/planpaz/estagios/beterraba/2.jpg'
        WHEN 'Cacto Coroa de Frade' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125412/planpaz/estagios/cacto_coroa_de_frade/2.jpg'
        WHEN 'Capim Limão' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/capim_limao/2.jpg'
        WHEN 'Cebolinha' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/cebolinha/2.png'
        WHEN 'Coentro' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125434/planpaz/estagios/coentro/2.png'
        WHEN 'Colar de Pérolas' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125439/planpaz/estagios/colar_de_perolas/2.png'
        WHEN 'Couve' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526137/planpaz/estagios/couve/2.png'
        WHEN 'Erva Cidreira' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/erva_cidreira/2.jpg'
        WHEN 'Erva-doce' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/erva_doce/2.jpg'
        WHEN 'Espada de São Jorge' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125448/planpaz/estagios/espada_de_sao_jorge/2.png'
        WHEN 'Hortelã' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/hortela/2.jpg'
        WHEN 'Jade' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125450/planpaz/estagios/jade/2.jpg'
        WHEN 'Jibóia' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125454/planpaz/estagios/jiboia/2.jpg'
        WHEN 'Manjericão' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/manjericao/2.jpg'
        WHEN 'Mini Coroa de Cristo' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125457/planpaz/estagios/mini_coroa_de_cristo/2.jpg'
        WHEN 'Morango' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/morango/2.jpg'
        WHEN 'Ora-pro-nóbis' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/ora_pro_nobis/2.jpg'
        WHEN 'Orquídea (Phalaenopsis)' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125459/planpaz/estagios/orquidea/2.jpg'
        WHEN 'Orégano' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/oregano/2.jpg'
        WHEN 'Pimenta Biquinho' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/pimenta_biquinho/2.jpg'
        WHEN 'Rabanete' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/rabanete/2.jpg'
        WHEN 'Rúcula' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/rucula/2.jpg'
        WHEN 'Salsinha' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/salsinha/2.jpg'
        WHEN 'Samambaia' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125463/planpaz/estagios/samambaia/2.png'
        WHEN 'Tomate Cereja' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124728/planpaz/estagios/tomate_cereja/2.png'
        WHEN 'Tomilho' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/tomilho/2.jpg'
        WHEN 'Violeta' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124731/planpaz/estagios/violeta/2.jpg'
        ELSE NULL
    END
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
    CASE p.name
        WHEN 'Babosa' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125404/planpaz/estagios/babosa/3.png'
        WHEN 'Cacto Coroa de Frade' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/cacto_coroa_de_frade/3.jpg'
        WHEN 'Capim Limão' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/capim_limao/3.jpg'
        WHEN 'Colar de Pérolas' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125440/planpaz/estagios/colar_de_perolas/3.png'
        WHEN 'Erva Cidreira' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/erva_cidreira/3.jpg'
        WHEN 'Erva-doce' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/erva_doce/3.jpg'
        WHEN 'Espada de São Jorge' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/espada_de_sao_jorge/3.jpg'
        WHEN 'Hortelã' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/hortela/3.jpg'
        WHEN 'Jade' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125450/planpaz/estagios/jade/3.jpg'
        WHEN 'Jibóia' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125455/planpaz/estagios/jiboia/3.jpg'
        WHEN 'Manjericão' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/manjericao/3.jpg'
        WHEN 'Mini Coroa de Cristo' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125457/planpaz/estagios/mini_coroa_de_cristo/3.jpg'
        WHEN 'Orquídea (Phalaenopsis)' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/a_auto/v1790125460/planpaz/estagios/orquidea/3.jpg'
        WHEN 'Orégano' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/oregano/3.jpg'
        WHEN 'Salsinha' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/salsinha/3.jpg'
        WHEN 'Samambaia' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125464/planpaz/estagios/samambaia/3.png'
        WHEN 'Tomilho' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/tomilho/3.jpg'
        WHEN 'Violeta' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124731/planpaz/estagios/violeta/3.png'
        ELSE NULL
    END
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
    CASE p.name
        WHEN 'Alface' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526135/planpaz/estagios/alface/4.png'
        WHEN 'Almeirão Amarelo' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/almeirao_amarelo/4.png'
        WHEN 'Beterraba' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/beterraba/4.jpg'
        WHEN 'Cebolinha' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526137/planpaz/estagios/cebolinha/4.png'
        WHEN 'Coentro' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125436/planpaz/estagios/coentro/4.png'
        WHEN 'Couve' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526137/planpaz/estagios/couve/4.png'
        WHEN 'Morango' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/morango/4.jpg'
        WHEN 'Ora-pro-nóbis' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/ora_pro_nobis/4.jpg'
        WHEN 'Pimenta Biquinho' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/pimenta_biquinho/4.jpg'
        WHEN 'Rabanete' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/rabanete/4.jpg'
        WHEN 'Rúcula' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/rucula/4.jpg'
        WHEN 'Tomate Cereja' THEN 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124729/planpaz/estagios/tomate_cereja/4.png'
        ELSE NULL
    END
FROM public.plant p;
