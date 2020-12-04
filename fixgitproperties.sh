#!/bin/bash
#
# Fix issue with maven assembly plugin setting the wrong git.properties file into the jar
#

GITPROPS=git.properties

JAR=target/TKW-x-maven.jar

# delete the current file
zip -d $JAR $GITPROPS

# re add the correct file
jar uvf $JAR -C target/classes $GITPROPS

# copy to TKWROOT
cp -f $JAR $TKWROOT/TKW-x.jar
