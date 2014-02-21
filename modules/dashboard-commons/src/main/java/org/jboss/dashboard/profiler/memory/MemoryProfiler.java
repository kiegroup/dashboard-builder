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
package org.jboss.dashboard.profiler.memory;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.dashboard.CoreServices;
import org.jboss.dashboard.annotation.config.Config;
import org.jboss.dashboard.commons.cdi.CDIBeanLocator;
import org.jboss.dashboard.database.hibernate.HibernateInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The memory profiler provides information about the total and available memory of the underlying JVM running instance.
 */
@ApplicationScoped
public class MemoryProfiler {

    private static transient Logger log = LoggerFactory.getLogger(MemoryProfiler.class.getName());

    public static MemoryProfiler lookup() {
        return (MemoryProfiler) CDIBeanLocator.getBeanByType(MemoryProfiler.class);
    }

    /**
     * The minimum amount (in bytes) of free available memory allowed by the application.
     */
    @Inject @Config("1000000") // Less than 1MB means memory is running low
    private long minMemorySize;

    /**
     * The minimum percentage of free available memory allowed by the application.
     */
    @Inject @Config("0.05") // Less than 5% free memory means memory is running low
    private float minMemoryPercentage;

    public long getMinMemorySize() {
        return minMemorySize;
    }

    public void setMinMemorySize(long minMemorySize) {
        this.minMemorySize = minMemorySize;
    }

    public float getMinMemoryPercentage() {
        return minMemoryPercentage;
    }

    public void setMinMemoryPercentage(float minMemoryPercentage) {
        this.minMemoryPercentage = minMemoryPercentage;
    }

    public boolean isLowMemory() {
        long freeMemory = getFreeMemoryInBytes();
        long totalMemory = getTotalMemoryInBytes();
        if (freeMemory < minMemorySize) return true;

        float percentage = (float) (freeMemory / (totalMemory * 1.0));
        if (percentage < minMemoryPercentage) return true;
        return false;
    }

    /**
     * Returns the amount of free memory in the Java Virtual Machine.
     * Calling the
     * <code>gc</code> method may result in increasing the value returned
     * by <code>freeMemory.</code>
     *
     * @return  an approximation to the total amount of memory currently
     *          available for future allocated objects, measured in bytes.
     */
    public long getFreeMemoryInBytes() {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * Returns the total amount of memory in the Java virtual machine.
     * The value returned by this method may vary over time, depending on
     * the host environment.
     * <p>
     * Note that the amount of memory required to hold an object of any
     * given type may be implementation-dependent.
     *
     * @return  the total amount of memory currently available for current
     *          and future objects, measured in bytes.
     */
    public long getTotalMemoryInBytes() {
        return Runtime.getRuntime().totalMemory();
    }

    public MemoryProfiler freeMemory() {
        long freeMemory = getFreeMemoryInBytes();
        collectGarbage();
        if (isLowMemory()) {
            freeEvenMoreMemory();
            log.warn("Hibernate caches had to be deleted to free memory. Consider decreasing cache sizes, or increasing memory for current application.");
        }
        long newFree = getFreeMemoryInBytes();
        long newTotal = getTotalMemoryInBytes();
        float percentage = (float) (newFree * 100.0/ (newTotal * 1.0));
        long freed = newFree - freeMemory;
        log.debug("Freed " + formatSize(freed) + " bytes. Total = " + formatSize(newTotal)  + ". Free = " + formatSize(newFree) + " (" + percentage + "%)");
        return this;
    }

    protected void freeEvenMoreMemory() {
        // Empty some caches... Drastic measure when memory is low and a System.gc was insufficient to release memory
        HibernateInitializer hibernateInitializer = CoreServices.lookup().getHibernateInitializer();
        hibernateInitializer.evictAllCaches();
        collectGarbage();
    }

    public long getMemoryUsedInBytes() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        /*collectGarbage();
        long totalMemory = Runtime.getRuntime().totalMemory();
        collectGarbage();
        long freeMemory = Runtime.getRuntime().freeMemory();
        return (totalMemory - freeMemory);*/
    }

    private void collectGarbage() {
        try {
            System.gc();
            Thread.currentThread().sleep(10);
            System.runFinalization();
            Thread.currentThread().sleep(10);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    public static final String SIZE_UNITS[] = new String[] {"bytes", "Kb", "Mb", "Gb", "Tb", "Pb"};

    public static String formatSize(long bytes) {
        for (int exp=SIZE_UNITS.length-1; exp>=0; exp--) {
            String sizeUnit = SIZE_UNITS[exp];
            double size = bytes / Math.pow(1024, exp);
            if (((long) size) > 0) {
                NumberFormat df = DecimalFormat.getInstance();
                return df.format(size) + " " + sizeUnit;
            }
        }
        return bytes + " bytes";
    }
}
