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
package org.jdesktop.wonderland.client.assetmgr;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * The AssetDB class represents the client-side cache of assets. The database
 * itself simply stores the entries found in the cache; the cached assets are
 * actually stored in a corresponding directorly.
 * 
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public class AssetDB {

    /* The default name of the asset database */
    private static final String DB_NAME = "AssetDB";
    
    /* The maximum length of strings in the database */
    private static final int MAX_STRING_LENGTH = 8192;
    
    /* The error logger for this class */
    private static Logger logger = Logger.getLogger(AssetDB.class.getName());
   
    /* The database connection, null if not connected */
    private Connection dbConnection = null;
    
    /* The list of properties that represent the database configuration */
    private Properties dbProperties = null;
    
    /* True if the database is connected, false if not */
    private boolean isConnected = false;
    
    /* The name of the database, initially DB_NAME */
    private String dbName = null;

    /**
     * Represents a record in the Asset DB
     */
    public static class AssetDBRecord {
        public String assetURI = null;
        public String checksum = null;
        public String baseURL = null;
        public String type = null;
        public long size = 0;
        public long lastAccessed = 0;
    }

    /**
     * Default constructor.
     *
     * @throw AssetDBException Upon error connecting to the DB
     */
    public AssetDB() throws AssetDBException {
        /* Initialize the name to some default name */
        this.dbName = AssetDB.DB_NAME;

        /* Log a message saying we are kicking off the database */
        logger.fine("AssetDB: Initializing the asset database, name=" + this.dbName);
                
        /*
         * Attempt the set the base directory of the database, which could fail.
         * Log an error if it does and exit. XXX Do we really need to exit? XXX
         */
        if (this.setDBSystemDir() == false) {
            logger.severe("AssetDB: Unable to set database directory");
            throw new AssetDBException("Unable to set database directory");
        }
        
        /*
         * Attempt to open the database, exit with severe error. XXX Do we
         * really need to exit? Perhaps we can just continue without using a
         * cache in fail-safe mode? XXX
         */
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "AssetDB: jdbc EmbeddedDriver not available, exiting.", ex);
            ex.printStackTrace();
            System.exit(1);
        }
        
        /* Create the properties that describe the database */
        dbProperties = new Properties();
        dbProperties.put("user", "assetmgr");
        dbProperties.put("password", "wonderland");    
        dbProperties.put("derby.driver", "org.apache.derby.jdbc.EmbeddedDriver");
        dbProperties.put("derby.url", "jdbc:derby:");
        dbProperties.put("db.table", "ASSET");
        dbProperties.put("db.schema", "APP");   
        
        /*
         * Check to see if the database exists. If it does exist, attempt to
         * connect to it. If it does not exist, then try to create it first. This
         * also handles if a database exists, but is for a previous version.
         */
        if(!dbExists()) {
            try {
                logger.fine("AssetDB does not exist, creating it at location " +
                        getDatabaseLocation());
                createDatabase();
            } catch(Exception e) {
                e.printStackTrace();
            } catch(Error er) {
                er.printStackTrace();
            }
            
            /* Disconnect from the database after creation and attempt to re-connect */
            disconnect();
            if (!connect()) {
                logger.severe("Unable to open AssetDB, exiting");
                logger.severe("Check you don't have a Wonderland client already running");
                throw new AssetDBException("Unable to connect to database");
            }
        } else {            
            if (!connect()) {
                logger.severe("Unable to open AssetDB, exiting");
                logger.severe("Check you don't have a Wonderland client already running");
                throw new AssetDBException("Unable to connect to database");
            }
        }
    }
    
    /**
     * Disconnects from the database.
     */
    public void disconnect() {
        if(isConnected) {
            String dbUrl = getDatabaseUrl();
            dbProperties.put("shutdown", "true");
            try {
                DriverManager.getConnection(dbUrl, dbProperties);
            } catch (SQLException ex) {
                /*
                 * When shutting down the database, an ERROR 08006 is normal and
                 * indicates that the database has indeed been shutdown. Check
                 * for that here
                 */
                if (ex.getSQLState().equals("08006") == true) {
                    logger.log(Level.INFO, "AssetDB: Shutdown was normal");
                }
                else {
                    logger.log(Level.WARNING, "Failed to disconnect from " +
                            "AssetDB " + ex.getMessage(), ex);
                }
            }
            isConnected = false;
            dbConnection = null;
        }
    }
    
    /**
     * Adds a new asset to database. Throws a SQLException upon error, or if
     * the asset already exists in the database.
     * 
     * @param assetRecord The asset record to add to the database
     * @throw SQLException Upon error adding the asset
     */
    public void addAsset(AssetDBRecord assetRecord) throws SQLException {
        synchronized(stmtSaveNewRecord) {
            String checksum = (assetRecord.checksum == null) ? "" : assetRecord.checksum;
            
            stmtSaveNewRecord.clearParameters();
            stmtSaveNewRecord.setString(1, assetRecord.assetURI);
            stmtSaveNewRecord.setString(2, checksum);
            stmtSaveNewRecord.setString(3, assetRecord.baseURL);
            stmtSaveNewRecord.setString(4, assetRecord.type);
            stmtSaveNewRecord.setLong(5, System.currentTimeMillis());
            stmtSaveNewRecord.setLong(6, assetRecord.size);
            int row = stmtSaveNewRecord.executeUpdate();
        }
    }

    /**
     * Updates an existing asset in the database. Throws a SQLException upon
     * error.
     *
     * @param assetRecrord The asset record to update
     * @throw SQLException Upon error updating the asset
     */
    public void updateAsset(AssetDBRecord assetRecord) throws SQLException {
        synchronized(stmtUpdateExistingRecord) {
            String checksum = (assetRecord.checksum == null) ? "" : assetRecord.checksum;

            stmtUpdateExistingRecord.clearParameters();
            stmtUpdateExistingRecord.setString(1, assetRecord.assetURI);
            stmtUpdateExistingRecord.setString(2, checksum);
            stmtUpdateExistingRecord.setString(3, assetRecord.baseURL);
            stmtUpdateExistingRecord.setString(4, assetRecord.type);
            stmtUpdateExistingRecord.setLong(5, System.currentTimeMillis());
            stmtUpdateExistingRecord.setLong(6, assetRecord.size);
            stmtUpdateExistingRecord.setString(7, assetRecord.assetURI);
            stmtUpdateExistingRecord.setString(8, checksum);
            stmtUpdateExistingRecord.executeUpdate();
        }
    }

    /**
     * Removes an asset given its unique identifying URI and checksum. Throws
     * SQLException upon error
     *
     * @param assetID The unique ID of the asset (URI, checksum)
     * @throw SQLException Upon error deleting the asset
     */
    public void deleteAsset(AssetID assetID) throws SQLException {
        synchronized(stmtDeleteAsset) {
            String uri = assetID.getAssetURI().toExternalForm();
            String checksum = (assetID.getChecksum() == null) ? "" : assetID.getChecksum();

            stmtDeleteAsset.clearParameters();
            stmtDeleteAsset.setString(1, uri);
            stmtDeleteAsset.setString(2, checksum);
            stmtDeleteAsset.executeUpdate();
        }
    }

    /**
     * Return the asset record for the supplied unique asset ID, or null if
     * the asset described by the ID is not in the cache. Throws SQLException
     * upon error.
     *
     * @param assetID The unique asset ID (URI, checksum)
     * @return The asset record in the cache, null if not present.
     * @throw SQLException Upon error fetching the asset
     */
    public AssetDBRecord getAsset(AssetID assetID) throws SQLException {
        AssetDBRecord assetRecord = null;
        synchronized(stmtGetAsset) {
            String uri = assetID.getAssetURI().toExternalForm();
            String checksum = (assetID.getChecksum() == null) ? "" : assetID.getChecksum();

            logger.fine("Getting asset in database uri " + uri + " checksum " +
                    checksum);
            
            stmtGetAsset.clearParameters();
            stmtGetAsset.setString(1, uri);
            stmtGetAsset.setString(2, checksum);
            ResultSet result = stmtGetAsset.executeQuery();

            if (result != null && result.next() == true) {
                assetRecord = new AssetDBRecord();
                assetRecord.assetURI = result.getString("ASSET_URI");
                assetRecord.checksum = result.getString("CHECKSUM");
                assetRecord.baseURL = result.getString("URL");
                assetRecord.type = result.getString("TYPE");
                assetRecord.lastAccessed = result.getLong("LAST_ACCESSED");
                assetRecord.size = result.getLong("SIZE");

                // In the database, a null checksum is an empty string (""),
                // but in the code, a null checksum is null
                assetRecord.checksum = (assetRecord.checksum.equals("") == true) ?
                    null : assetRecord.checksum;
            }
        }
        return assetRecord;
    }

    /**
     * Returns a list of asset records for the unique asset uri. Returns an
     * empty list if no such assets exists. Throws SQLException upon error
     *
     * @param assetURI The unique uri of the assets to fetch
     * @return A list of assets that match the uri, an empty list if none
     * @throw SQLException Upon error fetching the list
     */
    public List<AssetDBRecord> getAssetList(AssetURI assetURI) throws SQLException {
        List<AssetDBRecord> assetList = new LinkedList();
        synchronized (stmtGetAssetList) {
            String uri = assetURI.toExternalForm();

            stmtGetAssetList.clearParameters();
            stmtGetAssetList.setString(1, uri);
            ResultSet result = stmtGetAssetList.executeQuery();

            if (result.next() == true) {
                AssetDBRecord assetRecord = new AssetDBRecord();
                assetRecord.assetURI = result.getString("ASSET_URI");
                assetRecord.checksum = result.getString("CHECKSUM");
                assetRecord.baseURL = result.getString("URL");
                assetRecord.type = result.getString("TYPE");
                assetRecord.lastAccessed = result.getLong("LAST_ACCESSED");
                assetRecord.size = result.getLong("SIZE");

                // In the database, a null checksum is an empty string (""),
                // but in the code, a null checksum is null
                assetRecord.checksum = (assetRecord.checksum.equals("") == true) ?
                    null : assetRecord.checksum;

                assetList.add(assetRecord);
            }
        }
        return assetList;
    }

    /**
     * Returns true if the asset database already exist. The asset database is
     * considered to exist, if the proper version of the database exists. If
     * not, a new one is created.
     * <p>
     * The version of the database is encoded in the path of the database. In
     * this way, multiple database versions may exist on a system at once.
     * 
     * @return True if the database exists, false if not
     */
    private boolean dbExists() {
        return getDatabaseLocation().exists();
    }
    
    /**
     * Sets up the directory in which the database resides, creating the directory
     * if it does not exist. Returns true if successfull, false if not. Also
     * checks to see that we are able to do this operation, logs an error if not
     * and returns false.
     */
    private boolean setDBSystemDir() {
        try {
            /* The Derby home directory is simply the user cache directory */
            String systemDir = ClientContext.getUserDirectory().getPath();
            System.setProperty("derby.system.home", systemDir);

            /* Log a message with this directory */
            logger.fine("AssetDB: Database home directory=" + systemDir);
            
            /*
             * Create the directories. Note: an odd thing happens here, if the
             * directories already exist, then mkdirs() return false. So we
             * should not rely upon the return value of mkdirs()
             */
            File fileSystemDir = new File(systemDir);
            fileSystemDir.mkdirs();
            return true;
        } catch (java.lang.SecurityException excp) {
            /* Log an error and return null */
            logger.severe("AssetDB: Not allowed to setup database: " + excp.toString());
            return false;
        }
    }

    /**
     * Returns the location of the database. This location is the full path name.
     *
     * @return The full location to the database
     */
    private File getDatabaseLocation() {
        return new File(System.getProperty("derby.system.home") + File.separator + dbName);
    }

    /**
     * Returns the URL representation of the database
     *
     * @return The URL representation of the database
     */
    private String getDatabaseUrl() {
        File path = getDatabaseLocation();
        String dbUrl;
        
        try {
            dbUrl = dbProperties.getProperty("derby.url") + path.getCanonicalPath();
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error getting URL for " + path, ioe);
            dbUrl = dbProperties.getProperty("derby.url") + dbName;
        }

        return dbUrl;
    }

    /**
     * Returns true if the database is connected, false if not.
     *
     * @return True if the database is connected, false if not.
     */
    private boolean isConnected() {
        return this.isConnected;
    }

    /**
     * Connect to the database. Return true upon success, false upon falure
     *
     * @return True upon success, false upon failure.
     */
    private boolean connect() {
        /*
         * Attempt to connect to the database, also compile some SQL statements
         * that we'll use. Set the isConnected flag upon result.
         */
        try {
            dbConnection = DriverManager.getConnection(this.getDatabaseUrl(), dbProperties);
            stmtSaveNewRecord = dbConnection.prepareStatement(strSaveAsset);
            stmtUpdateExistingRecord = dbConnection.prepareStatement(strUpdateAsset);
            stmtGetAsset = dbConnection.prepareStatement(strGetAsset);
            stmtGetAssetList = dbConnection.prepareStatement(strGetAssetList);
            stmtDeleteAsset = dbConnection.prepareStatement(strDeleteAsset);
            stmtUpdateLastAccessed = dbConnection.prepareStatement(strUpdateLastAccessed);
            stmtComputeTotalSize = dbConnection.prepareStatement(strComputeTotalSize);

            this.isConnected = dbConnection != null;
        } catch (SQLException ex) {
            isConnected = false;
            dbConnection = null;
            ex.printStackTrace();
        }

        logger.fine("AssetDB: Done attempting to connect, ret=" + this.isConnected);
        return isConnected;
    }

    
    /**
     * Create the tables in the database, takes an open connection to the database.
     * Returns true upon success, false upon failure.
     * 
     * @param dbConnection The open connection to the database
     * @return True upon success, false upon failure.
     */
    private boolean createTables(Connection dbConnection) {
        try {
            Statement statement = dbConnection.createStatement();
            statement.execute(strCreateAssetTable);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Creates the database. Returns true if the database was successfully
     * created, false if not
     * 
     * @return True if the database was successfully created, false if not
     */
    private boolean createDatabase() {
        boolean bCreated = false;

        /*
         * Create the database. Upon exception, print out a message to the log
         * and return false. Otherwise return true.
         */
        dbProperties.put("create", "true");        
        try {
            dbProperties.list(System.out);
            Connection tmpConnection = DriverManager.getConnection(this.getDatabaseUrl(), dbProperties);
            bCreated = createTables(tmpConnection);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to create database "+ex.getMessage(), ex);
            ex.printStackTrace();
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Failed to create database "+e.getMessage(), e);
            e.printStackTrace();
        } catch(Error er) {
            logger.log(Level.SEVERE, "Failed to create database "+er.getMessage(), er);
            er.printStackTrace();
            
        }
        dbProperties.remove("create");
        logger.fine("AssetDB: Created new database at " + this.getDatabaseLocation());
        return bCreated;
    }

    /**
     * Update the "last accessed" time with the current time (in milliseconds
     * since the epoch) for the given asset uri and checksum
     */
    private void updateLastAccessed(AssetID assetID) {
        synchronized(stmtUpdateLastAccessed) {
            try {
                String uri = assetID.getAssetURI().toExternalForm();
                String checksum = (assetID.getChecksum() == null) ? "" : assetID.getChecksum();
                
                stmtUpdateLastAccessed.clearParameters();
                stmtUpdateLastAccessed.setLong(1, System.currentTimeMillis());
                stmtUpdateLastAccessed.setString(2, uri);
                stmtUpdateLastAccessed.setString(3, checksum);
                stmtUpdateLastAccessed.executeUpdate();
            } catch(SQLException sqle) {
                logger.log(Level.SEVERE, "AssetDB: SQL Error updating last accessed for " + assetID.getAssetURI());
                sqle.printStackTrace();
            }
        }
    }
    
    /**
     * Computes and returns the sum of all of the assets. Throws a SQLException
     * upon error
     * 
     * @return The size in bytes of all of the assets
     * @throw SQLException Upon error reading the total size
     */
    public long getTotalSize() throws SQLException {
        synchronized (stmtComputeTotalSize) {
            /* Do the SQL statement to compute the sum */
            stmtComputeTotalSize.clearParameters();
            ResultSet result = stmtComputeTotalSize.executeQuery();

            /* Fetch the one result, which should be the sum */
            if (result.next() == true) {
                long size = result.getLong(0);
                return size;
            }
        }
        return 0;
    }
    
    /**
     * Prints out all of the assets to stdout
     */
    public void listAssets() {
        try {
            Statement queryStatement = dbConnection.createStatement();
            ResultSet result = queryStatement.executeQuery(strGetListEntries);
            logger.warning("[ASSET DB] LIST");
            while(result.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append(result.getString("ASSET_URI") + "\t");
                sb.append(result.getString("CHECKSUM") + "\t");
                sb.append(result.getString("URL")+"\t");
                sb.append(result.getString("TYPE") + "\t");
                sb.append(result.getLong("LAST_ACCESSED") + "\t");
                sb.append(result.getLong("SIZE") + "\n");
                logger.warning(sb.toString());
            }
            logger.warning("[ASSET DB] DONE LIST");
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    
    /**
     * Main method that has a simple command-line interface to test the database.
     * The usage is: java AssetDB [COMMAND] [ARGS], where COMMAND can be:
     * <p>
     * LIST: Lists all of the entries in the database
     * ADD: Add an entry to the database, followed by the required data fields
     */
    public static void main(String[] args) throws URISyntaxException, AssetDBException {
        /* Create the database and open a connection */
        AssetDB db = new AssetDB();
        
        /* Print out the essential information */
        logger.warning("AssetDB: Database Location: " + db.getDatabaseLocation());
        logger.warning("AssetDB: Database URL:      " + db.getDatabaseUrl());
        logger.warning("AssetDB: Is Connected?      " + db.isConnected());

//        AssetID assetID = new AssetID(new ResourceURI("wla://mpk20/sphere2.dae"), "4d92377dbd58f3ba2908354d2b9618f06303d5e9");
//        Asset asset = db.getAsset(assetID);
//        asset = new AssetFile(assetID);
//        db.addAsset(asset);
        
        /* List the assets in the database */
        db.listAssets();
        
        /* Disconnect from the database and exit */
        db.disconnect();
    }
   
    /* The various SQL statements to operate on the database */
    private PreparedStatement stmtSaveNewRecord;
    private PreparedStatement stmtUpdateExistingRecord;
    private PreparedStatement stmtGetAsset;
    private PreparedStatement stmtGetAssetList;
    private PreparedStatement stmtDeleteAsset;
    private PreparedStatement stmtUpdateLastAccessed;
    private PreparedStatement stmtComputeTotalSize;
   
    /* Creates the tables in the database */
    private static final String strCreateAssetTable =
            "create table APP.ASSET (" +
            "    ASSET_URI      VARCHAR(" + AssetDB.MAX_STRING_LENGTH + ") not null, " +
            "    CHECKSUM       VARCHAR(40) not null, " +
            "    URL            VARCHAR(" + AssetDB.MAX_STRING_LENGTH + "), " +
            "    TYPE           VARCHAR(10), " +
            "    LAST_ACCESSED  BIGINT, " +
            "    SIZE           BIGINT, " +
            "    PRIMARY KEY (ASSET_URI, CHECKSUM) " +
            ")";
    
    /* Get an asset based upon the unique resource path name and checksum */
    private static final String strGetAsset =
            "SELECT * FROM APP.ASSET WHERE ASSET_URI = ? AND CHECKSUM = ?";

    /* Get a list of assets based upon the unique resource path name */
    private static final String strGetAssetList =
            "SELECT * FROM APP.ASSET WHERE ASSET_URI = ?";

    /* Save an asset given all of its values */
    private static final String strSaveAsset =
            "INSERT INTO APP.ASSET " +
            "   (ASSET_URI, CHECKSUM, URL, TYPE, LAST_ACCESSED, SIZE)" +
            "VALUES (?, ?, ?, ?, ?, ?)";
    
    /* Return all of the entries based upon the unique resource path key */
    private static final String strGetListEntries =
            "SELECT ASSET_URI, CHECKSUM, URL, TYPE, LAST_ACCESSED, SIZE " +
            "FROM APP.ASSET ORDER BY ASSET_URI ASC";
    
    /* Updates an entry using its resource path and values */
    private static final String strUpdateAsset =
            "UPDATE APP.ASSET " +
            "SET ASSET_URI = ?, " +
            "    CHECKSUM = ?, " +
            "    URL = ?, " +
            "    TYPE = ?, " +
            "    LAST_ACCESSED = ?, " +
            "    SIZE = ? " +
            "WHERE ASSET_URI = ? AND CHECKSUM = ?";
    
    /* Updates an asset's last accessed time, used after a "get" */
    private static final String strUpdateLastAccessed =
            "UPDATE APP.ASSET " +
            "SET LAST_ACCESSED = ? " +
            "WHERE ASSET_URI = ? AND CHECKSUM = ?";
    
    /* Deletes an entry using its unique resource path */
    private static final String strDeleteAsset =
            "DELETE FROM APP.ASSET WHERE ASSET_URI = ? AND CHECKSUM = ?";
    
    /* Computes the sum of the sizes of the assets */
    private static final String strComputeTotalSize = "" +
            "SELECT SUM(SIZE) FROM APP.ASSET";
}
