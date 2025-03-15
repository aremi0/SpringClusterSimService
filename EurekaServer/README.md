# EurekaServer

### **Cos'è Eureka?**
Eureka è un sistema di **Service Registry e Discovery** (registro e scoperta dei servizi) sviluppato da Netflix ed è parte della suite di strumenti **Spring Cloud Netflix**. Il suo scopo principale è quello di permettere ai microservizi di:
1. **Registrarsi** in modo dinamico presso un registro centrale (Eureka Server).
2. **Scoprirsi** tra di loro senza bisogno di conoscere i dettagli di configurazione (come indirizzi IP o porte).

In poche parole, Eureka è il cuore di un'architettura a microservizi che permette ai servizi di interagire tra loro in modo flessibile e scalabile.

---

### **Perché è utile per il tuo progetto?**
Nel tuo progetto, hai un cluster di **RequestHandlerService** che deve fungere da proxy per inoltrare le richieste verso diversi microservizi. Senza Eureka:
- Ogni istanza del `RequestHandlerService` dovrebbe conoscere manualmente gli indirizzi (IP/porta) di ciascun microservizio.
- Se un'istanza di un microservizio cambia indirizzo, fallisce o viene aggiunta una nuova istanza, sarebbe necessario aggiornare manualmente le configurazioni.

Con Eureka, invece:
1. **Dinamica e Flessibilità**: I microservizi si registrano automaticamente presso il server Eureka con i loro indirizzi e porte.
2. **Bilanciamento del Carico**: Il client (in questo caso, `RequestHandlerService`) può distribuire automaticamente il carico su tutte le istanze disponibili.
3. **Resilienza**: Se un'istanza di un microservizio diventa non disponibile, Eureka segnala la sua inattività, evitando di indirizzare richieste a quella istanza.

---

### **Come funziona Eureka in pratica?**
1. **Eureka Server**: È il registro centrale che tiene traccia di tutti i servizi registrati. Può essere paragonato a una "rubrica" che memorizza quali servizi sono disponibili e dove si trovano.
2. **Eureka Client**: Qualsiasi microservizio che si registra (es. `RequestHandlerService`) o che necessita di scoprire altri servizi (es. client che chiama i servizi) è un client di Eureka.
3. **Heartbeat**: I microservizi inviano periodicamente un "segnale" (heartbeat) al server Eureka per confermare che sono attivi e funzionanti.

---

### **Perché ti serve?**
Nella tua architettura, hai un cluster di microservizi e il `RequestHandlerService` che funge da proxy. Con Eureka:
- Puoi facilmente scalare i microservizi aggiungendo nuove istanze senza modificare configurazioni statiche.
- L'interazione tra i microservizi diventa più semplice e dinamica.
- Riduci il rischio di errori legati a configurazioni statiche (es. indirizzi hardcoded).

---

Per il tuo progetto, hai bisogno di **entrambi**, ma con ruoli differenti. Lascia che ti spieghi:

---

### **EurekaServer**
- È il **registro centrale** che tiene traccia di tutti i servizi registrati. Funziona come una rubrica.
- **Ti serve per...**:
    - Fare in modo che tutti i tuoi microservizi (es. `RequestHandlerService` e altri microservizi) possano registrarsi e scoprire altri servizi.
    - In un sistema a microservizi, un server Eureka è indispensabile per centralizzare la gestione dei servizi attivi.

Quindi, **devi configurare un EurekaServer** (come spiegato nei passi precedenti) per ospitare il registro.

---

### **EurekaClient**
- È ogni microservizio che si registra presso il server Eureka e utilizza il registro per scoprire altri servizi.
- **Ti serve per...**:
    - Registrare automaticamente i tuoi microservizi presso EurekaServer.
    - Consentire al tuo `RequestHandlerService` di scoprire i microservizi associati a ogni richiesta.
    - Implementare il bilanciamento del carico automaticamente sulle istanze attive di un microservizio.

---

### **Come si relazionano**
- Il **EurekaServer** è unico e centrale nel sistema.
- Ogni istanza di un microservizio (incluso il `RequestHandlerService`) è un **EurekaClient**.

Nel tuo caso:
1. Configura **un EurekaServer** come registro centrale.
2. Configura ogni microservizio (`RequestHandlerService` e gli altri) come **EurekaClient** per registrarsi e scoprire servizi.
