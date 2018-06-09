package com.goldingmedia.mvp.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.ethernet.IP;
import com.goldingmedia.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SystemInfoFragment extends Fragment {
    private TextView mSysVer;
    private TextView mAppVer;
    private TextView mTotalM;
    private TextView mFreeM;
    private TextView mMemory;
    private TextView mMasterId;
    private TextView mDeviceId;
    private TextView mMac;
    private TextView mIp;

    public SystemInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_system_info, container, false);
        mSysVer = (TextView) view.findViewById(R.id.sysVer);
        mAppVer = (TextView) view.findViewById(R.id.appVer);
        mTotalM = (TextView) view.findViewById(R.id.total_m);
        mFreeM = (TextView) view.findViewById(R.id.free_m);
        mMemory = (TextView) view.findViewById(R.id.memory);
        mMasterId = (TextView) view.findViewById(R.id.master_id);
        mDeviceId = (TextView) view.findViewById(R.id.device_id);
        mMac = (TextView) view.findViewById(R.id.mac);
        mIp = (TextView) view.findViewById(R.id.ip);

        mSysVer.setText(Utils.getSysOsVersion());
        mAppVer.setText(Utils.getAppVersion(getActivity()));
        mTotalM.setText(Utils.getRomTotalSize(getActivity()));
        mFreeM.setText(Utils.getRomAvailableSize(getActivity()));
        mMemory.setText(Utils.getRAMInfo(getActivity()));
        mMasterId.setText(Contant.MasterId);
        mDeviceId.setText("0x"+Utils.getSerialID());
        mMac.setText(IP.getLocalMacAddresss());
        mIp.setText(IP.getLocalHostIp());
        return view;
    }

}
