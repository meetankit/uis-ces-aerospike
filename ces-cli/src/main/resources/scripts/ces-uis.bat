@echo off

setlocal

REM path to the java interpreter
set JAVA=java

REM end configurables
REM
REM ===================================================================
REM
REM calculate true location

pushd %~dp0
set script_dir=%CD%
cd ..

set CES_HOME=%CD%

REM --------------------------------------------------------------------

%JAVA% -cp %CES_HOME%/ces.jar com.adobe.dx.aep.skaluskar.poc.cesuis.cesuiscli.CesUISCli %*
