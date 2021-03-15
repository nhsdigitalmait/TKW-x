#!/bin/bash
#The argument is to label the container. If non is given then a date will be used
label=$1
if [ $# -eq 0 ]
  then
    echo "No arguments supplied - short rev number will be used for label"
	label=`git rev-parse --short master` 
fi

#Copy across the TKW-x build jar	
cp target/tkw-x* TKW-x.jar

#Build the docker image
docker build -t tkw-x .
#Tag the docker image with today's date or provided label
docker tag tkw-x nhsdigitalmait/tkw-x:$label
echo Docker Image tagged with $label
