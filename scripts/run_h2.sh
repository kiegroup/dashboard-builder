#!/bin/bash
export MAVEN_OPTS="-Xms1024M -Xmx3072M -XX:MaxPermSize=512m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
cd ../modules/dashboard-showcase/
mvn jetty:run -P h2,jetty

