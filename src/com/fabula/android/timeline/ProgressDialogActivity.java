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
