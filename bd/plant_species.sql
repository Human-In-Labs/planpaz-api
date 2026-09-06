-- Limpeza das tabelas
DELETE FROM public.garden_plant;
DELETE FROM public.plant_stage;
DELETE FROM public.plant;

-- Inserção do catálogo completo de plantas
INSERT INTO public.plant (
    id, name, scientific_name, description, watering_level, luminosity_level, temperature_level, size, type, image
) VALUES 
(gen_random_uuid(), 'Jibóia', 'Epipremnum aureum', 'Planta ornamental de fácil cultivo e folhagem marcante.', 'WEEKLY', 'MEDIUM', 'MEDIUM', 'MEDIUM', 'OTHER', NULL),
(gen_random_uuid(), 'Manjericão', 'Ocimum basilicum', 'Erva aromática muito utilizada na culinária.', 'DAILY', 'INTENSE', 'HIGH', 'SMALL', 'AROMATIC', NULL),
(gen_random_uuid(), 'Orégano', 'Origanum vulgare', 'Erva aromática resistente e tempero clássico.', 'SPORADIC', 'INTENSE', 'MEDIUM', 'SMALL', 'AROMATIC', NULL),
(gen_random_uuid(), 'Orquídea (Phalaenopsis)', 'Phalaenopsis spp.', 'Planta ornamental delicada com belas flores.', 'WEEKLY', 'MEDIUM', 'MEDIUM', 'MEDIUM', 'OTHER', NULL),
(gen_random_uuid(), 'Mini Coroa de Cristo', 'Euphorbia milii var. splendens', 'Planta ornamental espinhosa e bastante resistente.', 'SPORADIC', 'INTENSE', 'HIGH', 'SMALL', 'OTHER', NULL),
(gen_random_uuid(), 'Cacto Coroa de Frade', 'Melocactus zehntneri', 'Cacto ornamental nativo de regiões quentes e secas.', 'SPORADIC', 'INTENSE', 'HIGH', 'SMALL', 'OTHER', NULL),
(gen_random_uuid(), 'Alface', 'Lactuca sativa', 'Hortaliça comestível de crescimento rápido.', 'DAILY', 'INTENSE', 'LOW', 'SMALL', 'EDIBLE', NULL),
(gen_random_uuid(), 'Hortelã', 'Mentha spicata', 'Erva aromática excelente para chás e temperos.', 'DAILY', 'INTENSE', 'MEDIUM', 'SMALL', 'AROMATIC', NULL),
(gen_random_uuid(), 'Cebolinha', 'Allium fistulosum', 'Temperos e folhas comestíveis muito populares.', 'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', NULL),
(gen_random_uuid(), 'Tomilho', 'Thymus vulgaris', 'Erva aromática de porte baixo e sabor marcante.', 'SPORADIC', 'INTENSE', 'MEDIUM', 'SMALL', 'AROMATIC', NULL),
(gen_random_uuid(), 'Couve', 'Brassica oleracea var. acephala', 'Hortaliça nutritiva de folhas comestíveis.', 'DAILY', 'INTENSE', 'MEDIUM', 'MEDIUM', 'EDIBLE', NULL),
(gen_random_uuid(), 'Coentro', 'Coriandrum sativum', 'Erva e tempero de sabor característico.', 'DAILY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', NULL),
(gen_random_uuid(), 'Jade', 'Crassula ovata', 'Suculenta ornamental de folhas resistentes.', 'SPORADIC', 'INTENSE', 'HIGH', 'MEDIUM', 'OTHER', NULL),
(gen_random_uuid(), 'Violeta', 'Saintpaulia ionantha', 'Planta ornamental delicada de vasos internos.', 'WEEKLY', 'MEDIUM', 'MEDIUM', 'SMALL', 'OTHER', NULL),
(gen_random_uuid(), 'Colar de Pérolas', 'Senecio rowleyanus', 'Suculenta pendente muito apreciada.', 'SPORADIC', 'MEDIUM', 'MEDIUM', 'MEDIUM', 'OTHER', NULL),
(gen_random_uuid(), 'Samambaia', 'Nephrolepis exaltata', 'Planta ornamental de folhagem volumosa.', 'DAILY', 'MEDIUM', 'MEDIUM', 'LARGE', 'OTHER', NULL),
(gen_random_uuid(), 'Espada de São Jorge', 'Dracaena trifasciata', 'Planta ornamental extremamente resistente.', 'SPORADIC', 'MEDIUM', 'MEDIUM', 'MEDIUM', 'OTHER', NULL),
(gen_random_uuid(), 'Tomate Cereja', 'Solanum lycopersicum var. cerasiforme', 'Planta frutífera comestível de porte médio.', 'DAILY', 'INTENSE', 'HIGH', 'MEDIUM', 'EDIBLE', NULL),
(gen_random_uuid(), 'Rabanete', 'Raphanus sativus', 'Raiz comestível de ciclo muito rápido.', 'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', NULL),
(gen_random_uuid(), 'Salsinha', 'Petroselinum crispum', 'Erva aromática indispensável na cozinha.', 'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'AROMATIC', NULL),
(gen_random_uuid(), 'Erva Cidreira', 'Melissa officinalis', 'Erva calmante usada principalmente para chás.', 'WEEKLY', 'MEDIUM', 'MEDIUM', 'MEDIUM', 'AROMATIC', NULL),
(gen_random_uuid(), 'Ora-pro-nóbis', 'Pereskia aculeata', 'Planta alimentícia não convencional (PANC) muito nutritiva.', 'WEEKLY', 'INTENSE', 'HIGH', 'LARGE', 'EDIBLE', NULL),
(gen_random_uuid(), 'Pimenta Biquinho', 'Capsicum chinense', 'Pimenta comestível saborosa e sem ardência.', 'DAILY', 'INTENSE', 'HIGH', 'MEDIUM', 'EDIBLE', NULL),
(gen_random_uuid(), 'Babosa', 'Aloe vera', 'Planta ornamental e medicinal muito conhecida.', 'SPORADIC', 'INTENSE', 'HIGH', 'MEDIUM', 'OTHER', NULL),
(gen_random_uuid(), 'Morango', 'Fragaria x ananassa', 'Planta frutífera comestível pequena.', 'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', NULL),
(gen_random_uuid(), 'Erva-doce', 'Foeniculum vulgare', 'Erva aromática com aroma e sabor de anís.', 'WEEKLY', 'INTENSE', 'MEDIUM', 'MEDIUM', 'AROMATIC', NULL),
(gen_random_uuid(), 'Alecrim', 'Salvia rosmarinus', 'Erva aromática muito resistente e perfumada.', 'SPORADIC', 'INTENSE', 'HIGH', 'MEDIUM', 'AROMATIC', NULL),
(gen_random_uuid(), 'Camomila', 'Matricaria chamomilla', 'Erva medicinal usada em infusões.', 'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'AROMATIC', NULL),
(gen_random_uuid(), 'Rúcula', 'Eruca vesicaria', 'Hortaliça comestível de sabor levemente picante.', 'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', NULL),
(gen_random_uuid(), 'Almeirão Amarelo', 'Cichorium intybus', 'Hortaliça comestível de folhas levemente amargas.', 'DAILY', 'INTENSE', 'MEDIUM', 'MEDIUM', 'EDIBLE', NULL);