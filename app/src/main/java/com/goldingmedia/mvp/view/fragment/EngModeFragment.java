package com.goldingmedia.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.golding.goldinglauncher.GoldingLauncherActivity;
import com.goldingmedia.R;
import com.goldingmedia.ethernet.IP;
import com.goldingmedia.temporary.SystemInfo;

@SuppressLint("NewApi")
public class EngModeFragment extends BaseFragment implements View.OnClickListener {
	private static final String IME = "com.iflytek.inputmethod.pad";

	private ImageButton backBtn;
	private ListView listview;
	private EngModeAdapter adapter;
	
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private Context mContext;

	public boolean change = false;
	String ip;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_engmode, container, false);
		listview = (ListView) view.findViewById(R.id.listview);
		backBtn = (ImageButton) view.findViewById(R.id.backBtn);

		initListener();
		mContext = getContext();
		manager = getActivity().getFragmentManager();
		initData();
		return view;
	}

	private void initData(){
		ip = IP.getLocalHostIp();
		Log.i("","-----ip = "+ip);
		if (TextUtils.isEmpty(ip) || GoldingLauncherActivity.getValue() == 1) {
			EngModeFragment.Toast(getResources().getString(R.string.netdisc), mContext);
		}

		String[] data = getResources().getStringArray(R.array.setting);
		adapter = new EngModeAdapter(mContext, data);
		adapter.setSelectPosition(0);
		listview.setAdapter(adapter);

		transaction = manager.beginTransaction();
		String msg = "0";
		if (getArguments() != null) {
			msg = getArguments().getString("otherMsg");
		}
		if (msg == null) msg = "0";
		String[] msgA = msg.split("&");
		if("openVersion".equals(msgA[0])) {
			OpenVersionFragment openVersionFragment = new OpenVersionFragment();
			Bundle bundle = new Bundle();
			if(msgA.length >= 2){
				bundle.putString("versionCode", msgA[1]);
			}
			if(msgA.length >= 3){
				bundle.putString("versionName", msgA[2]);
			}
			openVersionFragment.setArguments(bundle);
			transaction.replace(R.id.right, openVersionFragment, "openVersion");
			transaction.commit();
		} else {
			Bundle bundle = new Bundle();
			SeatNumberFragment seatNumberFragment = new SeatNumberFragment();
			bundle.putString("ip", ip);
			seatNumberFragment.setArguments(bundle);
			transaction.replace(R.id.right, seatNumberFragment, "SeatNumber");
			transaction.commit();
		}
		register();
	}


	private void initListener() {
		backBtn.setOnClickListener(this);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				adapter.setSelectPosition(position);
				adapter.notifyDataSetChanged();

				transaction = manager.beginTransaction();
				Bundle bundle = new Bundle();
				switch (position) {
					case 0:
						SeatNumberFragment seatNumberFragment = new SeatNumberFragment();
						bundle.putString("ip", ip);
						seatNumberFragment.setArguments(bundle);
						transaction.replace(R.id.right, seatNumberFragment, "SeatNumber");
						change = true;
						break;

					case 1:
						AppManagementFragment appManagementFragment = new AppManagementFragment();
						bundle.putString("ip", ip);
						appManagementFragment.setArguments(bundle);
						transaction.replace(R.id.right, appManagementFragment, "appManagement");
						break;

					case 2:
						INICBurningFragment mINICBurningFragment = new INICBurningFragment(mContext);
						bundle.putString("ip", ip);
						mINICBurningFragment.setArguments(bundle);
						transaction.replace(R.id.right, mINICBurningFragment, "mINICBurning");
						break;
                    case 3:
						UpradeFragment mUpradeFragment = new UpradeFragment(mContext);
						bundle.putString("ip", ip);
						mUpradeFragment.setArguments(bundle);
						transaction.replace(R.id.right, mUpradeFragment, "mmUpradeFragment");
                        break;

					default:
						DetailFragment detailFragment = new DetailFragment();
						bundle.putString("id", (String) adapter.getItem(position));
						detailFragment.setArguments(bundle);
						transaction.replace(R.id.right, detailFragment, "dfragment_etail");
						break;
				}

				transaction.commit();

			}
		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.backBtn:
				getActivity().finish();
				break;
		}
	}

	public static void Toast(String mString, Context context){
		Toast toast = Toast.makeText(context, R.string.toast_line, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toastview, null);
		TextView tV = (TextView) view.findViewById(R.id.textView);
		tV.setText(mString);

		LinearLayout toastView = (LinearLayout) toast.getView();
		toastView.addView(view);
		toast.show();
	}

	private class EngModeAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private String[] list;
		private int selectPosition = 0;
		public EngModeAdapter(Context context, String[] list) {
			mInflater = LayoutInflater.from(context);
			this.list = list;
		}

		public void setSelectPosition(int selectPosition) {
			this.selectPosition = selectPosition;
		}

		public Object getSelectItem() {
			return getItem(selectPosition);
		}

		public int getCount() {
			return list.length;
		}

		public Object getItem(int position) {
			return list[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.engmode_list_item, parent,false);
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.nametext);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == selectPosition) {
				convertView.setBackgroundResource(R.mipmap.seat_listitem_select);
			} else {
				convertView.setBackgroundResource(R.color.transparent);
			}

			holder.text.setText(list[position]);
			return convertView;
		}

		class ViewHolder {
			TextView text;
			ImageView imageView;
		}
	}
	
	void register() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.golding.seat.finishactivity");
		filter.addAction("com.golding.seat.seatsetting");
		filter.addAction("com.golding.seat.updatesetting");
		filter.addAction("com.golding.seat.updatenumberset");
		getActivity().registerReceiver(receiver, filter);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.golding.seat.finishactivity")) {
				getActivity().finish();
			} else if (action.equals("com.golding.seat.seatsetting")) {
				if(intent.getBooleanExtra("issucceed", false)) {
					String elementValue = intent.getStringExtra("elementValue");
					SystemInfo.SetLocalSeat(elementValue);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String string = SystemInfo.getLocalSeat();
					if(elementValue.split("#")[1].equals(string)) {
                 		EngModeFragment.Toast(getResources().getString(R.string.setsuccess), mContext);
					} else {
						EngModeFragment.Toast(getResources().getString(R.string.setfailed), mContext);
					}
				} else {
					EngModeFragment.Toast(getResources().getString(R.string.reset), mContext);
				}
				
				transaction = manager.beginTransaction();
				Bundle bundle = new Bundle();
				SeatNumberFragment seatNumberFragment = new SeatNumberFragment();
				bundle.putString("ip", ip);
				seatNumberFragment.setArguments(bundle);
				transaction.replace(R.id.right, seatNumberFragment, "SeatNumber");
				transaction.commit();
				change = true;
			}else if(action.equals("com.golding.seat.updatesetting")){
				Log.e("here","updatesetting");
				transaction = manager.beginTransaction();
				Bundle bundle = new Bundle();
				SeatArrangementFragment seatArrangementFragment = new SeatArrangementFragment();
				bundle.putString("ip", ip);
				seatArrangementFragment.setArguments(bundle);
				transaction.replace(R.id.right, seatArrangementFragment, "SeatArrangement");
				transaction.commit();
			}else if(action.equals("com.golding.seat.updatenumberseat")){
				Bundle bundle = new Bundle();
				SeatNumberFragment seatNumber = new SeatNumberFragment();
				bundle.putString("ip", ip);
				seatNumber.setArguments(bundle);
				transaction.replace(R.id.right, seatNumber, "SeatArrangement");
				transaction.commit();
				change = true;
			}
		}
	};

	void unRegister() {
		getActivity().unregisterReceiver(receiver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unRegister();
	}
}

