package com.goldingmedia.mvp.view.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.goldingmedia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpradeFragment extends Fragment {
    private Context mContext;
    private ListView mAppLv;
    private ListView mSysLv;


    public UpradeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public UpradeFragment(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uprade, container, false);
        mAppLv = (ListView) view.findViewById(R.id.applv);
        mSysLv = (ListView) view.findViewById(R.id.syslv);

        return view;
    }


    public class UpgradeViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

}
