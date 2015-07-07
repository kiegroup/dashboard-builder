Deployment onto Apache TomEE 1.7.1
==========================

The <code>tomee</code> directory contains all the artifacts to build the distribution for Apache TomEE 1.7.1, in the
simplest way possible and using a default configuration with the H2 database.

Please follow the next steps in order to deploy the application.

Configure Apache TomEE 1.7.1 server
--------------------------------

A context and a H2 datasource are automatically created, according to the values set in <code>/builder/src/main/tomee1.7.1/META-INF/context-xml</code>

You can override these values by using the appropriate configuration in <code><tomee_home>/conf/server.xml</code>. Please, refer to
the Apache TomEE 1.7.1 documentation about the different available options.

Authentication and authorization
---------------------------------

The dashboard builder module uses container managed authentication and authorization.

Two user roles must be defined: "admin" and "user", as configured in the <code>web.xml</code>. Therefore, you must define this roles and create
users with them in order to be able to log in.

In a default Tomcat installation, edit <code><tomcat_home>/conf/tomcat-users.xml</code> and customize and add the following lines:

         <role rolename="admin"/>
         <role rolename="user"/>
         <user username="root"   password="root"  roles="admin"/>
         <user username="admin"  password="admin"  roles="admin"/>
         <user username="demo"   password="demo"  roles="user"/>

The <code>root</code> user is an special user that is granted all the permissions.

Deploy the application
--------------------------
Get the proper war file <code>dashbuilder-tomee-1.7.1.war</code> and copy it to <code><tomee_home>/webapps</code>:

NOTE: when the application starts for the first time it may take some minutes due to the database initialization procedure.
The configured database user must also have enough database grants to create the database objects.
Take in count that the H2 database downgrades the application performance compared with other databases like PostgreSQL,
MySQL, etc., normally used in production environments.

User Authentication
--------------------------

Once started, open a browser and type the following URL:
<code>http://localhost:8080/dashbuilder-tomee-1.7.1/</code>. A login page should be displayed.

You can sign in with any of the users defined above.

