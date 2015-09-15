#!/bin/bash

echo "Building war and deploying to Tomcat ... "

cd ../rvl-server
mvn war:war -DskipTests -U

rm /opt/tomcat-base/webapps/semvis.war
rm -R /opt/tomcat-base/webapps/semvis

cd target
cp -pr semvis.war /opt/tomcat-base/webapps

cd ../../scripts

#sudo service tomcat8 restart


