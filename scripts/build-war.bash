#!/bin/bash

echo "Building war without deploying to Tomcat ... "

cd ../rvl-server
mvn war:war -DskipTests -U