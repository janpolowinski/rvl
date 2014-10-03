#!/bin/bash

cd "org.purl.rvl.tooling.codegen.rdfreactor"
mvn install
cd ../
cd "org.purl.rvl.tooling.rvl-interpreter"
mvn install -DskipTests
cd ../
cd "org.purl.rvl.vocabulary"
mvn install -DskipTests
cd ../
cd "org.purl.rvl.tooling.d3vis"
mvn install -DskipTests
cd ../
