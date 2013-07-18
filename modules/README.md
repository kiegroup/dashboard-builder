This readme is intended for developers that wants to work and contribute to the project.
It will assist you in compiling and running the project.

Prerequisites
--------------------

This guide assumes you have Java JDK 1.6 (set as JAVA_HOME), and Maven 3.0.5+ (set as MAVEN_HOME)

* Additional configuration for Maven:

  In order to run the application the following environment variable must be set with the suggested values:

    MAVEN_OPTS=-Xms1024M -Xmx3072M -XX:MaxPermSize=512m

Database setup
-------------------------

Two maven profiles are provided for development purposes "postgres" and "h2".
Database configuration for each profile should be done in the "modules/dashboard-showcase/pom.xml" file.

The parameters that you can configure are: db.url, db.user and db.password

      <db.url>jdbc:postgresql://localhost:5432/dashbuilder</db.url>
      <db.user>dashbuilder</db.user>
      <db.password>dashbuilder</db.password>

  NOTE: the database user must have admin grants in order to allow the application to automatically
  create the database objects.

* **Postgres**

  The postgres database can be created with the following sql sentence:

    <pre>CREATE DATABASE dashbuilder
    WITH ENCODING='UTF8'
           OWNER=dashbuilder
           CONNECTION LIMIT=-1</pre>

  NOTE: the database encoding must be UTF8.

  (The currently tested postgres version for development is 8.4.x)

* **H2**

    In H2, the database is created automatically. The database itself are two binary files called <code>dashbuilder.h2.db</code>
    and <code>dashbuilder.trace.db</code> stored by default in the user's home directory. Of course, the location of this files can
    be easily changed modifying the proper setting in the pom.xml's H2 profile.

        <db.url>jdbc:h2:~/dashbuilder</db.url>
        <db.user>dashbuilder</db.user>
        <db.password>dashbuilder</db.password>

* **DB2**

  The DB2 database can be created with the following sql sentence:

    <pre>CREATE DATABASE dashb PAGESIZE 16384</pre>

  NOTE: The default pagesize for DB2 systems is 4k, not enought for dashbuilder table columns size, so force it to 16384 as the above example

  (The currently tested DB2 version for development is 9.7)


Compile and build
----------------------

The project must be compiled executing the following Maven command in the root directory.

* To generate a development distribution for postgresql:

    <pre>$ mvn clean install -P postgres,jetty -Dmaven.test.skip=true</pre>

* To generate a development distribution for H2:

    <pre>$ mvn clean install -P h2,jetty -Dmaven.test.skip=true</pre>

* Finally, if you want to do a complete build (including the distribution files) execute the following command:

    <pre>$ mvn clean install -Dfull -DskipTests</pre>

Run the application
--------------------

To run the application go to the <code>modules/dashboard-showcase</code> directory and execute one of the following command:

    $ mvn jetty:run -P postgres,jetty

 or

    $ mvn jetty:run -P h2,jetty

Depending on the build profile you used to compile the application (read the previous section).

To access the application type the URL: <code>http://localhost:8080/dashbuilder</code>.
The following user/password are available by default:

* <code>root/root</code>: to sign-in as the superuser
* <code>demo/demo</code>: to sign-in as an end user.
