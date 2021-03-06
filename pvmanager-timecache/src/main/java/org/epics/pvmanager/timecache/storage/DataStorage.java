/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.timecache.storage;

import java.util.SortedSet;

import org.epics.pvmanager.timecache.Data;
import org.epics.pvmanager.timecache.DataChunk;
import org.epics.pvmanager.timecache.source.DataSource;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;

/**
 * Retrieves and stores samples from/to a storage.
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public interface DataStorage extends DataSource {

	/**
	 * Returns <code>true</code> if some data is available within the requested
	 * interval, <code>false</code> otherwise.
	 */
	public boolean hasAvailableData(TimeInterval interval);

	/**
	 * Returns all available {@link Data} within the requested
	 * {@link TimeInterval}.
	 */
	public SortedSet<Data> getAvailableData(TimeInterval interval);

	/**
	 * Stores all {@link Data} of the specified {@link DataChunk} and returns
	 * the corresponding set of {@link Data} ordered by {@link Timestamp}.
	 * @param chunk to store.
	 * @return set of {@link Data} ordered by {@link Timestamp}.
	 */
	public SortedSet<Data> storeData(DataChunk chunk);

	/**
	 * Adds a {@link DataStorageListener} to be notified when data is lost.
	 * @param listener to be added.
	 */
	public void addListener(DataStorageListener listener);

	/**
	 * Removes a {@link DataStorageListener}.
	 * @param listener to be removed
	 */
	public void removeListener(DataStorageListener listener);

}
