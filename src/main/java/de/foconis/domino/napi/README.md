de.foconis.domino.napi
======================
Dieses Paket enthält die __API__ aber keine Implementierung zum Zugriff auf die Low-Level-NAPI-Funktionen.
Unterschieden wird hierbei zwischen __Napi__ und __NapiEx__.
Funktionen aus der __Napi__ können mit Domino-Bordmitteln realisiert werden.
Für Funktionen aus der __NapiEx__ muss man direkt auf die DLL Datei zugreifen.

de.foconis.domino.napi.types
============================
Typen, die verwendet werden

de.foconis.domino.napi.c
========================
Dieses Paket enthält die __Napi__ Implementierung.
Hierzu werden die Domino-NAPI-Klassen verwendet, welche auf allen Plattformen zur Verfügung stehen sollten.

Funktionen aus der __NapiEx__ können 

de.foconis.domino.napi.jna
==========================
Dieses Paket enthält die __NapiEx__ Implementierung.
Um auf die DLL zuzugreifen wird JNA (Java Native Access: https://github.com/java-native-access/jna)
was zumindest etwas Platformunabhängigkeit bietet.

Allerdings werden aktuell von JNA nur folgende Plattformen unterstützt
* freebsd-x86
* freebsd-x86-65
* linux-arm
* linux-x86
* linux-x86-64
* openbsd-x86
* openbsd-x86-64
* sunos-sparc
* sunos-sparcv9
* sunos-x86
* sunos-x64
* w32ce-arm
* win32-x86
* win32-x86-64

Auf Platformen die von JNA nicht unterstützt werden (z.B. AS400) können aktuell keine __NapiEx__ Funtionen aufgerufen werden.

Ausblick
========
Man kann durch die Modularisierung ein weiteres "de.foconis.domino.napi.as400" Paket erstellen, dass z.B. über eine
eigens compilierte Library auf die Notes-Funktionen zugreift. Hierzu wird allerdings eine Entwicklungsumgebung benötigt!