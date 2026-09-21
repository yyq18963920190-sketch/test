@echo off
setlocal
cd /d "%~dp0"
set PYTHONUTF8=1
where py >nul 2>nul
if %errorlevel% equ 0 (
    py -3 project.py test
) else (
    python project.py test
)
set "RESULT=%errorlevel%"
echo Test exit code: %RESULT%
pause
exit /b %RESULT%
