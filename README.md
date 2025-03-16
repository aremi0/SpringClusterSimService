# Spring Cluster Simulation
Architettura software a microservizio distribuita.  

## Flusso
Client -> (Cluster) RequestHandlerProxy -> {AuthenticationMicroservice, ...}

## Monitoraggio
- EurekaServer: http://localhost:8761
- Health: http://localhost:<port>/actuator/health

# Ambiente dockerizzato
Il FE comunicherà con l'unica istanza di RequestHandlerProxy, il quale utilizzerà un balancedRestTemplate per inoltrare 
la richiesta al microservizio richiesto più scarico (grazie a EurekaServer)  
Il RequestHandlerProxy sarà esposto su http://localhost:8080

## Login Event
Al login, se concluso con successo, il microservizio di Autenticatione invierà un messaggio ad una apposita coda RabbitMQ
includendo l'ID dell'utente. Il microservizio DocumentLoader iscritto alla coda, alla ricezione del messaggio, caricherà in RAM
la prima pagina di un documento (salvato sul suo disco) a cui ha accesso l'utente autenticato, cachandolo in attesa che venga richiesto.
