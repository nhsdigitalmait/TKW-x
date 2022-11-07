#!/bin/bash
# usage fixtkwroot.sh [-u] [target]
# default target is $TKWROOT
# find unexpanded TKW_ROOT and fix and vice versa
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

if [[ "$1" == "" ]]
then
	target=$TKWROOT
else
	target=$1
fi

if [[ "$opt" == "-u" ]]
then
	# unfix ie convert from install path to substitution tag
	find $target -type f \
		-not -name '*.html' \
		-not -name '*.log' \
		-not -name '*.sh' \
		-not -name 'COMMIT_EDITMSG' \
		-not -name 'HEAD' \
		-not -name 'master' \
		-not -name '*.yml' \
		-not -name 'Dockerfile*' \
		-not -name '*.jar' \
		-exec grep -l $TKWROOT {} \; -exec tkwsubst.sh $opt {} \;
else
	# convert from substitution tag to install path
	find $target -type f \
		-not -name '*.html' \
		-not -name '*.log' \
		-not -name '*.sh' \
		-not -name 'COMMIT_EDITMSG' \
		-not -name 'HEAD' \
		-not -name 'master' \
		-not -name '*.yml' \
		-not -name 'Dockerfile*' \
		-not -name '*.jar' \
		-exec grep -l TKW_ROOT {} \; -exec tkwsubst.sh $opt {} \;
fi
