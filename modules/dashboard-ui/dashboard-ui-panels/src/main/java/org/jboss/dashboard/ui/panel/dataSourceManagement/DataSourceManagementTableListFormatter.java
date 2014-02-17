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
package org.jboss.dashboard.ui.panel.dataSourceManagement;

import org.jboss.dashboard.ui.taglib.formatter.Formatter;
import org.jboss.dashboard.ui.taglib.formatter.FormatterException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

public class DataSourceManagementTableListFormatter extends Formatter {

    private static String TR_ALT_CLASS = "skn-even_row";
    private static String TR_ON_CLASS = "skn-row_on";

    @Inject
    private transient Logger log;

    @Inject
    private DataSourceManagementHandler handler;

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            //get all tables of the datasource that has been edited
            if(handler.getINTROSPECT_MODE()){
                String dsName = handler.getDS_EDIT();
                setAttribute("dsName",dsName);
                renderFragment("outputTitleForm");
                if(handler.getINTROSPECT_RESULT().equals(handler.RESULT_OK)){
                    renderFragment("outputTitleTableTD");
                    List existingTableEntries = handler.getIntrospectedTables(dsName);
                    List selectedTablesList = DataSourceTableManager.lookup().getSelectedTablesName(dsName);
                    Collections.sort(selectedTablesList);
                    int i, index;
                    String checked, trClass, currentTrClass;
                    for (i=0; i < existingTableEntries.size(); i++) {
                        checked = "";
                        DataSourceTableEntry entry = (DataSourceTableEntry) existingTableEntries.get(i);

                        if((i%2)==0)
                            trClass = TR_ALT_CLASS;
                        else
                            trClass = "";

                        currentTrClass=trClass;
                        //search current entrie in selected list
                        if(selectedTablesList.size()>0){
                            index = Collections.binarySearch(selectedTablesList,entry.getName());
                            if(index>=0){
                                checked = "checked";
                                selectedTablesList.remove(index);
                                currentTrClass = TR_ON_CLASS;
                            }
                        }
                        setAttribute("checked", checked);
                        setAttribute("tableName", entry.getName());
                        setAttribute("tableIndex", i);
                        setAttribute("currentTrClass", currentTrClass);
                        setAttribute("trClass", trClass);

                        renderFragment("outputRow");
                    }
                    renderFragment("outputEndRow");
                    renderFragment("outputBeginButtonsTR");
                    renderFragment("outputSaveButton");
                    renderFragment("outputCancelButton");
                    renderFragment("outputEndButtonsTR");
                    setAttribute("numberOfTables",i);
                    renderFragment("outputEnd");
                }
                else{
                    setAttribute("errorDescription",handler.getINTROSPECT_RESULT());
                    renderFragment("outputDSError");
                    renderFragment("outputBeginButtonsTR");
                    renderFragment("outputCancelButton");
                    renderFragment("outputEndButtonsTR");
                    renderFragment("outputEnd");
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }
}
