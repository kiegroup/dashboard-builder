Deployment onto JBoss
==========================

The jbossas7 directory contains all the artifacts to build the distribution for the JBoss AS 7.0 and 7.1 server.

Please follow the next steps in order to deploy the application.

* NOTE: the following procedure is for a JBoss standalone server, but it can easily be extended for other installation types.

Generate a distribution
---------------------------

Open a terminal window, go to the root directory and execute the following Maven command:

    $ mvn clean install -DskipTests -P full-build

Go to the 'builder/target' directory and get the file called **dashbuilder-jboss-as7.0.war**.

Install a JBoss 7.1 server
---------------------------

Add the following additional configuration.
Edit the <jboss_home>/bin/standalone.conf file and add the following line at the end of the file:

    JAVA_OPTS="$JAVA_OPTS -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false"

Datasource creation
-------------------------------

The application is configured by default to use a data source defined in the JBoss server with the following
JNDI name: <code>java:jboss/datasources/dashbuilderDS</code>

The following lines gives you an example of how to create this datasource using the CLI commands, but you can use any
other method supported by the JBoss 7.1 server like the web console.

Create the H2 datasource by executing the following command.

    $ ./jboss-cli.sh --connect --file=[path_to_datasource_file]

    [path_to_datasource_file]: the file create-datasource.src file is provided with the distribution and has the following content (one line per command).

    /subsystem=datasources/data-source="dashbuilderDS":add(jndi-name="java:jboss/datasources/dashbuilderDS", driver-name="h2", connection-url="jdbc:h2:~/dashbuilder-ds", user-name="dashbuilder", password="dashbuilder")
    /subsystem=datasources/data-source=dashbuilderDS:enable

The create-datasource.src can be located in the following directory: builder/src/main/jbossas7/scripts.

NOTE: If you want to add the data source definition manually in the JBoss standalone.xml configuration file just copy
this xml fragment inside the <datasources> ... </datasources> definition section.

    <datasource jndi-name="java:jboss/datasources/dashbuilderDS" pool-name="dashbuilderDS" enabled="true">
        <connection-url>jdbc:h2:~/dashbuilder-ds</connection-url>
        <driver>h2</driver>
        <security>
            <user-name>dashbuilder</user-name>
            <password>dashbuilder</password>
        </security>
    </datasource>


Deploy the application
--------------------------

Get the proper war file (e.g. dashbuilder-jboss-as7.0.war) distribution file and execute the following command:

    $ cd <jboss_home>/bin
    $ ./jboss-cli.sh --connect --command="deploy <path_to_war_file>"

    <path_to_war_file>: is the local path to the application war file.
    e.g. $ ./jboss-cli.sh --connect --command="deploy /home/myuser/myfiles/dashbuilder-jboss-as7.0.war" )

NOTE: when the application starts for the first time it may take some minutes due to the database initialization procedure.
The configured database user must also have enough database grants to create the database objects.
Take in count that the H2 database downgrades the application performance compared with other databases like PostgreSQL,
MySQL, etc., normally used in production environments.

User Authentication
--------------------------

Once started, you can log into the application by typing the following url:
<code>http://localhost:8080/dashbuilder</code>. A login screen should be displayed.

However, some extra configuration is needed before you can sign in:

* The application is based on the J2EE container managed authentication  mechanism.
This means that the login itself is delegated to the application server.

* First of all, you must first create a user with login=<code>root</code> & role=<code>root</code>
(using the JBoss [jboss-as7]/bin/adduser command utility) in order to login as superuser.

* The application roles are defined at [builder/src/main/jbossas7/WEB-INF/web.xml](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/jbossas7/WEB-INF/web.xml) file.
Roles can be used to create access profiles and define custom authorization policies to the dashboards.

* The application uses the JBoss' default security domain as you can see [here](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/jbossas7/WEB-INF/jboss-web.xml).
You can define your own security domain and use, for instance, an LDAP, a database, or whatever mechanism you want to use as your credential storage.
There are plenty of examples in the JBoss AS documentation about.

Feel free to change any settings regarding the application security and, once finished, to generate a distribution war that fits your needs.


jBPM Dashboard & Human Tasks Console integration
=================================================

The Dashboard Builder comes with an out-of-the-box Process Dashboard for jBPM which can be accessed in two ways:

* As root, by selecting the jBPM Dashboard workspace at the top administration toolbar, or
* Typing the following url <code>http://localhost:8080/dashbuilder/workspace/jbpm-dashboard</code>

As you will see, the dashboard is empty. No data is displayed.

The next sections explain how to install the jBPM Human Task console in order to feed the dashboard with data coming
from the jBPM engine.

Deploy the JBPM console
---------------------------

Get the proper jbpm-console-ng application (e.g. jbpm-console-ng-jboss-as7.0.war) distribution file and deploy it in
the JBoss 7.1 server.

(You can find more information about how to build and configure this project in the following url: [https://github.com/droolsjbpm/jbpm-console-ng](https://github.com/droolsjbpm/jbpm-console-ng))

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


Application configuration
----------------------------

In order to link the jBPM dashboard with the data generated by the jbpm-console-ng application the following configuration
must be done:

* Log into the Dashboard Builder application typing the following url: <code>http://localhost:8080/dashbuilder</code> and the user/password
    root/root or demo/demo.

* Create the external connection to access the processes data.

  Go to the menu option <code>Administration -> External connections</code> and create a new "Data Source" with the
  following parameters.

        Type: JNDI
        Name: JbpmData
        JNDI path: java:jboss/datasources/ExampleDS
        Test Query: select * from bamtasksummary

  Use the "Check DataSource" button to execute the test query and save the data source.

  NOTE: the jbpm-console-ng uses the <code>java:jboss/datasources/ExampleDS</code> datasource. This data source
  is intended for development/demo purposes. If you have configured any other data source for the jbpm-console-ng
  application you must use this name here.

* Adjust the data providers related to the processes.

  Go to the menu option "Administration -> Data providers" and you will se the following three pre-installed SQL data providers.

  * jBPM Count Processes
  * jBPM Process Summary
  * jBPM Task Summary

  Using the "Edit data provider" action, edit the configuration for each of the three data providers and select the
  recently created "JbpmData" as the "Datasource to use". (use the save button to change the configuration)

Once the three datasources has been properly configured, the application is ready to display the information for the processes
instantiated in the jbpm-console-ng.
