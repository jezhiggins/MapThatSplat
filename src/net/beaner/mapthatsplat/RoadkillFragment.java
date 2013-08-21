package net.beaner.mapthatsplat;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class RoadkillFragment extends Fragment implements OnClickListener {
	private MapView map_;
	private MyLocationOverlay location_;
	private SplatOverlay splats_;
	
    @Override
    public View onCreateView(LayoutInflater inflater, 
    						 ViewGroup container,
    						 Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_roadkill, container, false);

        map_ = (MapView)rootView.findViewById(R.id.mapview);
        map_.setTileSource(TileSourceFactory.MAPNIK);
        map_.setBuiltInZoomControls(true);
        map_.setMultiTouchControls(true);     
        
        location_ = new MyLocationOverlay(getActivity(), map_);
        location_.enableMyLocation();
        map_.getOverlays().add(location_);
        
        splats_ = new SplatOverlay(getActivity(), map_);
        map_.getOverlays().add(splats_);

        final Button findMe = (Button)rootView.findViewById(R.id.findme_btn);
        findMe.setOnClickListener(this);
        
        return rootView;
    }

	@Override
	public void onPause() {
		super.onPause();
		
		final SharedPreferences.Editor edit = prefs().edit();
		
	    final IGeoPoint centre = map_.getMapCenter();
	    int lon = centre.getLongitudeE6();
	    int lat = centre.getLatitudeE6();
	    edit.putInt("lon", lon);
	    edit.putInt("lat", lat);
	    edit.putInt("zoom", map_.getZoomLevel());
	    
	    edit.putBoolean("follow", location_.isFollowLocationEnabled());
	    
	    edit.commit();
	    
	    location_.disableMyLocation();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		final SharedPreferences prefs = prefs();
		
		int lon = prefs.getInt("lon", 4042968);
		int lat = prefs.getInt("lat", 45828799); 
		int zoom = prefs.getInt("zoom", 3);
		
		final GeoPoint centre = new GeoPoint(lat, lon);
		map_.getController().setCenter(centre);
		map_.getController().setZoom(zoom);

		location_.enableMyLocation();
		final boolean follow = prefs.getBoolean("follow", true);
		if(follow)
			location_.enableFollowLocation();
		else
			location_.disableFollowLocation();
	} 	
	
    private SharedPreferences prefs() {
    	return getActivity().getSharedPreferences("roadkillmap", Context.MODE_PRIVATE);
    }

	@Override
	public void onClick(View v) {
		location_.enableFollowLocation();
	}

}
