-- ============================================================================
-- PlanPaz — Selective Plant Stages Seed (Only Stages with Cloudinary Photos)
-- Inserts 90 verified Cloudinary-backed stages (3 stages × 30 species)
-- with contiguous per-plant "order" (1, 2, 3) and zero NULL images.
-- ============================================================================

-- 1. Safe cleanup: if garden_plant rows already exist, detach or remap before delete
DELETE FROM public.plant_stage;

WITH raw_stages (plant_name, stage_name, raw_order, days, description, image) AS (
    VALUES
    -- ========================================================================
    -- ESTÁGIO 1: Germinação / Muda (30 espécies com foto no Cloudinary)
    -- ========================================================================
    ('Alface', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124697/planpaz/estagios/alface/1.png'),
    ('Almeirão Amarelo', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526135/planpaz/estagios/almeirao_amarelo/1.jpg'),
    ('Babosa', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125385/planpaz/estagios/babosa/1.png'),
    ('Beterraba', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526135/planpaz/estagios/beterraba/1.jpg'),
    ('Cacto Coroa de Frade', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125408/planpaz/estagios/cacto_coroa_de_frade/1.jpg'),
    ('Capim Limão', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/capim_limao/1.jpg'),
    ('Cebolinha', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/cebolinha/1.png'),
    ('Coentro', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125432/planpaz/estagios/coentro/1.png'),
    ('Colar de Pérolas', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125437/planpaz/estagios/colar_de_perolas/1.png'),
    ('Couve', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526137/planpaz/estagios/couve/1.png'),
    ('Erva Cidreira', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526137/planpaz/estagios/erva_cidreira/1.jpg'),
    ('Erva-doce', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/erva_doce/1.jpg'),
    ('Espada de São Jorge', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125446/planpaz/estagios/espada_de_sao_jorge/1.png'),
    ('Hortelã', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/hortela/1.jpg'),
    ('Jade', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125449/planpaz/estagios/jade/1.jpg'),
    ('Jibóia', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125452/planpaz/estagios/jiboia/1.jpg'),
    ('Manjericão', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/manjericao/1.jpg'),
    ('Mini Coroa de Cristo', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125456/planpaz/estagios/mini_coroa_de_cristo/1.jpg'),
    ('Morango', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/morango/1.jpg'),
    ('Ora-pro-nóbis', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/ora_pro_nobis/1.jpg'),
    ('Orquídea (Phalaenopsis)', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125458/planpaz/estagios/orquidea/1.jpg'),
    ('Orégano', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/oregano/1.jpg'),
    ('Pimenta Biquinho', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/pimenta_biquinho/1.jpg'),
    ('Rabanete', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/rabanete/1.jpg'),
    ('Rúcula', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/rucula/1.jpg'),
    ('Salsinha', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/salsinha/1.jpg'),
    ('Samambaia', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125461/planpaz/estagios/samambaia/1.png'),
    ('Tomate Cereja', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125466/planpaz/estagios/tomate_cereja/1.png'),
    ('Tomilho', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/tomilho/1.jpg'),
    ('Violeta', 'Germinação / Muda', 1, 7, 'Início do desenvolvimento, surgimento dos primeiros brotos e raízes iniciais.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124730/planpaz/estagios/violeta/1.jpg'),

    -- ========================================================================
    -- ESTÁGIO 2: Crescimento Vegetativo (30 espécies com foto no Cloudinary)
    -- ========================================================================
    ('Alface', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124698/planpaz/estagios/alface/2.png'),
    ('Almeirão Amarelo', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526135/planpaz/estagios/almeirao_amarelo/2.jpg'),
    ('Babosa', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125391/planpaz/estagios/babosa/2.png'),
    ('Beterraba', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526135/planpaz/estagios/beterraba/2.jpg'),
    ('Cacto Coroa de Frade', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125412/planpaz/estagios/cacto_coroa_de_frade/2.jpg'),
    ('Capim Limão', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/capim_limao/2.jpg'),
    ('Cebolinha', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/cebolinha/2.png'),
    ('Coentro', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125434/planpaz/estagios/coentro/2.png'),
    ('Colar de Pérolas', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125439/planpaz/estagios/colar_de_perolas/2.png'),
    ('Couve', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526137/planpaz/estagios/couve/2.png'),
    ('Erva Cidreira', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/erva_cidreira/2.jpg'),
    ('Erva-doce', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/erva_doce/2.jpg'),
    ('Espada de São Jorge', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125448/planpaz/estagios/espada_de_sao_jorge/2.png'),
    ('Hortelã', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/hortela/2.jpg'),
    ('Jade', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125450/planpaz/estagios/jade/2.jpg'),
    ('Jibóia', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125454/planpaz/estagios/jiboia/2.jpg'),
    ('Manjericão', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/manjericao/2.jpg'),
    ('Mini Coroa de Cristo', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125457/planpaz/estagios/mini_coroa_de_cristo/2.jpg'),
    ('Morango', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/morango/2.jpg'),
    ('Ora-pro-nóbis', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/ora_pro_nobis/2.jpg'),
    ('Orquídea (Phalaenopsis)', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125459/planpaz/estagios/orquidea/2.jpg'),
    ('Orégano', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/oregano/2.jpg'),
    ('Pimenta Biquinho', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/pimenta_biquinho/2.jpg'),
    ('Rabanete', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/rabanete/2.jpg'),
    ('Rúcula', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/rucula/2.jpg'),
    ('Salsinha', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/salsinha/2.jpg'),
    ('Samambaia', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125463/planpaz/estagios/samambaia/2.png'),
    ('Tomate Cereja', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124728/planpaz/estagios/tomate_cereja/2.png'),
    ('Tomilho', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/tomilho/2.jpg'),
    ('Violeta', 'Crescimento Vegetativo', 2, 21, 'Desenvolvimento acelerado de folhas, caules e estrutura principal da planta.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124731/planpaz/estagios/violeta/2.jpg'),

    -- ========================================================================
    -- ESTÁGIO 3 (GRUPO 1 — 18 Ornamentais/Aromáticas): Floração / Maturação
    -- ========================================================================
    ('Babosa', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125404/planpaz/estagios/babosa/3.png'),
    ('Cacto Coroa de Frade', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/cacto_coroa_de_frade/3.jpg'),
    ('Capim Limão', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/capim_limao/3.jpg'),
    ('Colar de Pérolas', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125440/planpaz/estagios/colar_de_perolas/3.png'),
    ('Erva Cidreira', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/erva_cidreira/3.jpg'),
    ('Erva-doce', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/erva_doce/3.jpg'),
    ('Espada de São Jorge', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526138/planpaz/estagios/espada_de_sao_jorge/3.jpg'),
    ('Hortelã', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/hortela/3.jpg'),
    ('Jade', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125450/planpaz/estagios/jade/3.jpg'),
    ('Jibóia', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125455/planpaz/estagios/jiboia/3.jpg'),
    ('Manjericão', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526139/planpaz/estagios/manjericao/3.jpg'),
    ('Mini Coroa de Cristo', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125457/planpaz/estagios/mini_coroa_de_cristo/3.jpg'),
    ('Orquídea (Phalaenopsis)', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/a_auto/v1790125460/planpaz/estagios/orquidea/3.jpg'),
    ('Orégano', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/oregano/3.jpg'),
    ('Salsinha', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/salsinha/3.jpg'),
    ('Samambaia', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125464/planpaz/estagios/samambaia/3.png'),
    ('Tomilho', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/tomilho/3.jpg'),
    ('Violeta', 'Floração / Maturação', 3, 35, 'Aparecimento de botões florais ou folhas maduras prontas para colheita/apreciação.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124731/planpaz/estagios/violeta/3.png'),

    -- ========================================================================
    -- ESTÁGIO 3 (GRUPO 2 — 12 Comestíveis/Horta): Colheita / Fase Adulta
    -- Re-ordered from 4 -> 3 so each plant has contiguous stages (1, 2, 3)
    -- ========================================================================
    ('Alface', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526135/planpaz/estagios/alface/4.png'),
    ('Almeirão Amarelo', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/almeirao_amarelo/4.png'),
    ('Beterraba', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526136/planpaz/estagios/beterraba/4.jpg'),
    ('Cebolinha', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526137/planpaz/estagios/cebolinha/4.png'),
    ('Coentro', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790125436/planpaz/estagios/coentro/4.png'),
    ('Couve', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526137/planpaz/estagios/couve/4.png'),
    ('Morango', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/morango/4.jpg'),
    ('Ora-pro-nóbis', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526140/planpaz/estagios/ora_pro_nobis/4.jpg'),
    ('Pimenta Biquinho', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/pimenta_biquinho/4.jpg'),
    ('Rabanete', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526141/planpaz/estagios/rabanete/4.jpg'),
    ('Rúcula', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1792526142/planpaz/estagios/rucula/4.jpg'),
    ('Tomate Cereja', 'Colheita / Fase Adulta', 3, 50, 'Planta em estágio pleno de desenvolvimento, pronta para colheita ou manutenção adulta contínua.', 'https://res.cloudinary.com/hvrq4r5h/image/upload/v1790124729/planpaz/estagios/tomate_cereja/4.png')
)
INSERT INTO public.plant_stage (id, plant_id, name, "order", days, description, image)
SELECT
    gen_random_uuid(),
    p.id,
    rs.stage_name,
    ROW_NUMBER() OVER (PARTITION BY p.id ORDER BY rs.raw_order ASC)::INTEGER,
    rs.days,
    rs.description,
    rs.image
FROM raw_stages rs
JOIN public.plant p ON p.name = rs.plant_name;
