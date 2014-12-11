package org.jboss.dashboard.showcase;

import java.io.File;
import java.io.FileInputStream;

import org.jboss.dashboard.Application;
import org.jboss.dashboard.DataDisplayerServices;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.export.ImportManager;
import org.jboss.dashboard.export.ImportResults;
import org.jboss.dashboard.kpi.KPI;
import org.jboss.dashboard.profiler.CodeBlockTrace;
import org.jboss.dashboard.test.KPITestMethodGenerator;
import org.jboss.dashboard.test.MavenProjectHelper;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Generates the JUnit test methods used to check that all the Showcase dashboard KPIs.
 */
public class ShowcaseKPITestsGenerator {

    public static void main(String[] args) throws Exception {
        WeldContainer container = new Weld().initialize();
        CDIBeanLocator.beanManager = container.getBeanManager();
        CodeBlockTrace.RUNTIME_CONTRAINTS_ENABLED = false;

        File rootDir = MavenProjectHelper.getModuleDir("dashboard-samples");
        File webAppDir = new File(rootDir, "src/main/webapp");
        Application.lookup().setBaseAppDirectory(webAppDir.getAbsolutePath());

        File kpisFile = new File(webAppDir, "WEB-INF/deployments/showcaseKPIs.kpis");
        FileInputStream is = new FileInputStream(kpisFile);

        ImportManager importManager = DataDisplayerServices.lookup().getImportManager();
        ImportResults context = importManager.parse(is);

        // Just copy the output generated as replacing methods of the ShowcaseKpisTest class.
        for (KPI kpi : context.getKPIs()) {
            // Leave out report-like KPIs with a large number of rows.
            if (kpi.getCode().equals("kpi_30771353684719633")) continue;
            if (kpi.getCode().equals("kpi_29761353668431694")) continue;

            String method = KPITestMethodGenerator.generateKPITestMethod(kpi);
            System.out.println(method);
        }
    }
}
