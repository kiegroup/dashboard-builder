Dashboard Builder
==========================

This readme is intended to give you all the needed information to build, configure and use the
Dashboard Builder application and other related subprojects.

If you discover pitfalls, tips and tricks not described in this document,
please update it using the [markdown syntax](http://daringfireball.net/projects/markdown/syntax).

Table of contents
------------------

* **[What is?](#what-is)**

* **[General information](#general-information)**

* **[Quickstart](#quickstart)**

* **[Subprojects](#subprojects)**

What is?
----------------

The jBPM Dashboard Builder is a full featured web application which allows non-technical users to visually create business dashboards.
Dashboard data can be extracted from from heterogeneous sources of information such as JDBC databases or regular text files.
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
* IT / System architects. Connectivity and data extraction
* Analyst. Dashboard composition & configuration.

**Distribution**:
* Independent application to deploy in WAR format.
* Standalone demo installation for quick evaluation.

General information
-------------------

There are currently three ways to use or work with the product.

1. **Developer mode**:
    This work mode is used by developers that wants to work and contribute with the project.
    If you are interested in develop and contribute to the project we recommend that you read the following sections.

2. **Demonstration mode**:
    The product demonstration is a stand alone version intended for users that wants to try the product without
    having to do a complex installation and configuration.

    If you are interested to run the demonstration read sections 2 and 3.

3. **System integrators**:
    The system integrators need to configure the product distribution to be installed on a given application server.
    If you want learn about how to configure the product using the dist-config tool read sections 4.2 and 4.3.


Quickstart
-------------------

If you want to build the project and execute the demo application in 5 minutes follow this steps.

0. Prerequisites:

  This guide assumes you have Java JDK 1.6 (set as JAVA_HOME), and Maven 3.0.4+ (set as MAVEN_HOME) in your system.

1. Open a terminal window, go to the root directory and execute the following Maven command:

   <pre>$ mvn clean install -DskipTests -P full-build</pre>

2. Go to the 'builder/target' directory and unzip the dashbuilder-demo-installer.zip ile to any selected directory. (e.g. <my_target_directory>)

  After unzipping you will have a directory structure like this:

  <pre>
    [my_target_directory]/dashbuilder-demo
         start-demo.sh
         README.md
         /db
         /log
   </pre>

3. To start the demo application go to the <my_target_directory>/dashbuilder-demo directory and execute
the start-demo.sh script (start-demo.bat on windows systems) to run the demo application.

4. Once the application is started open a browser window and type the following url http://localhost:8080
to access the application. The following user/password will be automatically created: root/root, demo/demo.

  NOTE: when you start the demo application for the first time it may take some minutes due to the 
  database initialization procedure, moreover the H2 database downgrades the application performance 
  compared with other databases like PostgreSQL, MySQL, normally used in production environments.


Subprojects
-------------------

Currently the project is composed of the following three main subprojects.

* **modules**:

It contains all the modules beloging to the application.
If you are a project developer o contributor read the modules/README.md file.

* **builder**:

This project generates the different distribution formats for the application.
Read the builder/README.md file for more information.

* **toolkit**:

This project implements some basic tools. If you need to automate the application distribution configuration
you will be probably interested in the "dist-config" tool.
Read the toolkit/README.md file for more information.