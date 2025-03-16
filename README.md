# Spring Cluster Simulation
Architettura software a microservizio a scopo didattica.  

## Tecnologie utilizzate:
- **Hibernate**, **JPA**, **SQ**L: Registrazione e login degli utenti.
- **JWT**, **AES Encryption**, **BCrypt Hashing**: Cifratura dei dati sensibili degli utenti e gestione della sessione con JWT.
- **Docker**, **Spring Cloud**, **Spring Eureka**: Distribuzione in subnet docket con Load Balancing del carico tramite Spring Cloud e Discovery dinamico dei nodi tramite Eureka
- **WebClient**, **RabbitMQ**: WebClient per la comunicazione bloccante tra i servizi dell'architettura, RabbitMQ per la comunicazione asincrona.
- **Spring Cache**, **Redis, AOP**: Implementazione di un layer di cache per migliorare lettura di documenti pesanti con integrazione della Aspect Oriented Programming per tenere traccia di cache-hit e cache-miss.

## Flusso di esecuzione
Client -> RequestHandlerProxy -> Cluster di microservizi {AuthenticationMicroservice, DocumentLoaderMicroservice, ...}  

I client si interfacciano con un Proxy che, effettuati i dovuti controlli, indirizzerà il traffico verso il microservizio adibito più scarico.  
Il RequestHandlerProxy sarà esposto su http://localhost:8080

### API
**POST** http://localhost:8080/api/auth/register con RequestBody JSON
```json
  {
  "username": "PvVnx6o8lafNpY0dfP+Jyw==",
  "password": "3vC5sJjF5DyGZIrR5HEykw=="
  }
```
#### Note
Username e Password devono essere inviati verso il servizio già cifrati con la stessa chiave usata dallo stesso.
Nel microservizio "AuthentiocationMicroservice" -> package "util" -> classe "DecryptUtil" è presente un main in cui si può cifrare username,password.  

I dati vengono salvati a DB nel seguente modo:
- username: viene decifrato da AES in chiaro e salvato in chiaro.
- password: viene decifrata da AES in chiaro e poi da in chiaro viene hashata on sale tramite BCrypt, poi salvata hashata.

---

**POST** http://localhost:8080/api/auth/login con RequestBody JSON
```json
  {
  "username": "PvVnx6o8lafNpY0dfP+Jyw==",
  "password": "3vC5sJjF5DyGZIrR5HEykw=="
  }
```
#### Note
Username e Password devono essere inviati verso il servizio già cifrati con la stessa chiave usata dallo stesso.
Nel microservizio "AuthentiocationMicroservice", package "util", classe "Decryptutil" è presente un main in cui si può cifrare qualsiasi stringa.

In caso di autenticazione avvenuta con successo ritorna un JWT, inoltre verrà inviato un messaggio ad una coda RabbitMQ che provocherà
il precaricamento in una cache condivisa tra i microservizi delle prime pagine di un documento associato all'utenza {idUser}.
Se l'utente successivamente al login debba richiedere il documento allora avverrà un cache-hit con un tempo di attesa del documento nullo.

---

**GET** http://localhost:8080/api/documents/first-pages/{userId}
#### Note
Ottiene le prime pagine del documento associato all'utente cercandole prima in cache e nel caso di cache-miss sul disco.

---

## Monitoraggio
- Dashboard di Eureka: http://localhost:8761
- Health: http://localhost:<port>/actuator/health
- RabbitMQ Managment: http://localhost:15672
  - username:guest, pwd:guest
