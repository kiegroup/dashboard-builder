This readme is intended for developers and will assist you in compiling and running of the project.

Prerequisites
--------------------

This guide assumes you have Java JDK 1.6 (set as JAVA_HOME), and Maven 3.0.4+ (set as MAVEN_HOME)

* Additional configuration for Maven:

  In order to run the application the following environment variable must be set with de suggested values:

    MAVEN_OPTS=-Xms1024M -Xmx3072M -XX:MaxPermSize=512m

DB Configuration
-------------------------

Two maven profiles are provided for development purposes "postgres" and "h2".
Database configuration for each profile should be done in the "modules/dashboard-showcase/pom.xml" file.

The parameters that you can configure are: db.url, db.user and db.password

      <db.url>jdbc:postgresql://localhost:5432/dashbuilder</db.url>
      <db.user>dashbuilder</db.user>
      <db.password>dashbuilder</db.password>

  NOTE: the database user must have admin grants in order to allow the application to automatically
  create the database objects.

* Postgres database creation

  The postgres database can be created with the following sql sentence:

    <pre>CREATE DATABASE dashbuilder
    WITH ENCODING='UTF8'
           OWNER=dashbuilder
           CONNECTION LIMIT=-1</pre>

  NOTE: the database encoding must be UTF8.

  (The currently tested postgres version for development is 8.4.x)

* H2 database will be created automatically

Log4j configuration
-------------------

The log4j.xml file is located the following directory: "modules/dashboard-showcase/src/test/resources/log4j.xml".

Configure the following parameter to the desired file:

    <param name="file" value="/tmp/dashbuilder-app.log"/>

Compile and build
----------------------

The project must be compiled executing the following Maven command in the root directory.

* To generate a development distribution for postgresql:

    <pre>$ mvn clean install -P postgres,jetty -Dmaven.test.skip=true</pre>

* To generate a development distribution for H2:

    <pre>$ mvn clean install -P h2,jetty -Dmaven.test.skip=true</pre>

* Finally, if you want to do a complete build (including the demo application and the distribution files) execute the
following command.

    <pre>$ mvn clean install -Dmaven.test.skip=true -P full-build</pre>

Run the application
--------------------

To run the application go to the "modules/dashboard-showcase" directory and execute one of the following command
depending the build profile used to compile the application (read the previous section):

    $ mvn jetty:run -P postgres,jetty
    $ mvn jetty:run -P h2,jetty

To access the application type the following url in your browser:

    http://localhost:8080/dashbuilder
    user: root
    password: root

jBPM Process Dashboard
------------------------

Once logged, the jBPM Process Dashboard can be accessed in two ways:

1. As root, by selecting the jBPM Dashboard workspace at the top administration toolbar.
2. Typing the following url <code>http://localhost:8080/dashbuilder/workspace/jbpm-dashboard</code>

In order to populate the jBPM dashboard with dummy data (only for the H2 database), go to the
**modules/dashboard-showcase/WEB-INF/etc/sql** directory and run the **jbpm_demo.sh** script.
The target database needs to have been created before this, either by launching the application, or by
running the create-h2.sql manually.

If you are interested in setup an integrated installation on JBoss AS of both the jBPM Dashboard and the jBPM Human Task
console, please, take a look at the following [guide](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/jbossas7/README.md).