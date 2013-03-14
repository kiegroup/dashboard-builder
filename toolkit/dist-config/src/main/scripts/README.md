The aim of the dist-config tool is to make easier the task of configure the different parameters of the product
distribution in order to be deployed in the application server.

1) Prerequisites
----------------------

In order to run the dist-config tool you must have the JDK 1.6 (set as JAVA_HOME), and Ant 1.8.x (set as ANT_HOME)
installed in your system.

To verify your ANT installation open a terminal window and type the following command:

    $ ant -version

If you have Ant installed and with the correct path setting you well see a command output like this.
"Apache Ant(TM) version 1.8.4 compiled on May 22 2012"

2) How too use the tool
----------------------

The dist-config tool has the following input parameters:

** <code>script option</code>: one of the following values: jboss, jetty, factory.

** <code>jboss</code>: indicates that the distribution will be configured for the JBoss application server.

** <code>jetty</code>: indicates that the distribution will be configured for the Jetty server.

** <code>factory</code>: indicates that only application components will be configured. NOT including web application descriptors
             as web.xml, jboss-web.xml, etc.

** <code>product</code>: a path to the product distribution file you want to configure. e.g. ./product/dashbuilder-jboss-as7.0.war

** <code>config</code>: a path to a java property file with the configuration parameters for the product components and
            web application descriptors. e.g. ./env/jboss.postgres.properties.


Invocation e.g.

        $ ant jboss -Dproduct=./product/dashbuilder-jboss-as7.0.war -Dconfig=./env/jboss.postgres.properties

 When the command finishes the configured file can be found in the "output" directory.

NOTE: the tool makes a copy of the input file, so the original product distribution file will remain intact.

3) Configuration parameters
----------------------------

In the following lines we will describe some of the supported configuration parameters using
the **jboss.postgres.properties** example configuration file located in the **env** directory:

<pre>
# *************************************
# * Database configuration parameters *
# ************************************

# Application internal JNDI name for the datasource that defines the connection to the application database.
# It's not recommended to change this internal name.
# note: If you change this name you must also adjust the value for the parameter jboss-ref-name in the
# JBoss application parameters section.

org.jboss.dashboard.database.HibernateProperties/hibernate.connection.datasource=java\:comp/env/jdbc/dashbuilder

# This parameters establishes the Hibernate database dialect to use by the application.
# If you use a database different than Postgres set the proper value.
org.jboss.dashboard.database.HibernateProperties/hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# This parameters instructs Hibernate to use the second level cache.
org.jboss.dashboard.database.HibernateProperties/hibernate.cache.use_second_level_cache=true


# ***************************************
# * JBoss application server parameters *
# ***************************************

# The following parameters establishes the mapping between the internal JNDI name for the application datasource
# and the datasource defined in the JBoss server.
# Internal name. (It's not recommended to change the internal name)

jboss.res-ref-name=jdbc/dashbuilder

# JEE datasource type.

jboss.res-type=javax.sql.DataSource

# The name of the datasource defined in the JBoss server.

jboss.jndi-name=java:/dashbuilderPG


# *************************
# * Log system parameters *
# *************************

# This parameter specify the application log file that will be created to store the application log traces.
# note: other log4j log system parameters can be changed in the template/log4j.xml file.

log4j.file=/tmp/dashbuilder-app.log
</pre>

