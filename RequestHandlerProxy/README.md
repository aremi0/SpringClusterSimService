# RequestHandlerProxy
Servizio Proxy distribuito (tramite Eureka Server) che si frappone tra i client ed il servizio richiesto al fine di
bilanciare il carico e disaccoppiare i vari servizi tra di loro.  

Fornisce una interfaccia completa al client, esempio:

Ottima domanda! Ti spiego il flusso di esecuzione in dettaglio, immaginando uno scenario in cui un **client richiede un documento**. Vedremo come il tuo sistema (basato su un'architettura a microservizi con il RequestHandlerProxy) gestisce questa richiesta.

---

### **Flusso di Esecuzione**

#### **1. Il Client invia una richiesta**
- Il client (ad esempio un'applicazione frontend o un altro sistema) invia una richiesta HTTP al tuo microservizio **RequestHandlerProxy**, con i dettagli del documento richiesto.
    - Ad esempio: `GET /api/request-document?documentId=123`.

---

#### **2. Il RequestHandlerProxy riceve la richiesta**
- **RequestHandlerProxy** agisce come proxy e primo punto di ingresso.
    - Valuta la richiesta e determina quale microservizio finale deve elaborarla (ad esempio, il microservizio "DocumentService" che gestisce documenti).

---

#### **3. Individuazione del microservizio finale (Service Discovery)**
- Il RequestHandlerProxy utilizza Eureka per individuare dinamicamente il microservizio "DocumentService".
    - Ad esempio, scopre che ci sono 2 istanze attive:
        - `http://localhost:8081`
        - `http://localhost:8082`.

---

#### **4. Bilanciamento del carico**
- Il RequestHandlerProxy utilizza un meccanismo di bilanciamento del carico (ad esempio, tramite **Spring Cloud LoadBalancer**) per decidere a quale istanza inviare la richiesta.
    - Supponiamo che invii la richiesta all'istanza `http://localhost:8081`.

---

#### **5. Comunicazione con il microservizio finale**
- Il RequestHandlerProxy inoltra la richiesta a "DocumentService".
    - Ad esempio: `GET http://localhost:8081/document?id=123`.
- Il microservizio "DocumentService" elabora la richiesta, recupera il documento richiesto dal database e restituisce una risposta.

---

#### **6. Il RequestHandlerProxy restituisce la risposta al client**
- Il RequestHandlerProxy riceve la risposta dal "DocumentService".
    - Ad esempio, il contenuto del documento in formato JSON.
- Restituisce quindi questa risposta al client che aveva inviato la richiesta iniziale.

---

### **Diagramma semplificato del flusso**
```
Client --> RequestHandlerProxy --> Service Discovery (Eureka) --> DocumentService
    ^                                                                  |
    |------------------------------------------------------------------|
```

---

### **Vantaggi di questo approccio**
1. **Scalabilità**:
    - Puoi aggiungere più istanze di "DocumentService" senza modificare il RequestHandlerProxy, poiché Eureka gestisce dinamicamente la lista delle istanze.

2. **Flessibilità**:
    - Il client non ha bisogno di sapere quale microservizio elaborerà la richiesta; tutto è astratto dal proxy.

3. **Resilienza**:
    - Se una delle istanze del "DocumentService" è offline, il RequestHandlerProxy indirizzerà automaticamente le richieste a un'altra istanza attiva.

---