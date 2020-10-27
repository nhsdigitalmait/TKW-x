#!/bin/bash
#
# Fix issue with maven assembly plugin setting the wrong git.properties file into the jar
#

GITPROPS=git.properties

JAR=target/TKW-x-maven.jar

zip -d $JAR $GITPROPS
jar uvf $JAR -C target/classes $GITPROPS

cp -f $JAR $TKWROOT/TKW-x.jar
