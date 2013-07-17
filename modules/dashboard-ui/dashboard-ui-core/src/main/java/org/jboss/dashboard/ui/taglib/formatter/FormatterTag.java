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
package org.jboss.dashboard.ui.taglib.formatter;

import org.jboss.dashboard.factory.Factory;
import org.jboss.dashboard.database.hibernate.HibernateTxFragment;
import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.profiler.CodeBlockType;
import org.jboss.dashboard.profiler.CoreCodeBlockTypes;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.lang.reflect.Constructor;
import java.util.*;

public class FormatterTag extends BodyTagSupport {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FormatterTag.class.getName());

    public static final int STAGE_READING_PARAMS = 1;
    public static final int STAGE_RENDERING_FRAGMENTS = 2;

    public static final String OUTPUT_MODE_REPLACE = "replace";
    public static final String OUTPUT_MODE_ATTRIBUTE = "attribute";

    private int currentStage;
    private Formatter formatter;
    private String currentEnabledFragment = "";
    private List processingInstructions = new ArrayList();
    private int currentProcessingInstruction = 0;

    protected Object name;
    protected HashMap params = new HashMap();
    protected HashMap fragmentParams = new HashMap();
    protected FormaterTagDynamicAttributesInterpreter formaterTagDynamicAttributesInterpreter = null;
    protected Set fragments = new HashSet();

    protected transient FormatterTrace trace;

    public void clearFragmentParams() {
        fragmentParams.clear();
    }

    public HashMap getFragmentParams() {
        return fragmentParams;
    }

    public FormaterTagDynamicAttributesInterpreter getFormaterTagDynamicAttributesInterpreter() {
        return formaterTagDynamicAttributesInterpreter;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public void setParam(String name, Object value) throws JspException {
        if (params.containsKey(name)) throw new JspException("Duplicated param name \"" + name + '"');
        params.put(name, value);
    }

    public void addFragment(String name) throws JspException {
        if (fragments.contains(name)) throw new JspException("Duplicated fragment name \"" + name + '"');
        fragments.add(name);
    }

    public String getCurrentEnabledFragment() {
        return currentEnabledFragment;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public final int doStartTag() throws JspException {
        currentStage = STAGE_READING_PARAMS;
        if (name instanceof Formatter) {
            formatter = (Formatter) name;
        } else {
            formatter = (Formatter) Factory.lookup(String.valueOf(name));
        }
        if (formatter == null)
            try {
                log.warn("Unable to locate formatter " + name + " in the Factory. Trying class name directly.");
                Class formatterClass = Class.forName(String.valueOf(name));
                Constructor constr = formatterClass.getConstructor(new Class[]{});
                formatter = (Formatter) constr.newInstance(new Object[]{});
            } catch (Exception e) {
                throw new JspException(e);
            }
        if (formatter == null) {
            log.error("Unable to find formatter " + name + " in Factory or through class name. ");
            return SKIP_BODY;
        }
        formatter.setTag(this);
        return EVAL_BODY_INCLUDE;
    }

    public int doAfterBody() throws JspException {
        final Integer[] result = new Integer[] {SKIP_BODY};
        try {
            // Init instrumentation trace.
            if (trace == null) trace = new FormatterTrace(this, pageContext);

            // Execute within the current tx.
            new HibernateTxFragment() {
            protected void txFragment(Session session) throws Throwable {

                // Generate the formatter's rendering instructions.
                if (currentStage == STAGE_READING_PARAMS) {
                    currentStage = STAGE_RENDERING_FRAGMENTS;
                    if (formatter != null) {
                        synchronized (formatter) {
                            formatter.service((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
                        }
                    }
                }
                // Process all the instructions until finish.
                while (currentProcessingInstruction < processingInstructions.size()) {
                    ProcessingInstruction pi = (ProcessingInstruction) processingInstructions.get(currentProcessingInstruction++);
                    if (log.isDebugEnabled()) log.debug("Processing instruction " + pi);
                    trace.update(FormatterTag.this, pi);

                    switch (pi.getType()) {
                        case ProcessingInstruction.SET_ATTRIBUTE:
                            fragmentParams.put(pi.getName(), pi.getValue());
                            break;

                        case ProcessingInstruction.RENDER_FRAGMENT:
                            currentEnabledFragment = pi.getName();
                            result[0] = EVAL_BODY_AGAIN;
                            return;

                        case ProcessingInstruction.INCLUDE_PAGE:
                            for (Iterator it = fragmentParams.entrySet().iterator(); it.hasNext();) {
                                Map.Entry entry = (Map.Entry) it.next();
                                pageContext.getRequest().setAttribute((String) entry.getKey(), entry.getValue());
                            }
                            pageContext.include(pi.getName());
                            for (Iterator it = fragmentParams.entrySet().iterator(); it.hasNext();) {
                                Map.Entry entry = (Map.Entry) it.next();
                                pageContext.getRequest().removeAttribute((String) entry.getKey());
                            }
                            clearFragmentParams();
                            break;

                        case ProcessingInstruction.WRITE_OUT:
                            pageContext.getOut().print(pi.getValue());
                            clearFragmentParams();
                            break;

                        case ProcessingInstruction.SET_DYNAMIC_ATTRIBUTES_INTERPRETER:
                            formaterTagDynamicAttributesInterpreter = (FormaterTagDynamicAttributesInterpreter) pi.getValue();
                            break;
                    }
                }
                // Do after rendering.
                formatter.afterRendering((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
            }}.execute();
        } catch (Throwable e) {
            throw new JspException(e);
        } finally {
            // Finish the trace.
            trace.end();
            trace = null;
        }
        return result[0];
    }

    public int doEndTag() throws JspException {
        //clean up
        currentEnabledFragment = "";
        processingInstructions = new ArrayList();
        currentProcessingInstruction = 0;
        params = new HashMap();
        fragmentParams = new HashMap();
        fragments = new HashSet();
        return EVAL_PAGE;
    }

    public void addProcessingInstruction(ProcessingInstruction instruction) {
        processingInstructions.add(instruction);
    }

    public Object getParam(String name) {
        return params.get(name);
    }

    public void release() {
    }

    /** Formatter trace class */
    static class FormatterTrace extends CodeBlockTrace {

        protected String jsp;
        protected String bean;
        protected String scope;
        protected String fragment;
        protected Map readableParams;

        public FormatterTrace(FormatterTag tag, PageContext pageContext) {
            super(tag.formatter.getComponentName());
            this.bean = tag.formatter.getComponentName();
            this.scope = tag.formatter.getComponentScope();
            this.jsp = calculateJSP(pageContext);
            begin();
            update(tag, null);
        }

        public void update(FormatterTag tag, ProcessingInstruction pi) {
            fragment = tag.currentEnabledFragment;
            if (tag.params != null && !tag.params.isEmpty()) {
                Iterator it = tag.params.keySet().iterator();
                while (it.hasNext()) {
                    String param = (String) it.next();
                    Object value = tag.params.get(param);
                    if (value != null) {
                        String str = tag.formatter.formatObject(value);
                        if (!StringUtils.isEmpty(str) && str.indexOf("\u0040") == -1) {
                            if (readableParams == null) readableParams = new HashMap();
                            readableParams.put(param, str);
                        }
                    }
                }
            }
        }

        public void end() {
            super.end();
            fragment = null;
        }

        public CodeBlockType getType() {
            return CoreCodeBlockTypes.JSP_FORMATTER;
        }

        public String getDescription() {
            return bean;
        }

        public Map<String,Object> getContext() {
            Map<String,Object> ctx = new LinkedHashMap<String,Object>();
            ctx.put("Formatter", bean);
            ctx.put("Formatter Scope", scope);
            if (!StringUtils.isEmpty(jsp)) ctx.put("Formatter JSP", jsp);
            if (!StringUtils.isEmpty(fragment)) ctx.put("Formatter Fragment", fragment);
            if (readableParams != null) {
                Iterator it = readableParams.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    ctx.put("Formatter param:" + key, readableParams.get(key));
                }
            }
            return ctx;
        }

        /**
         * Convert f.i: "org.apache.jsp.panels.bpe.tasklist.m_005ftask_005ftypes_jsp" to
         * panels/bpe/tasklist/m_task_types.jsp
         */
        protected String calculateJSP(PageContext pageContext) {
            String pageName = pageContext.getPage().getClass().getName();
            pageName = StringUtils.remove(pageName, "org\u002Eapache\u002Ejsp\u002E");
            pageName = StringUtils.replace(pageName, "\u005F005f", "\u005F");
            pageName = StringUtils.replace(pageName, "\u002E", "\u002F");
            pageName = StringUtils.replace(pageName, "\u005Fjsp", "\u002Ejsp");
            return pageName;
        }
    }
}
