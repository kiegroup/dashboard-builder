/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jboss.dashboard.provider.sql;

import org.jboss.dashboard.domain.date.DateDomain;
import org.jboss.dashboard.domain.label.LabelDomain;
import org.jboss.dashboard.domain.numeric.NumericDomain;
import org.jboss.dashboard.provider.AbstractDataProperty;
import java.sql.Types;

/**
 * A column in a database table.
 */
public class SQLDataProperty extends AbstractDataProperty {

    private int type;
    private String tableName;
    private String columnName;

    public SQLDataProperty() {
        super();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void calculateDomain() {
        switch (type) {

            // Text-like columns.
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.BIT: // Booleans
                setDomain(new LabelDomain());
                break;

             // Number-like columns.
            case Types.BIGINT:
            case Types.INTEGER:
            case Types.BOOLEAN:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
                setDomain(new NumericDomain());
                break;

            // Date-like columns.
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                setDomain(new DateDomain());
                break;

            // Unsupported types are treated as a string values.
            default:
                setDomain(new LabelDomain());
                break;
        }
    }
}
