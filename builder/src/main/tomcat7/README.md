Deployment onto Apache Tomcat 7
==========================

The <code>tomcat</code> directory contains all the artifacts to build the distribution for Apache Tomcat 7, in the
simplest way possible and using a default configuration with the H2 database.

Please follow the next steps in order to deploy the application.

Generate a distribution
---------------------------

Open a terminal window, go to the root directory and execute the following Maven command:

    $ mvn clean install -Dfull -DskipTests

Go to the <code>builder/target</code> directory and get the file called **dashbuilder-TOMCAT-7.0.war**.

Configure Apache Tomcat 7 server
--------------------------------

A context and a H2 datasource are automatically created, according to the values set in <code>/builder/src/main/tomcat7/META-INF/context-xml</code>

You can override these values by using the appropriate configuration in <code><tomcat_home>/conf/server.xml</code>. Please, refer to
the Apache Tomcat 7 documentation about the different available options.

Authentication and authorization
---------------------------------

The dashboard builder module uses container managed authentication and authorization.

Two user roles must be defined: "admin" and "user", as configured in the <code>web.xml</code>. Therefore, you must define this roles and create
users with them in order to be able to log in.

In a default Tomcat installation, edit <code><tomcat_home>/conf/tomcat-users.xml</code> and customize and add the following lines:

<code>
    <role rolename="admin"/>
    <role rolename="user"/>

    <user username="<ADMIN USER>"     password="<PASSWORD>"  roles="admin"/>
    <user username="root"             password="<PASSWORD>"  roles="admin"/>
    <user username="<A REGULAR USER>" password="<PASSWORD>"  roles="user"/>
</code>

The user with name 'root' is an special user that is granted all the permissions.

Deploy the application
--------------------------
Get the proper war file (dashbuilder-tomcat-7.0.war) and copy it to <code><tomcat_home>/webapps</code>:

NOTE: when the application starts for the first time it may take some minutes due to the database initialization procedure.
The configured database user must also have enough database grants to create the database objects.
Take in count that the H2 database downgrades the application performance compared with other databases like PostgreSQL,
MySQL, etc., normally used in production environments.

User Authentication
--------------------------

Once started, open a browser and type the following URL:
<code>http://localhost:8080/dashbuilder-tomcat-7.0/</code>. A login dialog should be displayed.

