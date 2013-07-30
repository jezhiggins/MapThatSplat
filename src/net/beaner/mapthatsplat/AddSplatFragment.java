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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AddSplatFragment extends Fragment implements OnClickListener {
	private View first_;
	private View second_;
	private View third_;
	private View upload_;
	private MapView map_;
	private MyLocationOverlay location_;
	private Button backBtn_;
	private Button nextBtn_;

    @Override
    public View onCreateView(LayoutInflater inflater, 
    						 ViewGroup container,
    						 Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_splat_layout, container, false);
        
        first_ = rootView.findViewById(R.id.first);
        second_ = rootView.findViewById(R.id.second);
        third_ = rootView.findViewById(R.id.third);
        upload_ = rootView.findViewById(R.id.upload);
        
        backBtn_ = setupButton(rootView, R.id.back);
        nextBtn_ = setupButton(rootView, R.id.next);
        
    	  Spinner spinner = (Spinner)rootView.findViewById(R.id.animal_choice);
    	  ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.animal_array, android.R.layout.simple_spinner_dropdown_item);
    	  adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    	  spinner.setAdapter(adapter);

        showPage(first_);
        
        setupMap(rootView);
  
        return rootView;
    }
    
    private Button setupButton(View rootView, int buttonId) {
        final Button btn = (Button)rootView.findViewById(buttonId);
        btn.setOnClickListener(this);
        return btn;
    }
    
    private void setupMap(View rootView) {
        map_ = (MapView)rootView.findViewById(R.id.mapview);
        map_.setTileSource(TileSourceFactory.MAPNIK);
        map_.setBuiltInZoomControls(true);
        map_.setMultiTouchControls(true);  
        
        location_ = new MyLocationOverlay(getActivity(), map_);
        location_.enableMyLocation();
        map_.getOverlays().add(location_);
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
	}
 
    private SharedPreferences prefs() {
    	return getActivity().getSharedPreferences("roadkillmap", Context.MODE_PRIVATE);
    }

    // when someone clicks next, this method is called
	@Override
	public void onClick(View buttonThatWasClicked) {
		switch(buttonThatWasClicked.getId()) {
		case R.id.back:
			goBack();
			break;
		case R.id.next:
			goNext();
			break;
		}
	}
	
	private void goBack() {
		
		if(second_.getVisibility() == View.VISIBLE) {
			showPage(first_);
		}
		else if (third_.getVisibility() == View.VISIBLE) {
			showPage(second_);
		}

	}
	
	private void goNext() {
		if(first_.getVisibility() == View.VISIBLE) {
		    showPage(second_);
		}
		else if(second_.getVisibility() == View.VISIBLE) {
			showPage(third_);
		    nextBtn_.setText("Upload!");
		}
		else{
			showPage(upload_);
		}
	}
	
	private void showPage(View page) {
		first_.setVisibility(View.INVISIBLE);
		second_.setVisibility(View.INVISIBLE);
		third_.setVisibility(View.INVISIBLE);
		upload_.setVisibility(View.INVISIBLE);

		page.setVisibility(View.VISIBLE);
		
		if((page == first_) || (page == upload_)) {
			backBtn_.setVisibility(View.INVISIBLE);
		}
		else{
			backBtn_.setVisibility(View.VISIBLE);
		}
		
		if(page == upload_) {
			nextBtn_.setVisibility(View.INVISIBLE);
		}
		else {
			nextBtn_.setVisibility(View.VISIBLE);	
		}
		
		if(page == third_) {
			nextBtn_.setText("Upload!");
		}
		else {
			nextBtn_.setText("Next");
		}
	}

}
		

