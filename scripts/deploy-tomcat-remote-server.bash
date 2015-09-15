#!/bin/bash

echo "Deploying to Tomcat and restarting ... (requires sudo)"

sudo rm /opt/tomcat-base/webapps/semvis.war
sudo rm -R /opt/tomcat-base/webapps/semvis

sudo cp -pr ../rvl-server/target/semvis.war /opt/tomcat-base/webapps

sudo service tomcat8 restart
