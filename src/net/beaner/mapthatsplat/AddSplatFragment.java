package net.beaner.mapthatsplat;

import java.io.IOException;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddSplatFragment extends Fragment 
		implements OnClickListener, OnItemSelectedListener, UploadListener {
	private View chooseLocationView_;
	private View choosePhotoView_;
	private View chooseAnimalView_;
	private View uploadView_;
	private View currentPage_;
	private MapView map_;
	private MyLocationOverlay location_;
	private ThereOverlay there_;
	private Button backBtn_;
	private Button nextBtn_;
	private Button findMe_;
	private EditText customAnimal_;
	private Spinner spinner_;
	private int lastSpinnerPosition_;
	private String photoFile_ = null;
  private Bitmap photo_ = null;
  private ImageView photoView_;
	
	private final static int TAKE_PHOTO_ID = 1;
	private final static int CHOOSE_PHOTO_ID = 2;
     
  @Override
  public View onCreateView(LayoutInflater inflater, 
    						 ViewGroup container,
    						 Bundle savedInstanceState) {
  	// this method is called once, when the Fragment is first created
    View rootView = inflater.inflate(R.layout.add_splat_layout, container, false);
        
    chooseLocationView_ = rootView.findViewById(R.id.location);
    choosePhotoView_ = rootView.findViewById(R.id.choosephoto);
    chooseAnimalView_ = rootView.findViewById(R.id.chooseanimal);
    uploadView_ = rootView.findViewById(R.id.upload);
      
    backBtn_ = setupButton(rootView, R.id.back);
    nextBtn_ = setupButton(rootView, R.id.next);
      
  	spinner_ = (Spinner)rootView.findViewById(R.id.animal_choice);
  	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.animal_array, android.R.layout.simple_spinner_dropdown_item);
  	adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
  	spinner_.setAdapter(adapter);
  	spinner_.setOnItemSelectedListener(this);
  	lastSpinnerPosition_ = 0;

  	findMe_ = setupButton(rootView, R.id.findme_btn);
  	
  	setupButton(rootView, R.id.takephoto);
  	setupButton(rootView, R.id.usephoto);
  	
  	customAnimal_ = (EditText)rootView.findViewById(R.id.customanimal);
  	photoView_ = (ImageView)rootView.findViewById(R.id.splatograph);
  	  
  	showPage(choosePhotoView_);
        
  	setupMap(rootView);
  
    return rootView;
  }    
    
  private Button setupButton(View rootView, int buttonId) {
  	// this wires up out buttons to our clicklistener
    final Button btn = (Button)rootView.findViewById(buttonId);
    btn.setOnClickListener(this);
    return btn;
  }
    
  private void setupMap(View rootView) {
  	// set up the map controls, and add the "where am I" overlay
    map_ = (MapView)rootView.findViewById(R.id.mapview);
    map_.setTileSource(TileSourceFactory.MAPNIK);
    map_.setBuiltInZoomControls(true);
    map_.setMultiTouchControls(true);  
        
    location_ = new MyLocationOverlay(getActivity(), map_);
    location_.enableMyLocation();
    map_.getOverlays().add(location_);

    there_ = new ThereOverlay(getActivity(), map_);
    map_.getOverlays().add(there_);
  } // setupMap
    
  @Override
	public void onPause() {
    // called when the fragment is hidden for any reason
  	// eg we have moved to another fragment or another app
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
		// called when the fragment is shown 
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

	@Override
	public void onClick(View buttonThatWasClicked) {
    // when someone clicks one of buttons we've set up 
		// the listener on this method is called
		switch(buttonThatWasClicked.getId()) {
		case R.id.back:
			goBack();
			break;
		case R.id.next:
			goNext();
			break;
		case R.id.findme_btn:
			findme();
			break;
		case R.id.takephoto:
			takePhoto();
			break;
		case R.id.usephoto:
			choosePhoto();
			break;
		} // switch
	} // onClick
	
	private void goBack() {
		if(currentPage_ == chooseLocationView_) {
			showPage(choosePhotoView_);
		}
		else if (currentPage_ == chooseAnimalView_) {
			showPage(chooseLocationView_);
		}
	} // goBack
	
	private void goNext() {
		if(currentPage_ == choosePhotoView_) {
		    showPage(chooseLocationView_);
		}
		else if(currentPage_ == chooseLocationView_) {
			showPage(chooseAnimalView_);
		}
		else{
			showPage(uploadView_);
			uploadToWebsite();
		}
	} // goNext
	
	private void findme() {
		location_.enableFollowLocation();
	} // findme
	
	private void takePhoto() {
		// ask Android to take a photo and then tell us about it
		try {
			Intent photoIntent = Photos.PhotoIntent(getActivity());
			startActivityForResult(photoIntent, TAKE_PHOTO_ID);
		} catch (IOException e) {
			// Bums!
			e.printStackTrace();
		}
	} // takePhoto
		
	private void choosePhoto() {
    Intent choose = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    startActivityForResult(choose, CHOOSE_PHOTO_ID);
	} // choosePhoto

	@Override
	public void onActivityResult(int requestCode,
	                             int resultCode,
	                             Intent data) {
		if (resultCode != Activity.RESULT_OK)
      return;

		try {
      photoFile_ = Photos.getImageFilePath(getActivity(), data);
      if(photo_ != null)
        photo_.recycle();
      photo_ = Bitmaps.loadFile(photoFile_);
		  splatPhoto();
    } catch(Exception e) {
			Toast.makeText(getActivity(), "There was a problem grabbing the photo : " + e.getMessage(), Toast.LENGTH_LONG).show();
		  if(requestCode == TAKE_PHOTO_ID)
		    choosePhoto();
		}
	} // onActivityResult
	
	private void splatPhoto() {
		photoView_.setImageBitmap(photo_);
	} // splatPhoto
	
	private void showPage(View page) {
		// this is our method that we use to show the page 
		// of our layout that we want to see
	  chooseLocationView_.setVisibility(View.INVISIBLE);
		choosePhotoView_.setVisibility(View.INVISIBLE);
		chooseAnimalView_.setVisibility(View.INVISIBLE);
		uploadView_.setVisibility(View.INVISIBLE);
		
		currentPage_ = page;

		page.setVisibility(View.VISIBLE);
		
		if((page == choosePhotoView_) || (page == uploadView_)) {
			backBtn_.setVisibility(View.INVISIBLE);
		} else {
			backBtn_.setVisibility(View.VISIBLE);
		}
		
		if(page == chooseLocationView_) {
			findMe_.setVisibility(View.VISIBLE);
		} else {
			findMe_.setVisibility(View.INVISIBLE);
		}

		if(page == uploadView_) {
			nextBtn_.setVisibility(View.INVISIBLE);
		} else {
			nextBtn_.setVisibility(View.VISIBLE);	
		}
		
		if(page == chooseAnimalView_) {
			nextBtn_.setText("Upload!");
			nextBtn_.setEnabled(lastSpinnerPosition_ != 0);
		} else {
			nextBtn_.setText("Next");
			nextBtn_.setEnabled(true);
			customAnimal_.setEnabled(false);
		}
	} // showPage
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// called by the spinner when the chosen animal changes
  	lastSpinnerPosition_ = position;
  	if(currentPage_ == chooseAnimalView_) {
  		nextBtn_.setEnabled(lastSpinnerPosition_ != 0);    		
  		customAnimal_.setEnabled(lastSpinnerPosition_ == spinner_.getCount()-1);
  	}
	} // onItemSelected

	private String getAnimal() {
		if(lastSpinnerPosition_ == spinner_.getCount()-1)
			return customAnimal_.getText().toString();
		return (String)spinner_.getAdapter().getItem(lastSpinnerPosition_);
	} // getAnimal

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// we're not interested in this
	} // onNothingSelected
	
	private void uploadToWebsite() {
		final IGeoPoint loc = there_.there();
		UploadPhotoTask uploader = new UploadPhotoTask(this,
                      													   loc,
                      													   getAnimal(),
                      													   photoFile_);
		uploader.execute();
	} // uploadToWebsite	
	
	public void uploadComplete() {
		Toast.makeText(getActivity(), "Thanks dude", Toast.LENGTH_LONG).show();
	} // uploadComplete
	
	public void uploadFailed() {
		Toast.makeText(getActivity(), "There was a problem.  Bum!", Toast.LENGTH_LONG).show();
	} // uploadFailed
} // AddSplatFragment
		

