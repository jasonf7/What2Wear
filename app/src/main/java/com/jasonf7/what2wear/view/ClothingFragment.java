package com.jasonf7.what2wear.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jasonf7.what2wear.R;
import com.jasonf7.what2wear.database.Clothing;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClothingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClothingFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private ArrayList<Clothing> clothingList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 ArrayList of Clothing.
     * @return A new instance of fragment ClothingFragment.
     */
    public static ClothingFragment newInstance(ArrayList<Clothing> param1) {
        ClothingFragment fragment = new ClothingFragment();
        Bundle args = new Bundle();
        // TODO: Make ArrayList<Clothing> Parcelable
        args.putParcelableArray(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ClothingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clothingList = getArguments().getParcelableArrayList(ARG_PARAM1);
        }

//        getActivity().getActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View clothingView = inflater.inflate(R.layout.fragment_clothing, container, false);

        return clothingView;
    }

}
