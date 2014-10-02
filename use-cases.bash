#!/bin/bash

echo "Run use case tests ... "

cd "org.purl.rvl.example"
mvn install -DskipTests
mvn test
cd ../