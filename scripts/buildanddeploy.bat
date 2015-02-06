cd..
call mvn clean install -P h2,jetty -DskipTests
cd builder
call mvn clean install -Dfull -DskipTests
cd ..\scripts