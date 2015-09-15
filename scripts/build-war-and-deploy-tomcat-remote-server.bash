#!/bin/bash

echo "Building war and deploying to Tomcat (without restarting, this may require more rights) ... "

cd ../rvl-server
mvn war:war -DskipTests -U

sudo rm /opt/tomcat-base/webapps/semvis.war
sudo rm -R /opt/tomcat-base/webapps/semvis

sudo cp -pr target/semvis.war /opt/tomcat-base/webapps

# may require more rights
# sudo service tomcat8 restart

cd ..
