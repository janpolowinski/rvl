#!/bin/bash

echo "Run core tests ... "

cd "org.purl.rvl.tooling.rvl-interpreter"
mvn install -DskipTests
mvn test
cd ../
