#!/bin/bash
if [ "$1" = "" ] ; then
   echo "Run the application for a given database."
   echo ""
   echo "USAGE: run.sh [h2|postgres]"
else
  echo "-----------------------------------------------------------------"
  echo "Running the application for the '$1' database..."
  echo "------------------------------------------------------------------"

  export MAVEN_OPTS="-Xms1024M -Xmx2048M -XX:MaxPermSize=512m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
  cd ../modules/dashboard-showcase/
  mvn jetty:run -P $1,jetty
fi


