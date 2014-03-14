#!/bin/bash
if [ "$1" = "" ] ; then
   echo "Build the application for a given database."
   echo ""
   echo "USAGE: buildandrun.sh [h2|postgres]"
else
  echo "-----------------------------------------------------------------"
  echo "Building the application for the '$1' database..."
  echo "------------------------------------------------------------------"
  cd ..
  mvn clean install -P $1,jetty -DskipTests
fi
