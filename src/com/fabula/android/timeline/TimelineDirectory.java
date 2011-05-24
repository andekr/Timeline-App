package com.fabula.android.timeline;

import java.io.File;

/**
 * 
 * Helper class for filehandling of timeline databases.
 * 
 * @author andekr
 *
 */
public class TimelineDirectory {
	
	private final static String DIRECTORY_PATH = "data/data/com.fabula.android.timeline/databases/";
	private File directory;

	public TimelineDirectory() {
		
		directory = new File(DIRECTORY_PATH);
		if(!directory.exists())
			directory.mkdirs();
	}

	public boolean deleteTimeline(String timelineName) {
		return new File(DIRECTORY_PATH+timelineName).delete();
	}
}
