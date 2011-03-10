package com.fabula.android.timeline.models;

public enum ModelType {
	SimpleNote(1), SimplePicture(2), SimpleVideo(3), SimpleRecording(4);
	
	private final int numberOfType;
	
	private ModelType(int number) {
		this.numberOfType = number;
	}
	
	public int numberOfType() { return numberOfType; }
}
