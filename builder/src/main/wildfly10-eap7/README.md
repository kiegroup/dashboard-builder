Deployment into WildFly 10 / EAP 7
==================================

Please follow the next steps in order to deploy the application.

Preparing the environment for running the Dashboard Builder
-----------------------------------------------------------

Get the proper WAR file (e.g. dashbuilder-wildfly10.war or dashbuilder-eap7.war) and execute the following command:

    $ cd <jboss_home>/bin
    $ ./jboss-cli.sh --connect --command="deploy <path_to_war_file>"

    <path_to_war_file>: is the local path to the application war file.
    e.g. $ ./jboss-cli.sh --connect --command="deploy /home/myuser/myfiles/dashbuilder-wildfly10.war" )


The application is configured to use a data source with the following JNDI name: <code>java:jboss/datasources/ExampleDS</code>.
Notice, this data source is intended for development/demo purposes and it's present by default at any JBoss installation.

If you want to deploy on a database different from H2 like Oracle, MySQL, Postgres or MS SQL Server please follow the next steps:

**1.- Install the database driver on the JBoss Wildfly server (read the JBoss documentation)**

Considering a PostgreSQL 9.3 database, the filesystem structure for adding a postgres 9.3 driver module for jdbc4 can be as:

        <JBOSS_HOME>/modules/system/layers/base/
                                                org/
                                                    postgres/
                                                            main/
                                                                 postgresql-9.3-1103.jdbc4.jar
                                                                 module.xml
The content for the `module.xml` can be as:

        <?xml version="1.0" encoding="UTF-8"?>

        <!--
          ~ JBoss, Home of Professional Open Source.
          ~ Copyright 2010, Red Hat, Inc., and individual contributors
          ~ as indicated by the @author tags. See the copyright.txt file in the
          ~ distribution for a full listing of individual contributors.
          ~
          ~ This is free software; you can redistribute it and/or modify it
          ~ under the terms of the GNU Lesser General Public License as
          ~ published by the Free Software Foundation; either version 2.1 of
          ~ the License, or (at your option) any later version.
          ~
          ~ This software is distributed in the hope that it will be useful,
          ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
          ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
          ~ Lesser General Public License for more details.
          ~
          ~ You should have received a copy of the GNU Lesser General Public
          ~ License along with this software; if not, write to the Free
          ~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
          ~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.
          -->

          <module xmlns="urn:jboss:module:1.1" name="org.postgres">
          <resources>
        	<resource-root path="postgresql-9.3-1103.jdbc4.jar"/>
          </resources>
          <dependencies>
        	<module name="javax.api"/>
        	<module name="javax.transaction.api"/>
          </dependencies>
        </module>

**2.- Create an empty database & users**

Considering the PostgreSQL example above, run the `psql` command and type:

        postgres=# CREATE USER myuser WITH PASSWORD 'myuser';
        postgres=# CREATE DATABASE dashbuilder;
        postgres=# GRANT ALL PRIVILEGES ON DATABASE dashbuilder to myuser;

Check you can connect successfully using the above user to the recently created database:

        [root@host ~]# psql -U myuser -d dashbuilder -W

**IMPORTANT NOTE**: If you have permission issues trying to connect the database from the Dashboard Builder, please configure your `pg_hba.conf` for allowing local TCP connections.

**3.- Create a JBoss data source**

By default, the dashbuilder WAR file for JBoss Wildfly 10 / EAP 7 uses the default wildfly data source named `ExampleDS`.

The easiest way is just re-using the `ExampleDS` but configuring it the PostgreSQL connection, in that case, edit your `standalone-full.xml` and modify the default data source named `ExampleDS` as:

        <datasources>
            <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
                <connection-url>jdbc:postgresql://localhost:5432/dashbuilder</connection-url>
                <driver>postgres</driver>
                <security>
                <user-name>myuser</user-name>
                <password>myuser</password>
                </security>
            </datasource>
            <drivers>
                <driver name="postgres" module="org.postgres">
                    <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
                </driver>
            </drivers>
        </datasources>

Another option is to create a new data source for the PostgreSQL database, if you choose this option, you have to specify the data source to use in the dashbuilder application (instead of the `ExampleDS` one used by default):
So you have to modify the file `dashboard-builder/builder/src/main/wildfly10-eap7/WEB-INF/jboss-web.xml` as:

        <jboss-web>
           <context-root>/dashbuilder</context-root>
           <resource-ref>
               <res-ref-name>jdbc/dashbuilder</res-ref-name>
               <res-type>javax.sql.DataSource</res-type>
               <jndi-name>java:jboss/datasources/myDataSource</jndi-name>
           </resource-ref>
           ...

   Replace the *jndi-name* parameter value by the JNDI path of the JBoss data source you've just created.

**4.- If the database user has several schemas available, you must specify the schema to use.**

*If your database configuration does not care about handling different schemas for the user, you can skip this section.*

  There are three options:

  - Specify the default schema in <code>dashboard-builder/modules/dashboard-webapp/src/main/webapp/WEB-INF/etc/hibernate.cfg.xml</code>
  file using the property named <code>default_schema</code>. Usually this option is used when you are going to build the
  application from sources and package it into a WAR file.

  - If the application is already packaged, you can modify the property <code>default_schema</code> in
  <code>WEB-INF/etc/hibernate.cfg.xml</code> file and re-assembly the WAR.

   - If the application is already packaged and you don't want to re-assembly the WAR, you can set the default schema to
   use via command line - Java environment variable.

   Example: <code>-Dorg.jboss.dashboard.database.hibernate.HibernateInitializer.defaultSchema=schema1</code>

   NOTE: The java environment variable has priority over the hibernate configuration property.

Languages supported
------------------------

The application supports a set of predefined languages (<code>availableLocaleIds</code>):

* English (en) -- The default language (<code>defaultLocaleId</code>)
* Spanish (es)
* German (de)
* French (fr)
* Chinese (zh)
* Japanese (ja)
* Portuguese (pt)

The language taken by default is the user's browser preferred language. So when a user access the application
for the first time the application will read the language from the HTTP request header in order to determine the language
to apply. If the user language is not supported then the application default language (<code>defaultLocaleId</code>) is taken instead.

There is an easy way to change these settings as the application reads them from the JVM's system properties.
So, in JBoss, we can just define them in the following file: <code>wildfly-10|jboss-eap-7.0/standalone/configuration/standalone.xml</code>,
under the &lt;extensions&gt; section.

    <system-properties>
        <property name="org.jboss.dashboard.LocaleManager.installedLocaleIds" value="en,es,de,fr,ja,pt"/>
        <property name="org.jboss.dashboard.LocaleManager.defaultLocaleId" value="fr"/>
    </system-properties>

In this example, the Chinese language has been removed from the list, so an user accessing from China (or any other
unsupported language) will get the content in French, as is the system's default.

NOTE: don't forget to restart the application server every time you change any of these settings.

User Authentication
--------------------------

Once started, open a browser and type the following URL:
<code>http://localhost:8080/dashbuilder</code>. A login screen should be displayed.

However, some extra configuration is needed before you can sign in:

* The application is based on the J2EE container managed authentication  mechanism.
This means that the login itself is delegated to the application server.

* First of all, in order to login as superuser, using the <code>wildfly-10|jboss-eap-7/bin/add-user.sh</code> command utility,
you must create a user with login=<code>root</code> and role=<code>admin</code>.
This is just for container authentication purposes, as the root user's application privileges are not role-linked,
but instead is granted with all permissions).

* The application roles are defined at [builder/src/main/wildfly10-eap7/WEB-INF/web.xml](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/wildfly10-eap7/WEB-INF/web.xml) file.
Roles can be used to create access profiles and define custom authorization policies.

* The application uses the JBoss' default security domain as you can see [here](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/wildfly10-eap7/WEB-INF/jboss-web.xml).
Alternatively, you can define your own security domain and use, for instance, an LDAP, a database, or whatever mechanism you want to use as your credential storage.
There are plenty of examples in the JBoss AS documentation about.

Feel free to change any settings regarding the application security and, once finished, to generate a distribution war that fits your needs.

Run the Dashboard Builder
-------------------------

The above sections explains how to configure the application's data source & realm for your environment. Please pay attention to them.

If you didn't read how to create an application user yet, here is quick code snippet of how to create a user and run the Dashboard Builder quickly:

        [root@host ~]# cd $JBOSS_HOME/bin
        [root@host ~]# ./add-user.sh
            * Select option "b) Application User (application-users.properties)"
            * username: myuser
            * password: myuser@mypassword
            * groups: admin
            * Is this new user going to be used for one AS process to connect to another AS process? -> Type "no"

Once having a user in the application server, let's start the server:

        [root@host ~]# cd $JBOSS_HOME/bin
        [root@host ~]# ./standalone.sh -b 0.0.0.0 --server-config=standalone-full.xml

Once application started, navigate to:

        http://localhost:8080/dashbuilder

And use the recently created user `myuser/myuser@mypassword`.
