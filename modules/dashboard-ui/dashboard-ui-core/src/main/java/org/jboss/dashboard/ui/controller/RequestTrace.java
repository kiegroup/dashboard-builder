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
package org.jboss.dashboard.ui.controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.profiler.CodeBlockType;
import org.jboss.dashboard.profiler.CoreCodeBlockTypes;
import org.jboss.dashboard.profiler.Profiler;
import org.jboss.dashboard.profiler.ThreadProfile;

/** Request trace */
public class RequestTrace extends CodeBlockTrace {

    protected Map<String,Object> context;

    public RequestTrace() {
        super(null);
    }

    public CodeBlockTrace begin(HttpServletRequest request) {
        super.id = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        this.context = buildContext(request);
        return super.begin();
    }

    public CodeBlockType getType() {
        return CoreCodeBlockTypes.CONTROLLER_REQUEST;
    }

    public String getDescription() {
        return (String) context.get("Request URL");
    }

    public Map<String,Object> getContext() {
        return context;
    }

    protected Map<String,Object> buildContext(HttpServletRequest request) {
        Map<String,Object> ctx = new LinkedHashMap<String,Object>();
        ctx.put("Request URL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        ctx.put("Request IP", request.getRemoteAddr());
        ctx.put("Request date", new Date());
        ctx.put("Request header:user-agent", request.getHeader("user-agent"));
        ctx.put("Request header:referer", request.getHeader("referer"));
        ctx.put("Request header:cookie", request.getHeader("cookie"));

        ThreadProfile threadProfile = Profiler.lookup().getCurrentThreadProfile();
        if (threadProfile != null) threadProfile.addContextProperties(ctx);

        Map params = request.getParameterMap();
        Iterator it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            ctx.put("Request param:" + key, toString(params.get(key)));
        }
        return ctx;
    }

    protected String toString(Object obj) {
        if (obj == null) return "";
        if (obj instanceof String[]) {
            StringBuffer buf = new StringBuffer();
            String[] array = (String[]) obj;
            for (String s : array) {
                if (buf.length() > 0) buf.append(",");
                buf.append(s);
            }
            return buf.toString();
        }
        return obj.toString();
    }
}
