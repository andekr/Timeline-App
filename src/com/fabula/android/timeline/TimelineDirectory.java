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
//	private ArrayList <Experience> timelines;
	

	public TimelineDirectory() {
		
		directory = new File(DIRECTORY_PATH);
		if(!directory.exists())
			directory.mkdirs();
		
//
//		FilenameFilter filter = new FilenameFilter() {
//			public boolean accept(File dir, String filename) {
//				boolean databaseExtension = filename.endsWith(".db");
//				int dot = filename.lastIndexOf("."); 
//				String filenameWithoutExtension = filename.substring(0, dot);
//				boolean notEmptyName = filenameWithoutExtension.length()>0;
//				boolean notAllTimelinesDatabaseName = !(filename.equals(Utilities.ALL_TIMELINES_DATABASE_NAME));
//				return (databaseExtension && notEmptyName && notAllTimelinesDatabaseName);
//			}
//		};
//		
//		addTimelineNamesToTimelinesList(filter);

	}

//	public void setTimelines(ArrayList<Experience> list) {
//		this.timelines = list;
//	}
//	
//	public ArrayList<Experience> getTimelines() {
//		return timelines;
//	}
//	
	public boolean deleteTimeline(String timelineName) {
		return new File(DIRECTORY_PATH+timelineName).delete();
	}
	
//	public String[] getTimelineNames() {
//		if(directory.exists()) {
//			String[] timelineArray = new String[timelines.size()];
//			for (int i = 0; i < timelineArray.length; i++) {
//				timelineArray[i] = timelines.get(i).toString();
//			}
//			return timelineArray;
//		}else{	
//			String[] emptyArray = new String[0];
//			return emptyArray;
//		}	
//	}
	
//	private void addTimelineNamesToTimelinesList(FilenameFilter filter) {
//		
//		timelines = new ArrayList<Experience>();
//			for (File file : directory.listFiles(filter)) {
//				Experience timeline = new Experience(file.getName());
//				timelines.add(timeline);
//			}
//		setTimelines(timelines);
//	}
}
