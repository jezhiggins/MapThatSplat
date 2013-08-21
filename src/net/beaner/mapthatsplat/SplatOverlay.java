package net.beaner.mapthatsplat;

import java.util.List;
import java.util.ArrayList;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class SplatOverlay extends ItemizedOverlay<SplatOverlay.SplatItem>
                          implements MapListener
{
  public static class SplatItem extends OverlayItem {

    public SplatItem(final String title, 
                     final String description, 
                     final GeoPoint position,
                     final Drawable marker) {
      super(title, description, position);
      setMarker(marker);
    }
  } // class SplatItem
  
  static private List<SplatItem> dummySplats(final Context context) {
    final Drawable marker = context.getResources().getDrawable(R.drawable.ic_launcher);
    final List<SplatItem> splats = new ArrayList<SplatItem>();
    splats.add(new SplatItem("I am a dummy",
                             "Woo!",
                             new GeoPoint(52.445705116864026, -1.8787561572292157),
                             marker));
    splats.add(new SplatItem("Pantaloons",
        "Woo!",
        new GeoPoint(52.0088426, -4.9146281),
        marker));
    return splats;
  }

  public SplatOverlay(final Context context, final MapView mapView) {
    super(context, mapView, dummySplats(context));
  }
  
  @Override
  public boolean onScroll(final ScrollEvent evt) {
    return false;
  }

  @Override
  public boolean onZoom(final ZoomEvent evt) {
    return false;
  }
} // class SplatOverlay
