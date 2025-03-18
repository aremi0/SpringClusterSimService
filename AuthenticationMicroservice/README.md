# AuthenticationMicroservice

## Monitoring
1. **`spring-boot-starter-actuator`**:
    - Abilita gli endpoint di monitoraggio e gestione di Spring Boot.
    - Fornisce l'endpoint `/actuator/prometheus`, che espone le metriche in formato Prometheus.

2. **`micrometer-registry-prometheus`**:
    - Configura Spring Actuator per esportare le metriche in un formato compatibile con Prometheus.
    - Permette a Spring di raccogliere le metriche interne della tua applicazione (es. richieste HTTP, utilizzo della memoria, thread, ecc.) e di formattarle in modo che Prometheus possa leggerle.

**Quindi**, queste dipendenze preparano la tua applicazione per essere "letta" da Prometheus, ma **non includono Prometheus stesso**, perché questo è uno strumento esterno necessario per raccogliere, archiviare e analizzare le metriche.

---

### **Perché Hai Bisogno di Prometheus**
Prometheus è il sistema che:
1. **Raccoglie le metriche** esposte dalla tua applicazione (tramite `/actuator/prometheus`).
2. **Archivia le metriche** in un database temporale.
3. **Permette query** avanzate sulle metriche raccolte (ad esempio, quante richieste HTTP sono state fatte negli ultimi 5 minuti).
4. **Integrazione con Grafana**: Consente a Grafana di visualizzare queste metriche sotto forma di grafici.

**In sintesi**: Senza Prometheus, puoi vedere le metriche esportate dalla tua applicazione accedendo manualmente a **`/actuator/prometheus`**, ma non avrai uno strumento per raccoglierle, storicizzarle o analizzarle.

---

### **Un'Analogia**
Pensa alla tua applicazione come una stazione meteo che fornisce dati (tramite `/actuator/prometheus`). Le dipendenze di Spring Boot sono il meccanismo che crea questi dati leggibili, ma Prometheus è il "database" che raccoglie e memorizza questi dati, mentre Grafana è lo "schermo" che ti mostra i grafici delle condizioni meteo.

---

### **Cosa Puoi Fare Senza Prometheus**
Se non vuoi usare Prometheus, puoi comunque vedere le metriche direttamente dall'endpoint della tua applicazione:
1. Accedi a **`http://localhost:8080/actuator/prometheus`**.
2. Visualizza manualmente le metriche esportate in formato Prometheus.

Questo approccio funziona, ma non è adatto per analisi complesse o storicizzazione dei dati.

---

Se vuoi, posso aiutarti a configurare Prometheus e Grafana per sfruttare al massimo queste metriche. Dimmi come procediamo! 😊