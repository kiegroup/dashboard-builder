Distribution Builder
==========================

The 'builder' project generates the product distribution for different applications servers as well as the
auto-instalable standalone demo.

Currently, the following artifacts are generated:

* **dashbuilder-jboss-as7.0.war:**

Product distribution for JBoss 7.0 and JBoss 7.1 servers.
By the default the distribution is configured to use H2 hibernate dialect and database drivers aren't packaged with
the distribution.

Read the src/main/dashbuilder-jbossas7/README.md file to get more information.

* **dashbuilder-demo-installer.zip:**

Product standalone demo.
Read the src/main/standalone-demo/README.md to get more information.

* **dashbuilder-demo.war:**

The war file used by the dashbuilder-demo-installer.zip

* Other files in src/main/target target directory shouldn't be used individually.