#!/bin/bash

echo "Building and Deploying to Tomcat ... "

./install.bash

cd ../org.purl.rvl.server
mvn package -DskipTests

rm /Library/Tomcat/webapps/semvis.war
rm -R /Library/Tomcat/webapps/semvis

cd target
cp -pr semvis.war /Library/Tomcat/webapps 

cd ../../scripts

./tomcat_restart.bash


