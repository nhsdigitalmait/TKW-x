#!/bin/bash
#
# invoke a different TKW entry point for swapping between json and xml formats
# fhirformatswap [-(2|3|4)] (<file> | - )
#
if [[ "$TKWROOT" == "" ]]
then
	echo "TKWROOT environment variable is not set"
	exit 1
fi

if [[ ! -e "$TKWROOT" ]]
then
	echo "TKWROOT environment variable ($TKWROOT) does not point to a valid folder"
	exit 1
fi

if [[ "$1" =~ ^-.+$ ]]
then
	OPTIONS=$1
	shift
fi

if [[ "$1" == "" || "$2" != "" || ! "$OPTIONS" =~ ^(-(2|3|4))?$ ]]
then
	echo 'usage : '`basename $0` '[-(2|3|4)] (<file> | - )'
	exit
fi

FILE=$1

if [[ "$FILE" != "-" && ! -e $FILE ]]
then
	echo "Input file $FILE does not exist"
	exit
fi

java -cp $TKWROOT/TKW-x.jar uk.nhs.digital.mait.tkwx.tk.boot.FhirFormatSwap $OPTIONS $FILE
