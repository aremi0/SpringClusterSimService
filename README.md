# Spring Cluster Simulation
Architettura software a microservizio distribuita.  

## Flusso
Client -> (Cluster) RequestHandlerProxy -> {AuthenticationMicroservice, ...}

## Monitoraggio
- EurekaServer: http://localhost:8761
- Health: http://localhost:<port>/actuator/health