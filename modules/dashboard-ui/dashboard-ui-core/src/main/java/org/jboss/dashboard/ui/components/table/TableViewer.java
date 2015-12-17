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
package org.jboss.dashboard.ui.components.table;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.dashboard.displayer.table.Table;
import org.jboss.dashboard.ui.annotation.panel.PanelScoped;
import org.jboss.dashboard.ui.components.DataDisplayerViewer;
import org.jboss.dashboard.displayer.DataDisplayer;
import org.jboss.dashboard.displayer.table.TableDisplayer;
import org.jboss.dashboard.ui.controller.CommandResponse;
import org.jboss.dashboard.ui.controller.CommandRequest;

@PanelScoped
@Named("default_table_viewer")
public class TableViewer extends DataDisplayerViewer {

    @Inject
    protected DataSetTableViewer tableHandler;

    public String getBeanJSP() {
        return "/components/bam/displayer/table/table_viewer.jsp";
    }

    public TableHandler getTableHandler() {
        return tableHandler;
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
