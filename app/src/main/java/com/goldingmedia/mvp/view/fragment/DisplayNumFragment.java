package com.goldingmedia.mvp.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldingmedia.R;
import com.goldingmedia.temporary.SystemInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayNumFragment extends Fragment {
    private TextView tv_carNum;
    private TextView tv_seatNum;


    public DisplayNumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_num, container, false);
        tv_carNum = (TextView) view.findViewById(R.id.carNum);
        tv_seatNum = (TextView) view.findViewById(R.id.seatNum);

        tv_seatNum.setText(SystemInfo.getLocalSeat());
        return view;
    }

}
