#!/bin/bash

cd ../org.purl.rvl.vocabulary
mvn clean install -U -DskipTests
cd ../org.purl.rvl.tooling.codegen.rdfreactor
mvn clean install -U -DskipTests
cd ../org.purl.rvl.tooling.commons
mvn clean install -U -DskipTests
cd ../org.purl.rvl.tooling.interpreter
mvn clean install -U -DskipTests
cd ../org.purl.rvl.tooling.d3vis
mvn clean install -U -DskipTests
cd ../org.purl.rvl.tooling.process
mvn clean install -U -DskipTests
cd ../org.purl.rvl.example
mvn clean install -U -DskipTests
cd ../org.purl.rvl.server
mvn clean install -U -DskipTests
