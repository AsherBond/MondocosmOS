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
package org.jdesktop.wonderland.modules.sharedstate.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.ObjectNotFoundException;
import com.sun.sgs.app.Task;
import com.sun.sgs.app.util.ScalableHashMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.OKMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedBoolean;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedByte;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedChar;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedData;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedDouble;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedFloat;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedInteger;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedLong;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedShort;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedString;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.ChangeValueMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.GetRequestMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.GetResponseMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.MapRequestMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.MapResponseMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.PutRequestMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.messages.RemoveRequestMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.state.SharedStateComponentServerState;
import org.jdesktop.wonderland.modules.sharedstate.common.state.SharedStateComponentServerState.MapEntry;
import org.jdesktop.wonderland.modules.sharedstate.common.state.SharedStateComponentServerState.SharedDataEntry;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO.ComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.eventrecorder.RecorderManager;

public class SharedStateComponentMO extends CellComponentMO {

    private static final Logger logger =
            Logger.getLogger(SharedStateComponentMO.class.getName());

    /** the channel from that cell */
    @UsesCellComponentMO(ChannelComponentMO.class)
    private ManagedReference<ChannelComponentMO> channelRef;

    /**
     * The message receiver for this component.  Most of the work happens t
     * in the receiver.
     */
    private final ManagedReference<SharedMessageReceiver> receiverRef;

    /** whether or not we are live */
    private boolean live = false;

    /** a cached setup object to apply when we are set live */
    private SharedStateComponentServerState state = null;

    /**
     * Create a SharedStateComponent for the given cell. The cell must already
     * have a ChannelComponent otherwise this method will throw an IllegalStateException
     * @param cell
     */
    public SharedStateComponentMO(CellMO cell) {
        super(cell);

        // set up the reference to the receiver
        SharedMessageReceiver receiver = new SharedMessageReceiver(cell, this);
        receiverRef = AppContext.getDataManager().createReference(receiver);
    }

    @Override
    public void setServerState(CellComponentServerState setup) {
        if (!(setup instanceof SharedStateComponentServerState)) {
            throw new IllegalArgumentException("Not shared state component state");
        }

        // convert our internal data into an array of maps
        SharedStateComponentServerState sscss =
                (SharedStateComponentServerState) setup;

        if (!live) {
            // cache the state for later and move on
            state = sscss;
            return;
        }
        
        // merge maps
        receiverRef.get().mergeMaps(sscss.getMaps());
    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState setup) {
        if (setup == null) {
            setup = new SharedStateComponentServerState();
        }

        if (!(setup instanceof SharedStateComponentServerState)) {
            throw new IllegalArgumentException("Not shared state component state");
        }

        // convert our internal data into an array of maps
        SharedStateComponentServerState sscss =
                (SharedStateComponentServerState) setup;
        sscss.setMaps(toMaps(receiverRef.get()));

        return setup;
    }

    public SharedMapSrv get(String name) {
        return receiverRef.get().getMap(name, true);
    }

    private MapEntry[] toMaps(SharedMessageReceiver recv) {
        List<MapEntry> out = new ArrayList<MapEntry>();

        for (Entry<String, ManagedReference<SharedMapImpl>> e :
                    recv.mapsRef.get().entrySet())
        {
            MapEntry me = new MapEntry(e.getKey());
            List<SharedDataEntry> l = new ArrayList<SharedDataEntry>();

            for (Entry<String, SharedData> de : e.getValue().get().entrySet()) {
                l.add(new SharedDataEntry(de.getKey(), de.getValue()));
            }

            me.setData(l.toArray(new SharedDataEntry[0]));
            out.add(me);
        }

        return out.toArray(new MapEntry[0]);
    }

    @Override
    public void setLive(boolean live) {
        // OWL issue #65: make sure to call super.setLive()
        super.setLive(live);
        
        this.live = live;

        if (live) {
            // set the channel in the receiver
            receiverRef.get().setChannel(channelRef.get());

            // set the state
            if (state != null) {
                setServerState(state);
                state = null;
            }

            // register for the messages we care about
            channelRef.get().addMessageReceiver(MapRequestMessage.class, receiverRef.get());
            channelRef.get().addMessageReceiver(GetRequestMessage.class, receiverRef.get());
            channelRef.get().addMessageReceiver(PutRequestMessage.class, receiverRef.get());
            channelRef.get().addMessageReceiver(RemoveRequestMessage.class, receiverRef.get());
        } else {
            // unregister message receivers
            channelRef.get().removeMessageReceiver(MapRequestMessage.class);
            channelRef.get().removeMessageReceiver(GetRequestMessage.class);
            channelRef.get().removeMessageReceiver(PutRequestMessage.class);
            channelRef.get().removeMessageReceiver(RemoveRequestMessage.class);
        }
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.sharedstate.client.SharedStateComponent";
    }

    private static class SharedMessageReceiver 
            implements ComponentMessageReceiver, ManagedObject
    {
        /** the map of maps we know about, indexed by name */
        private final ManagedReference<MapOfMaps> mapsRef =
                AppContext.getDataManager().createReference(new MapOfMaps());

        /** a reference to the SharedStateComponentMO */
        private final ManagedReference<SharedStateComponentMO> stateRef;

        /** a reference to the cell MO */
        private final ManagedReference<CellMO> cellRef;

        /** a reference to the channel component */
        private ManagedReference<ChannelComponentMO> channelRef;

        public SharedMessageReceiver(CellMO cell, SharedStateComponentMO state)
        {
            // create a reference to the shared data
            stateRef = AppContext.getDataManager().createReference(state);
            cellRef = AppContext.getDataManager().createReference(cell);
        }

        void setChannel(ChannelComponentMO channel) {
            channelRef = AppContext.getDataManager().createReference(channel);
        }

        @Override
        public void messageReceived(WonderlandClientSender sender,
                                    WonderlandClientID clientID,
                                    CellMessage message)
        {
            ResponseMessage response;

            logger.fine("[SharedStateComponentMO]: Received message: " +
                        message.getClass().getSimpleName());

            if (message instanceof MapRequestMessage) {
                response = handleMapRequest(clientID, (MapRequestMessage) message);
            } else if (message instanceof GetRequestMessage) {
                response = handleGetRequest(clientID, (GetRequestMessage) message);
            } else if (message instanceof PutRequestMessage) {
                response = handlePutRequest(clientID, (PutRequestMessage) message);
            } else if (message instanceof RemoveRequestMessage) {
                response = handleRemoveRequest(clientID, (RemoveRequestMessage) message);
            } else {
                String error = "[SharedStateComponentMO]: Unknown message " +
                               "type: " + message.getClass() + " " + message;
                logger.warning(error);
                response = new ErrorMessage(message.getMessageID(), error);
            }

            // send the response to the caller
            sender.send(clientID, response);
        }

        private MapResponseMessage handleMapRequest(WonderlandClientID clientID,
                                                    MapRequestMessage message)
        {
            logger.fine("[SharedStateComponentMO]: Handle map req: " +
                        message.getName());

            // find the appropriate map
            SharedMapImpl map = getMap(message.getName(), false);

            // if the map doesn't exist, return an empty message
            if (map == null) {
                List<String> l = Collections.emptyList();
                return new MapResponseMessage(message.getMessageID(), 0, l);
            }

            // create a list of all keys
            Collection<String> keys = new ArrayList<String>(map.keySet());

            logger.fine("[SharedStateComponentMO]: Respond to map req: " +
                        keys.size() + " keys");

            // return the response
            return new MapResponseMessage(message.getMessageID(),
                                          map.getVersion(), keys);

        }

        private GetResponseMessage handleGetRequest(WonderlandClientID clientID,
                                                    GetRequestMessage message)
        {
            logger.fine("[SharedStateComponentMO]: Handle get req: " +
                        message.getMapName() + " " + message.getPropertyName());

            // find the appropriate map
            SharedMapImpl map = getMap(message.getMapName(), false);
            
            // if the map doesn't exist, return an empty result
            if (map == null) {
                return new GetResponseMessage(message.getMessageID(), 
                                              0, null);
            }

            logger.fine("[SharedStateComponentMO]: Respond to get req: " +
                        map.get(message.getPropertyName()));

            return new GetResponseMessage(message.getMessageID(), 
                                          map.getVersion(), 
                                          map.get(message.getPropertyName()));
        }

        private ResponseMessage handlePutRequest(WonderlandClientID clientID,
                                                 PutRequestMessage message)
        {
            logger.fine("[SharedStateComponentMO]: Handle put req: " +
                        message.getMapName() + " " +
                        message.getPropertyName());

            // find the appropriate map
            SharedMapImpl map = getMap(message.getMapName(), true);

            if (map.put(clientID, message))
            {
                return new OKMessage(message.getMessageID());
            }

            return new ErrorMessage(message.getMessageID(), "Request vetoed");
        }

        private ResponseMessage handleRemoveRequest(WonderlandClientID clientID,
                                                    RemoveRequestMessage message)
        {
            logger.fine("[SharedStateComponentMO]: Handle remove req: " +
                        message.getMapName() + " " +
                        message.getPropertyName());

            // find the appropriate map
            SharedMapImpl map = getMap(message.getMapName(), false);

            // remove the key from the map if the map exists
            if (map == null || map.remove(clientID, message)) {
                return new OKMessage(message.getMessageID());
            }

            return new ErrorMessage(message.getMessageID(), "Request vetied");
        }

        private SharedMapImpl getMap(String name, boolean create) {
            MapOfMaps maps = mapsRef.get();

            ManagedReference<SharedMapImpl> mapRef = maps.get(name);
            if (mapRef == null && create) {
                SharedMapImpl map = new SharedMapImpl(name, this, channelRef.get());
                mapRef = addMap(name, map);
            } else if (mapRef == null) {
                logger.warning("[SharedMap] Request for unknown map: " + name);
                return null;
            }

            return mapRef.get();
        }

        private ManagedReference<SharedMapImpl> addMap(String mapName, SharedMapImpl map) {
            logger.fine("[SharedStateComponentMO]: creating map " + mapName);

            MapOfMaps maps = mapsRef.get();

            ManagedReference<SharedMapImpl> mapRef =
                    AppContext.getDataManager().createReference(map);
            maps.put(mapName, mapRef);

            return mapRef;
        }

        private void removeMap(String mapName) {
            logger.fine("[SharedStateComponentMO]: removing map " + mapName);

            MapOfMaps maps = mapsRef.get();
            ManagedReference<SharedMapImpl> mapRef = maps.remove(mapName);
            AppContext.getDataManager().removeObject(mapRef.get());
        }
        
        private void mergeMaps(MapEntry[] merge) {
            MapOfMaps maps = mapsRef.get();
            
            // first remove any maps that are not in the merge set
            Set<String> toRemove = new HashSet<String>(maps.keySet());
            for (MapEntry mergeMap : merge) {
                toRemove.remove(mergeMap.getName());
            }
            
            logger.fine("[SharedStateComponentMO]: Clear " + 
                        toRemove.size() + " maps");
            
            for (String removeMapName : toRemove) {
                // clear the map asynchronously
                getMap(removeMapName, false).clear();
            }
            
            logger.fine("[SharedStateComponentMO]: Merge " + 
                        merge.length + " maps");
            
            // now create or merge all remaining maps
            for (MapEntry mergeMap : merge) {
                // merge map asynchronously
                mergeMap(getMap(mergeMap.getName(), true), mergeMap.getData());
            }
        }
        
        private void mergeMap(SharedMapImpl map, SharedDataEntry[] data) {
            // find all keys that aren't in data, and convert data to a map
            Set<String> toRemove = new LinkedHashSet<String>(map.keySet());
            Map<String, SharedData> dm = new LinkedHashMap<String, SharedData>();
            for (SharedDataEntry mergeData : data) {
                toRemove.remove(mergeData.getKey());
                
                dm.put(mergeData.getKey(), mergeData.getValue());
            }
            
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("[SharedStateComponentMO]: Clear " + 
                            toRemove.size() + " keys from " + map.getName());
            }
            
            // remove keys not in data
            if (toRemove.size() > 0) {
                map.scheduleTask(new RemoveTask(null, toRemove));
            }
            
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("[SharedStateComponentMO]: Merge " + 
                            dm.size() + " keys from " + map.getName());
            }
            
            // now add in all data
            if (dm.size() > 0) {
                map.scheduleTask(new PutTask(null, dm));
            }
        }
        
        public void recordMessage(WonderlandClientSender sender,
                                  WonderlandClientID clientID,
                                  CellMessage message)
        {
            RecorderManager.getDefaultManager().recordMessage(sender, clientID,
                                                              message);
        }
    }

    static class MapOfMaps
            extends ScalableHashMap<String, ManagedReference<SharedMapImpl>>
    {
    }

    static class SharedMapImpl extends ScalableHashMap<String, SharedData>
        implements SharedMapSrv
    {
        /** version number must get incremented on every change to the map */
        private long version = 0;

        /** the name of this map */
        private final String name;

        /** listeners */
        private final Set<SharedMapListenerSrv> listeners =
                new LinkedHashSet<SharedMapListenerSrv>();

        /** the enclosing listener */
        private final ManagedReference<SharedMessageReceiver> receiverRef;

        /** the channel */
        private final ManagedReference<ChannelComponentMO> channelRef;

        /** asynchronous operations in flight on this map */
        private ManagedReference<MapTaskRunner> tasksRef;
        
        public SharedMapImpl(String name, SharedMessageReceiver receiver,
                             ChannelComponentMO channel)
        {
            super();

            this.name = name;
            this.receiverRef = AppContext.getDataManager().createReference(receiver);
            this.channelRef = AppContext.getDataManager().createReference(channel);
        }

        public String getName() {
            return name;
        }

        long getVersion() {
            return version;
        }

        @Override
        public SharedData get(Object key) {
            // if there are pending asynchronous tasks, check those first
            MapTaskRunner async = getTaskRunner();
            if (async != null) {
                SharedData value = async.get((String) key);
                if (!(value instanceof SharedDataNoEffect)) {
                    return value;
                }
            }
            
            // no async tasks, or the async task didn't affect this key.
            // Proceed normally.
            return super.get((String) key);
        }
        
        public <T extends SharedData> T get(String key, Class<T> type) {
            return (T) get(key);
        }

        @Override
        public boolean containsKey(Object key) {
            // take into account asynchronous operations
            return (get(key) != null);
        }

        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public boolean isEmpty() {
            // if there are asynchronous operations scheduled, use the keyset
            // so their size is accounted for
            MapTaskRunner async = getTaskRunner();
            if (async != null) {
                Set<String> initial = new LinkedHashSet<String>(super.keySet());
                async.adjustKeySet(initial);
                return initial.isEmpty();
            } else {
                return super.isEmpty();
            }
        }
        
        @Override
        public Set<String> keySet() {
            Set<String> initial = super.keySet();
            
            // if there are asynchronous operations scheduled, update the
            // keyset
            MapTaskRunner async = getTaskRunner();
            if (async != null) {
                // create a new set to modify
                initial = new LinkedHashSet<String>(initial);
                async.adjustKeySet(initial);
            }
            
            return initial;
        }
        
        @Override
        public void clear() {
            clear(keySet());
        }
        
        /**
         * Clear a subset of keys
         * @param keys the keys to clear
         */
        protected void clear(Set<String> keys) {
            // make sure there is something to clear
            if (keys.isEmpty()) {
                return;
            }
            
            scheduleTask(new RemoveTask(null, keys));
        }

        /**
         * A value change originated locally. Server-side listeners are not
         * notified in this case, but a message is sent to remote clients.
         */
        @Override
        public SharedData put(String key, SharedData value) {
            return asyncDoPut(null, key, value);
        }

        /**
         * A value change originated by a remote client.  Server-side listeners
         * are notified, and a message is sent to remote clients.
         */
        boolean put(WonderlandClientID senderID, PutRequestMessage message)
        {
            String key = message.getPropertyName();
            SharedData value =  message.getPropertyValue();
            SharedData prev = get(key);

            // notify listeners, see if they veto
            if (firePropertyChange(senderID, message, key, prev, value)) {
                asyncDoPut(senderID, key, value);
                return true;
            }

            return false;
        }

        private SharedData asyncDoPut(WonderlandClientID senderID, String key,
                                      SharedData value)
        {
            // if there are pending asynchronous tasks, defer this operation
            MapTaskRunner async = getTaskRunner();
            if (async != null) {
                async.getTasks().add(new PutTask(senderID, key, value));
                return get(key);
            }
            
            // nothing asynchronous, go ahead and do the put. Always notify
            // listeners, even if the values are equal
            return syncDoPut(senderID, key, value, true);
        }
        
        
        private SharedData syncDoPut(WonderlandClientID senderID, String key,
                                     SharedData value, boolean notifyIfEqual)
        {
            version++;
        
            // make sure this is actually a change before sending any messages
            SharedData current = super.put(key, value);
            if (!notifyIfEqual && value.equals(current)) {
                // no change
                return value;
            }
            
            // send a message to notify all clients
            CellMessage message = ChangeValueMessage.put(getName(), version,
                                                         key, value);
            channelRef.get().sendAll(senderID, message);
            return current;
        }

        @Override
        public void putAll(Map<? extends String, ? extends SharedData> m) {
            Map<String, SharedData> d = new LinkedHashMap<String, SharedData>(m);
            scheduleTask(new PutTask(null, d));
        }

        /**
         * A remove request originated locally. Server-side listeners are not
         * notified in this case, but a message is sent to remote clients.
         */
        @Override
        public SharedData remove(Object key) {
            return asyncDoRemove(null, (String) key);
        }

        /**
         * A remove request originated remotely. Server-side listeners are
         * notified in this case, and a message is sent to remote clients.
         */
        boolean remove(WonderlandClientID senderID,
                       RemoveRequestMessage message)
        {
            String key = message.getPropertyName();
            SharedData prev = get(key);

            // notify listeners, see if they veto
            if (firePropertyChange(senderID, message, key, prev, null)) {
                asyncDoRemove(senderID, key);
                return true;
            }

            return false;
        }

        private SharedData asyncDoRemove(WonderlandClientID senderID, String key) {
            // if there are pending asynchronous tasks, defer this operation
            MapTaskRunner async = getTaskRunner();
            if (async != null) {
                async.getTasks().add(new RemoveTask(senderID, key));
                return get(key);
            }
            
            // nothing asynchronous
            return syncDoRemove(senderID, key);
        }
        
        private SharedData syncDoRemove(WonderlandClientID senderID, String key) {
            // make sure there is a value to remove before sending any messages
            SharedData prev = super.remove(key);
            if (prev == null) {
                return null;
            }
            
            version++;

            CellMessage message = ChangeValueMessage.remove(getName(), version,
                                                            key);
            channelRef.get().sendAll(senderID, message);

            // if the map is now empty and there are no listeners, 
            // remove it from tha map of maps
            if (isEmpty() && listeners.isEmpty()) {
                receiverRef.getForUpdate().removeMap(getName());
            }

            return prev;
        }
        
        public void addSharedMapListener(SharedMapListenerSrv listener) {
            if (listener instanceof ManagedObject) {
                listener = new ListenerMOWrapper(listener);
            }

            listeners.add(listener);
        }

        public void removeSharedMapListener(SharedMapListenerSrv listener) {
            if (listener instanceof ManagedObject) {
                listener = new ListenerMOWrapper(listener);
            }

            listeners.remove(listener);
            
            // if there are no listeners and the map is empty, remove it
            if (isEmpty() && listeners.isEmpty()) {
                receiverRef.getForUpdate().removeMap(getName());
            }
        }

        protected boolean firePropertyChange(WonderlandClientID senderID,
                CellMessage message, String key, SharedData oldVal,
                SharedData newVal)
        {
            for (SharedMapListenerSrv listener : listeners) {

                SharedMapEventSrv event = new SharedMapEventSrv(
                        this, senderID, message, key, oldVal, newVal);
                if (!listener.propertyChanged(event)) {
                    return false;
                }
            }

            return true;
        }
        
        protected void scheduleTask(MapTask task) {
            MapTaskRunner runner = getTaskRunner();
            
            if (runner == null) {
                // if no tasks are scheduled, create a new runner
                runner = new MapTaskRunner(this, task);
                tasksRef = AppContext.getDataManager().createReference(runner);
                AppContext.getTaskManager().scheduleTask(runner);
            } else {
                // add to the end of the current list of tasks
                AppContext.getDataManager().markForUpdate(runner);
                runner.getTasks().add(task);
            }
        }
        
        protected MapTaskRunner getTaskRunner() {
            MapTaskRunner runner = null;
            if (tasksRef != null) {
                try {
                    runner = tasksRef.get();
                } catch (ObjectNotFoundException onfe) {
                    // the runner doesn't exist -- it will be created below
                }
            }
            
            return runner;
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
    }

    static class ListenerMOWrapper
            implements Serializable, SharedMapListenerSrv
    {
        private ManagedReference<SharedMapListenerSrv> listenerRef;

        public ListenerMOWrapper(SharedMapListenerSrv listener) {
            listenerRef = AppContext.getDataManager().createReference(listener);
        }

        public boolean propertyChanged(SharedMapEventSrv event)
        {
            return listenerRef.get().propertyChanged(event);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ListenerMOWrapper)) {
                return false;
            }

            ListenerMOWrapper o = (ListenerMOWrapper) obj;
            return listenerRef.equals(o.listenerRef);
        }
    }
    
    static class MapTaskRunner implements Task, ManagedObject, Serializable {
        // maximum number of properties to clear per task
        protected static final int MAX_OPS = 5;
        
        // the map to clear
        private final ManagedReference<SharedMapImpl> mapRef;
        
        // the tasks to run
        private final List<MapTask> tasks;
        
        public MapTaskRunner(SharedMapImpl map, MapTask... tasks) {
            this (map, Arrays.asList(tasks));
        }
 
        public MapTaskRunner(SharedMapImpl map, List<MapTask> tasks) {
            this.mapRef = AppContext.getDataManager().createReference(map);
            this.tasks = new ArrayList<MapTask>(tasks);
        }
        
        public void run() throws Exception {
            SharedMapImpl map = mapRef.getForUpdate();
            MapTask task = tasks.get(0);
            
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("[SharedStateComponentMO]: MapTaskRunner.run " +
                              task + " with queue " + tasks.size());
            }
            
            // execute up to MAX_OPS iterations of the first task.
            for (int i = 0; i < MAX_OPS; i++) {
                if (!task.run(map)) {
                    // the task is finished, so remove it from
                    // the list
                    tasks.remove(0);
                    break;
                }
            }
            
            // if there is more work to be done. Reschedule the runner
            if (!tasks.isEmpty()) {
                AppContext.getTaskManager().scheduleTask(this);
            } else {
                // this task is complete -- remove it from the data store
                AppContext.getDataManager().removeObject(this);
            }
        }
        
        public List<MapTask> getTasks() {
            return tasks;
        }
        
        /**
         * Get the value for the given key after applying all pending tasks
         * in this runner
         * @param key the key to check
         * @return the value for the given key, or SharedDataNoEffect if
         * the pending tasks don't affect key
         */
        public SharedData get(String key) {
            // look at each pending task starting at the end of the list
            // and working backwards
            ListIterator<MapTask> i = getTasks().listIterator(getTasks().size());
            while (i.hasPrevious()) {
                MapTask t = i.previous();
                if (t.affects(key)) {
                    return t.get(key);
                }
            }
            
            // no effect
            return new SharedDataNoEffect();
        }
        
        /**
         * Get the keyset after applying all pending tasks in this runner
         * @param keySet the starting keyset to modify
         */
        public void adjustKeySet(Set<String> keySet) {
            for (MapTask task : getTasks()) {
                task.adjustKeySet(keySet);
            }
        }
    }
    
    // marker for no effect
    static class SharedDataNoEffect extends SharedData {}
    
    static class RemoveTask implements MapTask {
        private final WonderlandClientID senderID;
        private final Set<String> keys;
        
        public RemoveTask(WonderlandClientID senderID, String key) {
            this (senderID, new LinkedHashSet<String>(Collections.singleton(key)));
        }
        
        public RemoveTask(WonderlandClientID senderID, Set<String> keys) {
            this.senderID = senderID;
            this.keys = keys;
        }
        
        public boolean run(SharedMapImpl map) {            
            Iterator<String> i = keys.iterator();
            if (i.hasNext()) {
                String key = i.next();
                
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Clear " + key);
                }
                
                map.syncDoRemove(senderID, key);
                i.remove();
            }
            
            return i.hasNext();
        }

        public boolean affects(String key) {
            return keys.contains(key);
        }

        public SharedData get(String key) {
            return null;
        }

        public void adjustKeySet(Set<String> keySet) {
            keySet.removeAll(keys);
        }
        
        @Override
        public String toString() {
            return "[RemoveTask: " + keys + "]";
        }
    }
    
    static class PutTask implements MapTask {
        private final WonderlandClientID senderID;
        private final Map<String, SharedData> data;
        
        public PutTask(WonderlandClientID senderID, String key, SharedData value) {
            this (senderID, new LinkedHashMap<String, SharedData>(
                                        Collections.singletonMap(key, value)));
        }
        
        public PutTask(WonderlandClientID senderID, Map<String, SharedData> data) {
            this.senderID = senderID;
            this.data = data;
        }
        
        public boolean run(SharedMapImpl map) {
            Iterator<Map.Entry<String, SharedData>> i = data.entrySet().iterator();            
            if (i.hasNext()) {
                Map.Entry<String, SharedData> e = i.next();
            
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("[SharedStateComponentMO]: Put " + 
                                  e.getKey() + " = " + e.getValue());
                }
                
                map.syncDoPut(senderID, e.getKey(), e.getValue(), false);
                
                i.remove();
            }
            
            return i.hasNext();
        }

        public boolean affects(String key) {
            return data.containsKey(key);
        }

        public SharedData get(String key) {
            return data.get(key);
        }

        public void adjustKeySet(Set<String> keySet) {
            keySet.addAll(data.keySet());
        }
        
        @Override
        public String toString() {
            return "[PutTask: " + data.keySet() + "]";
        }
    }
    
    interface MapTask extends Serializable {
        /**
         * Run an instance of this task. 
         * @param map the map
         * @return true to continue, or false if all tasks are complete.
         */
        public boolean run(SharedMapImpl map) throws Exception;
        
        /**
         * Return true if this task affects the value of the given key
         * @param key the key to test
         * @return true if this task affects the value of key, or false if not
         */
        public boolean affects(String key);
        
        /**
         * Return the value for the given key after this task has been applied.
         * This will only be called if affects(key) return true
         */
        public SharedData get(String key);
        
        /**
         * Adjust the keyset for the map based on this operation. Either add
         * or remove keys as necessary
         * @param keySet the set of keys to adjust
         */
        public void adjustKeySet(Set<String> keySet);
    }
}
