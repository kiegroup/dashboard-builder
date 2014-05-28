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

import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.hibernate.Session;
import org.jboss.dashboard.workspace.PanelInstance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The HTML panel persistent entity.
 */
public class HTMLText implements Serializable {

    protected Long dbid;
    protected Map<String, String> text;
    protected PanelInstance panelInstance;

    public HTMLText() {
        text = new HashMap<String, String>();
    }

    public Long getDbid() {
        return dbid;
    }

    public void setDbid(Long dbid) {
        this.dbid = dbid;
    }

    public PanelInstance getPanelInstance() {
        return panelInstance;
    }

    public void setPanelInstance(PanelInstance panelInstance) {
        this.panelInstance = panelInstance;
    }

    public Map<String, String> getText() {
        return text;
    }

    public void setText(Map<String, String> text) {
        if (text == null) this.text = new HashMap<String, String>();
        else this.text = text;
    }

    public String getText(String language) {
        String result = text.get(language);
        if (result != null) return result;
        else return "";
    }

    public void setText(String language, String value) {
        if (text == null) text = new HashMap<String, String>();
        if (value == null) value = "";

        text.put(language, value);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id=").append(getDbid());
        if (panelInstance != null) {
            sb.append(", workspace=").append(panelInstance.getWorkspace().getId());
            sb.append(", instance=").append(panelInstance.getId());
        }
        sb.append(", content=").append(getText());
        return sb.toString();
    }

    public void save() throws Exception {
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            boolean isTransient = (dbid == null);
            if (isTransient) session.save(HTMLText.this);
            else session.update(HTMLText.this);
            session.flush();
        }}.execute();
    }

    public void delete() throws Exception {
        new HibernateTxFragment() {
        protected void txFragment(Session session) throws Exception {
            boolean isTransient = (dbid == null);
            if (!isTransient) {
                session.delete(HTMLText.this);
                session.flush();
            }
        }}.execute();
    }
}
