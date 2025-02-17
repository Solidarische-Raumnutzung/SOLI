# solidarische raumnutzung
- [Pflichtenheft](https://solidarische-raumnutzung.github.io/SOLI/pflichtenheft.pdf)
- [Entwurfsheft](https://solidarische-raumnutzung.github.io/SOLI/entwurfsheft.pdf)
- [Implementationsbericht](https://solidarische-raumnutzung.github.io/SOLI/implementationsbericht.pdf)
- [Beispielpräsentation](https://solidarische-raumnutzung.github.io/SOLI/presentation.pdf)
- [Andere Artefakte](https://solidarische-raumnutzung.github.io/SOLI/)
- [Deployment](https://cc415dc2-136a-4cfd-adc9-45a126ee849e.ka.bw-cloud-instance.org/)

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
