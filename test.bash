#!/bin/bash

cd "org.purl.rvl.example"
mvn install -DskipTests
mvn test
cd ../
