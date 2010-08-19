/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.sim;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Test simulated pv function names parsing
 *
 * @author carcassi
 */
public class NameParserTest {

    public NameParserTest() {
    }

    @Test
    public void testParameterParsing() {
        // A couple of correct combinations
        List<Object> parameters = NameParser.parseParameters("1.0,2.0");
        assertThat(parameters, equalTo(Arrays.asList((Object) 1.0, 2.0)));
        parameters = NameParser.parseParameters("-1,.5,  23.25");
        assertThat(parameters, equalTo(Arrays.asList((Object) (-1.0), 0.5,  23.25)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testError1() {
        NameParser.parseParameters("1.0 2.0");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testError2() {
        NameParser.parseParameters("1.O");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testError3() {
        NameParser.parseParameters("1.1.2");
    }

    @Test
    public void testParsing() {
        // Couple of correct functions
        List<Object> parameters = NameParser.parseFunction("sine(1.0,2.0)");
        assertThat(parameters, equalTo(Arrays.asList((Object) "sine",  1.0, 2.0)));
        parameters = NameParser.parseFunction("ramp(-1,.5,  23.25)");
        assertThat(parameters, equalTo(Arrays.asList((Object) "ramp", -1.0, 0.5,  23.25)));
    }

    @Test
    public void testRamp() {
        Ramp ramp = (Ramp) NameParser.createFunction("ramp(1.0, 10.0, 1.0, 1.0)");
        assertThat(ramp.nextValue().getValue(), equalTo(1.0));

    }

    @Test
    public void testSine() {
        Sine ramp = (Sine) NameParser.createFunction("sine(0.0, 10.0, 4.0, 1.0)");
        assertEquals(5.0, ramp.nextValue().getValue(), 0.0001);
        assertEquals(10.0, ramp.nextValue().getValue(), 0.0001);
        assertEquals(5.0, ramp.nextValue().getValue(), 0.0001);
        assertEquals(0.0, ramp.nextValue().getValue(), 0.0001);
    }

    @Test
    public void testNoise() {
        Noise noise1 = (Noise) NameParser.createFunction("noise(0.0, 10.0, 1.0)");
        Noise noise2 = (Noise) NameParser.createFunction("noise");
        // Forces use of variables
        assertTrue(noise1.nextValue().getAlarmStatus().isEmpty());
        assertTrue(noise2.nextValue().getAlarmStatus().isEmpty());
    }

    @Test
    public void gaussianNoise() {
        Gaussian noise1 = (Gaussian) NameParser.createFunction("gaussian(0.0, 10.0, 1.0)");
        Gaussian noise2 = (Gaussian) NameParser.createFunction("gaussian");
        // Forces use of variables
        assertTrue(noise1.nextValue().getAlarmStatus().isEmpty());
        assertTrue(noise2.nextValue().getAlarmStatus().isEmpty());
    }

}