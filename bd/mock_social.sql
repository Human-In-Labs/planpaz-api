-- ==========================================================
-- SCRIPT DE INSERTS MOCKADOS: 3 USUÁRIOS E 10 POSTS SOCIAIS
-- ==========================================================
-- Senha padrão para todos os usuários mockados: 123456
-- (Hash BCrypt gerado para '123456': $2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.)

-- 1. INSERTS DE USUÁRIOS
INSERT INTO public.users (
    id, name, username, email, password, bio, email_verified, ecoscore, created_at
) VALUES 
(
    'a1111111-1111-1111-1111-111111111111',
    'Lucas Silveira',
    'lucas_jardim',
    'lucas@planpaz.com',
    '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.',
    'Cultivando plantas e buscando harmonia no dia a dia. Focado em hortas urbanas e suculentas.',
    TRUE,
    140,
    NOW() - INTERVAL '15 days'
),
(
    'a2222222-2222-2222-2222-222222222222',
    'Beatriz Rocha',
    'bia_botanica',
    'beatriz@planpaz.com',
    '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.',
    'Urban jungle em apartamento pequeno! Compartilhando o progresso das orquídeas e folhagens.',
    TRUE,
    185,
    NOW() - INTERVAL '10 days'
),
(
    'a3333333-3333-3333-3333-333333333333',
    'Mariana Costa',
    'mari_organica',
    'mariana@planpaz.com',
    '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.',
    'Plantando hoje para colher bem-estar amanhã. Ervas aromáticas, chás e alimentação consciente.',
    TRUE,
    110,
    NOW() - INTERVAL '7 days'
)
ON CONFLICT (id) DO NOTHING;

-- 2. INSERTS DE 10 POSTS COM FOTOS E HASHTAGS
INSERT INTO public.post (
    id, author_id, title, content, media, tags, posted_at
) VALUES 
(
    'b1111111-0001-0000-0000-000000000001',
    'a2222222-2222-2222-2222-222222222222',
    'Minha Jibóia cresceu demais!',
    'Olhem que espetáculo como as folhas da Jibóia se adaptaram bem à luz indireta da sala. A rega semanal tem sido ideal.',
    'https://images.unsplash.com/photo-1545241047-6083a3684587?auto=format&fit=crop&w=800&q=80',
    '#jiboia,#urbanjungle,#plantas',
    NOW() - INTERVAL '2 hours'
),
(
    'b1111111-0002-0000-0000-000000000002',
    'a1111111-1111-1111-1111-111111111111',
    'Primeira floração das orquídeas',
    'Depois de meses ajustando a rega e a luminosidade, as Phalaenopsis finalmente abriram flores delicadas!',
    'https://images.unsplash.com/photo-1525310072745-f49212b5ac6d?auto=format&fit=crop&w=800&q=80',
    '#orquideas,#flores,#jardim',
    NOW() - INTERVAL '5 hours'
),
(
    'b1111111-0003-0000-0000-000000000003',
    'a3333333-3333-3333-3333-333333333333',
    'Colheita de manjericão fresco',
    'O aroma do manjericão recém-colhido perfumou a casa toda. Vai direto para o molho de pesto do almoço.',
    'https://images.unsplash.com/photo-1618375569909-3c8616cf7733?auto=format&fit=crop&w=800&q=80',
    '#manjericao,#horta,#ervas',
    NOW() - INTERVAL '12 hours'
),
(
    'b1111111-0004-0000-0000-000000000004',
    'a1111111-1111-1111-1111-111111111111',
    'Coleção de suculentas tomando sol',
    'Nada como a luz da manhã para fortalecer as suculentas. Cuidado essencial: solo bem drenado para não apodrecer as raízes.',
    'https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?auto=format&fit=crop&w=800&q=80',
    '#suculentas,#cactos,#sol',
    NOW() - INTERVAL '1 day'
),
(
    'b1111111-0005-0000-0000-000000000005',
    'a2222222-2222-2222-2222-222222222222',
    'Samambaia renovada no cantinho da paz',
    'Borrifei água nas folhas dela pela manhã. Para quem mora em clima seco, umidificar as folhas faz toda a diferença.',
    'https://images.unsplash.com/photo-1512428813834-c702c7702b78?auto=format&fit=crop&w=800&q=80',
    '#samambaia,#verdes,#autocuidado',
    NOW() - INTERVAL '2 days'
),
(
    'b1111111-0006-0000-0000-000000000006',
    'a3333333-3333-3333-3333-333333333333',
    'Tomatinhos cereja amadurecendo',
    'Cultivar em vaso é muito possível! Vejam os primeiros tomatinhos cereja ganhando cor na jardineira da sacada.',
    'https://images.unsplash.com/photo-1592841200221-a6898f307baa?auto=format&fit=crop&w=800&q=80',
    '#tomatecereja,#hortaemcasa,#colheita',
    NOW() - INTERVAL '3 days'
),
(
    'b1111111-0007-0000-0000-000000000007',
    'a1111111-1111-1111-1111-111111111111',
    'Cantinho verde revitalizado',
    'Organizei os vasos por nível de luminosidade recomendada no PlanPaz. A estética do ambiente mudou completamente!',
    'https://images.unsplash.com/photo-1463936575829-25148e1db1b8?auto=format&fit=crop&w=800&q=80',
    '#decoracao,#espacoverde,#planpaz',
    NOW() - INTERVAL '4 days'
),
(
    'b1111111-0008-0000-0000-000000000008',
    'a3333333-3333-3333-3333-333333333333',
    'Alecrim: o rei da resistência',
    'O alecrim não exige muita água e tolera sol pleno muito bem. Uma ótima opção para quem está começando na jardinagem.',
    'https://images.unsplash.com/photo-1515542622106-78bda8ba0e5b?auto=format&fit=crop&w=800&q=80',
    '#alecrim,#aromaticas,#jardinagem',
    NOW() - INTERVAL '5 days'
),
(
    'b1111111-0009-0000-0000-000000000009',
    'a2222222-2222-2222-2222-222222222222',
    'Espada de São Jorge purificando o ar',
    'Além de quase indestrutível, ela é perfeita para purificar o ar do quarto e trazer calma para o ambiente de descanso.',
    'https://images.unsplash.com/photo-1509423350716-97f9360b4e09?auto=format&fit=crop&w=800&q=80',
    '#espadadesaojorge,#ar,#pazinterior',
    NOW() - INTERVAL '6 days'
),
(
    'b1111111-0010-0000-0000-000000000010',
    'a3333333-3333-3333-3333-333333333333',
    'Morangueiro dando frutos!',
    'Primeiro morango da horta caseira. Acompanhar cada estágio da planta traz uma sensação indescritível de paz.',
    'https://images.unsplash.com/photo-1464965911861-746a04b4bca6?auto=format&fit=crop&w=800&q=80',
    '#morango,#hortaurbana,#bemestar',
    NOW() - INTERVAL '7 days'
)
ON CONFLICT (id) DO NOTHING;
