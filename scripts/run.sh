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

  LOCALE_DEFAULT=-Dorg.jboss.dashboard.LocaleManager.defaultLocaleId=en
  LOCALE_INSTALLED=-Dorg.jboss.dashboard.LocaleManager.installedLocaleIds=en,es,de,fr,pt,ja,zh

  DS_SIZE=-Dorg.jboss.dashboard.dataset.DataSetSettings.maxDataSetSizeInBytes=104857600
  DS_MEM_USE=-Dorg.jboss.dashboard.dataset.DataSetSettings.maxMemoryUsedInDataLoad=209715200
  DS_LOAD_TIME=-Dorg.jboss.dashboard.dataset.DataSetSettings.maxDataSetLoadTimeInMillis=10000
  DS_FILTER_TIME=-Dorg.jboss.dashboard.dataset.DataSetSettings.maxDataSetFilterTimeInMillis=10000
  DS_GROUP_TIME=-Dorg.jboss.dashboard.dataset.DataSetSettings.maxDataSetGroupTimeInMillis=10000
  DS_SORT_TIME=-Dorg.jboss.dashboard.dataset.DataSetSettings.maxDataSetSortTimeInMillis=10000

  FILTER_MAX_ENTRIES=-Dorg.jboss.dashboard.ui.DashboardSettings.maxEntriesInFilters=1000

  echo mvn jetty:run -P $1,jetty $LOCALE_DEFAULT $LOCALE_INSTALLED $DS_SIZE $DS_MEM_USE $DS_LOAD_TIME $DS_FILTER_TIME $DS_GROUP_TIME $DS_SORT_TIME $FILTER_MAX_ENTRIES
  mvn jetty:run -P $1,jetty $LOCALE_DEFAULT $LOCALE_INSTALLED $DS_SIZE $DS_MEM_USE $DS_LOAD_TIME $DS_FILTER_TIME $DS_GROUP_TIME $DS_SORT_TIME $FILTER_MAX_ENTRIES
fi


