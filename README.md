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
It also provides a generic process dashboard for the jBPM Human Task module. Such dashboard can display multiple key performance indicators regarding process instances, tasks and users.

The application will also automatically install some ready-to-use sample dashboards, for demo and learning purposes.

**Key features**:
* Visual configuration of dashboards (Drag'n'drop).
* Graphical representation of KPIs (Key Performance Indicators).
* Configuration of interactive report tables.
* Data export to Excel and CSV format.
* Filtering and search, both in-memory or SQL based.
* Process and tasks dashboards with jBPM.
* Data extraction from external systems, through different protocols.
* Granular access control for different user profiles.
* Look'n'feel customization tools.
* Pluggable chart library architecture.
* Chart libraries provided: NVD3, JFreeChart, OFC2 & Gauge.

**Target users**:
* Managers / Business owners. Consumer of dashboards and reports.
* IT / System architects. Connectivity and data extraction.
* Analysts. Dashboard composition & configuration.

**Distribution**:
* Independent application to deploy in WAR format.


Quickstart
-------------------

If you want to build the project and execute a demo follow the next steps:

1. Prerequisites:

  This guide assumes you have Java JDK 1.6 (set as JAVA_HOME), and Maven 3.0.4+ (set as MAVEN_HOME) in your system.
  The <code>java</code> and <code>mvn</code> commands must be added to the executable's path as well.

2. Open a terminal window, go to the <code>scripts</code> directory and type the following command (for linux systems):

        $ ./buildandrun_h2.sh

    This command compiles, builds and runs the application.

    This procedure will take a few minutes (but only for the first time) because the maven build process needs to download a
    lot of third-party libraries.

3. Once the application is started, open a browser window and type the URL: <code>http://localhost:8080/dashbuilder</code>.
The following user/password are available by default:

    * <code>root/root</code>: to sign-in as the superuser
    * <code>demo/demo</code>: to enter as a end user.

  NOTE: The application uses an auto deployable H2 embedded database. So no extra configuration is needed.
  But when you start the application for the first time it may take some minutes due to the
  database initialization procedure. The H2 database downgrades the application performance
  compared with other databases like PostgreSQL, MySQL, normally used in production environments.


Subprojects
-------------------

* If you are a project developer o contributor read the
[modules/README.md](https://github.com/droolsjbpm/dashboard-builder/blob/master/modules/README.md) file.

* If you want to generate a product distribution for the JBoss application server read the
[builder/README.md](https://github.com/droolsjbpm/dashboard-builder/blob/master/builder/README.md) file.

