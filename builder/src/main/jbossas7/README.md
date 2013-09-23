Deployment onto JBoss
==========================

Please follow the next steps in order to deploy the application.

Generate a distribution
---------------------------

Open a terminal window, go to the root directory and execute the following Maven command:

    $ mvn clean install -Dfull  -DskipTests

Go to the <code>builder/target</code> directory and get the file called **dashbuilder-jboss-as7.war**.

Deploy the Dashboard Builder
----------------------------

Get the proper WAR file (e.g. dashbuilder-jboss-as7.war) and execute the following command:

    $ cd <jboss_home>/bin
    $ ./jboss-cli.sh --connect --command="deploy <path_to_war_file>"

    <path_to_war_file>: is the local path to the application war file.
    e.g. $ ./jboss-cli.sh --connect --command="deploy /home/myuser/myfiles/dashbuilder-jboss-as7.war" )


The application is configured to use a datasource with the following JNDI name: <code>java:jboss/datasources/ExampleDS</code>.
Notice, this datasource is intended for development/demo purposes and it's present by default at any JBoss installation.

If you want to deploy on a database different from H2 like Oracle, MySQL, Postgres or MS SQL Server please follow the next steps:

* Install the database driver on JBoss (read the JBoss documentation)

* Create an empty database and a JBoss data source which connects to it

* Modify the file *dashboard-builder/builder/src/main/jbossas7/WEB-INF/jboss-web.xml*:

        <jboss-web>
           <context-root>/dashbuilder</context-root>
           <resource-ref>
               <res-ref-name>jdbc/dashbuilder</res-ref-name>
               <res-type>javax.sql.DataSource</res-type>
               <jndi-name>java:jboss/datasources/myDataSource</jndi-name>
           </resource-ref>
           ...

   Replace the *jndi-name* parameter value by the JNDI path of the JBoss data source you've just created.

* Modify the file *dashboard-builder/builder/src/main/jbossas7/WEB-INF/jboss-deployment-structure.xml*.

  Add the following snippet of configuration inside the *deployment* tag, where *jdbcDriverModuleName* is the name of the JBoss JDBC driver module.

        <dependencies>
            <module name="jdbcDriverModuleName" />
        </dependencies>


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
So, in JBoss, we can just define them in the following file: <code>[jboss-as]/standalone/configuration/standalone.xml</code>,
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

* First of all, in order to login as superuser, using the <code>[jboss-as7]/bin/add-user.sh</code> command utility,
you must create a user with login=<code>root</code> and role=<code>admin</code>.
This is just for container authentication purposes, as the root user's application privileges are not role-linked,
but instead is granted with all permissions).

* The application roles are defined at [builder/src/main/jbossas7/WEB-INF/web.xml](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/jbossas7/WEB-INF/web.xml) file.
Roles can be used to create access profiles and define custom authorization policies.

* The application uses the JBoss' default security domain as you can see [here](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/src/main/jbossas7/WEB-INF/jboss-web.xml).
Alternatively, you can define your own security domain and use, for instance, an LDAP, a database, or whatever mechanism you want to use as your credential storage.
There are plenty of examples in the JBoss AS documentation about.

Feel free to change any settings regarding the application security and, once finished, to generate a distribution war that fits your needs.
