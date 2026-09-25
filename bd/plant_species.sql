-- Limpeza das tabelas
DELETE FROM public.garden_plant;
DELETE FROM public.plant_stage;
DELETE FROM public.plant;

-- Inserção do catálogo de plantas com os textos originais completos
INSERT INTO public.plant (
    id, name, scientific_name, description, care_guide, 
    watering_level, luminosity_level, temperature_level, size, type, image
) VALUES 
(
    gen_random_uuid(), 
    'Jibóia', 
    'Epipremnum aureum', 
    'A jibóia (Epipremnum aureum) é uma planta herbácea com comportamento pendente ou ascendente, possui folhagem extremamente ornamental. É uma planta muito conhecida e cultivada em ambientes internos, perfeita para quem está começando a cuidar de plantas por crescer bem com pouca luz e não demandar muitos cuidados. Ela pertence à família Araceae e é uma espécie de vida perene originária da Oceania. Na natureza, cresce apoiando-se no tronco de grandes árvores, mas em recipientes como vasos e floreiras, ela se desenvolve como pendente.', 
    'Cuidados Gerais: A jibóia é uma planta fácil de cuidar. Mantenha-a em um local com luz indireta e regue apenas quando a camada superficial do solo estiver seca. Evite o excesso de água para não apodrecer as raízes.
Rega: Regar de 1 a 2 vezes por semana. No verão, pode ser necessário regar com mais frequência, enquanto no inverno a rega deve ser reduzida. O solo deve estar úmido, mas não encharcado.
Iluminação: Luz indireta abundante. Evite exposição direta ao sol forte por mais de 1 hora por dia para não queimar as folhas. Ela também tolera locais com menor luminosidade.
Temperatura e Umidade: A temperatura ideal é entre 18°C e 26°C. A jibóia aprecia alta umidade, por isso borrifar água nas folhas nos dias mais secos pode ser benéfico.
Substrato: Solo leve, fértil, bem drenado e rico em matéria orgânica. Uma mistura de terra vegetal com fibra de coco e perlita é ideal.
Adubação: Adubar a cada 2 ou 3 meses durante a primavera e o verão com um adubo bem equilibrado para folhagens (ex: NPK 10-10-10).
Poda: Realize a poda na primavera ou verão para conter o crescimento e manter o formato desejado. Corte o ramo logo acima de um nó para estimular novas ramificações e remova folhas amareladas ou secas.', 
    'WEEKLY', 'MEDIUM', 'MEDIUM', 'MEDIUM', 'ORNAMENTAL', 
    'https://cdn.awsli.com.br/800x800/1539/1539472/produto/213557807/epipremnum-aureum-golden-pothos-6-n0nexjqvn6.jpg'
),
(
    gen_random_uuid(), 
    'Manjericão Verde', 
    'Ocimum basilicum', 
    'O manjericão (Ocimum basilicum) é uma planta aromática e medicinal clássica, famosa por seu aroma inconfundível e sabor marcante. Pertencente à família Lamiaceae, apresenta caule ereto e ramificado, com folhas ovais, macias e verde-brilhantes. É indispensável na culinária internacional e em hortas domésticas, adaptando-se perfeitamente a vasos, jardineiras ou canteiros.', 
    'Cuidados Gerais: O manjericão precisa de bastante sol direto para desenvolver seu aroma e sabor característicos. Requer solo úmido, mas bem drenado, e proteção contra ventos fortes e geadas.
Rega: Regar diariamente em épocas quentes ou sempre que a camada superficial do solo estiver seca. Evite encharcar o solo para prevenir o surgimento de fungos nas raízes.
Iluminação: Sol pleno. Necessita de pelo menos 4 a 6 horas diárias de luz solar direta para um bom desenvolvimento.
Temperatura e Umidade: Prefere temperaturas entre 20°C e 30°C. É sensível ao frio intenso e a geadas.
Substrato: Solo leve, rico em matéria orgânica, fértil e com excelente drenagem.
Adubação: Adubar mensalmente com adubo orgânico, como húngus de minhoca ou esterco curtido, para incentivar o crescimento de novas folhas.
Poda: Realize a poda de beliscamento (podar as pontas dos ramos) com frequência para estimular a ramificação e o surgimento de mais folhas. Remova as flores assim que surgirem, pois a floração altera o sabor das folhas e reduz a vida útil da planta.', 
    'DAILY', 'INTENSE', 'HIGH', 'SMALL', 'AROMATIC', 
    'https://cdn.awsli.com.br/600x700/416/416989/produto/129418172/manjeric-o-knz7c4z77f.jpg'
),
(
    gen_random_uuid(), 
    'Orégano', 
    'Origanum vulgare', 
    'O orégano (Origanum vulgare) é uma erva aromática perene nativa do Mediterrâneo, pertencente à família Lamiaceae. É mundialmente reconhecida pelo uso culinário, especialmente na cozinha italiana, além de possuir propriedades medicinais, antioxidantes e antimicrobianas. Apresenta porte rasteiro a semi-ereto, com pequenas folhas ovais altamente perfumadas.', 
    'Cuidados Gerais: Planta extremamente rústica e fácil de cultivar. Prefere locais ensolarados e solos bem drenados. O excesso de água é o principal fator de perda da planta.
Rega: Regar de forma moderada e espaçada. Espere o solo secar quase por completo entre as regas. Tolerante a curtos períodos de seca.
Iluminação: Sol pleno. Requer pelo menos 6 horas de sol direto por dia para concentrar seus óleos essenciais e aroma.
Temperatura e Umidade: A temperatura ideal situa-se entre 15°C e 30°C. Tolera bem o calor e suporta frios moderados.
Substrato: Solo levemente arenoso ou argiloso, mas obrigatoriamente bem drenado e de fertilidade média.
Adubação: Adubar a cada 3 meses com matéria orgânica (como composto ou húmus de minhoca). Adubação excessiva pode diminuir a intensidade do aroma.
Poda: Faça podas leves após a floração para renovar a folhagem e manter a planta compacta. Colha os ramos aparando-os acima dos nós.', 
    'SPORADIC', 'INTENSE', 'MEDIUM', 'SMALL', 'AROMATIC', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQfvNljI7o1Px_urceR7OUWDKu7qkVOUqlAN3o9dm_Gpw&s=10'
),
(
    gen_random_uuid(), 
    'Orquídea (Phalaenopsis)', 
    'Phalaenopsis spp.', 
    'A orquídea Phalaenopsis é uma das espécies de orquídeas mais populares e cultivadas do mundo. Originária das florestas tropicais da Ásia e Oceania, é famosa por suas floradas duradouras que lembram o formato de borboletas. É uma planta epífita, o que significa que na natureza ela vive presa ao tronco das árvores, utilizando suas raízes aéreas para absorver umidade e nutrientes.', 
    'Cuidados Gerais: Exige ambiente muito iluminado (sem sol direto) e substrato próprio para orquídeas. Não deve ser plantada em terra comum.
Rega: Regar em média 1 vez por semana, observando a cor das raízes: se estiverem prateadas, é hora de regar; se estiverem verdes, a umidade ainda é suficiente. Deixe a água escorrer totalmente pelo fundo do vaso.
Iluminação: Luz indireta abundante. A luz solar direta pode queimar suas folhas delicadas.
Temperatura e Umidade: Temperatura ideal entre 18°C e 25°C. Aprecia umidade relativa do ar acima de 50%.
Substrato: Substrato específico para orquídeas epífitas, composto por casca de pinus, carvão vegetal e fibra de coco.
Adubação: Adubar quinzenalmente ou mensalmente com adubo específico para orquídeas (NPK 20-20-20 para manutenção ou rico em Fósforo para floração).
Poda: Após o término total da floração e secagem da haste floral, corte a haste secada cerca de 2 cm acima do nó base com tesoura esterilizada.', 
    'WEEKLY', 'MEDIUM', 'MEDIUM', 'MEDIUM', 'ORNAMENTAL', 
    'https://images.tcdn.com.br/img/img_prod/487611/doce_phalaenopsis_pink_146_2_20230109105256.jpg'
),
(
    gen_random_uuid(), 
    'Mini Coroa de Cristo', 
    'Euphorbia milii var. imperatae', 
    'A Mini Coroa de Cristo é uma variante compacta e ornamental da famosa Euphorbia milii. Apresenta ramos espinhosos e pequenas flores (brácteas) muito vistosas que podem florescer ao longo de todo o ano. É uma planta extremamente resistente ao calor e à seca, amplamente utilizada no paisagismo como bordadura ou em vasos.', 
    'Cuidados Gerais: Muito fácil de manter. Exige pouca água e bastante exposição solar. Cuidado ao manusear: a seiva leitosa da planta é tóxica e irritante para a pele e olhos.
Rega: Regar moderadamente. Deixe o solo secar completamente entre as regas. É altamente resistente à seca e intolerante ao encharcamento.
Iluminação: Sol pleno. Quanto mais sol direto receber, mais abundante será sua floração.
Temperatura e Umidade: Desenvolve-se melhor em climas quentes (entre 20°C e 32°C). Não tolera geadas.
Substrato: Solo leve, arenoso e de secagem rápida.
Adubação: Adubar a cada 2 meses na primavera e verão com adubo rico em Fósforo (ex: NPK 04-14-08) para incentivar a floração.
Poda: Faça podas de contenção ou limpeza utilizando luvas para evitar o contato com a seiva leitosa (látex).', 
    'SPORADIC', 'INTENSE', 'HIGH', 'SMALL', 'ORNAMENTAL', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRIRrET1jHh-qU8WSyILqI19lWbsmOp8956TVOPajUWZg&s=10'
),
(
    gen_random_uuid(), 
    'Cacto Coroa de Frade', 
    'Melocactus zehntneri', 
    'O cacto Coroa de Frade (Melocactus zehntneri) é uma espécie nativa do semiárido e da caatinga brasileira. Destaca-se por seu formato globular com espinhos bem definidos e, na fase adulta, pelo surgimento de uma estrutura avermelhada no topo chamada cefálio (a "coroa"), de onde nascem pequenas flores e frutos rosa-chocantes.', 
    'Cuidados Gerais: Cacto nativo de regiões secas, necessita de sol intenso e pouquíssima água. Cultivar preferencialmente em vasos de barro com excelente drenagem.
Rega: Regas bem espaçadas. Aguarde o solo secar totalmente. No inverno, reduza ao mínimo as regas.
Iluminação: Sol pleno (mínimo de 6 horas de sol direto ao dia).
Temperatura e Umidade: Prefere altas temperaturas (22°C a 35°C) e ambientes de baixa umidade. Sensível ao frio extremo.
Substrato: Substrato próprio para cactos e suculentas (mistura rica em areia grossa, perlita e pouca matéria orgânica).
Adubação: Adubar 2 a 3 vezes ao ano com fertilizante diluído para cactos.
Poda: Não requer poda.', 
    'SPORADIC', 'INTENSE', 'HIGH', 'SMALL', 'ORNAMENTAL', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR33QD4UvfZNL_oxQxOtdtPeOflFxROrxNytaIpN9bc7A&s'
),
(
    gen_random_uuid(), 
    'Alface Crocantela', 
    'Lactuca sativa L', 
    'A Alface Crocantela é uma variedade moderna de alface que combina a textura crocante da alface americana com a facilidade de cultivo e o formato da alface crespa. Possui folhas verde-claras, onduladas e muito saborosas, sendo excelente para saladas e pratos frescos.', 
    'Cuidados Gerais: Planta de ciclo curto e fácil cultivo. Necessita de regas constantes e solos férteis para que as folhas fiquem macias e crocantes.
Rega: Regar diariamente, mantendo o solo sempre levemente úmido, sem encharcar. Evite regar nas horas mais quentes do dia.
Iluminação: Sol pleno ou meia-sombra abundante (mínimo de 4 a 5 horas de sol por dia).
Temperatura e Umidade: Prefere temperaturas amenas (entre 12°C e 24°C). O calor excessivo pode induzir o pendoamento precoce (floração) e amargar as folhas.
Substrato: Solo fértil, rico em composto orgânico, leve e bem drenado.
Adubação: Adubar com húmus de minhoca ou NPK rico em Nitrogênio antes do plantio e quinzenalmente durante o desenvolvimento.
Poda/Colheita: A colheita pode ser feita retirando as folhas externas conforme o uso ou cortando a cabeça inteira rente ao solo cerca de 45 a 60 dias após o plantio.', 
    'DAILY', 'INTENSE', 'LOW', 'SMALL', 'EDIBLE', 
    'https://lh3.googleusercontent.com/gg-dl/AAQ_wbF2DeZKlBoSnTHF6zdrpR1VMDA2rBWfLL7h6KjGe1tJG39xCaHENArGCFsR90fePAPgVA6i0x4Iie6jvzfJuOp6INSP1PSMI6o8zlsw-FAwOvFophzvQ1OsWcI3J1u8pkNksrUuFfDS8uOxssIOD9pvhUfSIFev0HQjlXQkvLz8njB8Xw=s1024-rj'
),
(
    gen_random_uuid(), 
    'Hortelã', 
    'Mentha sp', 
    'A hortelã (Mentha sp.) é uma das ervas aromáticas e medicinais mais difundidas no mundo. Possui folhas verde-escuro ovaladas e serrilhadas com um aroma refrescante inconfundível, devido à presença do mentol. É utilizada no preparo de chás, sucos, pratos culinários e coquetéis.', 
    'Cuidados Gerais: A hortelã tem crescimento rápido e alastrante devido às suas raízes subterrâneas (estolões). Por isso, recomenda-se cultivá-la em vaso individual para não sufocar outras plantas.
Rega: Requer regas frequentes. O solo deve ser mantido sempre úmido, mas nunca encharcado.
Iluminação: Sol pleno ou meia-sombra (necessita de pelo menos 3 a 4 horas de luz solar diária).
Temperatura e Umidade: Adapta-se bem a temperaturas entre 15°C e 28°C e gosta de ambientes com boa umidade.
Substrato: Solo fértil, fofo, drenado e rico em matéria orgânica.
Adubação: Adubar a cada 2 meses com adubo orgânico (húmus de minhoca ou esterco curtido).
Poda: Realize podas frequentes das pontas para estimular o crescimento de novos ramos e manter o vaso cheio. Se a planta começar a florir, corte a haste para preservar o aroma das folhas.', 
    'DAILY', 'INTENSE', 'MEDIUM', 'SMALL', 'AROMATIC', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSJaHNvWMNIQAjyPBaJTJddRhFJszRJdkbbuB4wVEEZAQ&s=10'
),
(
    gen_random_uuid(), 
    'Cebolinha', 
    'Allium fistulosum', 
    'A cebolinha (Allium fistulosum) é um tempero indispensável na culinária global e especialmente na brasileira, formando com o coentro ou salsinha o famoso "cheiro-verde". Apresenta folhas compridas, tubulares e ocas de cor verde-intensa.', 
    'Cuidados Gerais: De facílimo cultivo, cresce muito bem em pequenos vasos, canteiros ou floreiras. Permite colheita contínua por longos períodos.
Rega: Regar regularmente (de 2 a 3 vezes por semana ou diariamente em dias quentes), mantendo o solo levemente úmido.
Iluminação: Sol pleno (mínimo de 4 horas de luz solar direta diária).
Temperatura e Umidade: Prefere temperaturas entre 15°C e 26°C, mas adapta-se bem a diferentes climas.
Substrato: Solo leve, rico em matéria orgânica e bem drenado.
Adubação: Adubar mensalmente com adubo orgânico rico em Nitrogênio para manter as folhas verdes e fortes.
Poda/Colheita: A colheita é feita cortando as folhas externas perto da base (deixando cerca de 2 a 3 cm da haste acima do solo) para que a planta continue brotando.', 
    'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', 
    'https://cdn.awsli.com.br/800x800/416/416989/produto/391891843/cebolinha-2-jmdxiurgbp.jpg'
),
(
    gen_random_uuid(), 
    'Tomilho Comum', 
    'Thymus vulgaris', 
    'O tomilho (Thymus vulgaris) é um subarbusto aromático perene nativo do Mediterrâneo. Possui pequenos ramos lenhosos repleto de minúsculas folhas cinza-esverdeadas altamente perfumadas. É muito utilizado na culinária para temperar carnes, molhos e assados.', 
    'Cuidados Gerais: É uma planta bastante rústica que prefere solos mais secos e muito sol. O excesso de água e a falta de drenagem são suas maiores ameaças.
Rega: Regar de forma moderada. Deixe o solo secar completamente entre as regas.
Iluminação: Sol pleno (necessita de no mínimo 5 a 6 horas de sol directo por dia).
Temperatura e Umidade: Tolera bem o calor e suporta geadas leves. Temperatura ideal entre 15°C e 28°C.
Substrato: Solo leve, arenoso e extremamente bem drenado.
Adubação: Pouca necessidade de adubação. Adubar 1 a 2 vezes ao ano com matéria orgânica é suficiente.
Poda: Realize podas leves após a floração para evitar que a base fique excessivamente lenhosa e desfolhada.', 
    'SPORADIC', 'INTENSE', 'MEDIUM', 'SMALL', 'AROMATIC', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSvMD1jKCN3x2XuCDfYWiyVKrmmWwjyBVGvT266yM3fUQ&s=10'
),
(
    gen_random_uuid(), 
    'Couve Manteiga', 
    'Brassica oleracea var. acephala', 
    'A couve manteiga é uma das hortaliças mais tradicionais e nutritivas do Brasil, rica em ferro, cálcio e vitaminas A, C e K. Possui folhas largas, macias e ligeiramente onduladas de tom verde-escuro.', 
    'Cuidados Gerais: Planta de porte ereto que pode durar mais de um ano na horta se bem cuidada. Requer solo fértil e regas constantes.
Rega: Regar regularmente mantendo o solo úmido, sem encharcar.
Iluminação: Sol pleno (mínimo de 4 a 6 horas de luz solar por dia).
Temperatura e Umidade: Adapta-se melhor a climas amenos a frios (15°C a 25°C), mas cresce bem na maior parte do Brasil.
Substrato: Solo profundo, rico em matéria orgânica, fértil e bem drenado.
Adubação: Adubação frequente com composto orgânico ou esterco curtido a cada 30 dias.
Poda/Colheita: Colha as folhas maiores a partir da base conforme forem atingindo um bom tamanho, preservando o broto central para que continue crescendo na vertical.', 
    'DAILY', 'INTENSE', 'MEDIUM', 'MEDIUM', 'EDIBLE', 
    'https://www.picturethisai.com/image-handle/website_cmsname/image/1080/222010746480820224.jpeg?x-oss-process=image/format,webp/resize,s_500&v=1.0'
),
(
    gen_random_uuid(), 
    'Coentro', 
    'Coriandrum sativum', 
    'O coentro (Coriandrum sativum) é uma planta herbácea anual de crescimento rápido. É um tempero marcante e amplamente utilizado nas culinárias do Norte, Nordeste e em pratos asiáticos e mexicanos. Tanto suas folhas quanto suas sementes são utilizadas.', 
    'Cuidados Gerais: Apresenta ciclo curto (entre 50 e 70 dias). Prefere ser semeado diretamente no local definitivo, pois não tolera bem o transplante de mudas.
Rega: Mantenda o solo constantemente úmido através de regas suaves diárias.
Iluminação: Sol pleno ou meia-sombra bem iluminada.
Temperatura e Umidade: Desenvolve-se bem entre 18°C e 28°C. Sob calor extremo, a planta tende a florir precocemente, reduzindo a produção de folhas.
Substrato: Solo leve, fértil, fofo e bem drenado.
Adubação: Incorporar adubo orgânico ao solo antes da semeadura.
Poda/Colheita: Colha a planta inteira ou retire as folhas externas conforme a necessidade antes do surgimento das flores.', 
    'DAILY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0kjPGSs_Vmpcoeo7zSBCy1jK2WQpg47OzrxdsF2pOE5jnAbF6_hx5lNyU&s=10'
),
(
    gen_random_uuid(), 
    'Planta Jade', 
    'Crassula ovata', 
    'A Planta Jade (Crassula ovata) é uma suculenta de porte arbustivo com caules lenhosos e folhas carnosas, ovais e brilhantes de cor verde-jade. É popularmente conhecida como "planta da sorte" ou "árvore do dinheiro".', 
    'Cuidados Gerais: Extremamente resistente e de vida longa. Exige poucos cuidados, sendo ideal para ambientes internos muito iluminados ou varandas.
Rega: Regar somente quando o solo estiver completamente seco. É extremamente sensível ao excesso de umidade.
Iluminação: Sol pleno ou meia-sombra muito iluminada (algumas horas de sol direto deixam as margens das folhas avermelhadas).
Temperatura e Umidade: Prefere clima ameno a quente (18°C a 30°C). Não suporta geadas.
Substrato: Substrato poroso e drenável específico para cactos e suculentas.
Adubação: Adubar 2 a 3 vezes ao ano com fertilizante para suculentas.
Poda: Pode ser podada para modelar seu formato (semelhante a um bonsai) e remover ramos fracos.', 
    'SPORADIC', 'INTENSE', 'HIGH', 'MEDIUM', 'ORNAMENTAL', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQJWXY35hk09zLbA50cX1Li7JbQUZf8LdtwQqQZQhimfg&s=10'
),
(
    gen_random_uuid(), 
    'Violeta', 
    'Saintpaulia ionantha', 
    'A Violeta-africana é uma das plantas ornamentais de interior mais amadas no Brasil. De porte pequeno, apresenta folhas aveludadas dispostas em roseta e flores delicadas de diversas cores (roxa, rosa, branca, azul e bicolor).', 
    'Cuidados Gerais: Ideal para cultivo dentro de casa, próximas a janelas bem iluminadas. O segredo do seu sucesso é a irrigação correta sem molhar a folhagem.
Rega: Regue diretamente no solo ou por capilaridade (colocando água no pratinho por 15 min e descartando o excesso). NUNCA molhe as folhas e flores.
Iluminação: Luz indireta abundante. Evite sol direto que queima as folhas aveludadas.
Temperatura e Umidade: Mantida idealmente entre 18°C e 24°C.
Substrato: Substrato leve, solto e levemente ácido (mistura de terra vegetal, fibra de coco e vermiculita).
Adubação: Adubar mensalmente com adubo específico para violetas ou rico em Fósforo.
Poda: Remova folhas amareladas ou murchas e flores secas pela base da haste.', 
    'WEEKLY', 'MEDIUM', 'MEDIUM', 'SMALL', 'ORNAMENTAL', 
    'https://www.langdonsflowers.com/cdn/shop/products/shutterstock_35049577_2000x_a37a4fe1-e238-4015-b8e2-7fe3db137708.jpg?v=1661178288'
),
(
    gen_random_uuid(), 
    'Colar de Pérolas', 
    'Senecio rowleyanus', 
    'O Colar de Pérolas é uma suculenta pendente exótica cujas folhas modificadas têm formato de pequenas esferas verdes que lembram ervilhas ou pérolas. É uma excelente opção para vasos suspensos e prateleiras.', 
    'Cuidados Gerais: Delicada quanto ao excesso de água. Suas folhas esféricas armazenam água, permitindo suportar pequenos períodos de seca.
Rega: Regar com moderação somente quando o solo estiver seco. Evite jogar água diretamente sobre as "pérolas".
Iluminação: Luz indireta abundante ou sol suave da manhã (1 a 2 horas). Sol forte direto pode queimar os grãos.
Temperatura e Umidade: Climas amenos a quentes (16°C a 26°C).
Substrato: Substrato muito leve e drenante com areia grossa.
Adubação: Adubar raramente na primavera com fertilizante diluído para suculentas.
Poda: Apare os ramos pendentes que ficarem muito longos e replante os estalhos no solo para multiplicar a planta.', 
    'SPORADIC', 'MEDIUM', 'MEDIUM', 'MEDIUM', 'ORNAMENTAL', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRF50tEUkooIfRUxKRL8DWsR3dv-UTnJTVIjuxwKgURmw&s=10'
),
(
    gen_random_uuid(), 
    'Samambaia', 
    'Polypodium persicifolium', 
    'A Samambaia é uma planta ornamental clássica de folhagem exuberante e volumosa. Suas frondes delicadas e verdes trazem frescor e elegância para varandas, salas e ambientes sombreados.', 
    'Cuidados Gerais: Precisa de alta umidade ambiente e proteção contra ventos fortes que ressecam suas folhas.
Rega: Regar frequentemente para manter o solo constantemente úmido (sem encharcar). Borrifar água nas folhas regularmente em dias secos.
Iluminação: Meia-sombra ou luz filtrada. Não tolera sol direto.
Temperatura e Umidade: Ambientes quentes e úmidos (18°C a 28°C).
Substrato: Substrato rico em matéria orgânica, leve e drenável (com fibra de coco e turfa).
Adubação: Adubar a cada 2 meses com adubo específico para samambaias ou NPK 10-10-10.
Poda: Cortar folhas secas, amareladas ou danificadas rente à base.', 
    'DAILY', 'MEDIUM', 'MEDIUM', 'LARGE', 'ORNAMENTAL', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTEGf2iJWSVF85FMt48_qyP5EK5tGy0Y2JbvBMPM6IhnQ&s=10'
),
(
    gen_random_uuid(), 
    'Espada de São Jorge', 
    'Dracaena trifasciata', 
    'A Espada de São Jorge é uma das plantas mais resistentes do mundo. Apresenta folhas eretas, rígidas e de formato espadado com listras em tons de verde e cinza. Muito utilizada na decoração e conhecida por purificar o ar.', 
    'Cuidados Gerais: Planta praticamente indestrutível, ideal para quem tem pouco tempo ou pouca experiência com jardinagem.
Rega: Regar esporadicamente. Deixar o solo secar completamente antes de regar novamente (a cada 15-20 dias no inverno).
Iluminação: Altamente adaptável: tolera desde sombra e ambientes internos até sol pleno.
Temperatura e Umidade: Suporta variações extremas de temperatura (10°C a 35°C).
Substrato: Qualquer solo bem drenado.
Adubação: Adubação anual ou semestral leve.
Poda: Não requer poda, apenas remoção de folhas danificadas.', 
    'SPORADIC', 'MEDIUM', 'MEDIUM', 'MEDIUM', 'ORNAMENTAL', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRTrMAo7P2odHeQoQlOe7pWKtjXjAszu7YFoqXbYntn9A&s=10'
),
(
    gen_random_uuid(), 
    'Tomate Cereja', 
    'Solanum lycopersicum', 
    'O Tomate Cereja é uma variedade de tomateiro que produz frutos pequenos, doces e suculentos em cachos. Muito popular em hortas caseiras pela facilidade de cultivo em vasos e pela alta produtividade.', 
    'Cuidados Gerais: Necessita de suporte (estacas ou tutores) para apoiar os ramos conforme os frutos crescem.
Rega: Regar diariamente no solo sem molhar as folhas para evitar doenças fúngicas.
Iluminação: Sol pleno (mínimo de 5 a 6 horas de sol direto por dia).
Temperatura e Umidade: Prefere clima quente (20°C a 30°C).
Substrato: Solo fértil, rico em matéria orgânica e bem drenado.
Adubação: Adubar quinzenalmente durante a floração e frutificação com adubo rico em Fósforo e Potássio.
Poda: Realizar a poda dos brotos ladrões (ramos laterais que nascem nas axilas das folhas) para concentrar força na produção de frutos.', 
    'DAILY', 'INTENSE', 'HIGH', 'MEDIUM', 'EDIBLE', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTP_lMV3HJERJdPkHx0PikqtIbM_RSmHWlZ0PmvkPdIUg&s=10'
),
(
    gen_random_uuid(), 
    'Rabanete Vermelho Comprido', 
    'Raphanus sativus', 
    'O Rabanete Vermelho Comprido é uma hortaliça de raiz tuberosa com formato alongado, casca vermelha e polpa branca e crocante de sabor levemente picante. É uma das hortaliças de crescimento mais rápido do mundo.', 
    'Cuidados Gerais: Pode ser colhido entre 25 a 35 dias após a semeadura. Ótimo para hortas escolares e iniciantes.
Rega: Regar frequentemente para manter a umidade constante; a falta de água deixa a raiz dura e excessivamente picante.
Iluminação: Sol pleno ou meia-sombra.
Temperatura e Umidade: Prefere climas amenos a frescos (12°C a 22°C).
Substrato: Solo leve, fofo, livre de pedras e rico em matéria orgânica para que as raízes cresçam sem deformação.
Adubação: Adubar o solo antes do plantio com composto orgânico.
Poda/Colheita: Não requer poda. Colha assim que a raiz atingir cerca de 5-8 cm de comprimento.', 
    'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTmnNbIva3vqf0YHKjIcCQ5Cu4atmJ9hp3mY1I3h50d1w&s'
),
(
    gen_random_uuid(), 
    'Salsinha', 
    'Petroselinum crispum', 
    'A salsinha é uma erva aromática bisanual amplamente utilizada na culinária mundial. Apresenta folhas recortadas de tom verde-brilhante e sabor fresco levemente picante.', 
    'Cuidados Gerais: A germinação das sementes pode ser lenta (até 3 semanas). Depois de estabelecida, é muito produtiva.
Rega: Manter o solo sempre levemente úmido.
Iluminação: Sol pleno ou meia-sombra abundante.
Temperatura e Umidade: Climas amenos a quentes (15°C a 26°C).
Substrato: Solo fértil, fofo, rico em matéria orgânica e bem drenado.
Adubação: Adubar a cada 30 dias com adubo orgânico.
Poda/Colheita: Colher os talos externos inteiros perto da base para incentivar o surgimento de novos brotos no centro.', 
    'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'AROMATIC', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtP2VWIk9QEu1CeDI2JWSsCe-B4O7BRaRK6eITtGInBQ&s=10'
),
(
    gen_random_uuid(), 
    'Melissa Erva Cidreira', 
    'Melissa officinalis L', 
    'A Melissa (ou Erva-Cidreira verdadeira) é uma planta medicinal e aromática com folhas ovais, serrilhadas e um característico aroma de limão. Amplamente utilizada em chás por suas propriedades calmantes e relaxantes.', 
    'Cuidados Gerais: Planta herbácea e perene que forma touceiras densas.
Rega: Regar regularmente para manter o solo levemente úmido.
Iluminação: Sol pleno ou meia-sombra (proteja do sol escaldante do meio-dia).
Temperatura e Umidade: Desenvolve-se bem entre 15°C e 25°C.
Substrato: Solo fértil, rico em umus e bem drenado.
Adubação: Adubação orgânica trimestral.
Poda: Poda frequente dos ramos para manter a planta compacta e evitar que fique lenhosa.', 
    'WEEKLY', 'MEDIUM', 'MEDIUM', 'MEDIUM', 'AROMATIC', 
    'https://images.tcdn.com.br/img/img_prod/799330/sementes_de_erva_cidreira_melissa_linha_cheff_3025_1_ae9955547151ad0d55f6a4b4eb95b5a2.jpg'
),
(
    gen_random_uuid(), 
    'Ora-pro-nóbis', 
    'Pereskia aculeata', 
    'A Ora-pro-nóbis é uma cactácea trepadeira considerada uma PANC (Planta Alimentícia Não Convencional). É famosa por seu altíssimo teor de proteínas, fibras e minerais em suas folhas suculentas.', 
    'Cuidados Gerais: Planta extremamente rústica e resistente, mas possui espinhos afiados em seus ramos.
Rega: Regar moderadamente. Suporta bem a seca.
Iluminação: Sol pleno para melhor desenvolvimento foliar.
Temperatura e Umidade: Climas quentes e tropicais (20°C a 32°C).
Substrato: Adapta-se a quase todos os tipos de solo, desde que drenados.
Adubação: Adubação orgânica simples anual.
Poda: Realizar podas de contenção para controlar seu crescimento trepador e facilitar a colheita das folhas.', 
    'WEEKLY', 'INTENSE', 'HIGH', 'LARGE', 'EDIBLE', 
    'https://s2.glbimg.com/b071-m6rYLXsNqLjPwh1VKTeZBs=/620x455/e.glbimg.com/og/ed/f/original/2021/09/09/pereskia_aculeata_mill.jpg'
),
(
    gen_random_uuid(), 
    'Pimenta Biquinho', 
    'Capsicum chinense', 
    'A Pimenta Biquinho é famosa por seu aroma marcante e sabor adocicado sem ter ardência (sem capsaicina significativa). Produz frutos pequenos e arredondados com uma ponta que lembra um biquinho.', 
    'Cuidados Gerais: Excelente para cultivo em vasos e hortas urbanas. Arbusto compacto e muito produtivo.
Rega: Regar regularmente mantendo o solo úmido sem encharcar.
Iluminação: Sol pleno (necessita de luz solar direta para frutificar).
Temperatura e Umidade: Climas quentes (20°C a 30°C). Sensível ao frio.
Substrato: Solo leve, fértil e bem drenado.
Adubação: Adubar a cada 20 dias com adubo rico em Potássio durante a fase de frutos.
Poda: Poda de limpeza dos ramos secos ou fracos.', 
    'DAILY', 'INTENSE', 'HIGH', 'MEDIUM', 'EDIBLE', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQKCZv_3w-iBxJ51aemu1LCrkpcbo7ftM6Di1yaQ_0ZPA&s=10'
),
(
    gen_random_uuid(), 
    'Babosa', 
    'Aloe vera', 
    'A Babosa (Aloe vera) é uma suculenta medicinal amplamente conhecida por suas propriedades cicatrizantes, hidratantes e cosméticas extraídas do gel transparente interior de suas folhas carnosas e espinhosas.', 
    'Cuidados Gerais: Planta muito rústica que requer pouca manutenção e pouca água.
Rega: Regar apenas quando o solo estiver totalmente seco. O acúmulo de água pode apodrecer a base da planta.
Iluminação: Sol pleno ou meia-sombra muito iluminada.
Temperatura e Umidade: Prefere clima quente e seco (20°C a 35°C).
Substrato: Substrato leve, arenoso e com excelente drenagem.
Adubação: Adubar 1 a 2 vezes por ano com matéria orgânica.
Poda/Colheita: Retire as folhas mais externas e maduras da base cortando rente ao caule com lâmina afiada.', 
    'SPORADIC', 'INTENSE', 'HIGH', 'MEDIUM', 'ORNAMENTAL', 
    'https://images.tcdn.com.br/img/img_prod/1105580/babosa_aloe_vera_179_variacao_59_1_ca555c191aafb2fdff5454dd33c48f66.jpg'
),
(
    gen_random_uuid(), 
    'Morango', 
    'Fragaria vesca', 
    'O Morangueiro é uma planta herbácea rasteira que produz frutos vermelhos, aromáticos e saborosos. Fica lindo em vasos suspensos, jardineiras e canteiros.', 
    'Cuidados Gerais: Proteja os frutos do contato direto com a terra úmida utilizando palhada em volta da planta.
Rega: Regar frequentemente para manter o solo úmido, sem molhar os frutos e flores.
Iluminação: Sol pleno (mínimo de 5 a 6 horas de sol direto por dia).
Temperatura e Umidade: Climas amenos a frios (15°C a 22°C).
Substrato: Solo fértil, rico em matéria orgânica, leve e ligeiramente ácido.
Adubação: Adubar quinzenalmente durante a frutificação com adubo orgânico ou NPK rico em Potássio.
Poda: Remova folhas secas e velhas, além dos estolões (muda "cordão") se quiser focar a energia na frutificação.', 
    'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', 
    'https://www.thjardins.com.br/wp-content/uploads/2026/07/fragaria-x-ananassa-fragissimo-92532384.jpg'
),
(
    gen_random_uuid(), 
    'Erva-doce', 
    'Pimpinella anisum L', 
    'A Erva-doce é uma planta aromática conhecida desde a antiguidade. Apresenta folhas finas e flores amarelas dispostas em umbelas, produzindo sementes altamente perfumadas usadas em chás e doces.', 
    'Cuidados Gerais: Planta que pode atingir até 50 cm de altura. Deve ser cultivada preferencialmente por sementes.
Rega: Regar com frequência mantendo o solo levemente úmido.
Iluminação: Sol pleno.
Temperatura e Umidade: Climas amenos a quentes (18°C a 25°C).
Substrato: Solo fértil, bem drenado e de textura leve.
Adubação: Adubação orgânica simples no momento do plantio.
Poda: Não requer poda, apenas colheita das sementes quando estiverem marrons.', 
    'WEEKLY', 'INTENSE', 'MEDIUM', 'MEDIUM', 'AROMATIC', 
    'https://florayamamura.com.br/wp-content/uploads/2024/06/Erva-Doce.jpg'
),
(
    gen_random_uuid(), 
    'Capim Cidreira', 
    'Cymbopogon citratus', 
    'O Capim Cidreira (ou Capim-Santo/Capim-Limão) é uma gramínea perene que forma touceiras densas com folhas longas e cortantes de intenso aroma cítrico de limão. Muito apreciado para infusões.', 
    'Cuidados Gerais: De facílimo cultivo e crescimento rápido. Pode ser plantado em vasos grandes ou diretamente no solo.
Rega: Regar regularmente. Gosta de solo com boa umidade.
Iluminação: Sol pleno.
Temperatura e Umidade: Climas quentes e tropicais.
Substrato: Adapta-se a quase qualquer solo, preferindo os mais férteis e bem drenados.
Adubação: Adubar anualmente com matéria orgânica.
Poda: Fazer podas drásticas de limpeza 1 vez ao ano para renovação da touceira e colheita de folhas secas.', 
    'WEEKLY', 'INTENSE', 'HIGH', 'LARGE', 'AROMATIC', 
    'https://mondiniplantas.cdn.magazord.com.br/img/2026/04/produto/7854/alecrim-adulta.jpg?ims=800x800'
),
(
    gen_random_uuid(), 
    'Beterraba Maravilha', 
    'Beta vulgaris esculenta', 
    'A Beterraba Maravilha é uma variedade de raiz tuberosa arredondada de cor roxo-intensa e sabor adocicado. Suas folhas jovens também são comestíveis e altamente nutritivas.', 
    'Cuidados Gerais: Requer desbaste (raleio) das mudas jovens para que a raiz principal tenha espaço para se expandir.
Rega: Regar regularmente para manter o solo úmido de forma homogênea.
Iluminação: Sol pleno.
Temperatura e Umidade: Prefere clima ameno (15°C a 22°C).
Substrato: Solo leve, fofo, livre de pedras e rico em matéria orgânica.
Adubação: Adubar com composto rico em Fósforo e Potássio. Evite excesso de Nitrogênio para não desenvolver só folhas.
Poda/Colheita: Colher entre 60 a 80 dias após o plantio quando a raiz atingir tamanho comercial.', 
    'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQJWXY35hk09zLbA50cX1Li7JbQUZf8LdtwQqQZQhimfg&s=10'
),
(
    gen_random_uuid(), 
    'Rúcula', 
    'Eruca sativa', 
    'A Rúcula é uma hortaliça folhosa de sabor picante e marcante. É rica em ferro, cálcio e vitaminas A e C, sendo indispensável para saladas frescas.', 
    'Cuidados Gerais: Ciclo muito rápido (30 a 40 dias). Pode ser semeada sucessivamente a cada 15 dias para colheita contínua.
Rega: Regar diariamente com borrifador ou jato suave para manter o solo úmido.
Iluminação: Sol pleno ou meia-sombra.
Temperatura e Umidade: Climas amenos e frescos (15°C a 22°C).
Substrato: Solo fértil, fofo e rico em composto orgânico.
Adubação: Incorporar húmus de minhoca ao solo antes da semeadura.
Poda/Colheita: Colher as folhas externas ou a planta inteira antes do surgimento das flores.', 
    'WEEKLY', 'INTENSE', 'MEDIUM', 'SMALL', 'EDIBLE', 
    'https://www.infoescola.com/wp-content/uploads/2010/09/r%C3%BAcula_760063117.jpg'
),
(
    gen_random_uuid(), 
    'Almeirão Amarelo', 
    'Cichorium intybus', 
    'O Almeirão Amarelo é uma hortaliça folhosa de sabor amargo característico e propriedades digestivas. Apresenta folhas compridas de cor verde-claro a amareladas.', 
    'Cuidados Gerais: Excelente para hortas caseiras pela sua rusticidade e resistência a pragas.
Rega: Regar regularmente para manter o solo úmido e amenizar o sabor amargo excessivo.
Iluminação: Sol pleno.
Temperatura e Umidade: Desenvolve-se bem em temperaturas entre 15°C e 26°C.
Substrato: Solo fértil, bem drenado e rico em matéria orgânica.
Adubação: Adubar mensalmente com adubo orgânico.
Poda/Colheita: As folhas podem ser colhidas individualmente de fora para dentro.', 
    'DAILY', 'INTENSE', 'MEDIUM', 'MEDIUM', 'EDIBLE', 
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSbpBePCAiFhBANv04cqUJEK1_1Kx-PNLjurer8ggOHa1zy8Mx442QDAb8&s=10'
);