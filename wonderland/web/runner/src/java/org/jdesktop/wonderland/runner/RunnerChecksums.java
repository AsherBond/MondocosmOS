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

package org.jdesktop.wonderland.runner;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.checksums.Checksum;
import org.jdesktop.wonderland.common.checksums.ChecksumList;
import org.jdesktop.wonderland.web.asset.deployer.AssetDeployer;
import org.jdesktop.wonderland.web.asset.deployer.AssetDeployer.DeployedAsset;
import org.jdesktop.wonderland.web.checksums.ChecksumFactory;
import org.jdesktop.wonderland.web.checksums.ChecksumFactory.ChecksumAction;
import org.jdesktop.wonderland.web.checksums.ChecksumManager;
import org.jdesktop.wonderland.web.checksums.modules.ModuleAssetDescriptor;

/**
 * The ChecksumList class represents a collection of checkums for all runners
 * <p>
 * This class uses JAXB to encode/decode the class to/from XML, either on disk
 * or over the network
 * 
 * @author kaplanj
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="runner-checksums")
public class RunnerChecksums {
    private static final Logger logger =
            Logger.getLogger(RunnerChecksums.class.getName());

    /* A list of checksum entries */
    @XmlElements({
        @XmlElement(name="checksum")
    })
    public List<RunnerChecksum> checksums = new LinkedList<RunnerChecksum>();

    /*
     * The internal representation of the checksums as a hashed map. The HashMap
     * class is not supported by JAXB so we must convert it to a list for
     * serialization
     */
    @XmlTransient
    public Map<String, RunnerChecksum> internalChecksums =
            new LinkedHashMap<String, RunnerChecksum>();
    
    /* The JAXB context for later use */
    private static JAXBContext context = null;
    
    /* Create the XML marshaller and unmarshaller once for all ModuleInfos */
    static {
        try {
            context = JAXBContext.newInstance(RunnerChecksums.class);
        } catch (javax.xml.bind.JAXBException excp) {
            System.out.println(excp.toString());
        }
    }
    
    /** Default constructor */
    public RunnerChecksums() {
    }
    
    /**
     * Sets the array of individual checksums.
     * 
     * @param checksums A map of Checksum objects
     */
    public void setChecksums(Map<String, RunnerChecksum> checksums) {
        this.internalChecksums = Collections.synchronizedMap(new LinkedHashMap(checksums));
    }
    
    /**
     * Returns the array of individual checksums.
     * 
     * @return An array of Checksum objects
     */
    @XmlTransient
    public Map<String, RunnerChecksum> getChecksums() {
        return this.internalChecksums;
    }
    
    /**
     * Takes a map of checksums and puts it in this map, overwriting any existing
     * entries.
     * 
     * @param checksums A map of Checksums objects to add
     */
    public void putChecksums(Map<String, RunnerChecksum> checksums) {
        this.internalChecksums.putAll(checksums);
    }
    
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the ChecksumList class
     * <p>
     * @param r The input reader of the version XML file
     * @throw ClassCastException If the input file does not map to ChecksumList
     * @throw JAXBException Upon error reading the XML file
     */
    public static RunnerChecksums decode(Reader r) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        RunnerChecksums rc = (RunnerChecksums) unmarshaller.unmarshal(r);
        
        /* Convert metadata to internal representation */
        if (rc.checksums != null) {
            rc.internalChecksums = new LinkedHashMap<String, RunnerChecksum>();
            for (RunnerChecksum c : rc.checksums) {
                rc.internalChecksums.put(c.getPathName(), c);
            }
        }
        else {
            rc.internalChecksums = null;
        }
        return rc;
    }
    
    /**
     * Writes the ChecksumList class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        /* Convert internal checksum hash to one suitable for serialization */
        updateInternal();

        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, w);
    }

    /**
     * Writes the ChecksumList class to an output stream.
     * <p>
     * @param os The output stream to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(OutputStream os) throws JAXBException {
        updateInternal();

        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(this, os);
    }
    
    /**
     * Update the internal state of the object so the checksums and
     * internalChecksums objects represent the same data.  This
     * method makes checksums reflect any change to internalChecksums
     */
    protected void updateInternal() {
        if (this.internalChecksums != null) {
            this.checksums = new LinkedList<RunnerChecksum>(internalChecksums.values());
        } else {
            this.checksums = null;
        }
    }

    /**
     * Creates an returns a new instance of the RunnerCheckums object given
     * a set of asset types to accept.  This will search all deployed modules
     * for assets of that type
     * @param types the set of acceptable type of asset
     * @param builder the UriBuilder for generating URLs
     */
    public static RunnerChecksums generate(List<String> types, UriBuilder builder) {
        Map<String, RunnerChecksum> out = new LinkedHashMap<String, RunnerChecksum>();

        /*
         * Get a map of all of the Checksum objects for each art asset. We see
         * if the module name matches each entry and collect its checksum
         * entries into a single map.
         */
        Map<DeployedAsset, File> partMap = AssetDeployer.getFileMap();
        for (DeployedAsset asset : partMap.keySet()) {
            if (types.contains(asset.assetType)) {
                String moduleName = asset.moduleName;

                // go through each checksum, and create a checksum
                // to add to the output. If there is no factory to load the
                // checksum for the asset, the go onto the next deployed asset
                // part
                ModuleAssetDescriptor mad = new ModuleAssetDescriptor(moduleName, asset.assetType, null);
                ChecksumManager checksumManager = ChecksumManager.getChecksumManager();
                ChecksumFactory factory = checksumManager.getChecksumFactory(mad);
                ChecksumList checksumList = factory.getChecksumList(mad, ChecksumAction.DO_NOT_GENERATE);
                if (checksumList == null) {
                    continue;
                }

                for (Map.Entry<String, Checksum> e : checksumList.getChecksumMap().entrySet()) {
                    String assetName = e.getKey();
                    Checksum assetChecksum = e.getValue();

                    try {
                        URL assetURL = getAssetURL(builder, moduleName, assetName);
                        out.put(assetName,
                                new RunnerChecksum(assetChecksum,
                                                     moduleName,
                                                     assetURL));
                    } catch (IOException ioe) {
                        logger.log(Level.WARNING, "Error getting url for " +
                                   assetName, ioe);
                    }
                }
            }
        }

        // create the checksums object to write
        RunnerChecksums ret = new RunnerChecksums();
        ret.putChecksums(out);
        return ret;
    }


    /**
     * Get the URL for an asset on this server
     * @return the asset URL
     */
    protected static URL getAssetURL(UriBuilder builder, String moduleName,
                                     String assetPath)
        throws MalformedURLException
    {
        // Should we fetch the context prefix from a property? XXX -jslott
        String assetPrefix = "webdav/content/modules/installed/";
        builder.replacePath(assetPrefix + moduleName + "/" + assetPath);

        return builder.build().toURL();
    }
  
}
