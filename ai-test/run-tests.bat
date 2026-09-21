@echo off
setlocal
cd /d "%~dp0"
set PYTHONUTF8=1
where py >nul 2>nul
if %errorlevel% equ 0 (
    py -3 run_tests.py %*
) else (
    python run_tests.py %*
)
set "TEST_EXIT=%errorlevel%"
echo.
echo Test exit code: %TEST_EXIT%
pause
exit /b %TEST_EXIT%
