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
package org.jboss.dashboard.ui.components.table;

import org.jboss.dashboard.displayer.table.Table;
import org.jboss.dashboard.ui.components.DataDisplayerViewer;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.table.TableDisplayer;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.CommandRequest;


public class TableViewer extends DataDisplayerViewer {

    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(TableViewer.class);

    /**
     * The table component handler.
     */
    protected TableHandler tableHandler;

    public TableViewer() {
        super();
    }

    public TableHandler getTableHandler() {
        return tableHandler;
    }

    public void setTableHandler(TableHandler tableHandler) {
        this.tableHandler = tableHandler;
    }

    public void setDataDisplayer(DataDisplayer dataDisplayer) {
        super.setDataDisplayer(dataDisplayer);

        // Pass the table to the table handler.
        TableDisplayer tableDisplayer = (TableDisplayer) dataDisplayer;
        Table table = tableDisplayer.getTable();
        tableHandler.setTable(table);
    }

    public CommandResponse actionSubmitViewer(CommandRequest request) throws Exception {
        return tableHandler.actionExecAction(request);
    }

    public CommandResponse actionExportData(CommandRequest request) throws Exception {
        String format = request.getRequestObject().getParameter(TableHandler.EXPORT_FORMAT);
        return tableHandler.actionExportData(format);
    }
}