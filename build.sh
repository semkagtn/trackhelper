#!/bin/sh

set -u -e

JAR=trackhelper-1.0-SNAPSHOT-jar-with-dependencies.jar
CONF=.trackhelper.conf

mvn clean package
mv target/$JAR .
mv $JAR trackhelper.jar

if [ ! -f ~/$CONF ]; then
  cp src/main/resources/$CONF ~
fi

