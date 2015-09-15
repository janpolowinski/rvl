#!/bin/bash

echo "Deploying to Tomcat and restarting ... (requires sudo)"

sudo rm /opt/tomcat-base/webapps/semvis.war
sudo rm -R /opt/tomcat-base/webapps/semvis

cd target
sudo cp -pr semvis.war /opt/tomcat-base/webapps

cd ../../scripts

sudo service tomcat8 restart
