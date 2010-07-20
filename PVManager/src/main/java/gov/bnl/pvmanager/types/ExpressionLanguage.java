/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package gov.bnl.pvmanager.types;

import gov.bnl.pvmanager.AggregatedExpression;
import gov.bnl.pvmanager.Collector;
import gov.bnl.pvmanager.Expression;
import gov.bnl.pvmanager.MonitorRecipe;
import gov.bnl.pvmanager.Function;
import gov.bnl.pvmanager.QueueCollector;
import gov.bnl.pvmanager.TimeDuration;
import gov.bnl.pvmanager.TimedCacheCollector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides support for the standard types and the basic building blocks of
 * the expression language.
 *
 * @author carcassi
 */
public class ExpressionLanguage {

    static {
        // Installs support for standard types
        StandardTypeSupport.install();
    }

    /**
     * Converts a list of expressions to and expression that returns the list of results.
     * @param expression a list of expressions
     * @return an expression representing the list of results
     */
    public static <T> AggregatedExpression<List<T>> listOf(AggregatedExpression<T>... expressions) {
        return listOf(Arrays.asList(expressions));
    }

    /**
     * Converts a list of expressions to and expression that returns the list of results.
     * @param expression a list of expressions
     * @return an expression representing the list of results
     */
    public static <T> AggregatedExpression<List<T>> listOf(List<AggregatedExpression<T>> expressions) {
        // The connections needed are the union of all connections.
        // Calculate all the needed functions to combine
        List<MonitorRecipe> recipes = new ArrayList<MonitorRecipe>();
        List<Function> functions = new ArrayList<Function>();
        for (AggregatedExpression<T> expression : expressions) {
            recipes.addAll(expression.getMonitorRecipes());
            functions.add(expression.getFunction());
        }

        // If the list of expression is large, the name is going to be big
        // and it might trigger an OutOfMemoryException just for this.
        // We cap the list of names to 10
        String name = null;
        if (expressions.size() < 10) {
            name = "list" + expressions;
        } else {
            name = "list(...)";
        }

        @SuppressWarnings("unchecked")
        AggregatedExpression<List<T>> expression = new AggregatedExpression<List<T>>(recipes,
                (Class<List<T>>) (Class) List.class, (Function<List<T>>) (Function) new ListOfFunction(functions), name);
        return expression;
    }

    /**
     * Aggregates the sample at the scan rate and takes the average.
     * @param doublePv the expression to take the average of; can't be null
     * @return an expression representing the average of the expression
     */
    public static AggregatedExpression<Double> averageOf(Expression<Double> doublePv) {
        Collector<Double> collector = new QueueCollector<Double>(doublePv.getFunction());
        return new AggregatedExpression<Double>(doublePv.createMontiorRecipes(collector),
                Double.class, new AverageAggregator(collector), "avg(" + doublePv.getDefaultName() + ")");
    }

    /**
     * Applies {@link #averageOf(gov.bnl.nsls2.pvmanager.Expression) to all
     * arguments.
     *
     * @param doubleExpressions a list of double expressions
     * @return a list of average expressions
     */
    public static List<AggregatedExpression<Double>> averageOf(List<Expression<Double>> doubleExpressions) {
        List<AggregatedExpression<Double>> expressions = new ArrayList<AggregatedExpression<Double>>();
        for (Expression<Double> doubleExpression : doubleExpressions) {
            expressions.add(averageOf(doubleExpression));
        }
        return expressions;
    }

    /**
     * Aggregates the sample at the scan rate and calculates statistical information.
     * @param doublePv the expression to calculate the statistics information on; can't be null
     * @return an expression representing the statistical information of the expression
     */
    public static AggregatedExpression<DoubleStatistics> statisticsOf(Expression<Double> doublePv) {
        Collector<Double> collector = new QueueCollector<Double>(doublePv.getFunction());
        return new AggregatedExpression<DoubleStatistics>(doublePv.createMontiorRecipes(collector),
                DoubleStatistics.class, new StatisticsAggregator(collector), "stats(" + doublePv.getDefaultName() + ")");
    }

    /**
     * Applies {@link #statisticsOf(gov.bnl.nsls2.pvmanager.Expression) to all
     * arguments.
     *
     * @param doubleExpressions a list of double expressions
     * @return a list of statistical expressions
     */
    public static List<AggregatedExpression<DoubleStatistics>> statisticsOf(List<Expression<Double>> doubleExpressions) {
        List<AggregatedExpression<DoubleStatistics>> expressions = new ArrayList<AggregatedExpression<DoubleStatistics>>();
        for (Expression<Double> doubleExpression : doubleExpressions) {
            expressions.add(statisticsOf(doubleExpression));
        }
        return expressions;
    }

    /**
     * A CA channel with the given name of type double.
     * @param name the channel name; can't be null
     * @return an expression representing the channel
     */
    public static Expression<Double> doublePv(String name) {
        return new Expression<Double>(name, Double.class);
    }

    public static List<Expression<Double>> doublePvs(List<String> names) {
        List<Expression<Double>> expressions = new ArrayList<Expression<Double>>();
        for (String name : names) {
            expressions.add(doublePv(name));
        }
        return expressions;
    }

    public static <T> AggregatedExpression<SynchronizedArray<T>>
            synchronizedArrayOf(TimeDuration tolerance, List<Expression<T>> expressions) {
        List<MonitorRecipe> recipes = new ArrayList<MonitorRecipe>();
        List<String> names = new ArrayList<String>();
        List<TimedCacheCollector<T>> collectors = new ArrayList<TimedCacheCollector<T>>();
        for (Expression<T> expression : expressions) {
            TimedCacheCollector<T> collector =
                    new TimedCacheCollector<T>(expression.getFunction(), tolerance.multiplyBy(10));
            collectors.add(collector);
            recipes.addAll(expression.createMontiorRecipes(collector));
            names.add(expression.getDefaultName());
        }
        SynchronizedArrayAggregator<T> aggregator =
                new SynchronizedArrayAggregator<T>(names, collectors, TimeDuration.ms(100));
        return new AggregatedExpression<SynchronizedArray<T>>(recipes,
                aggregator.getType(),
                aggregator, "syncArray");
    }

    public static <T> AggregatedExpression<SynchronizedArray<T>>
            synchronizedArrayOf(TimeDuration tolerance, TimeDuration distanceBetweenSamples, List<Expression<T>> expressions) {
        List<MonitorRecipe> recipes = new ArrayList<MonitorRecipe>();
        List<String> names = new ArrayList<String>();
        List<TimedCacheCollector<T>> collectors = new ArrayList<TimedCacheCollector<T>>();
        for (Expression<T> expression : expressions) {
            TimedCacheCollector<T> collector =
                    new TimedCacheCollector<T>(expression.getFunction(), distanceBetweenSamples.multiplyBy(5));
            collectors.add(collector);
            recipes.addAll(expression.createMontiorRecipes(collector));
            names.add(expression.getDefaultName());
        }
        SynchronizedArrayAggregator<T> aggregator =
                new SynchronizedArrayAggregator<T>(names, collectors, tolerance);
        return new AggregatedExpression<SynchronizedArray<T>>(recipes,
                aggregator.getType(),
                aggregator, "syncArray");
    }

}