/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A collector the keeps the last n elements.
 *
 * @author carcassi
 */
public class NewCacheCollector<T> implements NewCollector<T, List<T>> {
    
    private final Object lock = new Object();
    private final List<T> readBuffer = new ArrayList<>();
    private final List<T> writeBuffer = new LinkedList<>();
    private final int nElements;

    public NewCacheCollector(int nElements) {
        this.nElements = nElements;
    }

    @Override
    public void setValue(T newValue) {
        synchronized(lock) {
            writeBuffer.add(newValue);
            if (writeBuffer.size() > nElements) {
                writeBuffer.remove(0);
            }
        }
    }

    @Override
    public List<T> getValue() {
        readBuffer.clear();
        synchronized(lock) {
            readBuffer.addAll(writeBuffer);
        }
        return readBuffer;
    }
    
}