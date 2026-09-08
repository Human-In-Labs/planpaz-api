# Planpaz API 🌱

API RESTful para a plataforma e aplicativo móvel **Planpaz** (gestão inteligente de cultivo doméstico, lembretes de rega climáticos, gamificação e comunidade).

- **Base URL:** `http://localhost:8080/api`
- **Dashboard de Testes Interativo:** `http://localhost:8080/test` (ou `http://localhost:8080/test.html`)

---

## Sumário
1. [Autenticação (`/api/auth`)](#1-autenticação-apiauth)
2. [Usuário, Perfil & Social (`/api/user`)](#2-usuário-perfil--social-apiuser)
3. [Catálogo de Espécies & Estágios (`/api/species`)](#3-catálogo-de-espécies--estágios-apispecies)
4. [Meu Jardim & Regas (`/api/garden`)](#4-meu-jardim--regas-apigarden)
5. [Clima & Previsão do Tempo (`/api/clima`)](#5-clima--previsão-do-tempo-apiclima)
6. [Conquistas & Gamificação (`/api/achievements`)](#6-conquistas--gamificação-apiachievements)
7. [Interface de Testes](#7-interface-de-testes)

---

## 1. Autenticação (`/api/auth`)

Endpoints públicos destinados a registro inicial e obtenção de token JWT.

### `POST /api/auth/register`
Registra as credenciais básicas de um novo usuário.
- **Body:**
  ```json
  {
    "name": "Maria Silva",
    "username": "mariasilva",
    "email": "maria@exemplo.com",
    "password": "senhaSegura123"
  }
  ```
- **Resposta (200 OK):**
  ```json
  {
    "name": "Maria Silva",
    "username": "mariasilva",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

### `POST /api/auth/login`
Autentica o usuário e emite um novo token JWT.
- **Body:**
  ```json
  {
    "email": "maria@exemplo.com",
    "password": "senhaSegura123"
  }
  ```
- **Resposta (200 OK):**
  ```json
  {
    "name": "Maria Silva",
    "username": "mariasilva",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

---

## 2. Usuário, Perfil & Social (`/api/user`)

> 🔒 **Requer Header:** `Authorization: Bearer <token>`

### `GET /api/user`
- **Descrição:** Valida se o token JWT atual é válido. Retorna mensagem de sucesso.

### `GET /api/user/settings`
- **Descrição:** Retorna todos os dados de perfil, configurações, ecoscore, coordenadas e preferências do usuário autenticado.
- **Resposta (200 OK):**
  ```json
  {
    "id": "c1f7b0a8-9d41-4b71-b0db-5287f39420dc",
    "name": "Maria Silva",
    "username": "mariasilva",
    "email": "maria@exemplo.com",
    "bio": "Amante de suculentas e hortas urbanas.",
    "birthdate": "1995-06-15",
    "gender": "FEMALE",
    "mainGoal": "WELL_BEING",
    "roomLuminosity": ["INTENSE", "MEDIUM"],
    "spaceAvailability": ["MEDIUM"],
    "experienceLevel": "INTERMEDIATE",
    "timeAvailability": "DAILY",
    "wateringTime": "08:00:00",
    "ecoscore": 120,
    "cityName": "São Paulo",
    "latitude": -23.55052,
    "longitude": -46.633308,
    "fcmToken": null,
    "createdAt": "2026-03-01T10:00:00"
  }
  ```

### `PUT /api/user/preferences`
- **Descrição:** Atualiza as preferências opcionais de cultivo e onboarding do usuário.
- **Body:**
  ```json
  {
    "bio": "Nova biografia",
    "birthdate": "1995-06-15",
    "gender": "FEMALE",
    "mainGoal": "DECORATION",
    "roomLuminosity": ["INTENSE"],
    "spaceAvailability": ["SMALL", "MEDIUM"],
    "experienceLevel": "BEGINNER",
    "timeAvailability": "FREQUENT",
    "wateringTime": "07:30:00",
    "cityName": "Campinas",
    "latitude": -22.9056,
    "longitude": -47.0608,
    "fcmToken": null
  }
  ```

### `PUT /api/user/settings`
- **Descrição:** Atualiza as configurações completas do usuário (dados cadastrais e preferências).

### `GET /api/user/search?username={termo}`
- **Descrição:** Busca usuários cadastrados por @username.
- **Resposta:** Lista de `[ { "id", "name", "username", "email" } ]`.

### `POST /api/user/{id}/follow`
- **Descrição:** Segue o usuário com o ID informado.

### `DELETE /api/user/{id}/unfollow`
- **Descrição:** Deixa de seguir o usuário com o ID informado.

### `GET /api/user/{id}/followers`
- **Descrição:** Lista os seguidores do usuário informado.

### `GET /api/user/{id}/following`
- **Descrição:** Lista as pessoas que o usuário informado está seguindo.

### `DELETE /api/user/followers/{followerId}`
- **Descrição:** Remove um seguidor da lista do usuário autenticado.

---

## 3. Catálogo de Espécies & Estágios (`/api/species`)

> 🔒 **Requer Header:** `Authorization: Bearer <token>`

### `GET /api/species`
- **Descrição:** Lista todas as espécies de plantas disponíveis no catálogo.

### `GET /api/species/{id}`
- **Descrição:** Obtém detalhes completos de uma espécie pelo UUID.

### `POST /api/species`
- **Descrição:** Cadastra uma nova espécie de planta.
- **Body:**
  ```json
  {
    "name": "Alecrim",
    "scientificName": "Salvia rosmarinus",
    "description": "Erva aromática excelente para culinária e fácil de cuidar.",
    "type": "AROMATIC",
    "size": "MEDIUM",
    "wateringLevel": "SPORADIC",
    "luminosityLevel": "INTENSE",
    "temperatureLevel": "MEDIUM",
    "imagePath": "https://exemplo.com/alecrim.jpg"
  }
  ```

### `PUT /api/species/{id}`
- **Descrição:** Atualiza os dados de uma espécie existente.

### `DELETE /api/species/{id}`
- **Descrição:** Remove uma espécie do catálogo.

### `GET /api/species/{id}/stages`
- **Descrição:** Retorna os estágios de desenvolvimento da espécie informada (ex: Germinação, Muda, Floração, Colheita).

---

## 4. Meu Jardim & Regas (`/api/garden`)

> 🔒 **Requer Header:** `Authorization: Bearer <token>`

Gerenciamento das plantas cultivadas pelo usuário autenticado.

### `GET /api/garden`
- **Descrição:** Lista todas as plantas do jardim do usuário.

### `GET /api/garden/{id}`
- **Descrição:** Retorna detalhes de uma planta específica do usuário.

### `POST /api/garden/add`
- **Descrição:** Adiciona uma planta ao jardim do usuário autenticado.
- **Body:**
  ```json
  {
    "nickname": "Minha Samambaia da Sala",
    "plant": { "id": "uuid-da-especie" },
    "stage": { "id": "uuid-do-estagio" },
    "room": "LIVING_ROOM",
    "directRain": false,
    "wateringNotification": true
  }
  ```

### `PUT /api/garden/{id}`
- **Descrição:** Atualiza dados de uma planta do jardim (apelido, estágio, cômodo, etc.).

### `DELETE /api/garden/{id}`
- **Descrição:** Remove a planta do jardim do usuário.

### `POST /api/garden/watering/{id}`
- **Descrição:** Registra a realização da rega na data atual.

### `GET /api/garden/next-watering/{id}`
- **Descrição:** Retorna o lembrete e previsão da próxima data/horário de rega da planta.
- **Resposta (200 OK):**
  ```json
  {
    "order": 1,
    "gardenPlantId": "uuid-da-planta",
    "plantNickname": "Minha Samambaia",
    "plantImage": null,
    "date": "2026-09-09",
    "time": "08:00:00",
    "status": "EM_DIA"
  }
  ```

### `GET /api/garden/next-waterings/{id}`
- **Descrição:** Retorna a projeção das **próximas 4 regas** agendadas para a planta.

### `GET /api/garden/watering-status/{id}?cidade={cidade}`
- **Descrição:** Cruzamento inteligente entre as necessidades da planta e o clima atual da cidade informada, retornando recomendação de rega ajustada pelo tempo (chuva, calor, umidade).

---

## 5. Clima & Previsão do Tempo (`/api/clima`)

> 🌐 **Acesso Público:** Não requer autenticação obrigatória para consulta.

Permite consulta por nome da cidade (`cidade`) ou por coordenadas geográficas (`latitude` e `longitude`).

### `GET /api/clima/current`
- **Parâmetros de Query:** `?cidade={cidade}` **OU** `?latitude={lat}&longitude={lon}`
- **Descrição:** Retorna a condição climática em tempo real (temperatura, sensação térmica, umidade, probabilidade de chuva, se está chovendo agora e ícone).
- **Exemplo:** `/api/clima/current?cidade=São%20Paulo` ou `/api/clima/current?latitude=-23.55&longitude=-46.63`

### `GET /api/clima/forecast`
- **Parâmetros de Query:** `?cidade={cidade}` **OU** `?latitude={lat}&longitude={lon}`
- **Descrição:** Retorna a previsão detalhada para as próximas horas/dia seguinte dividida em intervalos horários.

### `GET /api/clima`
- **Descrição:** Atalho equivalente a `/api/clima/current`.

---

## 6. Conquistas & Gamificação (`/api/achievements`)

> 🔒 **Requer Header:** `Authorization: Bearer <token>`

### `GET /api/achievements`
- **Descrição:** Lista as conquistas já desbloqueadas pelo usuário autenticado.

### `GET /api/achievements/catalog`
- **Descrição:** Lista o catálogo completo de todas as conquistas cadastradas na plataforma.

### Endpoints de Teste / Gatilhos Manuais
Endpoints disponíveis para conceder conquistas ao usuário autenticado:
- `POST /api/achievements/cultivar-3` (Cultivar 3 plantas)
- `POST /api/achievements/cultivar-5` (Cultivar 5 plantas)
- `POST /api/achievements/cultivar-10` (Cultivar 10 plantas)
- `POST /api/achievements/cuidar-3-dias` (Cuidar por 3 dias seguidos)
- `POST /api/achievements/cuidar-5-dias` (Cuidar por 5 dias seguidos)
- `POST /api/achievements/cuidar-10-dias` (Cuidar por 10 dias seguidos)
- `POST /api/achievements/estagio-crescimento` (Alcançar estágio de crescimento)
- `POST /api/achievements/estagio-colheita` (Alcançar colheita ou floração)

---

## 7. Interface de Testes

A aplicação inclui um dashboard web interativo para validação rápida de todos os fluxos da API:
- URL: **`http://localhost:8080/test`** (ou abra diretamente `test.html` no navegador apontando para o servidor local).