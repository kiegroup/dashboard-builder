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
package org.jboss.dashboard.dataset.sql;

import org.jboss.dashboard.DataProviderServices;
import org.jboss.dashboard.command.*;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An SQL statement template.
 */
public class SQLStatement {

    /**
     * The SQL template sentence. May contain SQL commands like {sql_condition, amount, AMOUNT}.
     */
    protected String SQLTemplate;

    /**
     * The SQL sentence obtained from the SQL template.
     */
    protected transient String SQLSentence;

    /**
     * The SQL parameters.
     */
    protected transient List SQLParameters;

    /**
     * Initialize an SQL statement.
     */
    public SQLStatement(String sqlTemplate) throws Exception {
        SQLTemplate = sqlTemplate;

        // Parse the SQL commands inside the SQL and get a ready-to-run SQL statement.
        TemplateProcessor tp = DataProviderServices.lookup().getTemplateProcessor();
        CommandProcessor cp = DataProviderServices.lookup().getCommandProcessorFactory().createCommandProcessor();
        SQLSentence = tp.processTemplate(SQLTemplate, cp);

        // Get the parameters used to generate the SQL sentence.
        SQLParameters = new ArrayList();
        Iterator commandIt = cp.getSuccessfulCommands().iterator();
        while (commandIt.hasNext()) {
            try {
                SQLConditionCommand sqlCommand = (SQLConditionCommand) commandIt.next();
                SQLParameters.addAll(sqlCommand.getPreparedStatementParameters());
            } catch (ClassCastException e) {
                // Ignore the non-SQL condition commands encountered into the SQL.
            }
        }
    }

    public String getSQLTemplate() {
        return SQLTemplate;
    }

    public String getSQLSentence() {
        return SQLSentence;
    }

    public List getSQLParameters() {
        return Collections.unmodifiableList(SQLParameters);
    }

    public int hashCode() {
        return new HashCodeBuilder().append(SQLSentence).toHashCode();
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;
            if (SQLTemplate == null) return false;

            SQLStatement other = (SQLStatement) obj;
            if (!SQLTemplate.equals(other.SQLTemplate)) return false;
            if (!SQLSentence.equals(other.SQLSentence)) return false;
            if (SQLParameters.size() != other.SQLParameters.size()) return false;
            for (int i = 0; i < SQLParameters.size(); i++) {
                if (!SQLParameters.get(i).equals(other.SQLParameters.get(i))) return false;
            }
            return true;
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * Get a JDBC prepared statement representing the SQL sentence.
     */
    public PreparedStatement getPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(SQLSentence);
        int psParamIndex = 1;
        Iterator paramIt = SQLParameters.iterator();
        while (paramIt.hasNext()) {
            Object param = paramIt.next();
            if (param instanceof String) preparedStatement.setString(psParamIndex, (String) param);
            else if (param instanceof Date) preparedStatement.setTimestamp(psParamIndex, new Timestamp(((Date) param).getTime()));
            else if (param instanceof Float) preparedStatement.setFloat(psParamIndex, ((Float) param).floatValue());
            else if (param instanceof Double) preparedStatement.setDouble(psParamIndex, ((Double) param).doubleValue());
            else if (param instanceof Number) preparedStatement.setLong(psParamIndex, ((Number) param).longValue());
            else if (param instanceof Boolean) preparedStatement.setBoolean(psParamIndex, ((Boolean) param).booleanValue());
            psParamIndex++;
        }
        return preparedStatement;
    }
}
