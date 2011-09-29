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
package org.jdesktop.wonderland.modules.security.server.service.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * A cache implementation that will automatically remove entries older than
 * a certain age.
 * @author jkaplan
 */
public class TimeBasedCache<K, T> extends LinkedHashMap<K, AgedValue<T>> {
    // the maximum age of an entry in the map, in milliseconds
    private long maxAge = 10 * 60 * 1000;

    /**
     * Create a new cache with the default maximum age (10 minutes)
     * @param maxAge the maximum age, in milliseconds
     */
    public TimeBasedCache() {
        super (16, 0.75f, true);
    }

    /**
     * Create a new cache with the given maximum age
     * @param maxAge the maximum age, in milliseconds
     */
    public TimeBasedCache(long maxAge) {
        super (16, 0.75f, true);

        this.maxAge = maxAge;
    }

    @Override
    public AgedValue<T> get(Object key) {
        AgedValue res = super.get(key);
        if (res == null || isTooOld(res)) {
            return null;
        } else {
            return res;
        }
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, AgedValue<T>> entry) {
        return isTooOld(entry.getValue());
    }

    private boolean isTooOld(AgedValue value) {
        long age = System.currentTimeMillis() - value.getCreationTime();
        return (age > maxAge);
    }
}
