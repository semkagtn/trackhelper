#!/bin/sh

set -u -e

JAR=trackhelper-1.0-SNAPSHOT-jar-with-dependencies.jar
CONF=.trackhelper.conf
TAG=$1

git checkout tags/$TAG
mvn clean package
mv target/$JAR .
mv $JAR trackhelper.jar

if [ ! -f ~/$CONF ]; then
  cp src/main/resources/$CONF ~
fi
git checkout master
