/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.AggregatedExpression;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.DataRecipe;
import org.epics.pvmanager.Expression;
import org.epics.pvmanager.QueueCollector;
import org.epics.pvmanager.TimeDuration;
import org.epics.pvmanager.TimedCacheCollector;

/**
 * PVManager expression language support for EPICS types.
 *
 * @author carcassi
 */
public class ExpressionLanguage {
    private ExpressionLanguage() {}

    static {
        // Add support for Epics types.
        EpicsTypeSupport.install();
    }

    /**
     * A channel with the given name of type VDouble.
     *
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static Expression<VDouble> vDouble(String name) {
        return new Expression<VDouble>(name, VDouble.class);
    }

    public static List<Expression<VDouble>> vDoubles(List<String> names) {
        List<Expression<VDouble>> expressions = new ArrayList<Expression<VDouble>>();
        for (String name : names) {
            expressions.add(vDouble(name));
        }
        return expressions;
    }

    /**
     * Aggregates the sample at the scan rate and calculates statistical information.
     * @param doublePv the expression to calculate the statistics information on; can't be null
     * @return an expression representing the statistical information of the expression
     */
    public static AggregatedExpression<VStatistics> statisticsOf(Expression<VDouble> doublePv) {
        Collector<VDouble> collector = new QueueCollector<VDouble>(doublePv.getFunction());
        return new AggregatedExpression<VStatistics>(doublePv.createMontiorRecipes(collector),
                new StatisticsDoubleAggregator(collector), "stats(" + doublePv.getDefaultName() + ")");
    }

    public static AggregatedExpression<VMultiDouble>
            synchronizedArrayOf(TimeDuration tolerance, List<Expression<VDouble>> expressions) {
        List<String> names = new ArrayList<String>();
        List<TimedCacheCollector<VDouble>> collectors = new ArrayList<TimedCacheCollector<VDouble>>();
        DataRecipe recipe = new DataRecipe();
        for (Expression<VDouble> expression : expressions) {
            TimedCacheCollector<VDouble> collector =
                    new TimedCacheCollector<VDouble>(expression.getFunction(), tolerance.multiplyBy(10));
            collectors.add(collector);
            recipe = recipe.includeRecipe(expression.createMontiorRecipes(collector));
            names.add(expression.getDefaultName());
        }
        SynchronizedVDoubleAggregator aggregator =
                new SynchronizedVDoubleAggregator(names, collectors, tolerance);
        return new AggregatedExpression<VMultiDouble>(recipe,
                aggregator, "syncArray");
    }

    public static AggregatedExpression<VMultiDouble>
            synchronizedArrayOf(TimeDuration tolerance, TimeDuration cacheDepth, List<Expression<VDouble>> expressions) {
        if (cacheDepth.equals(TimeDuration.ms(0)))
            throw new IllegalArgumentException("Distance between samples must be non-zero");
        List<String> names = new ArrayList<String>();
        List<TimedCacheCollector<VDouble>> collectors = new ArrayList<TimedCacheCollector<VDouble>>();
        DataRecipe recipe = new DataRecipe();
        for (Expression<VDouble> expression : expressions) {
            TimedCacheCollector<VDouble> collector =
                    new TimedCacheCollector<VDouble>(expression.getFunction(), cacheDepth);
            collectors.add(collector);
            recipe = recipe.includeRecipe(expression.createMontiorRecipes(collector));
            names.add(expression.getDefaultName());
        }
        SynchronizedVDoubleAggregator aggregator =
                new SynchronizedVDoubleAggregator(names, collectors, tolerance);
        return new AggregatedExpression<VMultiDouble>(recipe,
                aggregator, "syncArray");
    }

}