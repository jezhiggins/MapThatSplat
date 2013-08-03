package net.beaner.mapthatsplat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public final class Photos {
	static private Uri photoUri_;
	
	static public Intent PhotoIntent(final Activity activity) throws IOException {
	      ContentValues values = new ContentValues();
	      values.put(MediaStore.Images.Media.TITLE, createPhotographFile());
	      Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
   		  photoUri_ = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		  photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri_);
		  photoIntent.putExtra("return-data", true);
		  return photoIntent;
	}
	
	static private String createPhotographFile() throws IOException {
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "splat-" + timeStamp;
	    return imageFileName;	
	}
	
	static public String getImageFilePath(final Activity activity, final Intent data)
    {
	    final Uri selectedImage = (data != null) ? data.getData() : photoUri_;
	    final String[] filePathColumn = { MediaStore.Images.Media.DATA };

	    final Cursor cursor = activity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	    try
	    {
	      cursor.moveToFirst();
	      return cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
	    } // try
	    finally
	    {
	      cursor.close();
	    } // finally
    } // getImageFilePath
}
