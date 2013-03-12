/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* TODO: Add an instance of this panel to the product showcase as a reference list of BAM commands available.
<b>Data property commands</b>. Access the dashboard data filter current status:
<ul>
<li>&#123;dashboard_minvalue, emp_id&#125; = {dashboard_minvalue, emp_id}, The minimum data filter value defined for a property.
<li>&#123;dashboard_minvalue, emp_id&#125; = {dashboard_maxvalue, emp_id}, The maximum data filter value defined for a property.
<li>&#123;dashboard_selectedvalues, emp_id&#125; = {dashboard_selectedvalues, emp_id}, The filter values the user has entered.
<li>&#123;dashboard_allvalues, emp_id&#125; = {dashboard_allvalues, emp_id}, All the filter allowed values.
</ul>
&lt;propertyId&gt; must be replaced by a property identifier belonging to the list of properties loaded by the data providers
contained in the dashboard. So we got a data provider that loads, for instance, an order_amount property, then we could extract
information of that property using those commands.

<p><b>Function commands</b>. Apply an scalar function over the values loaded for a given property:
<ul>
<li>&#123;dashboard_countfunction, emp_id&#125; = {dashboard_countfunction, emp_id}, The number of occurrences or values a given property has.
<li>&#123;dashboard_maxfunction, emp_id&#125; = {dashboard_maxfunction, emp_id}, The maximum value for a given property.
<li>&#123;dashboard_minfunction, emp_id&#125; = {dashboard_minfunction, emp_id}, The minimum value for a given property.
<li>&#123;dashboard_averagefunction, emp_id&#125; = {dashboard_averagefunction, emp_id}, The average value for a given property.
<li>&#123;dashboard_sumfunction, emp_id&#125; = {dashboard_sumfunction, emp_id}, The sum of all the values for a given property.
</ul>
The last 4 commands can only be applied to numeric properties.<br>

<p><b>Navigations commands</b>. Give access to the user navigation context within the workspace:
<ul>
<li>&#123;navigation_workspace_id&#125; = {navigation_workspace_id}, The current workspace identifier.
<li>&#123;navigation_section_id&#125; = {navigation_section_id}, The current section identifier.
<li>&#123;navigation_workspace_title&#125; = {navigation_workspace_title}, The current section title localized in the user language.
<li>&#123;navigation_section_title&#125; = {navigation_section_title}, The current workspace title localized in the user language.
<li>&#123;navigation_language&#125; = {navigation_language}, The current language (es, en, ca, ...)
<li>&#123;navigation_user_login&#125; = {navigation_user_login}, The logged user login.
<li>&#123;navigation_user_name&#125; = {navigation_user_name}, The logged user name.
<li>&#123;navigation_user_email&#125; = {navigation_user_email}, The logged user e-mail.
</ul>
*/
package org.jboss.dashboard.ui.panel.dashboard;

import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.command.*;
import org.jboss.dashboard.ui.Dashboard;
import org.jboss.dashboard.ui.panel.DashboardDriver;
import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.ui.panel.advancedHTML.HTMLDriver;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;

/**
 * Extension of the HTML base panel that makes possible to embed inside the HTML dashboard variables.
 * <p>For example, it's possible to display, as a part of an HTML fragment, the number of running cases that a given dashboard KPI is displaying, or
 * the total amount of sales made in a given period. The following HTML fragment is an example of how to do that. Suppose we want to display the following
 * HTML fragment: <br><b> &#8364; 9600K Total sales between January 1st, 2008 and July 31st, 2008</b>.<br>
 * As we can see we got 3 dynamic variables extracted from the
 * dashboard context. The HTML source that prints the previous HTML is: &lt;b&gt;&amp;#36; {dashboard_sumfunction, order_amount} Total sales between {dashboard_minvalue,
 * date} and {dashboard_maxvalue, date}&lt;/b&gt; Each BAM command is replaced by:
 * <ul>
 * <li>{dashboard_sumfunction, order_amount} =>  &#8364; 9600K
 * <li>{dashboard_minvalue, date}            => January 1st, 2008
 * <li>{dashboard_maxvalue, date}            => July 31st, 2008
 * </ul>
 * So with a simple command mechanism the HTML editor can mix HTML static code with information displayed by the dashboard&#39;s KPIs.
 * Next is a list fo some commands available:<br>
 * <b>Data property commands</b>. Access the dashboard data filter current status:
 * <ul>
 * <li>{dashboard_minvalue, &lt;propertyId&gt;}   => The minimum data filter value defined for a property.
 * <li>{dashboard_maxvalue, &lt;propertyId&gt;}   => The maximum data filter value defined for a property.
 * <li>{dashboard_selectedvalues, &lt;propertyId&gt;} => The filter values the user has entered.
 * <li>{dashboard_allvalues, &lt;propertyId&gt;}  => All the filter allowed values.
 * </ul>
 * &lt;propertyId&gt; must be replaced by a property identifier belonging to the list of properties loaded by the data providers
 * contained in the dashboard. So we got a data provider that loads, for instance, an order_amount property, then we could extract 
 * information of that property using those commands.
 *
 * <p><b>Function commands</b>. Apply an scalar function over the values loaded for a given property:
 * <ul>
 * <li>{dashboard_count, &lt;propertyId&gt;}   => The number of occurrences or values a given property has.
 * <li>{dashboard_max, &lt;propertyId&gt;}     => The maximum value for a given property.
 * <li>{dashboard_min, &lt;propertyId&gt;}     => The minimum value for a given property.
 * <li>{dashboard_average, &lt;propertyId&gt;} => The average value for a given property.
 * <li>{dashboard_sum, &lt;propertyId&gt;}     => The sum of all the values for a given property.
 * </ul>
 * The last 4 commands can only be applied to numeric properties.<br>
 *
 * <p><b>Navigations commands</b>. Give access to the user navigation context within the workspace:
 * <ul>
 * <li>{navigation_workspace_id}     => The current workspace identifier.
 * <li>{navigation_section_id}    => The current section identifier.
 * <li>{navigation_workspace_title}  => The current workspace title localized in the user language.
 * <li>{navigation_section_title} => The current section title localized in the user language.
 * <li>{navigation_language}      => The current language (es, en, ca, ...)
 * <li>{navigation_user_login}    => The logged user login.
 * <li>{navigation_user_name}     => The logged user name.
 * <li>{navigation_user_email}    => The logged user e-mail.
 * </ul>
 */
public class DashboardHTMLDriver extends HTMLDriver implements DashboardDriver {

    /**
     * Logger
     */
    protected transient static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DashboardHTMLDriver.class);

    /**
     * OVERWRITTEN in order to process BAM commands embedded in the HTML.
     */
    public Map getHtmlCode(Panel panel) {
        Map origMap = super.getHtmlCode(panel);
        if (origMap == null || !getPanelSession(panel).isShowMode()) return origMap;

        // Process the BAM commands embedded into the HTML.
        Map modifiedMap = new HashMap();
        Iterator it = origMap.keySet().iterator();
        while (it.hasNext()) {
            String language = (String) it.next();
            String html = (String) origMap.get(language);

            try {
                TemplateProcessor tp = DataProviderServices.lookup().getTemplateProcessor();
                String parsedHtml = tp.processTemplate(html, new DashboardCommandProcessor());
                modifiedMap.put(language, parsedHtml);
            } catch (Exception e) {
                log.error("HTML template processing error.", e);
                continue;
            }
        }
        return modifiedMap;
    }

    // DashboardDriver interface

    public Set<DataProvider> getDataProvidersUsed(Panel panel) throws Exception {
        Set<DataProvider> results = new HashSet<DataProvider>();
        Map origMap = super.getHtmlCode(panel);
        if (origMap == null || !getPanelSession(panel).isShowMode()) return results;

        String html = (String) origMap.get(LocaleManager.currentLang());
        if (StringUtils.isBlank(html)) return results;

        // Get the BAM commands embedded into the HTML.
        DashboardCommandProcessor cp = new DashboardCommandProcessor();
        cp.setCommandExecutionEnabled(false);
        TemplateProcessor tp = DataProviderServices.lookup().getTemplateProcessor();
        tp.processTemplate(html, cp);
        List<Command> commandList = cp.getSuccessfulCommands();

        // Get the data providers used by the BAM commands.
        Dashboard dashboard = DashboardHandler.lookup().getCurrentDashboard();
        Set<DataProvider> dataProviders = dashboard.getDataProviders();
        for (DataProvider dataProvider : dataProviders) {
            DataProperty[] dataProperties = dataProvider.getDataSet().getProperties();
            for (int i = 0; i < dataProperties.length; i++) {
                DataProperty dataProperty = dataProperties[i];
                for (Command command : commandList) {
                    if (command.containsProperty(dataProperty.getPropertyId())) {
                        results.add(dataProvider);
                    }
                }
            }
        }
        return results;
    }
}
