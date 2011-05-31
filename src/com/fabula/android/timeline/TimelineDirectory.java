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
