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
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.ui.SessionManager;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.responses.ShowPanelPage;
import org.jboss.dashboard.ui.panel.PanelProvider;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.PanelInstance;
import org.jboss.dashboard.workspace.PanelSession;
import org.jboss.dashboard.workspace.export.Exportable;
import org.jboss.dashboard.workspace.*;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.ui.panel.parameters.BooleanParameter;
import org.jboss.dashboard.ui.controller.responses.ShowPopupPanelPage;
import org.jboss.dashboard.security.PanelPermission;
import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.*;

public class HTMLDriver extends PanelDriver implements Exportable {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HTMLDriver.class.getName());

    public static final String PARAMETER_HTML = "html_code";
    public static final String PARAMETER_EDITING_LANG = "edit_lang";

    private final static String PAGE_SHOW = "show";
    private final static String PAGE_EDIT = "edit";

    private final static String PAGE_CHOOSE_IMAGE = "imageSelect";
    private final static String PAGE_CHOOSE_LINK = "linkSelect";

    public final static String PARAMETER_USE_DEFAULTS = "useDefaultLanguage";

    private static final String ATTR_TEXT = "text";
    private static final String ATTR_EDITING_LANGUAGE = "lang";

    public void init(PanelProvider provider) throws Exception {
        super.init(provider);
        addParameter(new BooleanParameter(provider, PARAMETER_USE_DEFAULTS, false, true));
        String[] methodsForEditMode = new String[]{"actionChangeEditingLanguage", "actionSaveChanges"};
        for (int i = 0; i < methodsForEditMode.length; i++) {
            String method = methodsForEditMode[i];
            addMethodPermission(method, PanelPermission.class, PanelPermission.ACTION_EDIT);
        }
    }

    public void initPanelSession(PanelSession status, HttpSession session) {
        status.setCurrentPageId(PAGE_SHOW);
    }

    protected void beforePanelInstanceRemove(final PanelInstance instance) throws Exception {
        super.beforePanelInstanceRemove(instance);

        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            HTMLText htmlText = load(instance);
            if (htmlText != null) htmlText.delete();
        }}.execute();
    }

    /**
     * Returns if this driver defines support to activate edit mode.
     */
    public boolean supportsEditMode(Panel panel) {
        return true;
    }

    public int getEditWidth(Panel panel, CommandRequest request) {
        return 1000;
    }

    public int getEditHeight(Panel panel, CommandRequest request) {
        return 695;
    }

    /**
     * Defines the action to be taken when activating edit mode
     */
    public void activateEditMode(Panel panel, CommandRequest request) throws Exception {
        super.activateEditMode(panel, request);
        HTMLText text = load(panel.getInstance());
        SessionManager.getPanelSession(panel).setAttribute(ATTR_TEXT, toEditableObject(text));
    }

    protected Map toEditableObject(HTMLText text) {
        Map m = new HashMap();
        for (Iterator it = text.getText().keySet().iterator(); it.hasNext();) {
            String lang = (String) it.next();
            String val = text.getText(lang);
            m.put(lang, val);
        }
        return m;
    }

    /**
     * Defines the action to be taken when activating edit mode
     */
    public void activateNormalMode(Panel panel, CommandRequest request) throws Exception {
        super.activateNormalMode(panel, request);
        SessionManager.getPanelSession(panel).removeAttribute(ATTR_TEXT);
        SessionManager.getPanelSession(panel).removeAttribute(ATTR_EDITING_LANGUAGE);
    }

    /**
     * Returns if this driver is using default language
     *
     * @param panel
     * @return if this driver is using default language
     */
    public boolean isUsingDefaultLanguage(Panel panel) {
        return Boolean.valueOf(panel.getParameterValue(PARAMETER_USE_DEFAULTS)).booleanValue();
    }

    /**
     * Returns if this driver is using default language
     *
     * @param panel
     * @return if this driver is using default language
     */
    public boolean isUsingDefaultLanguage(PanelInstance panel) {
        return Boolean.valueOf(panel.getParameterValue(PARAMETER_USE_DEFAULTS)).booleanValue();
    }

    /**
     * Determine the text being shown for given panel.
     *
     * @param panel
     * @return The text shown, i18n.
     */
    public Map getHtmlCode(Panel panel) {
        PanelSession pSession = SessionManager.getPanelSession(panel);
        Map m = (Map) pSession.getAttribute(ATTR_TEXT);
        if (m != null) return m;
        HTMLText text = load(panel.getInstance());
        if (text != null) return text.getText();
        try {
            HTMLText textToCreate = new HTMLText();
            textToCreate.setPanelInstance(panel.getInstance());
            Locale[] locales = LocaleManager.lookup().getPlatformAvailableLocales();
            for (int i = 0; i < locales.length; i++) {
                Locale locale = locales[i];
                ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.ui.panel.advancedHTML.messages", locale);
                textToCreate.setText(locale.getLanguage(), i18n.getString("defaultContent"));
            }
            textToCreate.save();
        } catch (Exception e) {
            log.error("Error creating empty text for panel: ", e);
        }
        text = load(panel.getInstance());
        if (text != null) return text.getText();
        log.error("Current HTML code is null for panel " + panel);
        return null;
    }

    /**
     * Determine the editing language.
     *
     * @return The text shown, i18n.
     */
    public String getEditingLanguage(Panel panel) {
        String lang = (String) SessionManager.getPanelSession(panel).getAttribute(ATTR_EDITING_LANGUAGE);
        return lang == null ? LocaleManager.lookup().getDefaultLang() : lang;
    }

    public CommandResponse actionChangeEditingLanguage(Panel panel, CommandRequest request) throws Exception {
        String currentText = request.getRequestObject().getParameter(PARAMETER_HTML);
        Map text = (Map) SessionManager.getPanelSession(panel).getAttribute(ATTR_TEXT);
        if (text == null) {
            text = toEditableObject(load(panel.getInstance()));
            SessionManager.getPanelSession(panel).setAttribute(ATTR_TEXT, text);
        }
        text.put(getEditingLanguage(panel), currentText);
        SessionManager.getPanelSession(panel).setAttribute(ATTR_TEXT, text);
        String paramLang = request.getRequestObject().getParameter(PARAMETER_EDITING_LANG);
        SessionManager.getPanelSession(panel).setAttribute(ATTR_EDITING_LANGUAGE, paramLang);
        return new ShowPanelPage();
    }

    public CommandResponse actionSaveChanges(Panel panel, CommandRequest request) throws Exception {
        String currentText = request.getRequestObject().getParameter(PARAMETER_HTML);
        Map m = (Map) SessionManager.getPanelSession(panel).getAttribute(ATTR_TEXT);
        HTMLText text = load(panel.getInstance());
        for (Iterator it = m.keySet().iterator(); it.hasNext();) {
            String lang = (String) it.next();
            String val = (String) m.get(lang);
            text.setText(lang, val);
        }
        text.setText(getEditingLanguage(panel), currentText);
        text.save();
        activateNormalMode(panel, request);
        return new ShowPanelPage();
    }

    public CommandResponse actionSelectImage(Panel panel, CommandRequest request) throws Exception {
        return new ShowPopupPanelPage(panel, PAGE_CHOOSE_IMAGE);
    }

    public CommandResponse actionSelectLink(Panel panel, CommandRequest request) throws Exception {
        return new ShowPopupPanelPage(panel, PAGE_CHOOSE_LINK);
    }

    public HTMLText load(final PanelInstance instance) {
        final List results = new ArrayList();
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                FlushMode oldFlushMode = session.getFlushMode();
                session.setFlushMode(FlushMode.NEVER);
                Query query = session.createQuery(" from " + HTMLText.class.getName() + " as text where text.panelInstance = :instance");
                query.setParameter("instance", instance);
                query.setCacheable(true);
                results.addAll(query.list());
                session.setFlushMode(oldFlushMode);
            }}.execute();
            HTMLText text = null;
            if (results.size() > 0) text = (HTMLText) results.get(0);
            else log.debug("Does not exist a html_text for HTML panel");
            return text;
        } catch (Exception e) {
            log.error("Can't retrive a data for HTML panel ", e);
            return null;
        }
    }

    /**
     * Replicates panel data.
     *
     * @param src  Source PanelInstance
     * @param dest Destinaton PanelInstance
     */
    public void replicateData(final PanelInstance src, PanelInstance dest) throws Exception {
        super.replicateData(src, dest);
        log.debug("HTMLDriver replicating Data from PanelInstance " + src.getDbid() + " to " + dest.getDbid() + ").");
        if (src.equals(dest)) {
            log.debug("Ignoring replication, panel instance is the same.");
            return;
        }

        final HTMLText[] textArray = new HTMLText[1];
        try {
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Exception {
                log.debug("Getting text to duplicate for instance " + src.getDbid());
                FlushMode oldMode = session.getFlushMode();
                session.setFlushMode(FlushMode.COMMIT);//Avoids flushing, as we know the text was not modified in this transaction.
                textArray[0] = load(src);
                session.setFlushMode(oldMode);
                log.debug("Got text to duplicate for instance " + src.getDbid());
            }}.execute();
        } catch (Exception e) {
            log.error("Error loading text for instance. ", e);
        }
        HTMLText text = textArray[0];
        if (text == null) {
            log.debug("Nothing to replicate from PanelInstance " + src.getDbid() + " to " + dest.getDbid());
            return;
        }
        Map htmlSrc = text.getText();

        log.debug("htmlCode to replicate = " + htmlSrc);
        HTMLText htmlDest = new HTMLText();
        htmlDest.setPanelInstance(dest.getInstance());
        for (Iterator it = htmlSrc.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            String val = (String) htmlSrc.get(key);
            htmlDest.setText(key, val);
        }
        try {
            log.debug("Updating HTMLText: IDText " + htmlDest.getDbid() + " Text " + htmlDest.getText());
            htmlDest.save();
        } catch (Exception e) {
            log.error("Replicating panel data", e);
        }
    }

    /**
     * Write instance content to given OutputStream, which must not be closed.
     */
    public void exportContent(PanelInstance instance, OutputStream os) throws Exception {
        HTMLText text = load(instance);
        if (text == null) {
            try {
                text = new HTMLText();
                text.setPanelInstance(instance);
                text.save();
            } catch (Exception e) {
                log.error("Error creating empty HTMLText object", e);
            }
        }
        ObjectOutputStream oos = new ObjectOutputStream(os);
        if (log.isDebugEnabled()) log.debug("Exporting content: " + text.getText());
        HashMap h = new HashMap(); // Avoids serializing a hibernate map
        h.putAll(text.getText());
        oos.writeObject(h);
    }

    /**
     * Read instance content from given InputStream, which must not be closed.
     */
    public void importContent(PanelInstance instance, InputStream is) throws Exception {
        HTMLText currentText = new HTMLText();
        currentText.setPanelInstance(instance);
        ObjectInputStream ois = new ObjectInputStream(is);
        Map text = (Map) ois.readObject();
        if (log.isDebugEnabled()) log.debug("Importing content: " + text);
        for (Iterator it = text.keySet().iterator(); it.hasNext();) {
            String lang = (String) it.next();
            String value = (String) text.get(lang);
            currentText.setText(lang, value);
        }
        currentText.save();
    }

    protected String getPanelHTMLContent(PanelInstance instance, String lang) {
        HTMLText text = load(instance);
        if (text != null) {
            String val = text.getText(lang);
            if (StringUtils.isEmpty(val) && isUsingDefaultLanguage(instance)) {
                LocaleManager localeManager = LocaleManager.lookup();
                val = text.getText(localeManager.getDefaultLang());
            }
            return val;
        }
        return null;
    }
}
