# ApiGateway
Servizio Proxy distribuito (tramite Eureka Server) che si frappone tra i client ed il servizio richiesto al fine di
bilanciare il carico e disaccoppiare i vari servizi tra di loro.

Fornisce una interfaccia completa al client, esempio:

Ottima domanda! Ti spiego il flusso di esecuzione in dettaglio, immaginando uno scenario in cui un **client richiede un documento**. Vedremo come il tuo sistema (basato su un'architettura a microservizi con il ApiGateway) gestisce questa richiesta.

---

### **Flusso di Esecuzione**

#### **1. Il Client invia una richiesta**
- Il client (ad esempio un'applicazione frontend o un altro sistema) invia una richiesta HTTP al tuo microservizio **ApiGateway**, con i dettagli del documento richiesto.
    - Ad esempio: `GET /api/request-document?documentId=123`.

---

#### **2. Il ApiGateway riceve la richiesta**
- **ApiGateway** agisce come proxy e primo punto di ingresso.
    - Valuta la richiesta e determina quale microservizio finale deve elaborarla (ad esempio, il microservizio "DocumentService" che gestisce documenti).

---

#### **3. Individuazione del microservizio finale (Service Discovery)**
- Il ApiGateway utilizza Eureka per individuare dinamicamente il microservizio "DocumentService".
    - Ad esempio, scopre che ci sono 2 istanze attive:
        - `http://localhost:8081`
        - `http://localhost:8082`.

---

#### **4. Bilanciamento del carico**
- Il ApiGateway utilizza un meccanismo di bilanciamento del carico (ad esempio, tramite **Spring Cloud LoadBalancer**) per decidere a quale istanza inviare la richiesta.
    - Supponiamo che invii la richiesta all'istanza `http://localhost:8081`.

---

#### **5. Comunicazione con il microservizio finale**
- Il ApiGateway inoltra la richiesta a "DocumentService".
    - Ad esempio: `GET http://localhost:8081/document?id=123`.
- Il microservizio "DocumentService" elabora la richiesta, recupera il documento richiesto dal database e restituisce una risposta.

---

#### **6. Il ApiGateway restituisce la risposta al client**
- Il ApiGateway riceve la risposta dal "DocumentService".
    - Ad esempio, il contenuto del documento in formato JSON.
- Restituisce quindi questa risposta al client che aveva inviato la richiesta iniziale.

---

### **Diagramma semplificato del flusso**
```
Client --> ApiGateway --> Service Discovery (Eureka) --> DocumentService
    ^                                                                  |
    |------------------------------------------------------------------|
```

---

### **Vantaggi di questo approccio**
1. **Scalabilità**:
    - Puoi aggiungere più istanze di "DocumentService" senza modificare il ApiGateway, poiché Eureka gestisce dinamicamente la lista delle istanze.

2. **Flessibilità**:
    - Il client non ha bisogno di sapere quale microservizio elaborerà la richiesta; tutto è astratto dal proxy.

3. **Resilienza**:
    - Se una delle istanze del "DocumentService" è offline, il ApiGateway indirizzerà automaticamente le richieste a un'altra istanza attiva.

---