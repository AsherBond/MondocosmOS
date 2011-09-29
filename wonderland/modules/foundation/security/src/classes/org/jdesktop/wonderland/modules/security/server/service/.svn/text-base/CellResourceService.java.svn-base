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
package org.jdesktop.wonderland.modules.security.server.service;

import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.impl.sharedutil.LoggerWrapper;
import com.sun.sgs.impl.sharedutil.PropertiesWrapper;
import com.sun.sgs.impl.util.AbstractService;
import com.sun.sgs.impl.util.TransactionContext;
import com.sun.sgs.impl.util.TransactionContextFactory;
import com.sun.sgs.kernel.ComponentRegistry;
import com.sun.sgs.kernel.KernelRunnable;
import com.sun.sgs.service.Transaction;
import com.sun.sgs.service.TransactionProxy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.common.security.annotation.Actions;
import org.jdesktop.wonderland.modules.security.common.ActionDTO;
import org.jdesktop.wonderland.modules.security.common.Permission;
import org.jdesktop.wonderland.modules.security.common.Permission.Access;
import org.jdesktop.wonderland.modules.security.common.Principal;
import org.jdesktop.wonderland.modules.security.server.SecurityComponentMO;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.security.Resource;

/**
 *
 * @author jkaplan
 */
public class CellResourceService extends AbstractService {
    /** The name of this class. */
    private static final String NAME = CellResourceService.class.getName();

    /** The package name. */
    private static final String PKG_NAME = "org.jdesktop.wonderland.modules.security.server.service";

    /** The logger for this class. */
	private static final LoggerWrapper logger =
        new LoggerWrapper(Logger.getLogger(PKG_NAME));

    /** The name of the version key. */
    private static final String VERSION_KEY = PKG_NAME + ".service.version";

    /** The major version. */
    private static final int MAJOR_VERSION = 1;

    /** The minor version. */
    private static final int MINOR_VERSION = 0;

    /** the component registry */
    private ComponentRegistry registry;

    /** manages the context of the current transaction */
    private TransactionContextFactory<CellResourceContext> ctxFactory;

    /** the set of resources, mapped by cell ID */
    private Map<CellID, CellResourceImpl> resourceCache =
            new ConcurrentHashMap<CellID, CellResourceImpl>();

    public CellResourceService(Properties props,
                               ComponentRegistry registry,
                               TransactionProxy proxy)
    {
        super(props, registry, proxy, logger);

        this.registry = registry;

        logger.log(Level.CONFIG, "Creating SecurityService properties: {0}",
                   props);
        PropertiesWrapper wrappedProps = new PropertiesWrapper(props);

        // create the transaction context factory
        ctxFactory = new TransactionContextFactory<CellResourceContext>(proxy, NAME) {
            @Override
            protected CellResourceContext createContext(Transaction txn) {
                return new CellResourceContext(txn);
            }
        };

        try {
            // Check service version
            transactionScheduler.runTask(new KernelRunnable() {
                public String getBaseTaskType() {
                    return NAME + ".VersionCheckRunner";
                }

                public void run() {
                    checkServiceVersion(
                            VERSION_KEY, MAJOR_VERSION, MINOR_VERSION);
                }
            }, taskOwner);
        } catch (Exception ex) {
            logger.logThrow(Level.SEVERE, ex, "Error reloading cells");
        }
    }

    public Resource getCellResource(CellID cellID) {
        List<CellResourceImpl> cells = new ArrayList<CellResourceImpl>();
        boolean allNull = true;

        // construct a tree by walking from this cell up to the root
        while (cellID != null) {
            CellResourceImpl curCell = getCellResourceImpl(cellID);
            if (curCell == null) {
                break;
            }

            // if there is a non-null resource, record that so that
            // we actually return a value at the end of the method
            if (!(curCell instanceof NullResourceImpl)) {
                allNull = false;
            }
            
            cells.add(curCell);
            cellID = curCell.getParentID();
        }

        // make sure there were some non-null contexts
        if (allNull || cells.size() == 0) {
            return null;
        } else {
            return new CellTreeResourceImpl(cells);
        }
    }

    /**
     * Get the cell resource associated with the given cell ID. A cached
     * resource is returned if possible.  If the given ID is not in
     * the cache, a new cache entry will be generated and returned.
     * @param cellID the ID of the cell to get
     * @return the cell resource associated with the given ID, or null
     * if no cell is associated with the given ID.
     */
    private CellResourceImpl getCellResourceImpl(CellID cellID) {
        // check the existing context object for this transaction.  This will
        // first check any local changes we have made, and return either the
        // locally modified version or the cached version.
        CellResourceContext ctx = ctxFactory.joinTransaction();
        CellResourceImpl rsrc = ctx.getResource(cellID);
        if (rsrc != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Found resource " + rsrc + " for " +
                           cellID);
            }

            return rsrc;
        }

        // if we didn't find the resource in the cache anywhere, recreate
        // it from the cell.
        CellMO cell = CellManagerMO.getCell(cellID);
        if (cell == null) {
            return null;
        }

        // find this cell's parent
        CellID parentID = null;
        if (cell.getParent() != null) {
            parentID = cell.getParent().getCellID();
        }

        // collect all the actions associated with this cell and its
        // components
        Set<Action> allActions = findActions(cell);

        // get the security compnent from the cell
        SecurityComponentMO sc = cell.getComponent(SecurityComponentMO.class);
        if (sc == null || !sc.isOwned()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "No security component for cell " +
                           cellID);
            }

            // there is no security component for this cell.  Add a null
            // entry to the cache, so we won't try to look it up every
            // time
            rsrc = new NullResourceImpl(cellID, parentID, allActions);
            ctx.addResource(cellID, rsrc);
            return rsrc;
        }

        // create a new resource for this cell and add it to the cache
        rsrc = new CellResourceImpl(cellID.toString());
        rsrc.setOwners(sc.getOwners());
        rsrc.setPermissions(sc.getPermissions());
        rsrc.setParentID(parentID);
        rsrc.setActions(findActions(cell));
        ctx.addResource(cellID, rsrc);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Created resource for cell " + cellID);
        }

        // return the newly created resource
        return rsrc;
    }

    /**
     * Get the actions associated with a given cell.  If the cell is not
     * in the cache, a new cache entry will be created.
     * @param cellID the id of the cell to get actions for
     * @return the actions associated with the cell id, or null if no cell is
     * associated with the given id
     */
    public Set<Action> getActions(CellID cellID) {
        CellResourceImpl rsrc = getCellResourceImpl(cellID);
        if (rsrc == null) {
            return null;
        }

        return rsrc.getActions();
    }

    /**
     * Update a particular resource in the cache.  If there is no entry
     * for this cell in the cache, ignore the update.
     * @param cellID the id of the cell to update
     * @param owners the updated owner set
     * @param permissions the update permission set
     */
    public void updateCellResource(CellID cellID, Set<Principal> owners,
                                   SortedSet<Permission> permissions)
    {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Update resource for cell " + cellID);
        }

        CellResourceContext ctx = ctxFactory.joinTransaction();
        CellResourceImpl rsrc = ctx.getResource(cellID);
        if (rsrc == null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Update ignored for non-cached cell " +
                           cellID);
            }

            // no cached resource for this cell, just return
            return;
        } else if (rsrc instanceof NullResourceImpl) {
            // there were no permissions, and now there are some.  Remove
            // the resource so it will be recreated at the next call to
            // get()
            ctx.removeResource(cellID);
            return;
        } else {
            // update the cached copy of the cell with new values
            rsrc.setOwners(owners);
            rsrc.setPermissions(permissions);
        }
    }

    /**
     * Update the parent of a particular resource in the cache.  If there is no
     * entry for this cell in the cache, ignore the update.
     * @param cellID the id of the cell to update
     * @param parentID the cellID of the resource's new parent
     */
    public void updateCellResource(CellID cellID, CellID parentID) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Update parent for cell " + cellID);
        }

        CellResourceContext ctx = ctxFactory.joinTransaction();
        CellResourceImpl rsrc = ctx.getResource(cellID);
        if (rsrc != null) {
            rsrc.setParentID(parentID);
        }
    }

    /**
     * Indicate that the components of the given resource have changed.
     * If the cell is cached, this method will recalculate the set of
     * actions for the cell based on the new set of components
     * @param cellID the id of the cell to update
     * @param component the component that changed
     * @param added true if the component was added, or false if it
     * was removed
     */
    public void updateCellResource(CellID cellID, CellComponentMO component,
                                   boolean added)
    {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Update components for cell " + cellID);
        }

        CellResourceContext ctx = ctxFactory.joinTransaction();
        CellResourceImpl rsrc = ctx.getResource(cellID);
        if (rsrc != null) {
            if (added) {
                // find the actions associated with the new component,
                // and add those to the set associated with this cell.
                // If there are duplicates, it doesn't matter.
                Set<Action> addedActions = new HashSet<Action>();
                getActions(component.getClass(), addedActions);
                rsrc.getActions().addAll(addedActions);
            } else {
                // if a component was removed, we need to recalculate
                // all the actions (since we don't know if a given
                // action is referenced by multiple components)
                // Since that is expensive, just remove the resource and
                // let everything get recalculated
                ctx.removeResource(cellID);
            }
        }
    }

    /**
     * Remove a particular cell from the cache.  It will be reloaded next
     * time a security check is requested.
     * @param cellID the cell id to update
     */
    public void invalidateCellResource(CellID cellID) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Invalidate resource for cell " + cellID);
        }

        CellResourceContext ctx = ctxFactory.joinTransaction();
        ctx.removeResource(cellID);
    }

    /**
     * Find all actions in this cell and its components
     * @return the set of actions defined by the cell and all its components
     */
    protected Set<Action> findActions(CellMO cell) {
        Set<Action> out = new LinkedHashSet<Action>();

        // add all the actions for the cell
        getActions(cell.getClass(), out);

        // go through each component and add its actions
        for (ManagedReference<CellComponentMO> componentRef : cell.getAllComponentRefs()) {
            getActions(componentRef.get().getClass(), out);
        }

        return out;
    }

    /**
     * Find all action annotations on a given class
     * @param clazz the class to search
     * @param actions the set of actions to add to
     * @return the actions for the class
     */
    private void getActions(Class clazz, Set<Action> actions) {
        Actions classActions = (Actions) clazz.getAnnotation(Actions.class);

        if (classActions != null) {
            for (Class ac : classActions.value()) {
                actions.add(Action.getInstance(ac));
            }
        }

        // search the superclass for any actions
        if (clazz.getSuperclass() != null) {
            getActions(clazz.getSuperclass(), actions);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void doReady() {
        logger.log(Level.CONFIG, "Security service ready");
    }

    @Override
    protected void doShutdown() {
        // nothing to do
    }

    @Override
    protected void handleServiceVersionMismatch(Version oldVersion,
                                                Version currentVersion)
    {
        throw new IllegalStateException(
 	            "unable to convert version:" + oldVersion +
	            " to current version:" + currentVersion);
    }

    /**
     * Transaction state
     */
    private class CellResourceContext extends TransactionContext {
        private final Map<CellID, CellResourceRecord> resources =
                new HashMap<CellID, CellResourceRecord>();
        
        public CellResourceContext(Transaction txn) {
            super (txn);
        }

        public void addResource(CellID cellID, CellResourceImpl resource) {
            resources.put(cellID, CellResourceRecord.add(resource));
        }

        public boolean containsResource(CellID cellID) {
            CellResourceRecord rec = resources.get(cellID);
            if (rec == null) {
                return resourceCache.containsKey(cellID);
            }
            
            return (rec.getAction() == CellResourceRecord.Action.ADD);
        }

        public CellResourceImpl getResource(CellID cellID) {
            CellResourceRecord rec = resources.get(cellID);
            if (rec == null) {
                return resourceCache.get(cellID);
            }

            return rec.getResource();
        }

        public void removeResource(CellID cellID) {
            resources.put(cellID, CellResourceRecord.remove());
        }

        @Override
        public void abort(boolean retryable) {
            resources.clear();
        }

        @Override
        public void commit() {
            isCommitted = true;

            for (Entry<CellID, CellResourceRecord> e : resources.entrySet()) {
                switch (e.getValue().getAction()) {
                    case ADD:
                        resourceCache.put(e.getKey(), e.getValue().getResource());
                        break;
                    case REMOVE:
                        resourceCache.remove(e.getKey());
                        break;
                }
            }
        }
    }

    private static class CellResourceRecord {
        private static final CellResourceRecord REMOVE_RECORD =
                new CellResourceRecord(Action.REMOVE, null);

        private enum Action { ADD, REMOVE };

        private Action action;
        private CellResourceImpl rsrc;

        protected CellResourceRecord(Action action, CellResourceImpl rsrc) {
            this.action = action;
            this.rsrc = rsrc;
        }

        public Action getAction() {
            return action;
        }

        public CellResourceImpl getResource() {
            return rsrc;
        }

        public static CellResourceRecord add(CellResourceImpl rsrc) {
            return new CellResourceRecord(Action.ADD, rsrc);
        }

        public static CellResourceRecord remove() {
            return REMOVE_RECORD;
        }
    }

    /**
     * A resource for a tree of cells.
     */
    private static class CellTreeResourceImpl implements Resource, Serializable {
        /**
         * the list if cells in the tree, sorted with the base first
         * then other cells until the root of the tree
         */
        private List<CellResourceImpl> resources;

        public CellTreeResourceImpl(List<CellResourceImpl> resources) {
            this.resources = resources;
        }

        public String getId() {
            return CellTreeResourceImpl.class.getName() +
                   "-" + resources.get(0).getId();
        }

        public Result request(WonderlandIdentity identity, Action action) {
            Set<Principal> userPrincipals =
                    UserPrincipals.getUserPrincipals(identity.getUsername(),
                                                     false);
            if (userPrincipals == null) {
                return Result.SCHEDULE;
            } else if (getPermission(resources, userPrincipals, action)) {
                return Result.GRANT;
            } else {
                return Result.DENY;
            }
        }

        public boolean request(WonderlandIdentity identity, Action action,
                               ComponentRegistry registry)
        {
            Set<Principal> userPrincipals =
                    UserPrincipals.getUserPrincipals(identity.getUsername(),
                                                     true);
            return getPermission(resources, userPrincipals, action);
        }

        protected boolean getPermission(List<CellResourceImpl> resources,
                                        Set<Principal> userPrincipals,
                                        Action action)
        {
            // no more parents to check
            if (resources.isEmpty()) {
                return false;
            }

            CellResourceImpl cur = resources.get(0);
            if (cur.getPermission(userPrincipals, action)) {
                // we have permission
                return true;
            }

            // if we don't have permission, move up to the parent. First
            // make sure the action is a top-level action: we don't
            // query sub-actions on the parent
            action = getTopLevelAction(action);
            return getPermission(resources.subList(1, resources.size()),
                                 userPrincipals, action);

        }

        protected Action getTopLevelAction(Action action) {
            if (action.getParent() != null) {
                return getTopLevelAction(action.getParent());
            }

            return action;
        }
    }

    /**
     * A resource for a particular cell
     */
    private static class CellResourceImpl implements Resource, Serializable {
        private String cellID;
        private SortedSet<Permission> permissions;
        private Set<Principal> owners;
        private Set<Action> allActions;
        private CellID parentID;

        public CellResourceImpl(String cellID) {
            this.cellID = cellID;
        }

        public void setParentID(CellID parentID) {
            this.parentID = parentID;
        }

        public CellID getParentID() {
            return parentID;
        }

        public void setActions(Set<Action> allActions) {
            this.allActions = allActions;
        }

        public Set<Action> getActions() {
            return allActions;
        }

        public void setPermissions(SortedSet<Permission> permissions) {
            this.permissions = permissions;
        }

        public void setOwners(Set<Principal> owners) {
            this.owners = owners;
        }

        public String getId() {
            return CellResourceImpl.class.getName() + "-" + cellID;
        }

        public Result request(WonderlandIdentity identity, Action action) {
            Set<Principal> userPrincipals =
                    UserPrincipals.getUserPrincipals(identity.getUsername(),
                                                     false);
            if (userPrincipals == null) {
                return Result.SCHEDULE;
            } else if (getPermission(userPrincipals, action)) {
                return Result.GRANT;
            } else {
                return Result.DENY;
            }
        }

        public boolean request(WonderlandIdentity identity, Action action,
                               ComponentRegistry registry)
        {
            Set<Principal> userPrincipals =
                    UserPrincipals.getUserPrincipals(identity.getUsername(),
                                                     true);
            return getPermission(userPrincipals, action);
        }

        /**
         * Return true if any of the given principals have the requested
         * permission.
         * @param userPrincipals a set of principals to check.
         * @param action the action to check for.
         * @return true if any of the specified principals have the given
         * permission, or false if the result is denied or undefined.
         */
        protected boolean getPermission(Set<Principal> userPrincipals,
                                        Action action)
        {
            // collect all permissions
            List<Permission> myPerms = new ArrayList<Permission>();
            for (Principal p : userPrincipals) {
                // first, check if this principal is an owner
                if (owners.contains(p)) {
                    return true;
                }

                // if this is the admin group, always return true
                if (p.getType() == Principal.Type.GROUP &&
                        p.getId().equals("admin"))
                {
                    return true;
                }

                // find the permission for this principal
                Permission perm = getPermission(p, action);
                if (perm != null) {
                    // now check if the permission is a user permission.  If
                    // it is, it means it was a permission specifically for
                    // this user, and therefore should be applied directly
                    // and not combined with any other permissions.
                    if (perm.getPrincipal().getType() == Principal.Type.USER) {
                        return (perm.getAccess() == Access.GRANT);
                    }

                    // if the permission is a group or everybody permission,
                    // put it on the list to combine later
                    myPerms.add(perm);
                }
            }

            // now go through all the group permissions. If there are any
            // positive group permissions, this means that at least one of
            // the groups this user is a member of has access to the content,
            // so access should be granted
            boolean hasGroupPerm = false;
            for (Iterator<Permission> i = myPerms.iterator(); i.hasNext();) {
                Permission perm = i.next();

                if (perm.getPrincipal().getType() == Principal.Type.GROUP) {
                    hasGroupPerm = true;
                    i.remove();

                    if (perm.getAccess() == Access.GRANT) {
                        return true;
                    }
                }
            }

            // at this point, if there was a group permission defined, we know
            // that it was a DENY because otherwise we would have returned
            // above.  That means that the user wasn't part of any groups
            // that had permission, but was part of at least one group that
            // was denied permission.  In this case, we should deny access.
            if (hasGroupPerm) {
                return false;
            }

            // last, we check for any remaining permissionm, which must
            // be everyone permissions. There is no way to combine these, so
            // just go with whatever the first one says (hopefully there is
            // only one)
            for (Permission perm : myPerms) {
                return (perm.getAccess() == Access.GRANT);
            }
            
            // if we get here, it means that there were no permissions for
            // this user, any of the user's groups or everybody.  In that case,
            // deny access.
            return false;
        }

        /**
         * Return whether a principal has the permission for the given action.
         * This will iterate up the action if the given action is not
         * specified but has a parent.
         * @param p the principal to search
         * @param action the action to search for
         * @return true if the given principal has permission for the given
         * action, or false if it is denied or undefined.
         */
        protected Permission getPermission(Principal p, Action action) {
            // construct a prototype permission to search for
            Permission search = new Permission(p, new ActionDTO(action), null);

            // use the sorted set to find the first matching permission.
            // This will correspond to the permission for this user
            // if it is defined.
            Permission perm = null;
            SortedSet<Permission> perms = permissions.tailSet(search);
            if (!perms.isEmpty() && perms.first().equals(search)) {
                perm = perms.first();
            }
            
            // if the permission exists, return its value
            if (perm != null) {
                return perm;
            }

            // If we get here, it means the permission was not specified.
            // If this is a sub-permission, iterate up the tree testing for
            // any parent permissions
            if (action.getParent() != null) {
                return getPermission(p, action.getParent());
            }

            // if we get here, the permission was a top-level permission that
            // was not specified.  Default to deny.
            return null;
        }

        @Override
        public String toString() {
            return "{CellResource:" + getId() + ":" + getParentID() + "}";
        }
    }

    /**
     * A placeholder resource implementation that has no data
     */
    private static class NullResourceImpl extends CellResourceImpl {
        public NullResourceImpl(CellID cellID, CellID parentID,
                                Set<Action> allActions)
        {
            super (cellID.toString());

            setParentID(parentID);
            setActions(allActions);
        }

        // always return false
        @Override
        protected boolean getPermission(Set<Principal> userPrincipals,
                                        Action action)
        {
            return false;
        }

        @Override
        public String toString() {
            return "{NullResource:" + getId() + ":" + getParentID() + "}";
        }
    }
}
