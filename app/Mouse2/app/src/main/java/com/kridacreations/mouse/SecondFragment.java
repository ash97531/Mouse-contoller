
package com.kridacreations.mouse;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AdView mAdView;

    View parentHolder;
    TextView youTubeDemoLink;
    ImageView keyboardImage, mouseImage;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parentHolder = inflater.inflate(R.layout.fragment_second, container,
                false);

        Configuration configuration = this.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;

        keyboardImage = (ImageView) parentHolder.findViewById(R.id.keyboard_ss);
        mouseImage = (ImageView) parentHolder.findViewById(R.id.mouse_ss);

        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(screenWidthDp - 16, (int)(screenWidthDp * 0.42));
        keyboardImage.setLayoutParams(parms);

//        parms = new LinearLayout.LayoutParams(screenWidthDp - 16, (int)(screenWidthDp * 3.5));
        parms = new LinearLayout.LayoutParams(0,0);
        mouseImage.setLayoutParams(parms);


        youTubeDemoLink = (TextView) parentHolder.findViewById(R.id.youtube_demo_link);
        youTubeDemoLink.setMovementMethod(LinkMovementMethod.getInstance());
        return inflater.inflate(R.layout.fragment_second, container, false);
    }


}