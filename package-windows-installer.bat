@echo off
setlocal
cd /d %~dp0

set APP_JAR=dist\field-ledger-win7.jar
set JRE_DIR=package\jre
set ISCC_EXE=

if not exist "%APP_JAR%" (
  echo Missing %APP_JAR%.
  echo Please run build-windows.bat first.
  pause
  exit /b 1
)

if not exist "%JRE_DIR%\bin\javaw.exe" (
  echo Missing bundled Java runtime: %JRE_DIR%\bin\javaw.exe
  echo Put a Windows x64 Java 8 JRE into package\jre first.
  pause
  exit /b 1
)

if exist "%ProgramFiles(x86)%\Inno Setup 6\ISCC.exe" set ISCC_EXE=%ProgramFiles(x86)%\Inno Setup 6\ISCC.exe
if exist "%ProgramFiles%\Inno Setup 6\ISCC.exe" set ISCC_EXE=%ProgramFiles%\Inno Setup 6\ISCC.exe

if "%ISCC_EXE%"=="" (
  echo Inno Setup 6 was not found.
  echo Please install Inno Setup 6 on the packaging computer.
  pause
  exit /b 1
)

echo Creating Windows installer...
"%ISCC_EXE%" installer\FieldLedgerWin7.iss
if errorlevel 1 (
  echo Installer build failed.
  pause
  exit /b 1
)

echo Done. Installer is in package\output
pause
