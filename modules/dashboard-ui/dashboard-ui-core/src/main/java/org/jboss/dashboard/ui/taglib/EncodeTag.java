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
package org.jboss.dashboard.ui.taglib;

import org.jboss.dashboard.ui.UIBeanLocator;
import org.jboss.dashboard.ui.components.UIBeanHandler;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.taglib.factory.UseComponentTag;
import org.jboss.dashboard.workspace.Panel;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.jsp.JspTagException;

/**
 * This tag must encodes the given string value to the namespace of the current panel.
 * <p/>
 * This tag should be used for named elements in the panel output (such as Javascript
 * functions and variables). The encoding ensures that the given name is uniquely
 * associated with this panel and avoids name conflicts with other elements on
 * the workspace page or with other panels on the page.
 * <p/>
 * The encode tag must not contain any body content.
 * <p/>
 * The following required attribute is defined for this tag:
 * <li>
 * name (Type: String, required): the name of the String that should be encoded
 * into the namespace of the panel. <br>
 * </li>
 * <p/>
 * An example of a JSP using the encode tag could be:
 * <p/>
 * <CODE>
 * &lt;a onclick='&lt;panel:encode name=&quot;doFoo()&quot;/&gt;'&gt;Foo&lt;/a&gt;
 * </CODE>
 * <p/>
 * The example references a JavaScript function with the name <code>doFoo</code>, which is encoded
 * to ensure uniqueness on the workspace page.
 */
public class EncodeTag extends BaseTag {

    /**
     * Text to encode
     */
    private String name = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        Panel panel = RequestContext.lookup().getActivePanel();
        UIBeanHandler uiBean = UIBeanLocator.lookup().getCurrentBean(pageContext.getRequest());
        String encodedName = encode(panel, uiBean, name);
        try {
            pageContext.getOut().print(encodedName);
        } catch (Exception e) {
            handleError(e);
        }
        return SKIP_BODY;
    }

    /**
     * Encode a name for a given panel context, appending it to a String depending on the panel.
     *
     * @param panel Panel being rendered
     * @param uiBean factoryComponent
     * @param name Symbolic name to encode @return an encoded version for that name, so that different panels have different names.
     * @return A encoded name
     */
    public static String encode(Panel panel, UIBeanHandler uiBean, String name) {
        StringBuffer sb = new StringBuffer();
        if (panel != null) {
            sb.append("panel_").append(panel.getPanelId().longValue() < 0 ?
                    ("NEG" + Math.abs(panel.getPanelId().longValue())) : panel.getPanelId().toString());
        }
        if (uiBean != null) {
            String beanName = uiBean.getBeanName();
            if (isJavaIdentifier(beanName)) {
                sb.append("_component_").append(beanName);
            } else {
                sb.append("_component_").append(Math.abs(beanName.hashCode()));
            }
        }
        sb.append("_").append(StringEscapeUtils.escapeHtml(name));
        return sb.toString();
    }

    /**
     * Returns true if s is a legal Java identifier.
     * @param s String to check
     */
    public static boolean isJavaIdentifier(String s) {
        if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * Encode a name for a given panel context, appending it to a String depending on the panel.
     *
     * @param panel Panel being rendered
     * @param name Symbolic name to encode
     * @return An encoded version for that name, so that different panels have different names.
     */
    public static String encode(Panel panel, String name) {
        return encode(panel, null, name);
    }
}
