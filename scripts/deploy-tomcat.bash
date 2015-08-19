#!/bin/bash

echo "Building war and deploying to Tomcat ... "

cd ../rvl-server
mvn war:war -DskipTests -o

rm /Library/Tomcat/webapps/semvis.war
rm -R /Library/Tomcat/webapps/semvis

cd target
cp -pr semvis.war /Library/Tomcat/webapps 

cd ../../scripts

./tomcat_restart.bash


