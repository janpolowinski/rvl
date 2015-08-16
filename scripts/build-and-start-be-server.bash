#!/bin/bash

export MAVEN_OPTS="-Xmx2048M"

cd ..

mvn clean install -DskipTests
#mvn clean install -U -DskipTests

cd scripts

./start-be-server.bash
