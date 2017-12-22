#!/bin/bash

echo "Building war and deploying to Tomcat (without restarting, this may require more rights) ... "

cd ../rvl-server
mvn war:war -DskipTests -U

rm /opt/tomcat-base/webapps/semvis.war
rm -R /opt/tomcat-base/webapps/semvis

cp -pr target/semvis.war /opt/tomcat-base/webapps

# may require more rights
# sudo service tomcat8 restart

cd ..
