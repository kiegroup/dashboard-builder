Dashboard Builder
==========================

This readme is intended to give you all the needed information to build, configure and use the
Dashboard Builder application and other related subprojects.

If you discover pitfalls, tips and tricks not described in this document,
please update it using the [markdown syntax](http://daringfireball.net/projects/markdown/syntax).

Table of contents
------------------

* **[What is it?](#what-is-it)**

* **[Quickstart](#quickstart)**

* **[Subprojects](#subprojects)**

What is it?
----------------

The Dashboard Builder is a full featured web application which allows non-technical users to visually create business dashboards.
Dashboard data can be extracted from heterogeneous sources of information such as JDBC databases or regular text files.
Some ready-to-use sample dashboards are provided for demo and learning purposes.

**Key features**:
* Visual configuration of dashboards (Drag'n'drop).
* Graphical representation of KPIs (Key Performance Indicators).
* Configuration of interactive report tables.
* Data export to Excel and CSV format.
* Filtering and search, both in-memory or SQL based.
* Data extraction from external systems, through different protocols.
* Granular access control for different user profiles.
* Look'n'feel customization tools.
* Pluggable chart library architecture.
* Chart libraries provided: NVD3 & OFC2.

**Target users**:
* Managers / Business owners. Consumer of dashboards and reports.
* IT / System architects. Connectivity and data extraction.
* Analysts. Dashboard composition & configuration.

**Distribution**:
* Independent application to deploy in WAR format.


Quickstart
-------------------

If you want to build the project and execute a quick demo, please, proceed as indicated:

1. Prerequisites:

  This guide assumes you have Java JDK 1.6 (set as JAVA_HOME), and Maven 3.0.5+ (set as MAVEN_HOME) in your system.
  The <code>java</code> and <code>mvn</code> commands must be added to the executable's path as well.

2. Open a terminal window, go to the <code>scripts</code> directory and type the following command (for linux systems):

        $ ./buildandrun.sh h2

  This command compiles, builds and runs the application.

  This procedure will take a few minutes (but only for the first time) because of the maven build process needs to download a
  lot of third-party libraries.


3. Once the application is started, open a browser and type the following URL: <code>http://localhost:8080/dashbuilder</code>.
  The following users are available by default:

  * <code>root/root</code>: to sign-in as the superuser
  * <code>demo/demo</code>: to sign-in as an end user.

NOTE: The application uses an auto deployable H2 embedded database. So no extra configuration is needed.
But when you start the application for the first time it may take some minutes due to the
database initialization procedure. The H2 database downgrades the application performance
compared with other databases like PostgreSQL, MySQL, normally used in production environments.


Subprojects
-------------------

* If you are a project developer o contributor read the
[modules/README.md](https://github.com/droolsjbpm/dashboard-builder/blob/master/modules/README.md) file.

* If you want to generate a distribution for any of the supported application servers, please, take a look
at the following [guide](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/README.md).

