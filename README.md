# Spring Cluster Simulation
Architettura software a microservizio distribuita.  

## Flusso
Client -> (Cluster) RequestHandlerProxy -> {AuthenticationMicroservice, ...}

## Monitoraggio
- EurekaServer: http://localhost:8761
- Health: http://localhost:<port>/actuator/health

# Ambiente dockerizzato
il FE comunicherà con l'unica istanza di RequestHandlerProxy, il quale utilizzerà un balancedRestTemplate per inoltrare 
la richiesta al microservizio richiesto più scarico (grazie a EurekaServer)  
Il RequestHandlerProxy sarà esposto su http://localhost:8080