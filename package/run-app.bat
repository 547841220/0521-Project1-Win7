@echo off
setlocal
cd /d %~dp0

jre\bin\javaw.exe -jar app\field-ledger-win7.jar
