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

import com.fabula.android.timeline.utilities.Utilities;


public enum Zoom {
	MONTH(Utilities.MONTH_MODE, 7, null),
	WEEK(Utilities.WEEK_MODE, 7, Zoom.MONTH),
	DAY(Utilities.DAY_MODE, 24, Zoom.WEEK),
	HOUR(Utilities.HOUR_MODE, 12, Zoom.DAY);

	private int type;
	private int columns;
	private Zoom previous, next;
	
	Zoom(int type, int columns, Zoom previous){
		this.type = type;
		this.columns = columns;
		this.previous = previous;
	}
	
	public int getType(){
		return type;
	}
	
	public int getColumns() {
		return columns;
	}
	
	public Zoom getPrevious() {
		return previous;
	}
	
	public Zoom getNext(){
		return next;
	}

	public void setNext(Zoom next) {
		this.next = next;
	}
	

}
