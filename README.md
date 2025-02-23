# Simulatore Spring Cluster per fini formativi
Servizio che espone una unica API con tempo di attesa randomico.

## Obiettivi
1. Clusterizzazione di un servizio Spring tramite Docker Container (docker-compose).
2. Load balancing sul cluster tramite Nginx (con futuro upgrade verso Spring Cloug Loadbalancer).
3. Logging avanzato tramite container Kafka.
4. Consumo dei messaggi (log) Kafka tramite Kibana. (Pacchetto ELK)

## Users
- Registrazione:
  1. Utente invia form di registrazione; {username, password} cifrati con algoritmo AES.
  2. Servizio decifra i dati.
  3. Controlla il DB e se tutto è nella norma procede con il salvataggio della nuova tupla {username in chiaro, password hashata BCrypt}.

### Database
- mysql alla porta 3306
- phpMyAdmin su http://localhost:80/