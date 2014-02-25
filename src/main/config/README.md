Zanata translations
==========================

The project i18n artifacts are pretty much bundle files stores into the several modules of the project. Usually under the
_src/main/resource_ folder. The developer usually only takes care of the maintenance of default bundles _messages.properties_
for the english language. Once the development has finished the following translation process begins:

Pushing to Zanata
--------------------

Zanata is a translation service holded by RedHat where a set of translation experts get the i18n resources of a project and
translate them back to the set of target languages.

The web site [https://translate.jboss.org](https://translate.jboss.org) is used by translator to actually carry out the translation
process. Before starting to translate the i18n artifacts must be pused to the Zanata server. To do so:

Go to the project root folder (the pom.xml file contains the Zanata plugin configuration).

* To push only sources (first check-in):

        mvn zanata:push-module -Dzanata.copyTrans=false -Dzanata.username=davidredhat

* To push the current i18n artifacts (hard copy including current translations, will overwrite everything in the server):

        mvn zanata:push-module -Dzanata.copyTrans=false -Dzanata.pushType=both -Dzanata.username=davidredhat


Translation
------------------

The translation process is carried out at [https://translate.jboss.org](https://translate.jboss.org).
You need to JBoss user in order to log in and check your the project status.

Pulling from Zanata
--------------------

Once Zanata translations are done (progress 100%). Yhe next step is to pull them and update the i18n bundles.

* Pull

        mvn zanata:pull

* Copy the files downloaded to the corresponding folder on the modules structure.

* Run the _ShowcaseBundleInjector_ class utility to inject literals into the Showcase XML files.

* Push changes to git.


