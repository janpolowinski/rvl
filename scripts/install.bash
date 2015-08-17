#!/bin/bash

export MAVEN_OPTS="-Xmx2048M"

cd ..

# only required on RVL or VISO changes:

cd rvl-vocabulary
mvn clean install -o -DskipTests

cd ..

cd codegen.rdfreactor
mvn clean install -o -DskipTests

cd ..

cd rvl-gen-api
mvn clean install -o -DskipTests

cd ..

# always required

mvn clean install -o -DskipTests
