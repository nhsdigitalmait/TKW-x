#!/bin/bash
#  substitute the tag for the real path from the env var for tkw config files
# used by fixtkwroot.sh
# usage tkwsubst.sh [-u] <filename>+
#
# converts from TKW_ROOT to $TKWROOT
# -u converts from $TKWROOT to TKW_ROOT
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

if [[ "$1" == "-u" ]]
then
	opt=$1
	shift 
fi

for f in $*
do
	if [[ "$opt" == "-u" ]]
	then
		# from installed to template
		sed -i $f -e s@$TKWROOT@TKW_ROOT@g
	else
		# from template to installed
		sed -i $f -e s@TKW_ROOT@$TKWROOT@g
	fi
done 
