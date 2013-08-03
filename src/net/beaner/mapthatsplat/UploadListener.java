package net.beaner.mapthatsplat;

import android.app.Activity;

public interface UploadListener {
	Activity getActivity();
	
	void uploadComplete();
	void uploadFailed();
}
