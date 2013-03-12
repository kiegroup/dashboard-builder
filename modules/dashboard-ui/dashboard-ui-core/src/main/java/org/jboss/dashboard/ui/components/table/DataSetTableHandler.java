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

import org.jboss.dashboard.domain.DomainConfigurationParser;
import org.jboss.dashboard.ui.controller.CommandRequest;
import org.jboss.dashboard.displayer.table.DataSetTable;
import org.jboss.dashboard.domain.DomainConfiguration;
import org.jboss.dashboard.provider.DataProperty;

/**
 * Table component handler extension.
 */
public class DataSetTableHandler extends TableHandler {

    protected boolean showGroupByConfig;
    protected int groupBySelectedColumnIndex;

    public DataSetTableHandler() {
        super();
        groupBySelectedColumnIndex = -1;
        showGroupByConfig = false;
    }

    public boolean showGroupByConfig() {
        return showGroupByConfig;
    }

    public int getGroupBySelectedColumnIndex() {
        if (groupBySelectedColumnIndex < 0) {
            DataSetTable dataSetTable = (DataSetTable) table;
            int [] nonGroupByColumns = dataSetTable.getNonGroupByColumnIndexes();
            if (nonGroupByColumns.length > 0) groupBySelectedColumnIndex = nonGroupByColumns[0];
        }
        return groupBySelectedColumnIndex;
    }

    public void actionExecAction(CommandRequest request) throws Exception {
        String action = request.getRequestObject().getParameter("tableaction");
        if ("selectGroupByProperty".equals(action)) actionSelectGroupByProperty(request);
        else if ("showGroupByConfig".equals(action)) actionShowGroupByConfig(request);
        else super.actionExecAction(request);
    }

    public void actionSelectColumn(CommandRequest request) throws Exception {
        super.actionSelectColumn(request);

        // Close the group by edition When edit a column.  
        groupBySelectedColumnIndex = -1;
        showGroupByConfig = false;
    }

    public void actionSaveTable(CommandRequest request) throws Exception {
        DataSetTable dataSetTable = (DataSetTable) table;
        String groupByPropId = request.getRequestObject().getParameter("groupbyproperty");
        String closeGroupByConfig = request.getRequestObject().getParameter("closegroupbyconfig");
        if (groupByPropId != null && !groupByPropId.equals("-1") && showGroupByConfig) {
            // Capture the group by domain configuration.
            DomainConfiguration config = new DomainConfiguration();
            DomainConfigurationParser parser = new DomainConfigurationParser(config);
            parser.parse(request);
            config.apply(dataSetTable.getGroupByProperty());
            dataSetTable.setGroupByShowTotals(Boolean.valueOf(request.getRequestObject().getParameter("groupbyshowtotals")).booleanValue());
            dataSetTable.setGroupByTotalsHtmlStyle(request.getRequestObject().getParameter("groupbytotalshtmlstyle"));            

            // Get for the selected non-group by column the scalar function to apply.
            if (dataSetTable.getNonGroupByColumnIndexes().length > 0) {
                int currentSelectedColumnIndex = Integer.parseInt(request.getRequestObject().getParameter("groupbyfunctionindex"));
                if (currentSelectedColumnIndex == groupBySelectedColumnIndex) {
                    String functionCode = request.getRequestObject().getParameter("groupbyfunctioncode");
                    DataProperty originalDataProperty = dataSetTable.getOriginalDataProperty(groupBySelectedColumnIndex);
                    if (originalDataProperty.getDomain().isScalarFunctionSupported(functionCode)) {
                        dataSetTable.setGroupByFunctionCode(groupBySelectedColumnIndex, functionCode);
                    }
                } else {
                    groupBySelectedColumnIndex = currentSelectedColumnIndex;
                }
            }
            // Refresh the group by after applying the changes.
            dataSetTable.refreshGroupBy();

            // Close the group by config if requested.
            if (closeGroupByConfig != null && closeGroupByConfig.equals("true")) {
                showGroupByConfig = false;
            }
        }
        super.actionSaveTable(request);
    }
                     
    public void actionSelectGroupByProperty(CommandRequest request) throws Exception {
        DataSetTable dataSetTable =(DataSetTable) table;
        String groupByPropId = request.getRequestObject().getParameter("groupbyproperty");
        groupBySelectedColumnIndex = -1;
        dataSetTable.setCurrentPage(1);

        if (groupByPropId == null || groupByPropId.equals("-1")) {
            dataSetTable.setGroupByProperty(null);
            showGroupByConfig = false;
            setStructuralChangesAllowed(true);
        } else {
            dataSetTable.setGroupByProperty(dataSetTable.getOriginalDataSet().getPropertyById(groupByPropId).cloneProperty());
            showGroupByConfig = true;
            setStructuralChangesAllowed(false);

            // Close the column edition When edit the group by.
            selectedColumnIndex = null;
        }
    }

    public void actionSelectGroupByPropertyFunction(CommandRequest request) throws Exception {
        // Capture the group by domain configuration.
        DataSetTable dataSetTable =(DataSetTable) table;
        DomainConfiguration config = new DomainConfiguration();
        DomainConfigurationParser parser = new DomainConfigurationParser(config);
        parser.parse(request);
        config.apply(dataSetTable.getGroupByProperty());
        dataSetTable.setGroupByShowTotals(Boolean.valueOf(request.getRequestObject().getParameter("groupbyshowtotals")).booleanValue());
        dataSetTable.setGroupByTotalsHtmlStyle(request.getRequestObject().getParameter("groupbytotalshtmlstyle"));

        // Get the selected property for the group by function setup. 
        groupBySelectedColumnIndex = Integer.parseInt(request.getRequestObject().getParameter("groupbyfunctionindex"));
    }

    public void actionShowGroupByConfig(CommandRequest request) throws Exception {
        showGroupByConfig = !showGroupByConfig;

        // Close the column edition when edit the group by.
        selectedColumnIndex = null;
    }
}
