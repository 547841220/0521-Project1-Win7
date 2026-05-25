@echo off
setlocal
cd /d %~dp0

if not exist build mkdir build
if not exist dist mkdir dist

echo Compiling Java 8 Swing app...
javac -encoding UTF-8 -source 1.8 -target 1.8 -d build src\com\aimoney\fieldledger\win7\*.java
if errorlevel 1 (
  echo Build failed.
  pause
  exit /b 1
)

echo Creating jar...
jar cfe dist\field-ledger-win7.jar com.aimoney.fieldledger.win7.FieldLedgerWin7App -C build .
if errorlevel 1 (
  echo Jar failed.
  pause
  exit /b 1
)

echo Done: dist\field-ledger-win7.jar
pause
