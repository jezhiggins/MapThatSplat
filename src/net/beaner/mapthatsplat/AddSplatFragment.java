package net.beaner.mapthatsplat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class AddSplatFragment extends Fragment implements OnClickListener {
	private View first_;
	private View second_;

    @Override
    public View onCreateView(LayoutInflater inflater, 
    						 ViewGroup container,
    						 Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_splat_layout, container, false);
        
        first_ = rootView.findViewById(R.id.first);
        second_ = rootView.findViewById(R.id.second);
        
        first_.setVisibility(View.VISIBLE);
        second_.setVisibility(View.INVISIBLE);
        
        
        final Button next = (Button)rootView.findViewById(R.id.next);
        next.setOnClickListener(this);

        return rootView;
    }

    // when someone clicks next, this method is called
	@Override
	public void onClick(View arg0) {
		if(first_.getVisibility() == View.VISIBLE) {
		  first_.setVisibility(View.INVISIBLE);
		  second_.setVisibility(View.VISIBLE);
		}
		
	} // onClick
		
}
