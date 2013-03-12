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
package org.jboss.dashboard.displayer.table;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.dashboard.LocaleManager;
import org.jboss.dashboard.annotation.Install;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.displayer.AbstractDataDisplayerType;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.DataDisplayerRenderer;
import org.jboss.dashboard.displayer.annotation.Table;
import org.jboss.dashboard.export.DataDisplayerXMLFormat;

@ApplicationScoped
@Install
@Table
public class TableDisplayerType extends AbstractDataDisplayerType {

    @Inject @Config(value ="table")
    protected String uid;

    @Inject @Config(value="components/bam/images/table.png")
    protected String iconPath;

    @Inject
    protected TableDisplayerXMLFormat xmlFormat;

    @Inject @Config(value="10")
    protected int maxRowsPerPage;

    @Inject @Config(value="border: 1px solid #000000; padding:0; text-align:center;")
    protected String htmlStyle;

    @Inject @Config(value="skn-table_border")
    protected String htmlClass;

    @Inject @Config(value="skn-even_row")
    protected String rowEventClass;

    @Inject @Config(value="skn-odd_row")
    protected String rowOddClass;

    @Inject @Config(value="skn-even_row_alt")
    protected String rowHoverClass;

    @Inject @Config(value="text-align:center; background-color:#DEE5EC; height:20px; font-weight:Bold;")
    protected String groupByTotalsHtmlStyle;

    @Inject @Install @Table
    protected Instance<DataDisplayerRenderer> tableRenderers;

    @PostConstruct
    protected void init() {
        displayerRenderers = new ArrayList<DataDisplayerRenderer>();
        for (DataDisplayerRenderer type: tableRenderers) {
            displayerRenderers.add(type);
        }
    }

    public String getUid() {
        return uid;
    }

    public String getIconPath() {
        return iconPath;
    }

    public DataDisplayerXMLFormat getXmlFormat() {
        return xmlFormat;
    }

    public String getDescription(Locale l) {
        ResourceBundle i18n = ResourceBundle.getBundle("org.jboss.dashboard.displayer.table.messages", LocaleManager.currentLocale());
        return i18n.getString("tableDisplayer.description");
    }

    public DataDisplayer createDataDisplayer() {
        DataSetTableModel model = new DataSetTableModel();

        DataSetTable table = new DataSetTable();
        table.setModel(model);
        table.setMaxRowsPerPage(10);
        table.setCurrentPage(1);
        table.setGroupByTotalsHtmlStyle(groupByTotalsHtmlStyle);
        table.setHtmlClass(htmlClass);
        table.setHtmlStyle(htmlStyle);
        table.setMaxRowsPerPage(maxRowsPerPage);
        table.setRowEventClass(rowEventClass);
        table.setRowOddClass(rowOddClass);
        table.setRowHoverClass(rowHoverClass);

        TableDisplayer displayer = new TableDisplayer();
        displayer.setDataDisplayerType(this);
        displayer.setTable(table);
        return displayer;
    }

    public void copyDataDisplayer(DataDisplayer source, DataDisplayer target) {
        // Not supported in tables
    }
}
