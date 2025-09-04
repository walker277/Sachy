@echo off
if not exist bin mkdir bin
javac -cp src -encoding UTF-8 -d bin src\*.java
pause

