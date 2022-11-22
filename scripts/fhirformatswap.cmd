@echo off
rem 
rem  invoke a different TKW entry point for swapping between json and xml formats
rem  fhirformatswap [-(2|3|4)] (<file> | - )
rem 
setlocal enabledelayedexpansion
set OP=%1
if "%OP:~0,1%" == "-" (
	set  OPTIONS=%1
	if "!OPTIONS!" == "-2" goto :ok
	if "!OPTIONS!" == "-3" goto :ok
	if "!OPTIONS!" == "-4" goto :ok
	if "!OPTIONS!" == "-" (
		set OPTIONS=
		goto :noshift
	)
:usage
echo usage : fhirformatswap.cmd [-(2^|3^|4^)] ^(^<file^> ^| - ^)
exit /B

:ok
	shift
)

:noshift
if  "%1" == "" (
	goto :usage
)

set FILE=%1

if not "%FILE%" == "-" (
	if not exist %FILE% (
		echo Input file %FILE% does not exist
		exit /B
	)
)

java -cp %TKWROOT%/TKW-x.jar uk.nhs.digital.mait.tkwx.tk.boot.FhirFormatSwap !OPTIONS! %FILE%
