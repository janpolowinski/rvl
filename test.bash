#!/bin/bash

echo "Run core tests ... "

cd "org.purl.rvl.tooling.process"
mvn install -DskipTests
mvn test
cd ../
