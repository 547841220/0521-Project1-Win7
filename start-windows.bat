@echo off
setlocal
cd /d %~dp0

if exist dist\field-ledger-win7.jar (
  java -jar dist\field-ledger-win7.jar
) else (
  java -cp build com.aimoney.fieldledger.win7.FieldLedgerWin7App
)

if errorlevel 1 pause
