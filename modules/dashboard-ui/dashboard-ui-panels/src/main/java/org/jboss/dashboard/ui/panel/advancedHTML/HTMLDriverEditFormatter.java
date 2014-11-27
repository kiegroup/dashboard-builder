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
package org.jboss.dashboard.ui.panel.advancedHTML;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.ui.UIServices;
import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * This class extends Formatter to provide support for the rendering of the edit page of advanced HTML panel.
 * <p/>
 * It serves the following output fragments, with given output attributes:
 * <li> outputStart. At the beginning.
 * <li> languagesOutputStart. At the beginning, when there are languages to show.
 * <li> languageOutput/selectedLanguageOutput. For every language, depending on wether or not it is the currently selected
 * It receives the following attributes:
 * <ul>
 * <li> langId. Language id.
 * <li> langName. Language name in the current locale.
 * <li> langParamName. Parameter name to use for the language.
 * <li> url. URL to go when the language is clicked.
 * </ul>
 * <li> languagesOutputEnd. At the end, when there are languages to show.
 * <p/>
 * <li> output. After the languages. It receives the following attributes:
 * <ul>
 * <li> content. Content for the textarea to edit.
 * <li> contentParamName. Parameter name to use for the textarea.
 * </ul>
 * <li> outputEnd. At the end. It receives the following attributes:
 * <ul>
 * <li> url. URL to go when the form is submitted.
 * </ul>
 * </ul>
 */
public class HTMLDriverEditFormatter extends Formatter {

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        HTMLDriver htmlDriver = (HTMLDriver) getPanel().getProvider().getDriver();
        String[] langs = getLocaleManager().lookup().getPlatformAvailableLangs();
        setAttribute("url", UIServices.lookup().getUrlMarkupGenerator().getLinkToPanelAction(getPanel(), "saveChanges", true));
        renderFragment("outputStart");
        if (langs != null) {
            renderFragment("languagesOutputStart");
            for (int i = 0; i < langs.length; i++) {
                String lang = langs[i];

                setAttribute("langId", lang);
                setAttribute("langName", StringUtils.capitalize(LocaleManager.lookup().getLangDisplayName(lang)));
                setAttribute("langParamName", HTMLDriver.PARAMETER_EDITING_LANG);
                setAttribute("url", UIServices.lookup().getUrlMarkupGenerator().getLinkToPanelAction(getPanel(), "changeEditingLanguage", HTMLDriver.PARAMETER_EDITING_LANG + "=" + lang, true));
                if (htmlDriver.getEditingLanguage(getPanel()).equals(lang)) {
                    renderFragment("selectedLanguageOutput");
                } else {
                    renderFragment("languageOutput");
                }
            }
            renderFragment("languagesOutputEnd");
            String content = htmlDriver.getHtmlCode(getPanel()).get(htmlDriver.getEditingLanguage(getPanel()));
            setAttribute("content", content == null ? "" : content);
            setAttribute("contentParamName", HTMLDriver.PARAMETER_HTML);
            renderFragment("output");
        }
        renderFragment("outputEnd");
    }
}
