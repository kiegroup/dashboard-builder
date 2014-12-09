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
  OFC2_ENABLED=-DOFC2DisplayerRenderer.enabled=true
  GCHARTS_ENABLED=-DGoogleDisplayerRenderer.enabled=false
  GCHARTS_API_URL=-DGoogleDisplayerRenderer.jsApiUrl=https://www.google.com/jsapi

  # XSS Protection
  XXS_PROTECTION=-DHTTPSettings.XSSProtectionEnabled=true

  # Frame busting prevention:
  #
  # - DENY, which prevents any domain from framing the content.
  # - SAMEORIGIN,  which only allows the current site to frame the content.
  # - ALLOW-FROM uri, which permits the specified 'uri' to frame this page. (e.g., ALLOW-FROM http://www.example.com)
  #   (The ALLOW-FROM option is a relatively recent addition (circa 2012) and may not be supported by all browsers yet.
  #    BE CAREFUL ABOUT DEPENDING ON ALLOW-FROM. If you apply it and the browser does not support it,
  #    then you will have NO clickjacking defense in place)
  #
  XFRAME_OPTIONS=-DHTTPSettings.XFrameOptions=SAMEORIGIN

  echo mvn jetty:run -P $1,jetty $2 $LOCALE_DEFAULT $LOCALE_INSTALLED $DS_SIZE $DS_MEM_USE $DS_LOAD_TIME $DS_FILTER_TIME $DS_GROUP_TIME $DS_SORT_TIME $FILTER_MAX_ENTRIES $GCHARTS_ENABLED $GCHARTS_API_URL $OFC2_ENABLED $XXS_PROTECTION $XFRAME_OPTIONS
  mvn jetty:run -P $1,jetty $2 $LOCALE_DEFAULT $LOCALE_INSTALLED $DS_SIZE $DS_MEM_USE $DS_LOAD_TIME $DS_FILTER_TIME $DS_GROUP_TIME $DS_SORT_TIME $FILTER_MAX_ENTRIES $GCHARTS_ENABLED $GCHARTS_API_URL $OFC2_ENABLED $XXS_PROTECTION $XFRAME_OPTIONS
fi


