@echo off
setlocal
cd /d "%~dp0"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0build_on_windows.ps1"
if errorlevel 1 (
  echo.
  echo BUILD FAILED - Read the messages above or attach build_errors.log.
  pause
  exit /b 1
)
echo.
echo BUILD SUCCESS - See dist\HaloFriends-1.20.1-forge-0.5.0.jar
pause
