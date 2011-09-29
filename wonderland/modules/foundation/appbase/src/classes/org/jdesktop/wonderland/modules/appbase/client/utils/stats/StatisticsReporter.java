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
package org.jdesktop.wonderland.modules.appbase.client.utils.stats;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.StableAPI;

/**
 * Periodically reports statistics. This class reports the values 
 * over the last measurement period, max, and cumulative values.
 *
 * @author deronj
 */ 

@StableAPI
public class StatisticsReporter implements Runnable {

    private static final Logger logger = Logger.getLogger(StatisticsReporter.class.getName());

    static {
	logger.setLevel(Level.INFO);
    }

    // By default report statistics every 30 sec
    private static final int REPORT_PERIOD_MS = 30000; 

    protected int reportPeriodMs = REPORT_PERIOD_MS;

    protected StatisticsSet period;
    protected StatisticsSet max;
    protected StatisticsSet cumulative;

    protected double startTimeSecs;
    protected double probeIntervalSecs;
    protected double totalSecs;

    private boolean stop;

    private Thread thread;

    private String name;

    /**
     * statSetClass specifies the subclass of StatisticSet that this reporter
     * should report for.
     */
    public StatisticsReporter (StatisticsSet period, StatisticsSet max, 
			       StatisticsSet cumulative) {
	this.period = period;
	this.max = max;
	this.cumulative = cumulative;
	name = period.getName();
    }

    public StatisticsReporter (int reportPeriodSecs, StatisticsSet period, 
			       StatisticsSet max, StatisticsSet cumulative) {
	this(period, max, cumulative);
	reportPeriodMs = reportPeriodSecs * 1000;
    }

    public void start () {
	thread = new Thread(this, name + " Statistics Reporter");
	logger.warning("Starting " + name + " Statistics Reporter");
	thread.start();
    }

    public void stop () {
	stop = true;
	thread = null;
    }

    private double currentTimeMillis () {
	long currentTimeNanos = System.nanoTime();
	return (double)currentTimeNanos / 1000000.0;
    }

    private void startTimer() {
	startTimeSecs = currentTimeMillis() / 1000.0;
    }

    private void stopTimer() {
	double stopTimeSecs = currentTimeMillis() / 1000.0;
	probeIntervalSecs = stopTimeSecs - startTimeSecs;
	totalSecs += probeIntervalSecs;
    }

    public void run () {
	while (!stop) {

	    startTimer();
	    try { Thread.sleep(reportPeriodMs); } catch (InterruptedException ex) {}
	    stopTimer();

	    period.probe();
	    period.max(max);
	    period.accumulate(cumulative);

	    if (period.hasTriggered() ||
		max.hasTriggered() ||
		cumulative.hasTriggered()) {
		logStats();
	    }

	    period.reset();
	}	    

	logger.warning("Stopped " + name + " Statistics Reporter");
    }

    private void logStats () {
	StringBuffer sb = new StringBuffer();
	sb.append("--------------------------------------------------------\n");
	sb.append(name + " statistics for last period (" + probeIntervalSecs + " secs)\n");
	period.appendStatsAndRates(sb, probeIntervalSecs);

	sb.append("\n");
	sb.append(name + " statistics maximums\n");
	max.appendStats(sb);

	sb.append("\n");
	sb.append(name + " statistics cumulative\n");
	cumulative.appendStatsAndRates(sb, totalSecs);

	logger.info(sb.toString());
    }
}

