How to localize to a given language
===========================================

The following languages: <code>es</code>, <code>en</code> are pre-installed by default. If you want to localize all the
static content to a different country/language then proceed as indicated:

Bundles
-----------------

Bundle files can be found across all the modules and are usually located under the <code>src/main/resources</code> folders.
Bundle file names stick to the following pattern: <code><bundleName>_<country>_<language>.properties</code>.

All the files within the aplication source tree following the above file name pattern should be considered as a bundle.
In order to localize to a given language a new version of each bundle must be created and its contents translated for the target language.


Dashboards
-----------------

Dashboards are composed by Workspaces and KPIs and are bundled with the aplication as XML files. These files are
automatically imported as part of the application install process and are usually located under the
<code>src/main/webapp/WEB-INF/etc/appdata/initialData</code> folders.

The localization process for these type of resources consists in edit these XML files and, every time you found tag containing
an attribute like <code>language="en"</code>, create a sibling entry for the target language you want to localize.

Next is the list of current dashboards assets bundled with the application:

       - dashboard-builder/modules/dashboard-samples/src/main/webapp/WEB-INF/etc/appdata/initialData/showcaseKPIs.xml
       - dashboard-builder/modules/dashboard-samples/src/main/webapp/WEB-INF/etc/appdata/initialData/showcaseWorkspace.xml
       - dashboard-builder/modules/dashboard-jbpm/src/main/webapp/WEB-INF/etc/appdata/initialData/jbpmKPIs.xml
       - dashboard-builder/modules/dashboard-jbpm/src/main/webapp/WEB-INF/etc/appdata/initialData/jbpmWorkspace.xml

UI Resources
-----------------

Skins, Envelopes and Layouts are UI resources bundled with application as zip files and are automatically created
during the aplication install process (like Dashboards). They are usually located under the <code>src/main/webapp/WEB-INF/etc/envelopes|skins|layouts</code> folders.

All these zip files contains a <code>.properties</code> file in the root dir which contains an i18n property called <code>name</code>.
The name for the target language must be inserted and the zip file must be repackaged with that change.

Next is the list of current UI assets bundled with the application:

       - dashboard-builder/modules/dashboard-ui/dashboard-ui-resources/src/main/webapp/WEB-INF/etc/envelopes/Default_Envelope.zip
       - dashboard-builder/modules/dashboard-ui/dashboard-ui-resources/src/main/webapp/WEB-INF/etc/skins/Default_Skin.zip
       - dashboard-builder/modules/dashboard-ui/dashboard-ui-resources/src/main/webapp/WEB-INF/etc/layouts/Default_Layout.zip
       - dashboard-builder/modules/dashboard-ui/dashboard-ui-resources/src/main/webapp/WEB-INF/etc/layouts/Default_template_with_sliding_menu.zip
       - dashboard-builder/modules/dashboard-ui/dashboard-ui-resources/src/main/webapp/WEB-INF/etc/layouts/Template_25-50-25.zip
       - dashboard-builder/modules/dashboard-ui/dashboard-ui-resources/src/main/webapp/WEB-INF/etc/layouts/Default_Layout.zip
       - dashboard-builder/modules/dashboard-ui/dashboard-ui-resources/src/main/webapp/WEB-INF/etc/layouts/Template_25-75.zip
       - dashboard-builder/modules/dashboard-ui/dashboard-ui-resources/src/main/webapp/WEB-INF/etc/layouts/Template_75-25.zip



