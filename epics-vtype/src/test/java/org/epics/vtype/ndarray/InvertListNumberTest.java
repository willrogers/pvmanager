/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype.ndarray;

import java.util.Arrays;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.epics.vtype.table.VTableFactory.*;
import static org.epics.vtype.ValueFactory.*;

/**
 *
 * @author carcassi
 */
public class InvertListNumberTest {
    
    @Test
    public void fillCoords1() {
        ListInt sizes = new ArrayInt(15,3,2);
        int[] actualCoords = new int[] {13,2,0};
        int index = (13 * 3 + 2) * 2 + 0;
        int[] coords = new int[3];
        InvertListNumber.fillCoords(coords, index, sizes);
        assertThat(coords, equalTo(actualCoords));
    }
    
    @Test
    public void fillCoords2() {
        ListInt sizes = new ArrayInt(15,3,2);
        int[] actualCoords = new int[] {10,0,1};
        int index = (10 * 3 + 0) * 2 + 1;
        int[] coords = new int[3];
        InvertListNumber.fillCoords(coords, index, sizes);
        assertThat(coords, equalTo(actualCoords));
    }
    
    @Test
    public void index1() {
        ListInt sizes = new ArrayInt(15,3,2);
        int[] actualCoords = new int[] {10,0,1};
        boolean[] invert = new boolean[] {false, false, false};
        int index = (10 * 3 + 0) * 2 + 1;
        assertThat(InvertListNumber.index(actualCoords, sizes, invert), equalTo(index));
    }
    
    @Test
    public void index2() {
        ListInt sizes = new ArrayInt(15,3,2);
        int[] actualCoords = new int[] {10,0,1};
        boolean[] invert = new boolean[] {false, true, false};
        int index = (10 * 3 + 2) * 2 + 1;
        assertThat(InvertListNumber.index(actualCoords, sizes, invert), equalTo(index));
    }
    
    @Test
    public void index3() {
        ListInt sizes = new ArrayInt(15,3,2);
        int[] actualCoords = new int[] {10,0,1};
        boolean[] invert = new boolean[] {true, true, false};
        int index = (4 * 3 + 2) * 2 + 1;
        assertThat(InvertListNumber.index(actualCoords, sizes, invert), equalTo(index));
    }
}