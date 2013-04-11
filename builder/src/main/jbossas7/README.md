Deployment onto JBoss
==========================

The <code>jbossas7</code> directory contains all the artifacts to build the distribution for the JBoss AS 7.0 and 7.1 server.

Please follow the next steps in order to deploy the application.

Generate a distribution
---------------------------

Open a terminal window, go to the root directory and execute the following Maven command:

    $ mvn clean install -DskipTests -P full

Go to the <code>builder/target</code> directory and get the file called **dashbuilder-jboss-as7.0.war**.

Install a JBoss 7.1 server
---------------------------

Add the following additional configuration.
Edit the <jboss_home>/bin/standalone.conf file and add the following line at the end of the file:

    JAVA_OPTS="$JAVA_OPTS -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false"

Deploy the application
--------------------------

Get the proper war file (e.g. dashbuilder-jboss-as7.0.war) distribution file and execute the following command:

    $ cd <jboss_home>/bin
    $ ./jboss-cli.sh --connect --command="deploy <path_to_war_file>"

    <path_to_war_file>: is the local path to the application war file.
    e.g. $ ./jboss-cli.sh --connect --command="deploy /home/myuser/myfiles/dashbuilder-jboss-as7.0.war" )


The application is configured to use the datasource with the following JNDI name: <code>java:jboss/datasources/ExampleDS</code>.
Notice, this is datasource present by default at any JBoss installation and is intended for development/demo purposes.

NOTE: when the application starts for the first time it may take some minutes due to the database initialization procedure.
The configured database user must also have enough database grants to create the database objects.
Take in count that the H2 database downgrades the application performance compared with other databases like PostgreSQL,
MySQL, etc., normally used in production environments.

User Authentication
--------------------------

Once started, open a browser and type the following URL:
<code>http://localhost:8080/dashbuilder</code>. A login screen should be displayed.

However, some extra configuration is needed before you can sign in:

* The application is based on the J2EE container managed authentication  mechanism.
This means that the login itself is delegated to the application server.

* First of all, in order to login as superuser, using the <code>[jboss-as7]/bin/adduser</code> command utility,
you must create a user with login=<code>root</code> and role=<whatever role has been defined in the web.xml file>.
This is just for container authentication purposes, as the root user's application privileges are not role-linked,
but instead is granted with all permissions).

* The application roles are defined at [builder/src/main/jbossas7/WEB-INF/web.xml](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/jbossas7/WEB-INF/web.xml) file.
Roles can be used to create access profiles and define custom authorization policies.

* The application uses the JBoss' default security domain as you can see [here](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/jbossas7/WEB-INF/jboss-web.xml).
Alternatively, you can define your own security domain and use, for instance, an LDAP, a database, or whatever mechanism you want to use as your credential storage.
There are plenty of examples in the JBoss AS documentation about.

Feel free to change any settings regarding the application security and, once finished, to generate a distribution war that fits your needs.


jBPM Dashboard & Human Tasks Console integration
=================================================

The Dashboard Builder comes with an out-of-the-box Process Dashboard for jBPM which can be accessed in two ways:

* As root, by selecting the jBPM Dashboard workspace at the top administration toolbar, or
* Typing the following URL: <code>http://localhost:8080/dashbuilder/workspace/jbpm-dashboard</code>

As you will see, the dashboard is empty. No data is displayed.

The next sections explain how to install the jBPM Human Task console in order to feed the dashboard with data coming
from the jBPM engine.

Deploy the JBPM console
---------------------------

Get the proper jbpm-console-ng application (e.g. jbpm-console-ng-jboss-as7.0.war) distribution file and deploy it in
the JBoss 7.1 server.

(You can find more information about how to build and configure this project in the following URL: [https://github.com/droolsjbpm/jbpm-console-ng](https://github.com/droolsjbpm/jbpm-console-ng))

In order to deploy the jbpm-console-ng you can use any of the JBoss deployment methods.
In the following lines we are going to use JBoss command line tooling.

Execute the following steps:

* Start the JBoss server.
* Open a terminal window and execute:

        $ cd [jboss_home]/bin
        $ ./jboss-cli.sh --connect --command="deploy [path_to_jbpm_console_file]"

        [path_to_jbpm_console_file]: is the local path to the jbpm console application war file.
        e.g. $ ./jboss-cli.sh --connect --command="deploy /home/myuser/myfiles/jbpm-console-ng-jboss-as7.0.war"

Assuming <code>[jboss_home]</code> is the path to the given JBoss installation dir (e.g. /opt/jboss-as-7.1.1.Final).
