# Solidarische Raumnutzung
- [Pflichtenheft](https://solidarische-raumnutzung.github.io/SOLI/pflichtenheft.pdf)
- [Entwurfsheft](https://solidarische-raumnutzung.github.io/SOLI/entwurfsheft.pdf)
- [Implementationsbericht](https://solidarische-raumnutzung.github.io/SOLI/implementationsbericht.pdf)
- [Testbericht](https://solidarische-raumnutzung.github.io/SOLI/testbericht.pdf)
- [Andere Artefakte](https://solidarische-raumnutzung.github.io/SOLI/)
- [Deployment](https://cc415dc2-136a-4cfd-adc9-45a126ee849e.ka.bw-cloud-instance.org/)

# Hintergrund
SOLI ist ein System zum Reservieren von Räumen.
Es ermöglicht die Priorisierung von Terminen, wobei deren Dringlichkeit hervorgehoben wird.
Entwickelt wurde es im Auftrag der Forschungsgruppe [HCI (Mensch-Maschine-Interaktion und Barrierefreiheit)](https://hci.iar.kit.edu/),
im Rahmen des Moduls PSE (Praxis der Softwareentwicklung) für den Informatik Bachelor of Science of des [KIT](https://kit.edu).

# Installation
Zur Installation des Projekts wird Docker benötigt.
Wenn Docker installiert ist, kann die Konfiguration aus dem Verzeichnis [server_deploy_config](./server_deploy_config) verwendet werden.
Die Umgebungsvariablen in der Datei `docker-compose.yml` müssen angepasst werden, um die Anwendung zu konfigurieren.
Außerdem sollte die Datei `Caddyfile` angepasst werden, um die Domain zu konfigurieren.
Danach kann das Projekt mit `docker-compose up` gestartet werden.
(Dieser Befehlt muss in dem erwähnten Verzeichnis ausgeführt werden.)

# Entwicklung
Details zur Entwicklung sind in der Datei [CONTRIBUTING.md](./CONTRIBUTING.md) zu finden.
Die Architektur ist in den oben verlinkten Dokumenten beschrieben.
