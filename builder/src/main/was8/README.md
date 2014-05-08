Deployment onto Websphere 8
=============================

Please follow the next steps in order to deploy the application on Websphere.

Configure a data source
--------------------------------

The application requires a datasource. To create it, please follow the next steps:

* Open the WebSphere's Adminitration Console _http://127.0.0.1:9060/ibm/console_

   Then login (if you have administrative security setup)

* Create the JDBC provider

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

* Create the data source

  - Left side panel, click on _Resources > JDBC > Data sources_
  - Select the appropriate scope and click on the _New_ button.
  - Fill out the creation form. Set the _JNDI name_ as, for instance, _jdbc/dashbuilder_. Click _Next_.
  - Select the existing JDBC provider you created. Click _Next_.
  - Keep clicking _Next_ until _Finish_.
  - Save to master configuration.
  - Edit the datasource you just created and click on the _Custom properties_ link.
  - Edit and fill the appropriate values required to set-up the connection. This depends on the database type.

           +------------+------------------------------------------------------+
           | Database   | Datasource custom properties                         |
           +------------+------------------------------------------------------+
           | H2         | URL, user, password                                  |
           | Postgres   | serverName, databaseName, portNumber, user, password |
           | Mysql      | serverName, databaseName, port, user, password       |
           +------------+------------------------------------------------------+


Security settings
------------------------------

The following settings are required in order to enable the container managed authentication mechanisms provided by the app. server.

Go to **_Security > Global security_**

   Ensure the option _Enable Application security_ is checked.

Go to **_Users and groups > Manage groups_**

   Create 2 groups: admin, user

Go to **_Users and groups > Manage users_**

   Create a single user with the groups defined above.

  - The _User ID_ field is the login, the word to be used to sign into the application.
  - if you create a user with login=_root_ it'll be granted with all the permissions within the application.

Deploy the application
--------------------------

**Deploy the WAR file**

  - Left side panel click on *Applications > Application types > Websphere enterprise applications*
  - Click on _Install_, select the *dashbuilder_was_8.war* file from your local filesystem. Click _Next_
  - From here, you will be asked with several deployments settings.
  - You'll need to select the datasource created above as the datasource to be used by the application.
  - We also recommend to set is the context patch of the webapp to _dashbuilder_.
  - Click _Next_ until finished.

**App. settings**

Go to _Applications > Application types > Websphere enterprise applications > dashbuilder app > Security role to user/group mapping_

   - Select the roles: admin, user.
   - Click on _Map Special Subjects_ and select the _All Authenticated in Application's Realm_ option.


Save the configurations to the master and restart the server.

Once restarted you should be able to access the application by typing the following URL: _http://localhost:9080/dashbuilder_

