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
package org.jdesktop.wonderland.client.datamgr;

import org.jdesktop.wonderland.client.assetmgr.Asset;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.common.AssetType;
import org.jdesktop.wonderland.common.AssetURI;

/**
 * Tests for the asset manager
 * 
 * @author paulby
 */
public class AssetManagerTest {

    private AssetManager assetManager;
    
    /* URLs to download */
    private static final String urls[] = {
        "wla://shape/MountainPicture.png",
    };
    
    public AssetManagerTest() {
    }
    
    public void setUp() {
        //System.setProperty("wonderland.dir",".wonderland-junit");
        assetManager = AssetManager.getAssetManager();
    }

    public void tearDown() {
    }

    public void testCachePath() {
        try {
            AssetURI assetURI1 = new AssetURI("http://www.foo.net/models/mymodel.gz");
            System.out.println("For: " + assetURI1.toString() + ", cache file=" + assetURI1.getRelativeCachePath());
            
            AssetURI assetURI2 = new AssetURI("models/mymodel.gz");
            System.out.println("For: " + assetURI2.toString() + ", cache file=" + assetURI2.getRelativeCachePath());
            
            AssetURI assetURI3 = new AssetURI("wlm://mpk20/models/mymodel.gz");
            System.out.println("For: " + assetURI3.toString() + ", cache file=" + assetURI3.getRelativeCachePath());           
        } catch (java.lang.Exception excp) {
        }
    }
    
//    public void checksumConversionTest(){
//        byte[] t1 = new byte[]{0, 1, 10, 13, 14, 15};
//
//        String resStr = Checksum.toHexString(t1);
//        byte[] resByteArray = AssetDB.fromHexString(resStr);
//        boolean fail = false;
//        for (int i = 0; i < t1.length; i++) {
//            if (t1[i] != resByteArray[i]) {
//                fail = true;
//            }
//        }
//        assertFalse(fail);        
//    }
    
    /*
    public void downloadBadURL() {
        try {
            Repository r = new Repository(new URL("http://error.error.com/"));
            Asset asset = assetManager.getAsset(AssetType.FILE, r, "foo", null);
            assertFalse(assetManager.waitForAsset(asset));
            assertNotNull(asset.getFailureInfo());
        } catch (MalformedURLException ex) {
            fail("Bad URL in test!");
        }
    }*/
    
    public void downloadFile() throws java.net.URISyntaxException {
        final Thread threads[] = new Thread[urls.length];
        for (int i = 0; i < urls.length; i++) {
            final int j = i;
            threads[i] = new Thread() {
                public void run() {
                    try {
                        AssetURI assetURI = new AssetURI(urls[j]);
                        Asset asset = assetManager.getAsset(assetURI, AssetType.FILE);
                        assertTrue(assetManager.waitForAsset(asset));
                        assertNull(asset.getFailureInfo());
                        assertNotNull(asset.getLocalCacheFile());
                        System.out.println("Done with: " + assetURI.toString());
                    } catch (java.net.URISyntaxException excp) {
                        System.out.println(excp.toString());
                    }
                }
            };
            threads[i].start();
        }
        
        for (int i = 0; i < urls.length; i++) {
            try {
                threads[i].join();
            } catch (java.lang.InterruptedException excp) {
                System.out.println(excp.toString());
            }
        }
    }
    
}
