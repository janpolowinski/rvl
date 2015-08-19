#!/bin/bash

export MAVEN_OPTS="-Xmx2048M"

cd ..

# only required on RVL or VISO changes:

cd rvl-vocabulary
mvn clean install -o -DskipTests

cd ..

cd rvl-codegen
mvn clean install -o -DskipTests

cd ..

cd rvl-gen-api
mvn clean install -o -DskipTests

cd ..