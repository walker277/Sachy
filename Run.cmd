@echo off
call Build.cmd
if errorlevel 1 (
    echo Kompilace selhala, program nebude spusten.
    pause
    exit /b 1
)

call Makedoc.cmd
if errorlevel 1 (
    echo Generovani dokumentace selhalo, pokracujeme ve spusteni programu.
)

java -cp bin Main %*
pause


