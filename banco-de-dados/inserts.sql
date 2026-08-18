-- Limpeza das tabelas
DELETE FROM public.garden_plant;
DELETE FROM public.plant;

-- Inserção do catálogo completo
INSERT INTO public.plant (
    id, description, image, luminosity_level, name, scientific_name, size, temperature_level, type, watering_level, experience_level
) VALUES 
(gen_random_uuid(), 'Planta ornamental de fácil cultivo e folhagem marcante.', NULL, 'MEDIUM', 'Jibóia', 'Epipremnum aureum', 'MEDIUM', 'MEDIUM', 'OTHER', 'WEEKLY', 'BEGINNER'),
(gen_random_uuid(), 'Erva aromática muito utilizada na culinária.', NULL, 'INTENSE', 'Manjericão', 'Ocimum basilicum', 'SMALL', 'HIGH', 'AROMATIC', 'DAILY', 'INTERMEDIATE'),
(gen_random_uuid(), 'Erva aromática resistente e tempero clássico.', NULL, 'INTENSE', 'Orégano', 'Origanum vulgare', 'SMALL', 'MEDIUM', 'AROMATIC', 'SPORADIC', 'BEGINNER'),
(gen_random_uuid(), 'Planta ornamental delicada com belas flores.', NULL, 'MEDIUM', 'Orquídea (Phalaenopsis)', 'Phalaenopsis spp.', 'MEDIUM', 'MEDIUM', 'OTHER', 'WEEKLY', 'ADVANCED'),
(gen_random_uuid(), 'Planta ornamental espinhosa e bastante resistente.', NULL, 'INTENSE', 'Mini Coroa de Cristo', 'Euphorbia milii var. splendens', 'SMALL', 'HIGH', 'OTHER', 'SPORADIC', 'BEGINNER'),
(gen_random_uuid(), 'Cacto ornamental nativo de regiões quentes e secas.', NULL, 'INTENSE', 'Cacto Coroa de Frade', 'Melocactus zehntneri', 'SMALL', 'HIGH', 'OTHER', 'SPORADIC', 'BEGINNER'),
(gen_random_uuid(), 'Hortaliça comestível de crescimento rápido.', NULL, 'INTENSE', 'Alface', 'Lactuca sativa', 'SMALL', 'LOW', 'EDIBLE', 'DAILY', 'BEGINNER'),
(gen_random_uuid(), 'Erva aromática excelente para chás e temperos.', NULL, 'INTENSE', 'Hortelã', 'Mentha spicata', 'SMALL', 'MEDIUM', 'AROMATIC', 'DAILY', 'BEGINNER'),
(gen_random_uuid(), 'Temperos e folhas comestíveis muito populares.', NULL, 'INTENSE', 'Cebolinha', 'Allium fistulosum', 'SMALL', 'MEDIUM', 'EDIBLE', 'WEEKLY', 'BEGINNER'),
(gen_random_uuid(), 'Erva aromática de porte baixo e sabor marcante.', NULL, 'INTENSE', 'Tomilho', 'Thymus vulgaris', 'SMALL', 'MEDIUM', 'AROMATIC', 'SPORADIC', 'BEGINNER'),
(gen_random_uuid(), 'Hortaliça nutritiva de folhas comestíveis.', NULL, 'INTENSE', 'Couve', 'Brassica oleracea var. acephala', 'MEDIUM', 'MEDIUM', 'EDIBLE', 'DAILY', 'INTERMEDIATE'),
(gen_random_uuid(), 'Erva e tempero de sabor característico.', NULL, 'INTENSE', 'Coentro', 'Coriandrum sativum', 'SMALL', 'MEDIUM', 'EDIBLE', 'DAILY', 'INTERMEDIATE'),
(gen_random_uuid(), 'Suculenta ornamental de folhas resistentes.', NULL, 'INTENSE', 'Jade', 'Crassula ovata', 'MEDIUM', 'HIGH', 'OTHER', 'SPORADIC', 'BEGINNER'),
(gen_random_uuid(), 'Planta ornamental delicada de vasos internos.', NULL, 'MEDIUM', 'Violeta', 'Saintpaulia ionantha', 'SMALL', 'MEDIUM', 'OTHER', 'WEEKLY', 'ADVANCED'),
(gen_random_uuid(), 'Suculenta pendente muito apreciada.', NULL, 'MEDIUM', 'Colar de Pérolas', 'Senecio rowleyanus', 'MEDIUM', 'MEDIUM', 'OTHER', 'SPORADIC', 'ADVANCED'),
(gen_random_uuid(), 'Planta ornamental de folhagem volumosa.', NULL, 'MEDIUM', 'Samambaia', 'Nephrolepis exaltata', 'LARGE', 'MEDIUM', 'OTHER', 'DAILY', 'INTERMEDIATE'),
(gen_random_uuid(), 'Planta ornamental extremamente resistente.', NULL, 'MEDIUM', 'Espada de São Jorge', 'Dracaena trifasciata', 'MEDIUM', 'MEDIUM', 'OTHER', 'SPORADIC', 'BEGINNER'),
(gen_random_uuid(), 'Planta frutífera comestível de porte médio.', NULL, 'INTENSE', 'Tomate Cereja', 'Solanum lycopersicum var. cerasiforme', 'MEDIUM', 'HIGH', 'EDIBLE', 'DAILY', 'INTERMEDIATE'),
(gen_random_uuid(), 'Raiz comestível de ciclo muito rápido.', NULL, 'INTENSE', 'Rabanete', 'Raphanus sativus', 'SMALL', 'MEDIUM', 'EDIBLE', 'WEEKLY', 'BEGINNER'),
(gen_random_uuid(), 'Erva aromática indispensável na cozinha.', NULL, 'INTENSE', 'Salsinha', 'Petroselinum crispum', 'SMALL', 'MEDIUM', 'AROMATIC', 'WEEKLY', 'BEGINNER'),
(gen_random_uuid(), 'Erva calmante usada principalmente para chás.', NULL, 'MEDIUM', 'Erva Cidreira', 'Melissa officinalis', 'MEDIUM', 'MEDIUM', 'AROMATIC', 'WEEKLY', 'BEGINNER'),
(gen_random_uuid(), 'Planta alimentícia não convencional (PANC) muito nutritiva.', NULL, 'INTENSE', 'Ora-pro-nóbis', 'Pereskia aculeata', 'LARGE', 'HIGH', 'EDIBLE', 'WEEKLY', 'BEGINNER'),
(gen_random_uuid(), 'Pimenta comestível saborosa e sem ardência.', NULL, 'INTENSE', 'Pimenta Biquinho', 'Capsicum chinense', 'MEDIUM', 'HIGH', 'EDIBLE', 'DAILY', 'INTERMEDIATE'),
(gen_random_uuid(), 'Planta ornamental e medicinal muito conhecida.', NULL, 'INTENSE', 'Babosa', 'Aloe vera', 'MEDIUM', 'HIGH', 'OTHER', 'SPORADIC', 'BEGINNER'),
(gen_random_uuid(), 'Planta frutífera comestível pequena.', NULL, 'INTENSE', 'Morango', 'Fragaria x ananassa', 'SMALL', 'MEDIUM', 'EDIBLE', 'WEEKLY', 'INTERMEDIATE'),
(gen_random_uuid(), 'Erva aromática com aroma e sabor de anís.', NULL, 'INTENSE', 'Erva-doce', 'Foeniculum vulgare', 'MEDIUM', 'MEDIUM', 'AROMATIC', 'WEEKLY', 'INTERMEDIATE'),
(gen_random_uuid(), 'Erva aromática muito resistente e perfumada.', NULL, 'INTENSE', 'Alecrim', 'Salvia rosmarinus', 'MEDIUM', 'HIGH', 'AROMATIC', 'SPORADIC', 'BEGINNER'),
(gen_random_uuid(), 'Erva medicinal usada em infusões.', NULL, 'INTENSE', 'Camomila', 'Matricaria chamomilla', 'SMALL', 'MEDIUM', 'AROMATIC', 'WEEKLY', 'BEGINNER'),
(gen_random_uuid(), 'Hortaliça comestível de sabor levemente picante.', NULL, 'INTENSE', 'Rúcula', 'Eruca vesicaria', 'SMALL', 'MEDIUM', 'EDIBLE', 'WEEKLY', 'BEGINNER'),
(gen_random_uuid(), 'Hortaliça comestível de folhas levemente amargas.', NULL, 'INTENSE', 'Almeirão Amarelo', 'Cichorium intybus', 'MEDIUM', 'MEDIUM', 'EDIBLE', 'DAILY', 'BEGINNER');