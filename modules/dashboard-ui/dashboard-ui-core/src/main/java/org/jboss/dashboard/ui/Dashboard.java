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
package org.jboss.dashboard.ui;

import org.jboss.dashboard.ui.components.DashboardHandler;
import org.jboss.dashboard.ui.controller.RequestContext;
import org.jboss.dashboard.ui.panel.DashboardDriver;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.provider.DataProvider;
import org.jboss.dashboard.provider.DataProperty;
import org.jboss.dashboard.ui.components.DashboardFilterProperty;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.domain.CompositeInterval;
import org.jboss.dashboard.domain.label.LabelInterval;
import org.jboss.dashboard.domain.date.DateInterval;
import org.jboss.dashboard.domain.numeric.NumericInterval;
import org.jboss.dashboard.commons.events.Publisher;
import org.jboss.dashboard.commons.filter.FilterByCriteria;
import java.util.*;

import org.jboss.dashboard.ui.panel.AjaxRefreshManager;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jboss.dashboard.ui.panel.PanelDriver;
import org.jboss.dashboard.workspace.Panel;
import org.jboss.dashboard.workspace.Section;

/**
 * A dashboard.
 */
public class Dashboard {

    /** Logger */
    protected static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Dashboard.class);

    /**
     * Dashboard section.
     */
    protected Long sectionDbid;

    /**
     * The dashboard filter.
     */
    protected DashboardFilter dashboardFilter;

    /**
     * Parent dashboard for Drill-down.
     */
    protected Dashboard parent;

    /**
     * Parent property to drill-up if dashboard filter does not contains it.
     */
    protected String parentProperty;

    /**
     * Dashboard listeners
     */
    protected transient Publisher listeners;

    public Dashboard() {
        sectionDbid = null;
        parent = null;
        listeners = new Publisher();
        setDashboardFilter(new DashboardFilter());
    }

    public int hashCode() {
        return new HashCodeBuilder().append(sectionDbid).toHashCode();
    }

    public boolean equals(Object obj) {
        try {
            if (obj == null) return false;
            if (obj == this) return true;
            if (sectionDbid == null) return false;

            Dashboard other = (Dashboard) obj;
            return sectionDbid.equals(other.sectionDbid);
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return new ToStringBuilder(this).append("sectionDbid", sectionDbid).toString();
    }

    public void addListener(DashboardListener listener) {
        listeners.subscribe(listener);
    }

    public String getParentProperty() {
        return parentProperty;
    }

    public void setParentProperty(String parentProperty) {
        this.parentProperty = parentProperty;
    }

    public Dashboard getParent() {
        return parent;
    }

    // Parent dashboard must be set using applyDrillDown to avoid infinite parent loop.
    private void setParent(Dashboard parent) {
        this.parent = parent;
    }

    public DashboardFilter getDashboardFilter() {
        return dashboardFilter;
    }

    public void setDashboardFilter(DashboardFilter dashboardFilter) {
        this.dashboardFilter = dashboardFilter;
        if (dashboardFilter != null) this.dashboardFilter.setDashboard(this);
    }

    public Section getSection() {
        try {
            if (sectionDbid == null) return null;
            return UIServices.lookup().getSectionsManager().getSectionByDbId(sectionDbid);
        } catch (Throwable e) {
            log.error("Error getting section: " + sectionDbid, e);
            return null;
        }
    }

    public void setSection(Section newSection) {
        sectionDbid = newSection.getDbid();
    }

    /**
     * Check if a given panel belongs to this dashboard.
     */
    public boolean belongsToDashboard(Panel panel) {
        return panel.getSection().equals(getSection());
    }

    public Set<DataProvider> getDataProviders() {
        Set<DataProvider> results = new HashSet<DataProvider>();
        for (Panel panel : getSection().getPanels()) {
            KPI kpi = DashboardHandler.lookup().getKPI(panel);

            // The KPI is null if the panel is not assigned to a region.
            if (kpi != null) results.add(kpi.getDataProvider());
        }
        return results;
    }

    public DataProvider getDataProviderByCode(String code) {
        if (code == null) return null;
        for (Object o : getDataProviders()) {
            DataProvider p = (DataProvider) o;
            if (code.equals(p.getCode())) return p;
        }
        return null;
    }

    /**
     * Search for a data property into the data providers.
     * @return The first data property found or null.
     */
    public DataProperty getDataPropertyById(String propertyId) {
        // Search for the property both in this dashboard and in its parents.
        Dashboard dashboard = this;
        while (dashboard != null) {
            Iterator it = dashboard.getDataProviders().iterator();
            while (it.hasNext()) {
                DataProvider provider = (DataProvider) it.next();
                try {
                    DataProperty p = provider.getDataSet().getPropertyById(propertyId);
                    if (p != null) return p;
                } catch (Exception e) {
                    log.error("Dashboard provider dataset load: " + provider.getCode(), e);
                    continue;
                }
            }
            // If the property is not found look at the parent dashboard (if exists).
            dashboard = dashboard.getParent();
        }
        return null;
    }

    /**
     * Init the dashboard with the KPIs in the section.
     * <p>All the KPIs and its related data sets are refreshed. The dashboard filter is cleared as well.
     */
    public void init() {
        // Clear the current dashboard filter.
        dashboardFilter.init();

        // Clear drill-down if any.
        parent = null;
        parentProperty = null;

        // Populate the dashboard with KPIs data for the first time.
        refresh();
    }

    /**
     * Refresh all the KPI data sets for this dashboard. The current dashboard filter is preserved.
     */
    public void refresh() {
        try {
            // A section instance is required.
            if (sectionDbid == null) return;

            // Reload all the data providers referenced by this dashboard.
            for (DataProvider dataProvider : getDataProviders()) {
                dataProvider.refreshDataSet();
            }

            // Preserve the current filter after refresh (if any)
            if (dashboardFilter.getPropertyIds().length > 0) {
                filter();
            }
            // Refresh all the dashboard panels.
            refreshPanels(null);
        } catch (Exception e) {
            throw new RuntimeException("Filter error after refresh the dashboard.", e);
        }
    }

    public boolean filter(String propertyId, Interval interval, int allowMode) throws Exception {
        // Get min, max and allowed values from interval.
        Object minValue = null; Object maxValue = null; Collection values = null;
        boolean minValueIncluded = true; boolean maxValueIncluded = true;
        if (interval instanceof NumericInterval) {
            minValue = ((NumericInterval)interval).getMinValue();
            minValueIncluded = ((NumericInterval)interval).isMinValueIncluded();
            maxValue = ((NumericInterval)interval).getMaxValue();
            maxValueIncluded = ((NumericInterval)interval).isMaxValueIncluded();
        }

        if (interval instanceof DateInterval) {
            minValue = ((DateInterval)interval).getMinDate();
            minValueIncluded = ((DateInterval)interval).isMinDateIncluded();
            maxValue = ((DateInterval)interval).getMaxDate();
            maxValueIncluded = ((DateInterval)interval).isMaxDateIncluded();
        }

        if (interval instanceof LabelInterval) {
            if (dashboardFilter.getPropertyIds().length == 0) values = Arrays.asList(interval);
            else values = Arrays.asList(((LabelInterval) interval).getLabel());
        }

        if (interval instanceof CompositeInterval) {
            minValue = ((CompositeInterval)interval).getMinValue();
            minValueIncluded = ((CompositeInterval)interval).isMinValueIncluded();
            maxValue = ((CompositeInterval)interval).getMaxValue();
            maxValueIncluded = ((CompositeInterval)interval).isMaxValueIncluded();
            if (minValue == null && maxValue == null) {
                if (dashboardFilter.getPropertyIds().length == 0) values = Arrays.asList(interval);
                else values = new HashSet(interval.getValues(interval.getDomain().getProperty()));
            }
        }

        return filter(propertyId, minValue, minValueIncluded, maxValue, maxValueIncluded, values, allowMode);
    }

    public boolean filter(String propertyId, Object minValue, boolean minValueIncluded, Object maxValue, boolean maxValueIncluded, Collection allowedValues, int allowMode) throws Exception {

        // Get the filter property configuration.
        DashboardFilterProperty dashboardFilterProperty = dashboardFilter.getPropertyInFilterComponents(propertyId);

        // Apply drill-down.
        if (dashboardFilterProperty != null && dashboardFilterProperty.isDrillDownEnabled()) {
            Dashboard targetDashboard = dashboardFilterProperty.getDrillDownDashboard();
            DashboardFilter targetFilter = targetDashboard.getDashboardFilter();
            if (targetDashboard.drillDown(this, propertyId)) {
                targetFilter.addProperty(propertyId, minValue, minValueIncluded, maxValue, maxValueIncluded, allowedValues, FilterByCriteria.ALLOW_ANY);
                targetDashboard.filter();
                return true;
            }
        }
        // Apply filter only.
        else {
            dashboardFilter.addProperty(propertyId, minValue, minValueIncluded, maxValue, maxValueIncluded, allowedValues, FilterByCriteria.ALLOW_ANY);
            filter();
            refreshPanels(dashboardFilter.getPropertyIds());
        }
        return false;
    }

    public boolean unfilter(String propertyId) throws Exception {
        if (dashboardFilter.containsProperty(propertyId)) {
            dashboardFilter.removeProperty(propertyId);
            Dashboard parent = drillUp();
            if (parent != null) {
                parent.filter();
                return true;
            } else {
                filter();
                refreshPanels(new String[]{propertyId});
            }
        }
        return false;
    }

    public boolean unfilter() throws Exception {
        String[] propIds = dashboardFilter.getPropertyIds();
        dashboardFilter.init();
        Dashboard parent = drillUp();
        if (parent != null) {
            parent.filter();
            return true;
        } else {
            refreshPanels(propIds);
            filter();
            return false;
        }
    }

    protected void filter() throws Exception {
        for (Object o : getDataProviders()) {
            DataProvider provider = (DataProvider) o;
            provider.filterDataSet(dashboardFilter);
        }
    }

    protected boolean drillDown(Dashboard parent, String parentPropertyId) {
        if (parent == null || parentPropertyId == null) return false;

        // Check if this dashboard is already in the drill down chain.
        if (!isDrillDownAllowed(parent)) {
            log.warn("Loop detected on drill-down between dashboards. Please review your filter's configuration and avoid dashboards drill-down loops.");
            return false;
        }

        // Set the parent.
        setParent(parent);
        setParentProperty(parentPropertyId);

        // Set drill down page.
        NavigationManager.lookup().setCurrentSection(getSection());
        listeners.notifyEvent(DashboardListener.EVENT_DRILL_DOWN, new Object[] {parent, this});

        // Apply parent dashboard parentFilter properties to target parentFilter.
        DashboardFilter filter = getDashboardFilter();
        filter.removeAllProperty();
        while (parent != null) {
            DashboardFilter parentFilter = parent.getDashboardFilter();

            // Add properties from parentFilter.
            String[] props = parentFilter.getPropertyIds();
            for (int i = 0; i < props.length; i++) {
                String prop = props[i];
                Object minValue = parentFilter.getPropertyMinValue(prop);
                Object maxValue = parentFilter.getPropertyMaxValue(prop);
                boolean minValueIncluded = parentFilter.minValueIncluded(prop);
                boolean maxValueIncluded = parentFilter.maxValueIncluded(prop);
                Collection allowedValues = parentFilter.getPropertyAllowedValues(prop);
                int allowMode = parentFilter.getPropertyAllowMode(prop);
                filter.addProperty(prop,minValue, minValueIncluded, maxValue, maxValueIncluded, allowedValues, allowMode);
            }
            // Get the next parent.
            parent = parent.getParent();
        }
        return true;
    }

    protected Dashboard drillUp() {
        String parentProperty = getParentProperty();
        if (parentProperty == null) return null;

        Dashboard parent = this;
        Dashboard firstParent = null;
        while (parentProperty != null) {
            if (!ArrayUtils.contains(getDashboardFilter().getPropertyIds(), parentProperty)) {
                // Apply drill-up.
                firstParent = parent.getParent();
            }
            parent = parent.getParent();
            if (parent != null) parentProperty = parent.getParentProperty();
            else parentProperty = null;
        }
        // No drill-up detected
        if (firstParent == null) return null;

        // Return back to the parent dashboard.
        NavigationManager.lookup().setCurrentSection(firstParent.getSection());
        listeners.notifyEvent(DashboardListener.EVENT_DRILL_UP, new Object[] {firstParent, this});
        return firstParent;
    }

    /**
     * Check if drill down from the specified page to this one is allowed.
     * @param fromParent The parent dasboard we are coming.
     */
    public boolean isDrillDownAllowed(Dashboard fromParent) {
        if (fromParent == null) return true;
        if (this.equals(fromParent)) return false;

        return isDrillDownAllowed(fromParent.getParent());
    }

    /**
     * Refresh those dashboard panels that are handling any of the properties specified.
     * @param propertySet If null then refresh all the dashboard panels.
     */
    protected void refreshPanels(String[] propertySet) throws Exception {
        AjaxRefreshManager ajaxMgr = AjaxRefreshManager.lookup();
        List panelIdsToRefresh = ajaxMgr.getPanelIdsToRefresh();
        panelIdsToRefresh.clear();

        // Inspect all the dashboard's panels.
        Panel currentPanel = RequestContext.lookup().getActivePanel();
        for (Panel panel : getSection().getPanels()) {

            // Leave out non dashboard related panels.
            PanelDriver driver = panel.getProvider().getDriver();
            if (!(driver instanceof DashboardDriver)) {
                continue;
            }
            // Don't refresh the active panel as it's being updated already along the execution of this request.
            Long panelId = panel.getPanelId();
            if (currentPanel != null && currentPanel.getPanelId().equals(panelId)) {
                continue;
            }
            // Don't refresh panels that are not displaying any dashboard data.
            Set<DataProvider> providersUsed = ((DashboardDriver) driver).getDataProvidersUsed(panel);
            if (providersUsed.isEmpty()) {
                continue;
            }
            // Mark panel as refreshable.
            if (propertySet == null) {
                panelIdsToRefresh.add(panelId);
            } else {
                for (int i = 0; i < propertySet.length; i++) {
                    String propertyId = propertySet[i];
                    for (DataProvider dataProvider : providersUsed) {
                        if (!panelIdsToRefresh.contains(panelId) && dataProvider.getDataSet().getPropertyById(propertyId) != null) {
                            panelIdsToRefresh.add(panelId);
                        }
                    }
                }
            }
        }
    }
}
