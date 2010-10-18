/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.types.ExpressionLanguage.*;

/**
 *
 * @author carcassi
 */
public class CustomFunctionTest {

    public CustomFunctionTest() {
    }

    public static class ExpressionTester {
        private DesiredRateExpression<?> expression;
        public ExpressionTester(DesiredRateExpression<?> expression) {
            this.expression = expression;
        }

        public void writeValue(String name, Object value) {
            for (Collector<?> collector : expression.getDataRecipe().getChannelsPerCollectors().keySet()) {
                ValueCache<Object> cache = expression.getDataRecipe().getChannelsPerCollectors().get(collector).get(name);
                if (cache != null) {
                    cache.setValue(value);
                    collector.collect();
                }
            }
        }
    }

    @Test
    public void singleArgFunction() {
        // Sets up a pipeline where we put data in the cache, and apply
        // a function to it
        SourceRateExpression<Double> vDouble = new SourceRateExpression<Double>("test", Double.class);
        @SuppressWarnings("unchecked")
        DesiredRateExpression<Double> expression = resultOf(new OneArgFunction<Double, Double>() {
            @Override
            public Double calculate(Double arg) {
                return - arg;
            }
        }, latestValueOf(vDouble));
        ExpressionTester tester = new ExpressionTester(expression);
        Function<Double> function = expression.getFunction();

        // Test values
        tester.writeValue("test", 1.0);
        assertEquals((Object) (-1.0), function.getValue());

        tester.writeValue("test", 123.0);
        assertEquals((Object) (-123.0), function.getValue());

        tester.writeValue("test", null);
        assertEquals((Object) (-123.0), function.getValue());

        tester.writeValue("test", 1.0);
        tester.writeValue("test", 2.0);
        tester.writeValue("test", 3.0);
        assertEquals((Object) (-3.0), function.getValue());
    }

    @Test
    public void twoArgFunction() {
        // Sets up a pipeline where we put data in the cache, and apply
        // a function to it
        SourceRateExpression<String> value1 = new SourceRateExpression<String>("test1", String.class);
        SourceRateExpression<Integer> value2 = new SourceRateExpression<Integer>("test2", Integer.class);
        DesiredRateExpression<String> expression = resultOf(new TwoArgFunction<String, String, Integer>() {
            @Override
            public String calculate(String name, Integer number) {
                return name + "(" + number + ")";
            }
        }, latestValueOf(value1), latestValueOf(value2));
        ExpressionTester tester = new ExpressionTester(expression);
        Function<String> function = expression.getFunction();

        // Test values
        tester.writeValue("test1", "test");
        tester.writeValue("test2", 1);
        assertEquals("test(1)", function.getValue());

        tester.writeValue("test2", 2);
        assertEquals("test(2)", function.getValue());

        tester.writeValue("test1", "A");
        tester.writeValue("test1", "B");
        tester.writeValue("test1", "C");
        assertEquals("C(2)", function.getValue());
    }

    @Test
    public void nArgFunction() {
        // Sets up a pipeline where we put data in the cache, and apply
        // a function to it
        SourceRateExpression<Integer> value1 = new SourceRateExpression<Integer>("test1", Integer.class);
        SourceRateExpression<Integer> value2 = new SourceRateExpression<Integer>("test2", Integer.class);
        SourceRateExpression<Integer> value3 = new SourceRateExpression<Integer>("test3", Integer.class);
        DesiredRateExpression<Double> expression = resultOf(new OneArgFunction<Double, List<Integer>>() {
            @Override
            public Double calculate(List<Integer> numbers) {
                double average = 0;
                for (int value : numbers) {
                    average += value;
                }
                return average / numbers.size();
            }
        }, listOf(latestValueOf(value1), latestValueOf(value2), latestValueOf(value3)));
        ExpressionTester tester = new ExpressionTester(expression);
        Function<Double> function = expression.getFunction();

        // Test values
        tester.writeValue("test1", 0);
        tester.writeValue("test2", 1);
        tester.writeValue("test3", 2);
        assertEquals((Object) (1.0), function.getValue());

        // Test values
        tester.writeValue("test3", 5);
        assertEquals((Object) (2.0), function.getValue());
    }

}