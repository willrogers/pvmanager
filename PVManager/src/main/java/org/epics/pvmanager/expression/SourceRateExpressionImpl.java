/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.DataRecipeBuilder;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.ValueCache;

/**
 * An expression that represent a pv read at the CA rate.
 * Objects of this class are not created directly but through the operators defined
 * in {@link ExpressionLanguage}.
 *
 * @param <T> type returned by the expression
 * @author carcassi
 */
public class SourceRateExpressionImpl<T> extends SourceRateExpressionListImpl<T> implements SourceRateExpression<T> {

    private Map<String, ValueCache> caches;
    private Function<T> function;
    private String name;
    
    {
        // Make sure that the list includes this expression
        addThis();
    }

    @Override
    public SourceRateExpressionImpl<T> as(String name) {
        this.name = name;
        return this;
    }

    /**
     * Constructor that represents a single pv of a particular type.
     *
     * @param pvName the name of the pv
     * @param pvType the type of the pv
     */
    public SourceRateExpressionImpl(String pvName, Class<T> pvType) {
        ValueCache<T> cache = new ValueCache<T>(pvType);
        caches = new HashMap<String, ValueCache>();
        caches.put(pvName, cache);
        this.function = cache;
        this.name = pvName;
    }

    /**
     * Creates a new source rate expression.
     * 
     * @param childExpression the expression used as argument used by this expression
     * @param function the function that will calculate the value for this expression
     * @param defaultName the name for this expression
     */
    public SourceRateExpressionImpl(SourceRateExpression<?> childExpression, Function<T> function, String defaultName) {
        this(Collections.<SourceRateExpression<?>>singletonList(childExpression), function, defaultName);
    }

    /**
     * Creates a new source rate expression.
     * 
     * @param childExpressions the expressions used as arguments by this expression
     * @param function the function that will calculate the value for this expression
     * @param defaultName the name for this expression
     */
    public SourceRateExpressionImpl(List<SourceRateExpression<?>> childExpressions, Function<T> function, String defaultName) {
        caches = new HashMap<String, ValueCache>();
        for (SourceRateExpression<?> childExpression : childExpressions) {
            for (Map.Entry<String, ValueCache> entry : childExpression.getSourceRateExpressionImpl().getCaches().entrySet()) {
                String pvName = entry.getKey();
                if (caches.keySet().contains(pvName)) {
                    throw new UnsupportedOperationException("Need to implement functions that take the same PV twice (right now we probably get double notifications)");
                }
                caches.put(pvName, entry.getValue());
            }
        }
        this.function = function;
        this.name = defaultName;
    }

    /**
     * Name representation of the expression.
     *
     * @return a name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns all the {@link ValueCache}s required by this expression.
     *
     * @return the value caches for this expression
     */
    private Map<String, ValueCache> getCaches() {
        return caches;
    }

    /**
     * Returns the function represented by this expression.
     *
     * @return the function
     */
    @Override
    public Function<T> getFunction() {
        return function;
    }

    /**
     * Creates a data recipe for the given expression.
     *
     * @param collector the collector to be notified by changes in this expression
     * @return a data recipe
     */
    DataRecipeBuilder createDataRecipe(Collector collector) {
        DataRecipeBuilder recipe = new DataRecipeBuilder();
        recipe.addCollector(collector, caches);
        return recipe;
    }

    @Override
    public SourceRateExpressionImpl<T> getSourceRateExpressionImpl() {
        return this;
    }

}