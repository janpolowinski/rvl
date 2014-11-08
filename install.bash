#!/bin/bash

cd org.purl.rvl.vocabulary
mvn install -DskipTests
cd ../org.purl.rvl.tooling.commons
mvn install -DskipTests
cd ../org.purl.rvl.tooling.codegen.rdfreactor
mvn install -DskipTests
cd ../org.purl.rvl.tooling.interpreter
mvn install -DskipTests
cd ../org.purl.rvl.tooling.d3vis
mvn install -DskipTests
cd ../org.purl.rvl.tooling.process
mvn install -DskipTests
cd ../
