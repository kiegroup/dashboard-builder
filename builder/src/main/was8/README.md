Deployment onto Websphere 8
=============================

Please follow the next steps in order to deploy the application on Websphere.

Generate a distribution
---------------------------

Open a terminal window, go to the root directory and execute the following Maven command:

    $ mvn clean install -Dfull -DskipTests

Go to the <code>builder/target</code> directory and get the file called **dashbuilder-was-8.war**.

Configure a data source
--------------------------------

The application requires a datasource. To create it, please follow the next steps:

1. Open the WebSphere's Adminitration Console _http://127.0.0.1:9060/ibm/console_

   Then login (if you have administrative security setup)

2. Create the JDBC provider

  - Left side panel, click on _Resources > JDBC > JDBC Providers_
  - Select the appropriate scope and click on the _New_ button.
  - Fill out the form. For non-listed database types (i.e: H2, Postgres & Mysql) you need to provide the path to the JDBC driver jar file plus the following class name:

          +------------+-------------------------------------------------------------+
          | Database   |  Implementation class name                                  |
          +------------+-------------------------------------------------------------+
          | H2         | org.h2.jdbcx.JdbcDataSource                                 |
          | Postgres   | org.postgresql.xa.PGXADataSource                            |
          | Mysql      | com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource |
          +------------+-------------------------------------------------------------+

   When you finish, click _Ok_. If there are no data entry errors, you should be back at the list of JDBC Providers, where you should now see your new provider displayed.

3. Create the data source

  - Left side panel, click on _Resources > JDBC > Data sources_
  - Select the appropriate scope and click on the _New_ button.
  - Fill out the creation form. The _JNDI name_ **MUST** be defined as _jdbc/dashbuilder_. Click _Next_.
  - Select the existing JDBC provider you created. Click _Next_.
  - Keep clicking _Next_ until _Finish_.
  - Save to master configuration.
  - Edit the datasource you just created and click on the _Custom properties_ link.
  - Edit and fill the appropriate values required to set-up the connection. This depends on the database type.

           +------------+------------------------------------------------------+
           | Database   |  Connection settings                                 |
           +------------+------------------------------------------------------+
           | H2         | URL, user, password                                  |
           | Postgres   | serverName, databaseName, portNumber, user, password |
           | Mysql      | serverName, databaseName, port, user, password       |
           +------------+------------------------------------------------------+


Deploy the application
--------------------------

1. http://127.0.0.1:9060/ibm/console

    Then login (if you have administrative security setup)

2. Deploy the WAR file

  - Left side panel click on *Applications > Application types > Websphere enterprise applications*
  - Click on _Install_, select the *dashbuilder_was_8.war* file from your local filesystem. Click _Next_
  - From here, you will be asked with several deployments settings. Click _Next_ until finished.
  - The only setting we recommend to set is the context patch of the webapp to _dashbuilder_.

  Once deployed you can start/stop the application from the Websphere console.


Authentication and authorization
---------------------------------

Dashbuilder uses container managed authentication and authorization.

Two user roles must be defined: "admin" and "user", as configured in the <code>web.xml</code>. Therefore, you must define this roles and create users with them in order to be able to log in. To do so from the WebSphere's Adminitration Console:

1. Enable security

  - Left side panel click on *Security > Global security*
  - Section *Application security*, Check the box *Enable application security*
  - Click *Apply*, then save to master config.

   Then you have application security turned on. Now you need to map the users of your application to users within Websphere.

2. Map users

  - _Applications > Enterprise Applications_ click on your application.
  - Under the _Detailed Properties_ section you will see a link _Security role to user/group mapping_. Click on it.
  - Select the roles you wish to use for authentication, _admin_ for instance.
  - Click look up users (you need to create users first, see below).
  - Click search and select users.
  - Use the arrows to move the selected users/groups to the right hand box.
  - Click ok and save to master configuration and restart the server.

3. Create users

  - Just go to the left side panel and click on *Users and Groups > Manage Users*.
  - Click on the _Create_ button, fill out the form and click _Ok_.
  - The _User ID_ field is the login, the word to be used to sign into the application.
  - if you create a user with login=<code>root</code> it'll be granted with all the permissions within the application.


User Authentication
--------------------------

Once started, open a browser and type the following URL:
<code>http://localhost:8080/dashbuilder</code>. A login page should be displayed.

You can sign in with any of the users defined above.