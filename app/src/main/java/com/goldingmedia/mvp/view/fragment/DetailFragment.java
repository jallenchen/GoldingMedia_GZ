package com.goldingmedia.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldingmedia.R;

@SuppressLint("NewApi")
public class DetailFragment extends Fragment {
	private TextView textView;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_detail, container,false);
		textView = (TextView) view.findViewById(R.id.textView);

		Bundle bundle = getArguments();
		String str = bundle.getString("id");

		textView.setText(str);

		return view;
	}
}
