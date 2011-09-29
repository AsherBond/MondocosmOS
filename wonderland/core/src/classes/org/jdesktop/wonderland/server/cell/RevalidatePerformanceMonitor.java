/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.server.cell;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collect performance metrics on the times it takes to revalidate the
 * a cell cache
 * @author paulby
 */

public class RevalidatePerformanceMonitor {
    // frequency to print totals
    private static final String PRINT_TOTALS_FREQUENCY_DEFAULT = "1000";
    private static final String PRINT_TOTALS_PROP = 
            RevalidatePerformanceMonitor.class.getName() + ".printTotals";
    private static int printTotalsFreq;
    
    // frequency to sample individual transactions
    private static final String PRINT_INDIVIDUAL_FREQUENCY_DEFAULT = "-1";
    private static final String PRINT_INDIVIDUAL_PROP =
            RevalidatePerformanceMonitor.class.getName() + ".printIndividual";
    private static int printIndividualFreq;
    
    // totals
    private static int  totalCount;
    private static long totalTime;
    private static long totalException;
    
    private static int  boundsTotalCount;
    private static long boundsTotalTime;
    private static int  boundsMinCount;
    private static long boundsMinTime;
    private static int  boundsMaxCount;
    private static long boundsMaxTime;
    
    private static int  visibleTotalCount;
    private static long visibleTotalTime;
    private static int  visibleMinCount;
    private static long visibleMinTime;
    private static int  visibleMaxCount;
    private static long visibleMaxTime;
    
    private static int  changeTotalCount;
    private static long changeTotalTime;
    private static int  newTotalCount;
    private static long newTotalTime;
    private static int  updateTotalCount;
    private static long updateTotalTime;
    private static int  oldTotalCount;
    private static long oldTotalTime;
    
    private static int  changeMinCount;
    private static long changeMinTime;
    private static int  changeMaxCount;
    private static long changeMaxTime;
    
    private static int  messageTotalCount;
    
    // individual values
    private long userTotalTime;
    
    private int  boundsCellCount;
    private long boundsGetTime;
    private long boundsCalcTime;
    
    private int  visibleCellCount;
    
    private int  newCellCount;
    private long newCellTime;
    private int  updateCellCount;
    private long updateCellTime;
    private int  oldCellCount;
    private long oldCellTime;
    
    private int msgCount;
    
    private boolean exception = false;
    
    private final Map<Class,CellClassStats> boundsClassStats =
            new TreeMap<Class, CellClassStats>(new Comparator<Class>() {
        public int compare(Class arg0, Class arg1) {
            return arg0.getName().compareTo(arg1.getName());
        } 
    });
    
    static {
        resetTotals();
    }
    
    public RevalidatePerformanceMonitor() {
    }
      
    public synchronized static void resetTotals() {
        // read frequencies from system property
        printTotalsFreq = Integer.parseInt(System.getProperty(PRINT_TOTALS_PROP,
                                           PRINT_TOTALS_FREQUENCY_DEFAULT));
        printIndividualFreq = Integer.parseInt(System.getProperty(PRINT_INDIVIDUAL_PROP,
                                               PRINT_INDIVIDUAL_FREQUENCY_DEFAULT));
        
        // reset counters
        totalCount        = 0;
        totalTime         = 0;
        totalException    = 0;
        boundsTotalCount  = 0;
        boundsTotalTime   = 0;
        boundsMinCount    = Integer.MAX_VALUE;
        boundsMinTime     = Long.MAX_VALUE;
        boundsMaxCount    = 0;
        boundsMaxTime     = 0;
        visibleTotalCount = 0;
        visibleTotalTime  = 0;
        visibleMinCount   = Integer.MAX_VALUE;
        visibleMinTime    = Long.MAX_VALUE;
        visibleMaxCount   = 0;
        visibleMaxTime    = 0;
        changeTotalCount  = 0;
        changeTotalTime   = 0;
        newTotalCount     = 0;
        newTotalTime      = 0;
        updateTotalCount  = 0;
        updateTotalTime   = 0;
        oldTotalCount     = 0;
        oldTotalTime      = 0;
        changeMinCount    = Integer.MAX_VALUE;
        changeMinTime     = Long.MAX_VALUE;
        changeMaxCount    = 0;
        changeMaxTime     = 0;
        messageTotalCount = 0;
    }
    
    public synchronized static boolean printTotals() {
        return (printTotalsFreq > 0) && (totalCount >= printTotalsFreq);
    }
    
    public synchronized static boolean printSingle() {
        return (printIndividualFreq > 0) && ((totalCount % printIndividualFreq) == 0);
    }
    
    public synchronized static String getTotals() {
        StringBuffer stats = new StringBuffer();
        stats.append("\nRevalidates: " + totalCount);
        stats.append(" average: " + scale(totalTime / totalCount));
        stats.append(" exception: " + totalException + "\n");
        
        stats.append("Details:\n");
        stats.append("  Total cells at: \n");
        stats.append("        Min    : " + boundsMinCount);
        stats.append(" / " + scale(boundsMinTime) + "\n");
        if (totalCount!=0) {
            stats.append("        Average: " + (boundsTotalCount / totalCount));
            stats.append(" / " + scale(boundsTotalTime / totalCount) + "\n");
        }
        stats.append("        Max    : " + boundsMaxCount);
        stats.append(" / " + scale(boundsMaxTime) + "\n");
        
        stats.append("  Visible cells  : \n");
        stats.append("        Min    : " + visibleMinCount);
        stats.append(" / " + scale(visibleMinTime) + "\n");
        if (totalCount!=0) {
            stats.append("        Average: " + (visibleTotalCount / totalCount));
            stats.append(" / " + scale(visibleTotalTime / totalCount) + "\n");
        }
        stats.append("        Max    : " + visibleMaxCount);
        stats.append(" / " + scale(visibleMaxTime) + "\n");
        
        stats.append("  Changed cells  : \n");
        stats.append("        Min    : " + changeMinCount);
        stats.append(" / " + scale(changeMinTime) + "\n");
        if (totalCount!=0 &&
                newTotalCount!=0 &&
                oldTotalCount!=0) {
            stats.append("        Average: " + (changeTotalCount / totalCount));
            stats.append(" / " + scale(changeTotalTime / changeTotalCount) + "\n"); 
            stats.append("          Added  : " + (newTotalCount / totalCount));
            stats.append(" / " + scale(newTotalTime / newTotalCount) + "\n");
            stats.append("          Updated: " + (updateTotalCount / totalCount));
            stats.append(" / " + scale(updateTotalTime / updateTotalCount) + "\n");
            stats.append("          Removed: " + (oldTotalCount / totalCount));
            stats.append(" / " + scale(oldTotalTime / oldTotalCount) + "\n");
            stats.append("        Max    : " + changeMaxCount);
            stats.append(" / " + scale(changeMaxTime) + "\n");
        }
        
        if (totalCount!=0) {
            stats.append("  Messages Sent  : \n");
            stats.append("        Average: " + (messageTotalCount / totalCount));
        }
        
        return stats.toString();
    }
    
    private synchronized static void record(RevalidatePerformanceMonitor monitor) {
        if (monitor.exception) {
            totalException++;
            
            // don't collect statistics when there is an exception
            return;
        }
        
        totalCount++;
        totalTime += monitor.userTotalTime;
        
        boundsTotalCount  += monitor.boundsCellCount;
        if (monitor.boundsCellCount > boundsMaxCount) {
            boundsMaxCount = monitor.boundsCellCount;
        }
        if (monitor.boundsCellCount < boundsMinCount) {
            boundsMinCount = monitor.boundsCellCount;
        }
        
        long userBoundsTotalTime = monitor.boundsCalcTime + monitor.boundsGetTime;
        boundsTotalTime   += userBoundsTotalTime;
        if (userBoundsTotalTime > boundsMaxTime) {
            boundsMaxTime = userBoundsTotalTime;
        }
        if (userBoundsTotalTime < boundsMinTime) {
            boundsMinTime = userBoundsTotalTime;
        }
        
        visibleTotalCount += monitor.visibleCellCount;
        if (monitor.visibleCellCount > visibleMaxCount) {
            visibleMaxCount = monitor.visibleCellCount;
        }
        if (monitor.visibleCellCount < visibleMinCount) {
            visibleMinCount = monitor.visibleCellCount;
        }
        
        long userVisibleTotalTime = monitor.userTotalTime - monitor.boundsGetTime - monitor.boundsCalcTime;
        visibleTotalTime  += userVisibleTotalTime;
        if (userVisibleTotalTime > visibleMaxTime) {
            visibleMaxTime = userVisibleTotalTime;
        }
        if (userVisibleTotalTime < visibleMinTime) {
            visibleMinTime = userVisibleTotalTime;
        }
        
        int changeCount = monitor.newCellCount + monitor.updateCellCount +
                          monitor.oldCellCount;
        long changeTime = monitor.newCellTime + monitor.updateCellTime +
                          monitor.oldCellTime;  
        
        changeTotalCount += changeCount;
        changeTotalTime  += changeTime;
        newTotalCount    += monitor.newCellCount;
        newTotalTime     += monitor.newCellTime;
        updateTotalCount += monitor.updateCellCount;
        updateTotalTime  += monitor.updateCellTime;
        oldTotalCount    += monitor.oldCellCount;
        oldTotalTime     += monitor.oldCellTime;
        
        if (changeCount > changeMaxCount) {
            changeMaxCount = changeCount;
        }
        if (changeCount < changeMinCount) {
            changeMinCount = changeCount;
        }
        
        if (changeTime > changeMaxTime) {
            changeMaxTime = changeTime;
        }
        if (changeTime < changeMinTime) {
            changeMinTime = changeTime;
        }
        
        messageTotalCount += monitor.msgCount;
    }
    
    public void updateTotals() {
        record(this);
    }
   
    public String getRevalidateStats() {
        StringBuffer stats = new StringBuffer();
        stats.append("Total Cell Count " + boundsCellCount);
        stats.append(" Total time " + scale(userTotalTime));
        stats.append(" Exception: " + exception + "\n");

        stats.append("Bounds Calculation:\n");
        stats.append("  Bounds calc time " + scale(boundsCalcTime));
        stats.append("  Sgs get object time "+ scale(boundsGetTime) + "\n");
        
        for (Map.Entry<Class, CellClassStats> me : boundsClassStats.entrySet()) {
            CellClassStats ccs = me.getValue();
            stats.append("  " + me.getKey().getSimpleName() + " :");
            stats.append("  Total time: " + scale(ccs.incTime.get()));
            stats.append("  Count: " + ccs.incCount.get() + ".");
            stats.append("  Average: " + scale(ccs.incTime.get()/ccs.incCount.get()) + "\n");
        }
        
        int changedCells = newCellCount + updateCellCount + oldCellCount;
        stats.append("Cell updates (" + changedCells + " / " + boundsCellCount + "):\n");
        
        if (newCellCount > 0) {
            stats.append("  New cells   : " + newCellCount + " / " + scale(newCellTime));
            stats.append(" Average: " + scale(newCellTime / newCellCount) + "\n");
        }
        
        if (updateCellCount > 0) {
            stats.append("  Update cells: " + updateCellCount + " / " + scale(updateCellTime));
            stats.append(" Average: " + scale(updateCellTime / updateCellCount) + "\n");
        }
        
        if (oldCellCount > 0) {
            stats.append("  Remove cells: " + oldCellCount + " / " + scale(oldCellTime));
            stats.append(" Average: " + scale(oldCellTime / oldCellCount) + "\n");
        }
        
        stats.append("  Messages sent: " + msgCount);
        
        return stats.toString();
    }
    
    public void setVisibleCellCount(int visibleCellCount) {
        this.visibleCellCount = visibleCellCount;
    }
    
    public void setException(boolean exception) {
        this.exception = exception;
    }
    
    public void incTotalTime(long incNanoSeconds) {
        userTotalTime += incNanoSeconds;
    }
     
    public void incNewCellTime(long incNanoSeconds) {
        newCellCount += 1;
        newCellTime += incNanoSeconds;
    }
    
    public void incUpdateCellTime(long incNanoSeconds) {
        updateCellCount += 1;
        updateCellTime += incNanoSeconds;
    }
    
    public void incOldCellTime(long incNanoSeconds) {
        oldCellCount += 1;
        oldCellTime += incNanoSeconds;
    }
    
    /**
     *  Increment the calc time by specified number of nano seconds
     */
    public void incBoundsCalcTime(long incNanoSeconds) {
        boundsCalcTime+=incNanoSeconds;
    }
    
    public void incBoundsGetTime(Class c, long incNanoSeconds) {
        boundsGetTime+=incNanoSeconds;
    
        CellClassStats stats = getBoundsClassStats(c);
        stats.incTime.addAndGet(incNanoSeconds);
    }
    
    public void incBoundsCellCount(Class c) {
        boundsCellCount++;
        
        CellClassStats stats = getBoundsClassStats(c);
        stats.incCount.incrementAndGet();
    }
    
    public void incMessageCount() {
        msgCount++;
    }
    
    private final CellClassStats getBoundsClassStats(Class c) {
        CellClassStats out = boundsClassStats.get(c);
        if (out == null) {
            out = new CellClassStats();
            out.incCount = new AtomicInteger();
            out.incTime = new AtomicLong();
            
            boundsClassStats.put(c, out);
        }
        
        return out;
    }
    
    private static final String scale(long nanoseconds) {
        double val = (double) nanoseconds / 1000000;
        return String.format("%.3f", val) + " ms.";
    }
    
    class CellClassStats {
        AtomicLong incTime;
        AtomicInteger incCount;
    }
}
