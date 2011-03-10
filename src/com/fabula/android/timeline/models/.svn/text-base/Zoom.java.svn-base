package com.fabula.android.timeline.models;

import com.fabula.android.timeline.Utilities;


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
