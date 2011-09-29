/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.modules.sharedstate.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.comms.OKErrorResponseListener;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedBoolean;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedByte;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedChar;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedData;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedDouble;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedFloat;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedInteger;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedLong;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedMap;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedShort;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedString;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.ChangeValueMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.MapRequestMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.MapResponseMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.GetRequestMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.GetResponseMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.PutRequestMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.RemoveRequestMessage;

/**
 * Implementation of the SharedMap interface.  This map is lazy, meaning it
 * doesn't load the value for a key unless you explicitly request the value.
 * At the time a request is made, the map queries the server for the
 * given value.
 * <p>
 * In addition to lazily requesting values, this implementation also snoops
 * for values. Whenever a value changes, the server sends a message to all
 * clients.  When the client receives one of these notifications, it populates
 * the relevant entry.
 * <p>
 * Although this implementation implements Map, it is implemented underneath
 * using a list as the primary storage, and a HashMap of the list elements
 * as secondary storage.  This is similar in concept to LinkedHashMap, but
 * with correct Iterator semantics for remote operation. Iterators of this
 * implementation never fail, they always return a valid
 * iteration over the list that was correct at some point in time.
 * <p>
 * Both Iterators over this map, as well as the ContainsKey and ContainsValue
 * method make requests to the server for values that aren't known, so are
 * quite expensive.  Use with caution.
 *
 * @author jkaplan
 */
class SharedMapImpl implements SharedMapCli {
    private static final Logger logger =
            Logger.getLogger(SharedMap.class.getName());

    /** the unique name of this map */
    private String name;

    /** channel to communicate on */
    private ChannelComponent channel;

    /** the clientID of this client */
    private BigInteger localSenderID;

    /** whether or not the map is initialized */
    private boolean initializing = false;

    /** The backing map */
    private VersionedMap backing = new VersionedMap();

    /** An executor for requests */
    private ExecutorService executor = Executors.newCachedThreadPool();

    /** the types of version supported */
    private enum VersionType { LOCAL, REMOTE };

    /** a lock to protected large changes to the values map */
    private final Object valuesLock = new Object();

    /** listeners */
    private List<ListenerRecord> listeners =
            new CopyOnWriteArrayList<ListenerRecord>();

    /**
     * Create a new shared map
     * @return
     */
    public SharedMapImpl(String name, Cell cell, ChannelComponent channel) {
        this.name = name;
        this.channel = channel;

        // get our sender ID from the current session
        this.localSenderID = cell.getCellCache().getSession().getID();
    }

    public String getName() {
        return name;
    }

    public synchronized int size() {
        checkInit();
        return backing.size();
    }

    public boolean isEmpty() {
        checkInit();
        return backing.isEmpty();
    }

    public <T extends SharedData> T get(String key, Class<T> type) {
        return (T) get(key);
    }

    public synchronized SharedData get(Object key) {
        checkInit();

        // see if we have the key at all
        VersionedValue val = backing.get(key);
        if (val == null) {
            return null;
        }

        // get the value from the holder. If this value is being requested,
        // this call will block until it is returned
        return val.get();
    }

    public boolean containsKey(Object key) {
        checkInit();
        return backing.containsKey(key);
    }

    public boolean containsValue(Object value) {
        checkInit();

        for (VersionedValue h : backing.values()) {
            if (value.equals(h.get())) {
                return true;
            }
        }
        
        return false;
    }

    public SharedData put(final String key, final SharedData value) {
        checkInit();

        // put the new value into the map
        final VersionedResult res = backing.putLocal(key, value);
        
        // send the request for a change, and revert if there is an error
        channel.send(new PutRequestMessage(getName(), key, value),
                new OKErrorResponseListener()
        {
            @Override
            public void onSuccess(MessageID messageID) {
                // do nothing
            }

            @Override
            public void onFailure(MessageID messageID, String message,
                                  Throwable cause)
            {
                // revert
                backing.revertPut(key, res.getLocalVersion());
            }
         });

         // return the previous value
         return res.getPrevValue();
    }

    public SharedData remove(Object keyObj) {
        checkInit();

        final String key = (String) keyObj;

        // remove the value from the map
        SharedData prev = backing.removeLocal(key);

        // send the request for a change, and revert if there is an error
        channel.send(new RemoveRequestMessage(getName(), key),
                new OKErrorResponseListener()
        {
            @Override
            public void onSuccess(MessageID messageID) {
                // do nothing
            }

            @Override
            public void onFailure(MessageID messageID, String message,
                                  Throwable cause)
            {
                backing.revertRemove(key);
            }
        });

        // change the value to an internal one
        return prev;
    }

    public void putAll(Map<? extends String, ? extends SharedData> m) {
        checkInit();
        for (Entry<? extends String, ? extends SharedData> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    public void clear() {
        checkInit();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<String> keySet() {
        checkInit();
        return backing.keySet();
    }

    public Collection<SharedData> values() {
        checkInit();

        // XXX TODO: this should be backed by the real list XXX
        Collection<SharedData> out = new ArrayList<SharedData>(backing.size());
        for (VersionedValue val : backing.values()) {
            out.add(val.get());
        }

        return out;
    }

    public Set<Entry<String, SharedData>> entrySet() {
        checkInit();

        // XXX TODO: this should be backed by the real list XXX
        Set<Entry<String, SharedData>> out = 
                new LinkedHashSet<Entry<String, SharedData>>();
        for (Entry<String, VersionedValue> e : backing.entrySet()) {
            out.add(new SharedDataEntry(e.getKey(), e.getValue().get()));
        }

        return out;
    }

    public void addSharedMapListener(SharedMapListenerCli listener) {
        addSharedMapListener(null, listener);
    }

    public void removeSharedMapListener(SharedMapListenerCli listener) {
        removeSharedMapListener(null, listener);
    }

    public void addSharedMapListener(String propRegex,
                                     SharedMapListenerCli listener)
    {
        listeners.add(new ListenerRecord(propRegex, listener));
    }

    public void removeSharedMapListener(String propRegex,
                                        SharedMapListenerCli listener)
    {
        listeners.remove(new ListenerRecord(propRegex, listener));
    }
    
    public String getString(String key) {
        SharedString value = get(key, SharedString.class);
        return (value == null ? null : value.getValue());
    }

    public void putString(String key, String value) {
        put(key, SharedString.valueOf(value));
    }

    public boolean getBoolean(String key) {
        SharedBoolean value = get(key, SharedBoolean.class);
        return (value == null ? false : value.getValue());
    }

    public void putBoolean(String key, boolean value) {
        put(key, SharedBoolean.valueOf(value));
    }

    public byte getByte(String key) {
        SharedByte value = get(key, SharedByte.class);
        return (value == null ? 0 : value.getValue());
    }

    public void putByte(String key, byte value) {
        put(key, SharedByte.valueOf(value));
    }

    public char getChar(String key) {
        SharedChar value = get(key, SharedChar.class);
        return (value == null ? 0 : value.getValue());
    }

    public void putChar(String key, char value) {
        put(key, SharedChar.valueOf(value));
    }

    public short getShort(String key) {
        SharedShort value = get(key, SharedShort.class);
        return (value == null ? 0 : value.getValue());
    }

    public void putShort(String key, short value) {
        put(key, SharedShort.valueOf(value));
    }

    public int getInt(String key) {
        SharedInteger value = get(key, SharedInteger.class);
        return (value == null ? 0 : value.getValue());
    }

    public void putInt(String key, int value) {
        put(key, SharedInteger.valueOf(value));
    }

    public long getLong(String key) {
        SharedLong value = get(key, SharedLong.class);
        return (value == null ? 0 : value.getValue());
    }

    public void putLong(String key, long value) {
        put(key, SharedLong.valueOf(value));
    }

    public float getFloat(String key) {
        SharedFloat value = get(key, SharedFloat.class);
        return (value == null ? 0f : value.getValue());
    }

    public void putFloat(String key, float value) {
        put(key, SharedFloat.valueOf(value));
    }

    public double getDouble(String key) {
        SharedDouble value = get(key, SharedDouble.class);
        return (value == null ? 0.0 : value.getValue());
    }

    public void putDouble(String key, double value) {
        put(key, SharedDouble.valueOf(value));
    }

    void handleMessage(ChangeValueMessage cvm) {
        VersionedValue prev;

        synchronized (valuesLock) {
            switch (cvm.getAction()) {
                case PUT:
                    backing.putRemote(cvm.getSenderID(),
                                      cvm.getPropertyName(),
                                      cvm.getVersion(),
                                      cvm.getPropertyValue());
                    break;
                case REMOVE:
                    backing.removeRemote(cvm.getSenderID(),
                                         cvm.getPropertyName(),
                                         cvm.getVersion());
                    break;
                default:
                    logger.warning("Unsupported operation: " + cvm.getAction());
                    break;
            }
        }
    }

    /**
     * Wait for the map to finish initializing
     */
    synchronized void waitForInit() throws InterruptedException {
        // if we are not already initializing, start now
        if (!isInitialized() && !initializing) {
            initializing = true;
            
            // read the initial data for this map in a new thread
            executor.submit(new Runnable() {
                public void run() {
                    doInit();
                }
            });
        }

        // wait for initialization to finish
        while (!isInitialized()) {
            wait();
        }
    }

    /**
     * Initialize the map by reading in all the keys. This method blocks
     * until the server has sent us the keys for this map.
     */
    private void doInit() {
        try {
            ResponseMessage rm = channel.sendAndWait(new MapRequestMessage(getName()));
            if (rm instanceof MapResponseMessage) {
                MapResponseMessage mrm = (MapResponseMessage) rm;
                backing.initialize(mrm.getVersion(), mrm.getKeys());
            } else {
                throw new IllegalStateException("Bad response to map request: " +
                                                rm.getClass().getName());
            }

        } catch (InterruptedException ie) {
            // oh well
        } finally {
            // no matter what, now is good time to wake up all the listeners
            synchronized (this) {
                initializing = false;
                notifyAll();
            }
        }
    }

    /**
     * Check to make sure the map is initialized before doing anything
     */
    private synchronized void checkInit() {
        if (!isInitialized()) {
            throw new IllegalStateException("Not initialized");
        }
    }

    private synchronized boolean isInitialized() {
        return backing.isInitialized();
    }
    
    class ValueGetter implements Callable<VersionedValue> {
        private String propName;

        public ValueGetter(String propName) {
            this.propName = propName;
        }

        public VersionedValue call() throws InterruptedException {
            CellMessage m = new GetRequestMessage(getName(), propName);
            ResponseMessage rm = channel.sendAndWait(m);
            if (rm instanceof GetResponseMessage) {
                GetResponseMessage grm = (GetResponseMessage) rm;
                return new ImmediateVersionedValue(VersionType.REMOTE,
                                                   grm.getVersion(), grm.getData());
            } else {
                throw new IllegalStateException("Invalid response to value " +
                        "request: " + rm.getClass());
            }
        }
    }

    class VersionedMap extends ConcurrentHashMap<String, VersionedValue> {
        /**
         * A map from key to values to revert to.  This map is used to support
         * reverting local versions back to the original version if something
         * fails.  A null value in this map means that on reversion, the given
         * key should be removed from the map.
         */
        private final Map<String, VersionedValue> reversions =
                                    new LinkedHashMap<String, VersionedValue>();
        
        /**
         * Whether or not this maps has been initialized.  On initialization,
         * a list of all the keys in the map are passed in.
         */
        private boolean initialized = false;

        /** the next local version number */
        private long nextLocalVersion = 0;

        /**
         * Whether or not the map has been initialized
         * @return true if the map is initialized, or false if not
         */
        public synchronized boolean isInitialized() {
            return initialized;
        }

        /**
         * Initialize this map with the given list of keys
         * @param version the version that the map should be initialized to
         * @param keys the list of keys to set
         */
        public synchronized void initialize(long version, Collection<String> keys) {
            // create a map with all keys set to either the value from
            // the reversions map (if the value was previously initialized)
            // or a lazy getter for the value if not
            Map<String, VersionedValue> vals =
                    new LinkedHashMap<String, VersionedValue>();
            for (String key : keys) {
                VersionedValue cur = reversions.get(key);

                // there are three cases here:
                // 1. there is no current value, in which case we want to add
                // a lazy getter
                // 2. there is a current value, in which case, we want to add
                // the value itself
                // 3. there is a current value, but get() returns null.  This
                // means the value represents a removal from the map.  We only
                // want to apply this removal if its version number is
                // *later* than the one we have

                if (cur != null && cur.get() != null) {
                    // add the value in
                    vals.put(key, cur);
                } else if (cur == null || cur.getVersion() < version) {
                    // add a lazy value
                    vals.put(key, new FutureVersionedValue(version, key));
                } else {
                    // do nothing -- this should only happen when the version
                    // of a removal is greater than the version we have.  In
                    // this case, we want to skip the key altogether
                }
            }

            // clear the reversions list
            reversions.clear();

            // apply the changes all at once to the underlying map
            super.putAll(vals);
            
            // we are now initialized
            initialized = true;
        }

        /**
         * Put a value from a local source.  Only valid after the map has
         * been initialized.
         * @param key the key to put
         * @param value the value to put
         * @return two pieces of information: the version of the new data that
         * was added to the map (that must be passed in during a revert) and
         * the previous value of this key
         */
        public synchronized VersionedResult putLocal(String key,
                                                     SharedData value)
        {
            // make sure we have been initialized
            if (!isInitialized()) {
                throw new IllegalStateException("Map not initialized");
            }

            // get the version for this action
            long localVersion = nextLocalVersion();

            // create a new local object to put in the map
            VersionedValue val = new ImmediateVersionedValue(VersionType.LOCAL,
                                                           localVersion, value);

            // add this new, local value to the map, and simultaneously get
            // the current version to revert to if necessary
            VersionedValue prev = super.put(key, val);

            // add the previous version to the reversion list
            addToReversionList(key, prev);

            // store the previous value, and then clear the result so
            // anyone waiting on the previous value will get this replacement
            SharedData prevVal = null;
            if (prev != null) {
                prevVal = prev.peek();
                prev.clear(value);
            }

            // return the result and newly created local version number
            return new VersionedResult(localVersion, prevVal);
        }

        /**
         * Put a value from a remote source.  Make sure the version is later
         * than the current version.  Also, if there is any reversion
         * information for this key in the map, remove it.
         * @param senderID the id of the intiator of this change
         * @param key the key to put
         * @param version the version of the value
         * @param data the data to put
         */
        public void putRemote(BigInteger senderID, String key, long version,
                              SharedData data)
        {
            // create a versioned object for this value
            VersionedValue val = new ImmediateVersionedValue(VersionType.REMOTE,
                                                             version, data);
            VersionedValue prev = null;

            synchronized (this) {
                // if the map is not yet initialized, just add this value to the
                // reversion list.  The reversion list will be processed in the
                // initialize() method above.
                if (!isInitialized()) {
                    reversions.put(key, val);
                    return;
                }

                // see if we have an existing version of this value in the map
                prev = get(key);
                if (prev != null && (compare(val, prev) < 0)) {
                    // older version -- skip the update
                    return;
                }

                // we are ok to replace the value
                super.put(key, val);

                // a local remote value always overwrites a remote one, so make
                // sure to clear this value from the reversions map.  The value
                // in the reversion map is also the previous value for this
                // key (before our local put).  If this key exists in the
                // reversion list, use the value on the reversion list
                // as the previous value (even if it is null).
                boolean inReversionList = reversions.containsKey(key);
                VersionedValue revert = reversions.remove(key);
                if (inReversionList) {
                    prev = revert;
                }
            }

            // notify listeners
            SharedData prevData = (prev == null) ? null : prev.peek();
            firePropertyChange(senderID, key, prevData, data);
        }

        /**
         * Revert a key that was put to the previous value.  The reversion will
         * only take place if the current value in the map has a local version
         * number,which is the same as the number that is passed in.  Reversion
         * may cause the value to be removed from the map if the value was
         * previously empty.
         * @param key the key to rever
         * @param localVersion the version number of the change we are reverting
         */
        public void revertPut(String key, long localVersion) {
            VersionedValue cur = null;
            VersionedValue revert = null;

            synchronized (this) {
                cur = get(key);

                // make sure we are OK to revert -- the current value must exist
                // and have a local version number equal to the one that was
                // passed in
                if (cur == null ||
                        cur.getVersionType() != VersionType.LOCAL ||
                        cur.getVersion() != localVersion)
                {
                    return;
                }

                // get the proper version from the reversion list
                revert = reversions.remove(key);
                if (revert == null) {
                    // if there is no value on the reversions list, it means we
                    // should remove the given value from the map on revert
                    super.remove(key);
                } else {
                    // revert
                    super.put(key, revert);
                }
            }

            // notify listeners
            SharedData curData = (cur == null) ? null : cur.peek();
            SharedData revData = (revert == null) ? null : revert.peek();
            firePropertyChange(localSenderID, key, curData, revData);
        }

        /**
         * Remove a value from a local source. Store the previous value in
         * case of a reversion.
         * @param key the key to remove
         * @return the previous value of the key, or null if there wasn't one
         */
        public synchronized SharedData removeLocal(String key) {
            // remove the current value from the map
            VersionedValue prev = super.remove(key);

            // add the previous version to the reversion list
            addToReversionList(key, prev);

            // get the value to return before clearing the result
            SharedData out = null;
            if (prev != null) {
                out = prev.peek();

                // clear the previous value, so anyone waiting on it will get
                // this replacement
                prev.clear(null);
            }

            // return the previous value
            return out;
        }

        /**
         * Remove a value from a remote source.  Clear the reversion list
         * for this value.
         * @param senderID the id of the initiator of this change
         * @param key the property to remove
         * @param version the version of the removal
         */
        public void removeRemote(BigInteger senderID, String key, long version) {
            // create a versioned object for this value
            VersionedValue val = new ImmediateVersionedValue(VersionType.REMOTE,
                                                             version, null);
            VersionedValue prev = null;

            synchronized (this) {
                // if the map is not yet initialized, add a null value to the
                // reversion list.  Applying this value will happen in the
                // initialize() method above.
                if (!isInitialized()) {
                    reversions.put(key, val);
                    return;
                }

                // see if we have an existing version of this value in the map
                prev = get(key);
                if (prev != null && (compare(val, prev) < 0)) {
                    // older version -- skip the update
                    return;
                }

                // we are ok to remove the value
                super.remove(key);

                // a local remote value always overwrites a remote one, so make
                // sure to clear this value from the reversions map.  The value
                // in the reversion map is also the previous value for this
                // key (before our local put).  If this key exists in the
                // reversion list, use the value on the reversion list
                // as the previous value (even if it is null).
                boolean inReversionList = reversions.containsKey(key);
                VersionedValue revert = reversions.remove(key);
                if (inReversionList) {
                    prev = revert;
                }
            }

            // notify listeners
            SharedData prevData = (prev == null) ? null : prev.peek();
            firePropertyChange(senderID, key, prevData, null);
        }

         /**
         * Revert a key that was removed to the previous value.  The reversion
         * will only take place if there is no current value in the map and
         * the reversion map has an entry for the given value.
         * @param key the key to revert
         */
        public void revertRemove(String key) {
            VersionedValue cur = null;
            VersionedValue revert = null;

            synchronized (this) {
                cur = get(key);

                // make sure we are OK to revert -- the current value must exist
                // and have a local version number equal to the one that was
                // passed in
                if (cur != null) {
                    return;
                }

                // get the proper version from the reversion list
                revert = reversions.remove(key);
                if (revert != null) {
                    super.put(key, revert);
                }
            }
            
            // notify listeners
            SharedData curData = (cur == null) ? null : cur.peek();
            SharedData revData = (revert == null) ? null : revert.peek();
            firePropertyChange(localSenderID, key, curData, revData);
        }

        /**
         * Add a value to the reversion list
         */
        private void addToReversionList(String key, VersionedValue prev) {
            // there are three possible cases here:
            // 1. the previous value is null, so make sure that no value is
            // on the reversion list
            // 2. the previous value is a remote value, so add that value to
            // the reversion list
            // 3. the previous version is local, in which case the reversion
            // list should already contain the correct version to revert to
            if (prev == null || prev.getVersionType() == VersionType.REMOTE) {
             
                // it is either case 1. or 2., so we should put the value on
                // the list. Putting a null on the list is fine -- if we revert
                // it will simply remove the value from the underlying map
                reversions.put(key, prev);
            }
        }

        /**
         * Versioned maps don't support putAll()
         */
        @Override
        public void putAll(Map<? extends String, ? extends VersionedValue> m) {
            throw new UnsupportedOperationException("putAll() not supported");
        }

        /**
         * Versioned maps don't support the clear operation
         */
        @Override
        public void clear() {
            throw new UnsupportedOperationException("Clear not supported");
        }

        /**
         * Return true of one value has a newer version than another.
         * @param other the other value to compare
         * @return -1 if the first value is smaller than the second, 0
         * if the two values are the same, or 1 if the second value is larger
         */
        public int compare(VersionedValue val1, VersionedValue val2) {
            // first see, if one value is local and the other remote.  Remote
            // values are always greater than local values
            if (val1.getVersionType() == VersionType.LOCAL &&
                    val2.getVersionType() == VersionType.REMOTE) {
                return -1;
            } else if (val1.getVersionType() == VersionType.REMOTE &&
                    val2.getVersionType() == VersionType.LOCAL) {
                return 1;
            }

            // at this point, we know both values are either local or remote
            // so we can just compare the version numbers
            if (val1.getVersion() < val2.getVersion()) {
                return -1;
            } else if (val2.getVersion() > val1.getVersion()) {
                return 1;
            }

            // they are equal
            return 0;
        }

        /**
         * Fire a property change event. It is a good idea to make sure you
         * don't hold any locks before calling this method, since it calls
         * outside this class.
         */
        private void firePropertyChange(BigInteger senderID, String key,
                                        SharedData oldValue, SharedData newValue)
        {
            for (ListenerRecord lr : listeners) {
                if (lr.matches(key)) {
                    lr.getListener().propertyChanged(new SharedMapEventCli(
                                                     SharedMapImpl.this,
                                                     senderID,key, oldValue,
                                                     newValue));
                }
            }
        }

        /**
         * Get the next local version number
         */
        private synchronized long nextLocalVersion() {
            return nextLocalVersion++;
        }
    }

    class VersionedResult {
        private long localVersion;
        private SharedData prevValue;

        public VersionedResult(long localVersion, SharedData prevValue) {
            this.localVersion = localVersion;
            this.prevValue = prevValue;
        }

        public long getLocalVersion() {
            return localVersion;
        }

        public SharedData getPrevValue() {
            return prevValue;
        }
    }

    /**
     * Holds a value in the map.
     */
    interface VersionedValue {
        /**
         * Get the type of version
         * @return the version type
         */
        public VersionType getVersionType();

        /**
         * Get the version associated with this value
         * @return the version
         */
        public long getVersion();

        /**
         * Get the value.  This method blocks until data is available.
         * @return the value.
         */
        public SharedData get();

        /**
         * Get the value but don't block. Return null if the data is not
         * immediately available
         * @return the value,  or null if the value isn't available
         */
        public SharedData peek();

        /**
         * Remove any value assocated with this holder.  Stop any requests
         * in progress.
         * @param newVal the new value to return to anyone waiting on a get
         * @return the value that was removed, or null if no value was removed.
         */
        public SharedData clear(SharedData newVal);
    }

    class FutureVersionedValue implements VersionedValue {
        private long version;
        private String key;

        private Future<VersionedValue> future;
        private SharedData replacement;
        
        public FutureVersionedValue(long version, String key) {
            this.key = key;
            this.version = version;
        }

        public VersionType getVersionType() {
            return VersionType.REMOTE;
        }

        public synchronized long getVersion() {
            return version;
        }

        synchronized void setVersion(long version) {
            this.version = version;
        }

        public SharedData get() {
            SharedData out = null;

            // if the future does not yet exist, create it
            synchronized (this) {
                if (future == null) {
                    future = executor.submit(new ValueGetter(key));
                }
            }

            try {
                // this should return an immediate value, otherwise we could
                // wait again...
                VersionedValue res = future.get();
                out = res.get();
                setVersion(res.getVersion());
            } catch (InterruptedException ie) {
                logger.log(Level.WARNING, "Get value interrupted", ie);
            } catch (ExecutionException ee) {
                logger.log(Level.WARNING, "Error getting value", ee);
            } catch (CancellationException ce) {
                // the request was cancelled, see if it was replaced
                out = getReplacement();
            }

            return out;
        }

        public SharedData peek() {
            if (future != null && future.isDone()) {
                return get();
            } else {
                return null;
            }
        }

        public SharedData clear(SharedData newVal) {
            SharedData out = null;
            setReplacement(newVal);

            if (future != null) {
                if (future.isDone()) {
                    out = get();
                } else {
                    future.cancel(true);
                    
                    synchronized (this) {
                        future = null;
                    }
                }
            }
            
            return out;
        }
        
        synchronized void setReplacement(SharedData replacement) {
            this.replacement = replacement;
        }
        
        synchronized SharedData getReplacement() {
            return replacement;
        }
    }

    class ImmediateVersionedValue implements VersionedValue {
        private VersionType type;
        private long version;
        private SharedData value;

        public ImmediateVersionedValue(VersionType type, long version,
                                       SharedData value)
        {
            this.type = type;
            this.version = version;
            this.value = value;
        }

        public VersionType getVersionType() {
            return type;
        }

        public long getVersion() {
            return version;
        }

        public SharedData get() {
            return value;
        }

        public SharedData peek() {
            return get();
        }

        public SharedData clear(SharedData newVal) {
            return value;
        }
    }

    class SharedDataEntry implements Entry<String, SharedData> {
        private String key;
        private SharedData value;

        public SharedDataEntry(String key, SharedData value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public SharedData getValue() {
            return value;
        }

        public SharedData setValue(SharedData value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class ListenerRecord {
        private String propRegex;
        private Pattern pattern;
        private SharedMapListenerCli listener;

        public ListenerRecord(String propRegex, SharedMapListenerCli listener) {
            this.propRegex = propRegex;
            this.listener = listener;

            if (propRegex != null) {
                this.pattern = Pattern.compile(propRegex);
            }
        }

        public String getPropRegex() {
            return propRegex;
        }

        public SharedMapListenerCli getListener() {
            return listener;
        }

        public boolean matches(String key) {
            if (pattern == null) {
                return true;
            }
            
            return pattern.matcher(key).matches();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ListenerRecord other = (ListenerRecord) obj;
            if ((this.propRegex == null) ? (other.propRegex != null) : !this.propRegex.equals(other.propRegex)) {
                return false;
            }
            if (this.listener != other.listener && (this.listener == null || !this.listener.equals(other.listener))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 23 * hash + (this.propRegex != null ? this.propRegex.hashCode() : 0);
            hash = 23 * hash + (this.listener != null ? this.listener.hashCode() : 0);
            return hash;
        }
    }
}
