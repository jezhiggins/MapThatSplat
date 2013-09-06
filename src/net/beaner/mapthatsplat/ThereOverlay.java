package net.beaner.mapthatsplat;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IProjection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

public class ThereOverlay extends Overlay {
  private final Drawable thereMarker_;
  private MapView mapView_;
  private IGeoPoint there_ = null;
  
  public ThereOverlay(final Context context, final MapView mapView) {
    super(context);
    mapView_ = mapView;
    
    final Resources res = context.getResources();
    thereMarker_  = res.getDrawable(R.drawable.x_marks_spot);
  } // ThereOverlay
  
  public IGeoPoint there() { return there_; }
  public void noOverThere(final IGeoPoint there) {
    there_ = there;
    
    recentre();
  } // noOverThere
  
  public void recentre() {
    if((there_ == null) || (mapView_ == null))
      return;
   
    mapView_.getController().animateTo(there_);
    mapView_.invalidate();    
  } // recentre
  
  @Override
  public void draw(final Canvas canvas, final MapView mapView, final boolean shadow) {
    if(there_ == null)
      return;
    
    final Point screenPos = new Point();
    final IProjection projection = mapView.getProjection();
    projection.toPixels(there_, screenPos);

    final int halfWidth = thereMarker_.getIntrinsicWidth()/2;
    final int halfHeight = thereMarker_.getIntrinsicHeight()/2;
    thereMarker_.setBounds(new Rect(screenPos.x - halfWidth, 
                                    screenPos.y - halfHeight, 
                                    screenPos.x + halfWidth, 
                                    screenPos.y + halfHeight));
    thereMarker_.draw(canvas);
  } // onDrawFinished

  @Override
  public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView) {
    final IGeoPoint p = mapView.getProjection().fromPixels((int)event.getX(), (int)event.getY());
    noOverThere(p);
    return true;
  } // onSingleTap
} // class ThereOverlay
