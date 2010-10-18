/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_CTRL_Int;
import gov.aps.jca.dbr.DBR_TIME_Int;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.ExceptionHandler;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.data.VInt;

/**
 *
 * @author carcassi
 */
public class VIntProcessor extends SingleValueProcessor<VInt, DBR_TIME_Int, DBR_CTRL_Int> {

    public VIntProcessor(final Channel channel, Collector collector,
            ValueCache<VInt> cache, final ExceptionHandler handler)
            throws CAException {
        super(channel, collector, cache, handler);
    }

    @Override
    protected DBRType getMetaType() {
        return DBR_CTRL_Int.TYPE;
    }

    @Override
    protected DBRType getEpicsType() {
        return DBR_TIME_Int.TYPE;
    }

    @Override
    protected VInt createValue(DBR_TIME_Int value, DBR_CTRL_Int metadata, boolean disconnected) {
        return new VIntFromDbr(value, metadata, disconnected);
    }
}