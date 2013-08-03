package net.beaner.mapthatsplat;

import android.app.ProgressDialog;
import android.content.Context;

public class Dialog
{
  private static class SplatProgressDialog extends ProgressDialog
  {
    public SplatProgressDialog(final Context context, final String message)
    {
      super(context);
      setMessage(message);
      setIndeterminate(true);
      setCancelable(false);
    } // CycleStreetsProgressDialog
    
    @Override
    public void dismiss()
    {
      try {
        super.dismiss();
      } // try
      catch(final IllegalArgumentException e) {
        // suppress
      } // catch
    } // dismiss
  } // class SplatProgressDialog
  
  static public ProgressDialog createProgressDialog(final Context context,
		  final String message)
  {
	  final ProgressDialog progress = new SplatProgressDialog(context, message);
	  return progress;
  } // createProgressDialog
}
