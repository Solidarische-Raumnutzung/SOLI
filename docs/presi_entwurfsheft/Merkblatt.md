# Architektur

- Architektur ist in *Frontend* und *Backend* unterteilt
- Folgt der *MVC-Struktur* zur klaren Trennung der Verantwortlichkeitsbereiche
- *Frontend* nutzt:
    - *HTML*
    - *DaisyUI* für das Design
    - *FullCalendar* in *JavaScript* für die Kalenderfunktionalität
- Kommunikation mit dem *Backend* erfolgt über *HTTPS*
- *Caddy*-Server läuft in einem eigenen *Container* und dient als *HTTPS-Reverse-Proxy*
- *Backend* basiert auf *Spring Boot* (Java 21) und läuft in einem weiteren *Container*
    - *Authentication Filter* prüft die Anfragen
    - Anfragen werden von einem *Controller* verarbeitet
    - Dynamische HTML-Antworten werden mithilfe von *JTE*-Templates generiert
- *Businesslogik* liegt in den *Services*, die Anfragen koordinieren und weiterleiten
- *Repositories* kapseln den SQL-Zugriff und ermöglichen eine saubere, abstrahierte Kommunikation mit der *PostgreSQL*-Datenbank in einem separaten *Container*
- Einsatz von *Docker* sorgt für eine einfache Bereitstellung und Wartbarkeit der Anwendung

# Controller

## Beispiel: BookingViewController
- **GET /{roomId}/bookings**
  - Zeigt die Terminübersicht des angemeldeten Nutzers für den gewählten Raum an.
  - Der `roomId`-Parameter wird aus der URL entnommen und verwendet, um Daten aus dem Service-Layer zu beziehen.
  - Die Daten werden an ein JTE-Template weitergegeben und im Frontend angezeigt.
- **GET /{roomId}/bookings/{eventId}**
  - Zeigt die Detailansicht eines spezifischen Termins.
  - Berechtigungsprüfung: Der Löschbutton ist nur für den Admin oder den Ersteller sichtbar.
  - Die `eventId`-Information wird verwendet, um den Termin aus der Datenbank zu laden.
- **DELETE /{roomId}/bookings/{eventId}**
  - Löscht den angegebenen Termin und leitet den Nutzer danach zur Terminübersicht weiter.
  - Hier erfolgt ein direkter Zugriff auf den Service-Layer, der die Löschlogik kapselt.

### **Projektstruktur:**
- **RESTful Design:**
  - `GET` für das Abrufen von Daten, `DELETE` für Löschaktionen.
- **Konsistente URL-Struktur:**
  - Einheitlicher Aufbau aller Endpunkte erleichtert die Wartung und Erweiterung.  

