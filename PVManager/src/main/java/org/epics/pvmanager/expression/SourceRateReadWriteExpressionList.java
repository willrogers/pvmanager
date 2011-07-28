/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import java.util.List;

/**
 * A list of expressions to write and to read at the rate of the source.
 * <p>
 * Don't implement objects with this interface, use {@link SourceRateReadWriteExpressionListImpl}.
 *
 * @param <R> type of the read payload
 * @param <W> type of the write payload
 * 
 * A list of desired rate expression, to have functions that work on multiple
 * expressions at the same time.
 *
 * @author carcassi
 */
public interface SourceRateReadWriteExpressionList<R, W> extends SourceRateExpressionList<R>, WriteExpressionList<W> {
    
    /**
     * Adds the given expressions to this list.
     * 
     * @param expressions a list of expressions
     * @return this
     */
    public SourceRateReadWriteExpressionList<R, W> and(SourceRateReadWriteExpressionList<R, W> expressions);

    /**
     * The expressions of this list.
     * 
     * @return a list of expressions
     */
    public List<SourceRateReadWriteExpression<R, W>> getSourceRateReadWriteExpressions();
}
