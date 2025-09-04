@echo off
if not exist doc\javadoc mkdir doc\javadoc
javadoc -encoding UTF-8 -sourcepath src -cp src -d doc\javadoc -version -author src\*.java
pause

