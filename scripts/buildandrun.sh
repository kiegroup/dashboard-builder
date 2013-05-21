#!/bin/bash
if [ "$1" = "" ] ; then
   echo "Build & Run the application for a given database."
   echo ""
   echo "USAGE: buildandrun.sh [h2|postgres]"
else
  echo "-----------------------------------------------------------------"
  echo "Building & Running the application for the '$1' database..."
  echo "------------------------------------------------------------------"

  cd ..
  mvn clean install -P $1,jetty -Dmaven.test.skip=true

  export MAVEN_OPTS="-Xms1024M -Xmx2048M -XX:MaxPermSize=512m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
  cd modules/dashboard-showcase/
  mvn jetty:run -P $1,jetty
fi
