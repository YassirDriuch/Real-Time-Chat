# chat-service

Een real-time chatapplicatie gebouwd met WebSocket en STOMP. Gebruikers kunnen zich aanmelden, openbare berichten sturen en privebeberichten uitwisselen. Verbindingen worden beheerd via STOMP over SockJS.

## Functionaliteit

- Verbinding maken via WebSocket op /ws
- Openbare berichten sturen naar alle verbonden gebruikers
- Privebeberichten sturen naar een specifieke gebruiker
- Geschiedenis van openbare en privebeberichten opvragen via REST

## Technologie

- Java, Spring Boot
- WebSocket met STOMP en SockJS
- Berichtenmakelaar: eenvoudige in-memory broker (/topic, /queue)
- Berichten worden in geheugen opgeslagen (geen database)
- Gradle

## Berichtenkanalen

| Bestemming | Richting | Omschrijving |
|---|---|---|
| /app/chat.registerUser | Client naar server | Gebruiker aanmelden |
| /app/chat.sendPublic | Client naar server | Openbaar bericht sturen |
| /app/chat.sendPrivate | Client naar server | Prive bericht sturen |
| /topic/public | Server naar clients | Openbare berichten ontvangen |
| /user/queue/private | Server naar gebruiker | Prive berichten ontvangen |

## REST endpoints

| Methode | Endpoint | Omschrijving |
|---|---|---|
| GET | /messages/public | Alle openbare berichten ophalen |
| GET | /messages/private?with={id} | Prive conversatie ophalen |