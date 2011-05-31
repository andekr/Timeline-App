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

/**
 * An interface for activities that should start with a progress bar.
 * 
 * @author andekr
 *
 */
public interface ProgressDialogActivity {

	public void callBack();
	public void runOnUiThread(Runnable runnable);
}
