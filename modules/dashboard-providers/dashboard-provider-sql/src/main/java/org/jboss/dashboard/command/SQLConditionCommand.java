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
package org.jboss.dashboard.command;

import org.apache.commons.lang.StringUtils;
import org.jboss.dashboard.commons.filter.SQLFilterByCriteria;
import org.jboss.dashboard.provider.DataFilter;

import java.util.*;

/**
 * SYNTAX: {sql_condition, required | optional, &lt;sqlColumn&gt;, &lt;filterPropertyId&gt;).<br>
 * ex: {sql_condition, optional, case_status)
 */
public class SQLConditionCommand extends AbstractCommand {

    public static final String ARGUMENT_REQUIRED = "required";
    public static final String ARGUMENT_OPTIONAL = "optional";

    /** The filter property id containing the values to search for. */
    protected transient String filterPropertyId;

    /** The filter where statement generated. */
    protected transient List filterWhereStatement;

    public SQLConditionCommand(String name) {
        super(name);
    }

    public String execute() throws Exception {
        if (getArguments().size() < 3) throw new IllegalArgumentException("[" + name + ", missing arguments]");
        String requiredStr = getArgument(0);

        boolean isRequired = false;
        if (ARGUMENT_REQUIRED.equalsIgnoreCase(requiredStr)) isRequired = true;
        else if (ARGUMENT_OPTIONAL.equalsIgnoreCase(requiredStr)) isRequired = false;
        else throw new IllegalArgumentException("[" + name + ", argument " + requiredStr + " is not allowed. Expected: required or optional]");

        String sqlColumn = getArgument(1);
        if (StringUtils.isBlank(sqlColumn)) {
            throw new IllegalArgumentException("[" + name + ", argument sqlColumn cannot be empty]");
        }
        filterPropertyId = getArgument(2);
        if (StringUtils.isBlank(filterPropertyId)) {
            filterPropertyId = sqlColumn.toLowerCase();
        }

        if (dataFilter != null) {
            String[] filterProperties = dataFilter.getPropertyIds();
            for (int i = 0; i < filterProperties.length; i++) {
                String filterProperty = filterProperties[i];
                if (filterPropertyId.equals(filterProperty)) {
                    SQLFilterByCriteria filter = new SQLFilterByCriteria(dataFilter);
                    filterWhereStatement = filter.SQL_getWhereClause(filterPropertyId, sqlColumn, false, true);
                    return getPreparedStatementFragment();
                }
            }
        }

        // If code gets this line, filter property doesn't exist on filter.
        if (isRequired) return "1 != 1";
        else return "1 = 1";
    }

    public boolean containsProperty(String propertyId) throws Exception {
        String arg = getArgument(2);
        return (arg != null && arg.equals(propertyId));
    }

    public String getFilterPropertyId() {
        return filterPropertyId;
    }

    public String getPreparedStatementFragment() {
        if (filterWhereStatement == null) return null;
        return (String) filterWhereStatement.get(0);
    }

    public List getPreparedStatementParameters() {
        if (filterWhereStatement == null) return Collections.EMPTY_LIST;
        return filterWhereStatement.subList(1, filterWhereStatement.size());
    }
}
