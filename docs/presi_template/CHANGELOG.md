# Changelog
Alle Änderungen an diesem Projekt werden in dieser Datei dokumentiert.
Die Versionsnummern folgt der Syntax in `sdqbeamer.cls`.

## [2022-05-03 v3.1.3]
- Die Breite des Gruppennamens in der Fußzeile kann nun über `\groupnamewidth{}` gesteuert werden
- FIX: zweizeilige Fußzeilen haben nun gleichmäßigen vertikalen Abstand (Issue #16 in Gitlab)

## [2021-08-10 v3.1.2]
- FIX: framesubtitle wird nun angezeigt (Issue #6 in Gitlab)

## [2020-12-08 v3.1.1]
- FIX: Titelbild (Issue #4 in Gitlab)

## [2020-12-07 v3.1]
- Umgebung ``contentblock`` (farbloser Block mit fetter Überschrift) hinzugefügt
- Farbboxen (``greenblock``, ``blueblock``, …) hinzugefügt
- Abstufungen der KIT-Farben in 10er-Schritten entsprechend der Gestaltungsrichtlinien eingeführt
- FIX: Navigationspunkte für Subsections in eine Zeile gesetzt, um vertikal Platz zu sparen
- FIX: ``inputenc`` an den Anfang von ``sdqbeamer.cls`` verschoben

## [2020-11-16 v3.0]
- Seitenformat 16:10 hinzugefügt
- Umstellung auf KIT-Design vom 1. August 2020
    - Anpassung auf neues Farbschema und Maße
    - neues Titelbild aus der KIT-Bildwelt
- Neue Optionen:
    - durch `smallfoot` und `bigfoot` kann die Schriftgröße der Fußzeile gesteuert werden
    - durch `navbarkit` kann eine Fußzeile nach KIT-Vorgaben erzwungen werden
- Deutsch (`de`) ist nun die Standard-Option
- Ordner `templates` wurde gelöscht und die Inhalte in `sdqbeamer.cls` integriert
- Globale Größe auf 10 pt verringert (vorher: 11 pt), da der beschreibbare Bereich im Vergleich zur 2009er Version kleiner geworden ist
- SDQ-spezifische Logos und Titelbilder entfernt. Diese sind ab sofort im Branch »sdq« verfügbar.
- Fix: Zeilenumbruch bei Titel in der Fußzeile repariert