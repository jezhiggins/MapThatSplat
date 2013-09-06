package net.beaner.mapthatsplat;


import org.osmdroid.api.IGeoPoint;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class UploadPhotoTask extends AsyncTask<Object, Void, Boolean>
{
	private final UploadListener frag_;
	private final String filename_;
	private final IGeoPoint location_;
	private final String animal_;
	private final ProgressDialog progress_;
        
	UploadPhotoTask(final UploadListener fragment,
					        final IGeoPoint location,
					        final String animal,
	                final String photo)
	{
		frag_ = fragment;
		location_ = location;
		animal_ = animal;
		filename_ = photo;
        progress_ = Dialog.createProgressDialog(fragment.getActivity(), "Uploading Splat ...");
	} // UploadPhotoTask
	    
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		progress_.show();
	} // onPreExecute
	    
	protected Boolean doInBackground(Object... params)
	{
	  try {
		  return Website.uploadSplat(
				  ((double)location_.getLongitudeE6())/1E6,
				  ((double)location_.getLatitudeE6())/1E6,
				  filename_,
				  animal_);
	  } // try
	  catch(Exception e) {
		  return false;
	  }
	} // doInBackground
	    
	@Override
	protected void onPostExecute(final Boolean result)
	{
		progress_.dismiss();
		if(result)
			frag_.uploadComplete();
		else
			frag_.uploadFailed();
	} // onPostExecute
} // class UploadPho

