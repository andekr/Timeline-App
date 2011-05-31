/*******************************************************************************
 * Copyright (c) 2011 Andreas Storlien and Anders Kristiansen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Andreas Storlien and Anders Kristiansen - initial API and implementation
 ******************************************************************************/
package com.fabula.android.timeline.models;

public enum ModelType {
	SimpleNote(1), SimplePicture(2), SimpleVideo(3), SimpleRecording(4);
	
	private final int numberOfType;
	
	private ModelType(int number) {
		this.numberOfType = number;
	}
	
	public int numberOfType() { return numberOfType; }
}
