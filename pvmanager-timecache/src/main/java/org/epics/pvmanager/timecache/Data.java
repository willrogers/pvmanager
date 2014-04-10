/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.epics.pvmanager.timecache;

import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;

/**
 * Represents a sample.
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public interface Data extends Comparable<Data> {

	public Timestamp getTimestamp();

	public VType getValue();

}