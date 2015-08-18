#!/bin/bash

export MAVEN_OPTS="-Xmx2048M"

cd ..

# always required

mvn clean install -o -DskipTests

cd ..