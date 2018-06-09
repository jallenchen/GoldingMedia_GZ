package com.goldingmedia.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldingmedia.R;

@SuppressLint("NewApi")
public class OpenVersionFragment extends Fragment {
	
	private View view;
	String ip;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.app_version, container,false);
		Bundle bundle = getArguments();
		String pkgName = bundle.getString("pkgName");
		String msgVersionCode = bundle.getString("versionCode");
		String msgVersionName = bundle.getString("versionName");
//		LinearLayout versionLay = (LinearLayout)view.findViewById(R.id.versionLay);
		TextView versionCodeTv = (TextView)view.findViewById(R.id.versionCodeTv);
		TextView versionNameTv = (TextView)view.findViewById(R.id.versionNameTv);

		String versionCode = "";
		String versionName = "";
		try {
			PackageManager manager = getActivity().getPackageManager();
			PackageInfo info = manager.getPackageInfo(pkgName, 0);
			versionCode = info.versionCode+"";
			versionName = info.versionName+"";
		} catch (Exception e) {
			e.printStackTrace();
			versionCode = getResources().getString(R.string.withoutThisApp);
		}
		versionCodeTv.setText(versionCode);
		if (!versionCode.equals(msgVersionCode)) {
			versionCodeTv.setBackgroundColor(getResources().getColor(R.color.redFFAAAA));
		}
		versionNameTv.setText(versionName);
		if (!versionName.equals(msgVersionName)) {
			versionNameTv.setBackgroundColor(getResources().getColor(R.color.redFFAAAA));
		}

		return view;
	}

}



