package net.beaner.mapthatsplat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SplatDetailFragment extends Fragment {
    private String title_;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SplatDetailFragment(String title) {
      title_ = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splat_detail, container, false);
        ((TextView) rootView.findViewById(R.id.splat_detail)).setText(title_);
        return rootView;
    }
}
