/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */
package org.jdesktop.wonderland.client.cell;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Stores statistics about the cells in a given cell cache.
 * <p>
 * Cell performance statistics are not shared between clients, they are
 * particular to the Wonderland instance on a given client.
 * <p>
 * This interface is simply an entry point for collecting statistics. The actual
 * work of collecting the statistics and processing them is handled by a
 * provider that implements the CellStatisticsSPI interface. The provider can
 * be set using the <code>setProvider()</code> method.
 *
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class CellStatistics {
    private CellStatisticsSPI provider = new NoopCellStatisticsProvider();

    /**
     * Set the provider that will collect statistics
     * @param provider the provider to set
     */
    public synchronized void setProvider(CellStatisticsSPI provider) {
        this.provider = provider;
    }

    /**
     * Get the current provider
     * @return the current provider
     */
    public synchronized CellStatisticsSPI getProvider() {
        return provider;
    }

    /**
     * Add the given statistic to the given cell, replacing it if it already
     * exists.
     * @param cell the cell to add a statistic to
     * @param stat the statistic to add
     */
    public synchronized void add(Cell cell, CellStat stat) {
        getProvider().add(cell, stat);
    }

    /**
     * Get a statistic from the given cell
     * @param cell the cell to get a statistic from
     * @param id the id of the statistic to get
     * @return the statistic from the given cell with the given id, or null
     *  if the cell does not have a statistic with the given id
     */
    public synchronized CellStat get(Cell cell, String id) {
        return getProvider().get(cell, id);
    }

    /**
     * Get all statistics from the given cell. The returned collection
     * will be a copy of the actual data, so changes to the result will
     * not change the underlying statistics.
     * @param cell the cell to get all statistics from
     * @return a collection of cell statistics for the given cell
     */
    public synchronized Collection<CellStat> getAll(Cell cell) {
        return getProvider().getAll(cell);
    }

    /**
     * Remove a statistic from the given cell.
     * @param cell the cell to remove a statistic from
     * @param id the id of the statistic to remove
     * @return the removed statistic, or null if no statistic with the given
     * id was found
     */
    public synchronized CellStat remove(Cell cell, String id) {
        return getProvider().remove(cell, id);
    }

    /**
     * Interface implemented by statistic providers.
     */
    public interface CellStatisticsSPI {
        public void add(Cell cell, CellStat stat);
        public CellStat get(Cell cell, String id);
        public Collection<CellStat> getAll(Cell cell);
        public CellStat remove(Cell cell, String id);
    }

    /**
     * The default statistics provider ignores all statistics
     */
    class NoopCellStatisticsProvider implements CellStatisticsSPI {
        public void add(Cell cell, CellStat stat) {
            // ignore
        }

        public CellStat get(Cell cell, String id) {
            return null;
        }

        public Collection<CellStat> getAll(Cell cell) {
            return Collections.EMPTY_SET;
        }

        public CellStat remove(Cell cell, String id) {
            return null;
        }
    }

    /**
     * The base class for cell statistics. Each statistic has a unique ID,
     * as well as a description and value.
     */
    public abstract static class CellStat {
        private final String id;
        private String desc;

        /**
         * Create a new statistic with the given id and description
         * @param id the id of this statistic
         * @param desc the description of this statistic
         */
        protected CellStat(String id, String desc) {
            this.id = id;
            this.desc = desc;
        }

        /**
         * Return the id of this statistic
         * @return the id of this statistic
         */
        public String getId() {
            return id;
        }

        /**
         * Get the description of this statistic
         * @return the description of this statistic
         */
        public String getDescription() {
            return desc;
        }

        /**
         * Set the description of this statistic
         * @param description the description of this statistic
         */
        public void setDescription(String description) {
            this.desc = description;
        }

        /**
         * Get the value of this statistic. Subclasses must override this method
         * to return a reasonable string representation of the statistic.
         */
        public abstract String getValue();

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CellStat other = (CellStat) obj;
            if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 73 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }

    /**
     * A statistic that records a string as its value
     */
    public static class StringCellStat extends CellStat {
        private String value;

        public StringCellStat(String id, String value) {
            this (id, id, value);
        }

        public StringCellStat(String id, String desc, String value) {
            super (id, desc);
            this.value = value;
        }

        @Override
        public synchronized String getValue() {
            return value;
        }

        public synchronized void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * A mutable statistic that returns a long as its value
     */
    public static class LongCellStat extends CellStat {
        private long value;

        public LongCellStat(String id) {
            this (id, id);
        }

        public LongCellStat(String id, String desc) {
            this (id, desc, 0);
        }

        public LongCellStat(String id, String desc, long initialValue) {
            super (id, desc);
            this.value = initialValue;
        }

        @Override
        public synchronized String getValue() {
            return String.valueOf(value);
        }

        public synchronized long getLongValue() {
            return value;
        }

        public synchronized void setValue(Long value) {
            this.value = value;
        }

        /**
         * Change the value by the given amount (positive or negative)
         * @param amount the amount to change the value by
         * @return the new value after adjustment
         */
        public synchronized Long changeValue(Long amount) {
            this.value += amount;
            return value;
        }
    }

    /**
     * A statistic that stores time values. The default time unit is
     * milliseconds, but other time units can be specified.
     */
    public static class TimeCellStat extends LongCellStat {
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        public TimeCellStat(String id) {
            super (id);
        }

        public TimeCellStat(String id, String desc) {
            super (id, desc, 0);
        }

        public TimeCellStat(String id, String desc, TimeUnit timeUnit) {
            super (id, desc, 0);
            this.timeUnit = timeUnit;
        }

        @Override
        public String getValue() {
            return String.valueOf(getLongValue()) + " " + getSuffix();
        }

        public synchronized TimeUnit getTimeUnit() {
            return timeUnit;
        }

        protected String getSuffix() {
            switch (getTimeUnit()) {
                case DAYS:
                    return "days";
                case HOURS:
                    return "hours";
                case MICROSECONDS:
                    return "us.";
                case MILLISECONDS:
                    return "ms.";
                case MINUTES:
                    return "min.";
                case NANOSECONDS:
                    return "ns.";
                case SECONDS:
                    return "s.";
                default:
                    return "";
            }
        }
    }

    /**
     * A statistic representing a size in bytes. The display will automatically
     * format into bytes, kilobytes or megabytes, depending on the value.
     */
    public static class SizeCellStat extends LongCellStat {
        public SizeCellStat(String id) {
            super (id);
        }

        public SizeCellStat(String id, String desc) {
            super (id, desc, 0);
        }

        @Override
        public String getValue() {
            if (getLongValue() < 1000) {
                return String.valueOf(getLongValue()) + " b.";
            } else if (getLongValue() < 1000000) {
                return String.format("%.3f kb.", (getLongValue() / 1000f));
            } else {
                return String.format("%.3f mb.", (getLongValue() / 1000000f));
            }
        }
    }
}
