# Simulatore Spring Cluster per fini formativi
Servizio che espone una unica API con tempo di attesa randomico.

## Obiettivi:
1. Clusterizzazione di un servizio Spring tramite Docker Container (docker-compose).
2. Load balancing sul cluster tramite Nginx (con futuro upgrade verso Spring Cloug Loadbalancer).
3. Logging avanzato tramite container Kafka.
4. Consumo dei messaggi (log) Kafka tramite Kibana. (Pacchetto ELK)
