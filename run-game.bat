@echo off
cd /d "%~dp0"
java -Dfile.encoding=UTF-8 -jar dist\deep-sea-growth-ai.jar
if errorlevel 1 pause
