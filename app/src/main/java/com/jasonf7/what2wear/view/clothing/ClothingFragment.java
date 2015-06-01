package com.jasonf7.what2wear.view.clothing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.jasonf7.what2wear.R;
import com.jasonf7.what2wear.database.Clothing;
import com.jasonf7.what2wear.database.ClothingList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClothingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClothingFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private ClothingList clothingList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 ArrayList of Clothing.
     * @return A new instance of fragment ClothingFragment.
     */
    public static ClothingFragment newInstance(ClothingList param1) {
        ClothingFragment fragment = new ClothingFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
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
            clothingList = getArguments().getParcelable(ARG_PARAM1);
        }

//        getActivity().getActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View clothingView = inflater.inflate(R.layout.fragment_clothing, container, false);

        ExpandableListView clothingListView = (ExpandableListView) clothingView.findViewById(R.id.clothingListView);
        ClothingListAdapter clothingListAdapter = new ClothingListAdapter(getActivity(), clothingList, Clothing.SORT_BY_TYPE);
        clothingListView.setAdapter(clothingListAdapter);
        clothingListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                View subMenu = v.findViewById(R.id.subMenuLayout);
                if (subMenu.getVisibility() == View.GONE) {
                    subMenu.setVisibility(View.VISIBLE);
                } else if (subMenu.getVisibility() == View.VISIBLE) {
                    subMenu.setVisibility(View.GONE);
                }
                return true;
            }
        });

        return clothingView;
    }

}
