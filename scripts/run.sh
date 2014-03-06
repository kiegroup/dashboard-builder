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

  # Language settings
  LOCALE_DEFAULT=-DLocaleManager.defaultLocaleId=en
  LOCALE_INSTALLED=-DLocaleManager.installedLocaleIds=en,es,de,fr,pt,ja,zh

  # Dataset engine settings
  DS_SIZE=-DDataSetSettings.maxDataSetSizeInBytes=104857600
  DS_MEM_USE=-DDataSetSettings.maxMemoryUsedInDataLoad=209715200
  DS_LOAD_TIME=-DDataSetSettings.maxDataSetLoadTimeInMillis=10000
  DS_FILTER_TIME=-DDataSetSettings.maxDataSetFilterTimeInMillis=10000
  DS_GROUP_TIME=-DDataSetSettings.maxDataSetGroupTimeInMillis=10000
  DS_SORT_TIME=-DDataSetSettings.maxDataSetSortTimeInMillis=10000

  # Dashboard filter settings
  FILTER_MAX_ENTRIES=-DDashboardSettings.maxEntriesInFilters=1000

  # Chart libs settings
  OFC2_ENABLED=-DOFC2DisplayerRenderer.enabled=false
  GCHARTS_ENABLED=-DGoogleDisplayerRenderer.enabled=false
  GCHARTS_API_URL=-DGoogleDisplayerRenderer.jsApiUrl=https://www.google.com/jsapi

  echo mvn jetty:run -P $1,jetty $LOCALE_DEFAULT $LOCALE_INSTALLED $DS_SIZE $DS_MEM_USE $DS_LOAD_TIME $DS_FILTER_TIME $DS_GROUP_TIME $DS_SORT_TIME $FILTER_MAX_ENTRIES $GCHARTS_ENABLED $GCHARTS_API_URL $OFC2_ENABLED
  mvn jetty:run -P $1,jetty $LOCALE_DEFAULT $LOCALE_INSTALLED $DS_SIZE $DS_MEM_USE $DS_LOAD_TIME $DS_FILTER_TIME $DS_GROUP_TIME $DS_SORT_TIME $FILTER_MAX_ENTRIES $GCHARTS_ENABLED $GCHARTS_API_URL $OFC2_ENABLED
fi


