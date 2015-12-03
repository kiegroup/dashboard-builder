@echo off

SET MAVEN_OPTS=-Xms1024M -Xmx2048M -XX:MaxPermSize=512m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
cd ../modules/dashboard-showcase/

REM Language settings
SET LOCALE_DEFAULT=-DLocaleManager.defaultLocaleId=en
SET LOCALE_INSTALLED=-DLocaleManager.installedLocaleIds=en,es,de,fr,pt,ja,zh

REM Dataset engine settings
SET DS_SIZE=-DDataSetSettings.maxDataSetSizeInBytes=104857600
SET DS_MEM_USE=-DDataSetSettings.maxMemoryUsedInDataLoad=209715200
SET DS_LOAD_TIME=-DDataSetSettings.maxDataSetLoadTimeInMillis=10000
SET DS_FILTER_TIME=-DDataSetSettings.maxDataSetFilterTimeInMillis=10000
SET DS_GROUP_TIME=-DDataSetSettings.maxDataSetGroupTimeInMillis=10000
SET DS_SORT_TIME=-DDataSetSettings.maxDataSetSortTimeInMillis=10000

REM Dashboard filter settings
SET FILTER_MAX_ENTRIES=-DDashboardSettings.maxEntriesInFilters=1000

REM Chart libs settings
SET OFC2_ENABLED=-DOFC2DisplayerRenderer.enabled=false
SET GCHARTS_ENABLED=-DGoogleDisplayerRenderer.enabled=true
SET GCHARTS_API_URL=-DGoogleDisplayerRenderer.jsApiUrl=https://www.google.com/jsapi

REM XSS Protection
SET XXS_PROTECTION=-DHTTPSettings.XSSProtectionEnabled=true

REM Frame busting prevention:
REM
REM - DENY, which prevents any domain from framing the content.
REM - SAMEORIGIN,  which only allows the current site to frame the content.
REM - ALLOW-FROM uri, which permits the specified 'uri' to frame this page. (e.g., ALLOW-FROM http://www.example.com)
REM   (The ALLOW-FROM option is a relatively recent addition (circa 2012) and may not be supported by all browsers yet.
REM    BE CAREFUL ABOUT DEPENDING ON ALLOW-FROM. If you apply it and the browser does not support it,
REM    then you will have NO clickjacking defense in place)
REM
SET XFRAME_OPTIONS=-DHTTPSettings.XFrameOptions=SAMEORIGIN

echo on

mvn jetty:run -P h2,jetty %LOCALE_DEFAULT% %LOCALE_INSTALLED% %DS_SIZE% %DS_MEM_USE% %DS_LOAD_TIME% %DS_FILTER_TIME% %DS_GROUP_TIME% %DS_SORT_TIME% %FILTER_MAX_ENTRIES% %GCHARTS_ENABLED% %GCHARTS_API_URL% %OFC2_ENABLED% %XXS_PROTECTION% %XFRAME_OPTIONS%

REM cd ../../scripts/