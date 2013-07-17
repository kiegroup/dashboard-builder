Distribution Builder
==========================

The 'builder' project generates the product distribution for different applications servers as well as
the auto-instalable standalone demo.

If you want to generate all the distribution files then go to the dashboard-builder root directory and
type the following command:

    $ mvn clean install -Dfull -DskipTests

Currently, the following artifacts are generated:

* **dashbuilder-jboss-as7.war:**  Product distribution for JBoss EAP 6.1 and JBoss AS 7.1 servers.

  By the default the distribution is configured to use H2 hibernate dialect but database drivers aren't packaged with the distribution.

  Read the [src/main/dashbuilder-jbossas7/README.md](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/jbossas7/README.md) file to get more information.

* **dashbuilder-tomcat-7.war:**  Product distribution for Apache Tomcat 7 server.

  By the default the distribution is configured to use H2 hibernate dialect.

  Read the [src/main/dashbuilder-tomcat7/README.md](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/tomcat7/README.md) file to get more information.

* **dashbuilder-demo-installer.zip:** Product standalone demo.

  Read the [src/main/standalone-demo/README.md](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/standalone-demo/README.md) file to get more information.


* **dashbuilder-demo.war:** The war file used by the dashbuilder-demo-installer.zip

(The rest of the files in src/main/target target directory shouldn't be used individually).