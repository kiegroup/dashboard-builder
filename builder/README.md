Distribution Builder
==========================

The 'builder' project generates the product distribution for different applications servers as well as
the auto-instalable standalone demo.

If you want to generate all the distribution files then go to the dashboard-builder root directory and
type the following command:

    $ mvn clean install -Dfull -DskipTests

Currently, the following artifacts are generated:

* **dashbuilder-wildfly-8.war:**  Product distribution for the Widlfly 8.x application server.

  By the default the distribution is configured to use the default H2 in-memory database provided by the app server.

  Detailed installation instructions [here](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/wildfly8/README.md).

* **dashbuilder-eap-6_4.war:**  Product distribution for the JBoss EAP 6.3 application server.

  By the default the distribution is configured to use the default H2 in-memory database provided by the app server.

  Detailed installation instructions [here](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/eap6_4/README.md).

* **dashbuilder-tomcat-7.war:**  Product distribution for Apache Tomcat 7 server.

  By the default the distribution is configured to use a local H2 database file.

  Detailed installation instructions [here](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/tomcat7/README.md).

* **dashbuilder-was-8.war:**  Product distribution for Websphere 8.5 server.

  It requires to set up a data source connection for any of the supported databases (at the time of this writing: DB2, Postgres, Mysql, H2, Oracle or SQLServer).

  Detailed installation instructions [here](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/was8/README.md).

* **dashbuilder-weblogic-12c.war:**  Product distribution for Weblogic 12c server.

  It requires to set up a data source connection for any of the supported databases (at the time of this writing: DB2, Postgres, Mysql, H2, Oracle or SQLServer).

  Detailed installation instructions [here](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/weblogic12c/README.md).

* **dashbuilder-demo-installer.zip:** App. server-independent self-contained demo of the product.

  Detailed installation instructions [here](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/standalone-demo/README.md).

* **dashbuilder-demo.war:** The war file used by the dashbuilder-demo-installer.zip
