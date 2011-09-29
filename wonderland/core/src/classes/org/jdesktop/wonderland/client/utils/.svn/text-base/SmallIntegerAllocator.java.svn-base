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
package org.jdesktop.wonderland.client.utils;

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Allocates a small integer which is different from all the other small integers which have been allocated 
 * in this program run. The numbers start from 0. Numbers should be freed when they are no longer in use.
 *
 * @author deronj
 */

@ExperimentalAPI
public class SmallIntegerAllocator {

    /** The amount to grow the list by when the list is exhausted */
    protected static final int INCREMENT = 10;

    /** An array which indicates which integers have been allocated */
    protected boolean[] allocated = new boolean[INCREMENT];
    
    /** The number of integers currently allocated */
    protected int numAllocated;

    /** 
     * Construct a new instance of SmallIntegerAllocator, with no integers preallocated.
     */
    public SmallIntegerAllocator () {}

    /** 
     * Construct a new instance of SmallIntegerAllocator, with the given number of integers preallocated.
     */
    public SmallIntegerAllocator (int numPreAlloc) {
	for (int i = 0; i < numPreAlloc; i++) {
	    allocate();
	}
    }

    /**
     * Allocate a small integer which is unique in this program run.
     *
     * @return The integer.
     */
    public synchronized int allocate () {
	int i = findInList();
	if (i >= 0) {
	    numAllocated++;
	    return i;
	}

	int prevLength = growList();
	i = findInList(prevLength);
	if (i < 0) {
	    throw new RuntimeException("Internal error: cannot allocate even after growing the list");
	}
	    
	numAllocated++;
	return i;
    }

    /**
     * Release an allocated integer
     *
     * @param i The value to release.
     */
    public synchronized void free (int i) {
	if (!allocated[i]) {
	    return;
	}
	allocated[i] = false;
	numAllocated--;
    }

    /**
     * The number of integers currently allocated.
     */
    public int getNumAllocated () {
	return numAllocated;
    }

    /** 
     * Find an unallocated integer and allocate it.
     * Start the search from 0.
     */
    protected int findInList () {
	return findInList(0);
    }

    /** 
     * Find an unallocated integer and allocate it.
     * Start the search from the given index.
     */
    protected int findInList (int startIdx) {
	for (int i = startIdx; i < allocated.length; i++) {
	    if (!allocated[i]) {
		allocated[i] = true;
		return i;
	    }
	}
	return -1;
    }

    /**
     * All integers in our current array have been allocated and we must grow the array to contain new 
     * unallocated ones.
     *
     * @return The length of the arary *BEFORE* this routine was called.
     */
    protected int growList () {
	int prevLength = allocated.length;

	boolean[] allocatedOld = allocated;
	allocated = new boolean[prevLength + INCREMENT];
	    
	// Copy old contents into new list
	System.arraycopy(allocatedOld, 0, allocated, 0, prevLength);

	return prevLength;
    }
}
