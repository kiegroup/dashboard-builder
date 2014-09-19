Weblogic deployment instructions
========================================

This document explains how to deploy a Dashbuilder file (_dashbuilder-weblogic12c.war_) on Weblogic Application Server 12c.

Open the Weblogic's Administration Console _http://localhost:7001/console_

Then login (if you have administrative security setup)

Before deploy the war file, some server configurations are required:

Increase JVM memory size
------------------------------

Set environment variable:
USER_MEM_ARGS=-Xms512m -Xmx1024m -XX:MaxPermSize=512m

Security settings
------------------------------

The following settings are required in order to enable the container managed authentication mechanisms provided by the app. server.

Go to **_Domain structure > Security realms > myrealm_**

On tabbed menu go to **_Users and groups > Groups_**

   Create 2 groups: admin, user

On tabbed menu go to **_Users and groups > Users_**

   Create a single user and add to it the two groups defined above.

Configure a data source
--------------------------------

The application requires a datasource which must be created prior to the deployment of the WAR:

**Create the data source**

  - Left side panel, click on _Services > Data sources_ and click _New > Generic Data source_
  - Fill out the creation form. Set the following JNDI name _jdbc/dashbuilder_
    (must match the data source defined in the _WEB-INF/etc/hibernate.cfg.xml.xml_ file contained in the _dashbuilder.war_)
  - Follow instructions provided by Weblogic console
  - NOTE: make sure that data source has assigned target server on which is going to be deployed

Deploy the application
--------------------------

Application must be deployed as exploded archive (as folder) to allow complete feature set to be activated.

Follow deployments screen with important selections:
- Application must be installed as Application and not library - second step of installation wizard.
- Security roles must be taken from deployment descriptor only - DD Only - on third step of of installation wizard

Once restarted you should be able to access the application by typing the following URL: _http://localhost:7001/{name of the folder of exploded archive}_
