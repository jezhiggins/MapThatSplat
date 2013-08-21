package net.beaner.mapthatsplat;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class SplatOverlay extends ItemizedIconOverlay<SplatOverlay.SplatItem>
                          implements MapListener
{
  
  public static class SplatItem extends OverlayItem {
    private final String url_;
    
    public SplatItem(final String title, 
                     final String url, 
                     final GeoPoint position) {
      super(title, "", position);
      url_ = url;
    }
    
    public String getUrl() { return url_; }
  } // class SplatItem
  
  private final MapView mapView_;
  private int zoomLevel_ = 99;
  private boolean loading_;
  private final int offset_;
  private final float radius_;
  private final Paint textBrush_;
  static private List<SplatItem> splats_ = new ArrayList<SplatItem>();
  static private final String LOADING = "Loading Splats ...";
  
  static public class SplatTapListener implements ItemizedIconOverlay.OnItemGestureListener<SplatItem>
  {
    private final Context context_;

    public SplatTapListener(final Context context) {
      context_ = context; 
    }
    
    @Override
    public boolean onItemLongPress(int index, SplatItem splat)
    {
      final Intent intent = new Intent(context_, DisplayPhotoActivity.class);
      intent.putExtra("caption", splat.getTitle());
      intent.putExtra("url", splat.getUrl());
      context_.startActivity(intent);
      return true;
    }

    @Override
    public boolean onItemSingleTapUp(int index, SplatItem splat)
    {
      Toast.makeText(context_, "SPLAT! " + splat.getTitle(), Toast.LENGTH_LONG).show();
      return false;
    }
  }

  public SplatOverlay(final Context context, final MapView mapView) {
    super(context, splats_, new SplatTapListener(context));

    mapView_ = mapView;
    
    offset_ = DrawingHelper.offset(context);
    radius_ = DrawingHelper.cornerRadius(context);
    textBrush_ = Brush.createTextBrush(offset_);

    mapView_.setMapListener(new DelayedMapListener(this));
  } // SplatOverlay

  @Override
  public void draw(final Canvas canvas, final MapView mapView, final boolean shadow) 
  {
    super.draw(canvas, mapView, shadow);
    
    if(!loading_) 
      return;
    
    final Rect bounds = new Rect();
    textBrush_.getTextBounds(LOADING, 0, LOADING.length(), bounds);

    int width = bounds.width() + (offset_ * 2);
    final Rect screen = canvas.getClipBounds();
    screen.left = screen.centerX() - (width/2); 
    screen.top += offset_ * 2;
    screen.right = screen.left + width;
    screen.bottom = screen.top + bounds.height() + (offset_ * 2);
      
    if(!DrawingHelper.drawRoundRect(canvas, screen, radius_, Brush.Grey))
      return;
    canvas.drawText(LOADING, screen.centerX(), screen.centerY() + bounds.bottom, textBrush_);
  } // drawButtons

  @Override
  public boolean onScroll(final ScrollEvent event) {
    refreshSplats();
    return true;
  } // onScroll
    
  @Override
  public boolean onZoom(final ZoomEvent event) {
    zoomLevel_ = event.getZoomLevel();
    refreshSplats();
    return true;
  } // onZoom
  
  protected void refreshSplats() { 
    final IGeoPoint centre = mapView_.getMapCenter();
    final int zoom = mapView_.getZoomLevel();
    final BoundingBoxE6 bounds = mapView_.getBoundingBox();
    
    if(!fetchSplatsInBackground(centre, zoom, bounds))
      return;

    loading_ = true;
    redraw();
  } // refreshPhotos

  protected void redraw() {
    mapView_.postInvalidate();
  } // redraw
  
  protected boolean fetchSplatsInBackground(final IGeoPoint mapCentre,
                                            final int zoom,
                                            final BoundingBoxE6 boundingBox) {
    GetSplatTask.fetch(this, mapCentre, zoom, boundingBox);
    return true;
  } // fetchSplatsInBackground
  
  protected void setItems(final List<SplatItem> newSplats)
  {
    if(newSplats != null) {
      splats_.clear();
      splats_.addAll(newSplats);
      populate();
    }
    
    loading_ = false;
    redraw();
  } // setItems
  
  ///////////////////////////////////////////////////
  static private class GetSplatTask extends AsyncTask<Object,Void,JSONArray> 
  {
    static void fetch(final SplatOverlay overlay, 
                      final Object... params)
    {
      new GetSplatTask(overlay).execute(params);
    } // fetch
    
    //////////////////////////////////////////////////////
    private final SplatOverlay overlay_;
    
    private  GetSplatTask(final SplatOverlay overlay)
    {
      overlay_ = overlay;
    } // GetPhotosTask
    
    protected JSONArray doInBackground(Object... params) 
    {
      final IGeoPoint mapCentre = (IGeoPoint)params[0];
      int zoom = (Integer)params[1];
      final BoundingBoxE6 boundingBox = (BoundingBoxE6)params[2];

      try {
        return Website.fetchSplatData();
      } 
      catch (final Exception ex) {
        // never mind, eh?
      }
      return null;
    } // doInBackground
    
    @Override
    protected void onPostExecute(final JSONArray splatData) 
    {
      final List<SplatItem> splats = makeSplatList(splatData);
      overlay_.setItems(splats);
    } // onPostExecute
    
    private List<SplatItem> makeSplatList(final JSONArray splatData) 
    {
      if(splatData == null)
        return null;
      
      final List<SplatItem> splats = new ArrayList<SplatItem>();
      for(int s = 0; s != splatData.length(); ++s) {
        try {
          final JSONObject splat = splatData.getJSONObject(s);
          
          final GeoPoint position = new GeoPoint(splat.getDouble("lat"),
                                                 splat.getDouble("lon"));
          
          final SplatItem newSplat = new SplatItem(splat.getString("name"),
                                                   splat.getString("img"),
                                                   position);
          
          splats.add(newSplat);
        } catch(final JSONException e) {
          // poop
        }
      }
      return splats;
    } // makeSplatList
  } // GetPhotosTask
} // class SplatOverlay
