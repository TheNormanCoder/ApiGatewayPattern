# Spring Cloud Microservices Demo

Questo progetto è una dimostrazione di un'architettura di microservizi utilizzando Spring Cloud, con particolare attenzione ai pattern di API Gateway e Circuit Breaker.

## Componenti

Il progetto è composto dai seguenti componenti:

1. **Eureka Server** - Servizio di Service Discovery basato su Netflix Eureka
2. **API Gateway** - Gateway API basato su Spring Cloud Gateway con Circuit Breaker
3. **Product Service** - Microservizio per la gestione dei prodotti
4. **Order Service** - Microservizio per la gestione degli ordini con Circuit Breaker per le chiamate al Product Service

## Pattern implementati

### API Gateway Pattern

Il pattern API Gateway è implementato utilizzando Spring Cloud Gateway. Le caratteristiche principali includono:

- Routing delle richieste ai microservizi appropriati
- Possibilità di implementare filtri per cross-cutting concerns come autenticazione e logging
- Gestione centralizzata degli errori tramite Circuit Breaker

### Circuit Breaker Pattern

Il pattern Circuit Breaker è implementato utilizzando Resilience4j. Le caratteristiche principali includono:

- Prevenzione di chiamate a servizi che probabilmente falliranno
- Degradazione graduale delle funzionalità quando i servizi dipendenti falliscono
- Recupero automatico quando i servizi tornano disponibili
- Configurazione fine-tuned per diversi tipi di servizi

## Struttura del Progetto

```
microservices-demo/
├── eureka-server/             # Service Discovery
├── api-gateway/               # API Gateway con Circuit Breaker
├── product-service/           # Microservizio per i prodotti
└── order-service/             # Microservizio per gli ordini
```

## Prerequisiti

- JDK 17
- Maven
- Docker e Docker Compose (opzionale, per l'esecuzione containerizzata)

## Installazione e Avvio

### Utilizzo di Maven

1. Clona il repository
2. Compila i progetti con Maven:

```bash
mvn clean package
```

3. Avvia i servizi nell'ordine corretto:

```bash
# Avvia Eureka Server
cd eureka-server
mvn spring-boot:run

# Avvia Product Service
cd ../product-service
mvn spring-boot:run

# Avvia Order Service
cd ../order-service
mvn spring-boot:run

# Avvia API Gateway
cd ../api-gateway
mvn spring-boot:run
```

### Utilizzo di Docker Compose

Per avviare tutti i servizi con Docker Compose:

```bash
docker-compose up -d
```

## Test dell'API

Dopo aver avviato tutti i servizi, puoi testare l'API tramite l'API Gateway.

### Endpoints principali:

1. **Product Service**:
    - `GET /api/products` - Recupera tutti i prodotti
    - `GET /api/products/{id}` - Recupera un prodotto specifico
    - `POST /api/products` - Crea un nuovo prodotto
    - `GET /api/products/test-error?throwError=true` - Simula un errore per testare il Circuit Breaker

2. **Order Service**:
    - `GET /api/orders` - Recupera tutti gli ordini
    - `GET /api/orders/{id}` - Recupera un ordine specifico
    - `POST /api/orders` - Crea un nuovo ordine
    - `GET /api/orders/test-error?throwError=true` - Simula un errore per testare il Circuit Breaker

### Esempio di creazione di un prodotto:

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Smartphone",
    "description": "Latest smartphone with great features",
    "price": 799.99,
    "stock": 100,
    "category": "Electronics"
  }'
```

### Esempio di creazione di un ordine:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Mario Rossi",
    "customerEmail": "mario.rossi@example.com",
    "shippingAddress": "Via Roma 123, Milano",
    "orderItems": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ]
  }'
```

## Test del Circuit Breaker

Per testare il Circuit Breaker, puoi simulare il fallimento del Product Service:

1. Ferma il Product Service (Ctrl+C o `docker-compose stop product-service`)
2. Prova a creare un nuovo ordine tramite l'API Gateway
3. Dovresti ricevere una risposta di fallback dal Circuit Breaker

## Dashboard

- Eureka Dashboard: http://localhost:8761
- H2 Console per Product Service: http://localhost:8081/h2-console
- H2 Console per Order Service: http://localhost:8082/h2-console
- Actuator Endpoints:
    - API Gateway: http://localhost:8080/actuator
    - Product Service: http://localhost:8081/actuator
    - Order Service: http://localhost:8082/actuator

## Tecnologie Utilizzate

- Spring Boot 3.2.3
- Spring Cloud 2023.0.0
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Resilience4j
- Spring WebFlux
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- Docker

## Note aggiuntive

Questo progetto è stato creato per scopi dimostrativi e utilizza database H2 in memoria. In un ambiente di produzione, si consiglia di utilizzare database persistenti e di implementare misure di sicurezza aggiuntive.