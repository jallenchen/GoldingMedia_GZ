package com.goldingmedia.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.golding.goldinglauncher.GoldingLauncherActivity;
import com.goldingmedia.R;
import com.goldingmedia.activity.TextdialogActivity;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.temporary.SystemInfo;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class SeatArrangementFragment extends Fragment {

	public static final int ID = 11000;
	private static final String  HKBUS = "5&12&01#05#09#13#17#21##25#29#33#37#41#02#06#10#14#18#22##26#30#34#38#42############45#B#04#08#12#16#20#24#28#32#36#40#44#A#03#07#11#15#19#23#27#31#35#39#43";
	private String ip;
//	private String LocalSeat;
	public int row = 0;
	public int col = 0;
	
	public static ArrayList<String> seatlist = new ArrayList<String>();

	public TableLayout tablayout = null;
	private Button plan,addbtn,canclebtn,addfinishbtn,changeTextButton;
	public EditText editROWText;
	public EditText editCOlText;
	public TableRow tableRow;
	
	private Dialog dialog;
	private Display display;
	
	private View view;
	public String mSeatString;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		ip = bundle.getString("ip");
		
//		LocalSeat = Utils.getLocalSeat();
		
		view = inflater.inflate(R.layout.fragment_seat_arrangements, container,false);
		
		tablayout = (TableLayout) view.findViewById(R.id.table);
		editROWText = (EditText) view.findViewById(R.id.editrow);
		editCOlText = (EditText) view.findViewById(R.id.editcol);
		plan = (Button) view.findViewById(R.id.plan);
		addbtn = (Button) view.findViewById(R.id.addbtn);
		canclebtn = (Button) view.findViewById(R.id.canclebtn);
		addfinishbtn = (Button) view.findViewById(R.id.addfinishbtn);
		
		plan.setOnClickListener(onClickListener);
		addbtn.setOnClickListener(onClickListener);
		canclebtn.setOnClickListener(onClickListener);
		addfinishbtn.setOnClickListener(onClickListener);
		showreset(SystemInfo.getSeatArrangement());

		return view;
	}

	public void showreset(String seatAarrengement) {
		if(TextUtils.isEmpty(seatAarrengement)) {
			return;
		}
		
		row = Integer.parseInt(seatAarrengement.split("&")[0]);
		col = Integer.parseInt(seatAarrengement.split("&")[1]);
		Log.e("col", seatAarrengement.split("&")[1]);
		String mtr =seatAarrengement.split("&")[2];
		
		editROWText.setText(row+"");
		Log.e("row",seatAarrengement.split("&")[0]);
		editCOlText.setText(col+"");
		Log.e("col","mtr");
		String[] mSeatStr = mtr.split("#", -1);
		tablayout.removeAllViews();
		Button[] mButtons = new Button[col * row];
		Button mButton;
		String posString;
		for (int i = 0; i < row; i++) {
			tableRow = new TableRow(getActivity());
			for (int j = 0; j < col; j++) {
				mButton = mButtons[i * col + j];
				mButton = new Button(getActivity());
				mButton.setId(i * col + j+ID);
				mButton.setTextColor(Color.WHITE);
				mButton.setTextSize(20);
				posString = mSeatStr[i * col + j];
				seatlist.add(posString);
				mButton.setText(posString.trim());
				if (posString.length() < 2) {
					mButton.setPadding(22, 12, 22, 12);
				} else if (posString.length() < 3) {
					mButton.setPadding(17, 12, 16, 12);
				} else {
					mButton.setPadding(11, 12, 11, 12);
				}
				mButton.setOnClickListener(seatClickListener);
				mButton.setBackgroundResource(R.mipmap.blueseat1);
				if (TextUtils.isEmpty(posString.trim())) {
					mButton.setBackgroundColor(0x00);
				}
				tableRow.addView(mButton);
			}
			tablayout.addView(tableRow);
		}
	}
	public void addRowTable() {
		tablayout.removeAllViews();
		Button[] mButtons = new Button[col * row];
		Button mButton;
		String posString;
		for (int i = 0; i < row; i++) {
			tableRow = new TableRow(getActivity());
			for (int j = 0; j < col; j++) {
				mButton = mButtons[i * col + j];
				mButton = new Button(getActivity());
				mButton.setId(i * col + j+ID);
				mButton.setTextColor(Color.WHITE);
				mButton.setTextSize(20);
				posString = String.valueOf(i * col + j);
				mButton.setText(posString);
				if (posString.length() < 2) {
					mButton.setPadding(22, 12, 22, 12);
				} else if (posString.length() < 3) {
					mButton.setPadding(17, 12, 16, 12);
				} else {
					mButton.setPadding(11, 12, 11, 12);
				}
				mButton.setOnClickListener(seatClickListener);
				mButton.setBackgroundResource(R.mipmap.blueseat1);
				tableRow.addView(mButton);
			}
			tablayout.addView(tableRow);
		}
	}

	//每个座位设置字符
	OnClickListener seatClickListener = new OnClickListener() {
		public void onClick(View v) {
			// 键盘窗口
			changeTextButton = (Button) v;
			Intent i = new Intent(getActivity(), TextdialogActivity.class);
			i.putExtra("buttonText", changeTextButton.getText());
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(i, 0);
		}
	};

	//每个座位号设置字符返回处理
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
			Bundle resultbundle = data.getExtras();
			if (resultbundle != null) {
				int mType = resultbundle.getInt("type");
				switch (mType) {
				case 0:
					String seatnum = resultbundle.getString("seatnum");
					changeTextButton.setText(seatnum);
					if (seatnum.length() < 2) {
						changeTextButton.setPadding(22, 12, 22, 12);
					} else if (seatnum.length() < 3) {
						changeTextButton.setPadding(17, 12, 16, 12);
					} else {
						changeTextButton.setPadding(11, 12, 11, 12);
					}
					if (TextUtils.isEmpty(seatnum)) {
						changeTextButton.setBackgroundColor(0x00);
					}else {
						changeTextButton.setBackgroundResource(R.mipmap.blueseat1);
					} 
					break;
				}
			}
		}
	}

	OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.plan:
				planSet();
				break;

			case R.id.addbtn:
				try {
					row = Integer.parseInt(editROWText.getText().toString());
					col = Integer.parseInt(editCOlText.getText().toString());
					if (row <= 30 && col <= 30) {
						addRowTable();
					} else {
						EngModeFragment.Toast(getResources().getString(R.string.seat_row_col), getActivity());
	    				row = 0;
	    				col = 0;
					}
				} catch (Exception e) {
					// TODO: handle exception
					EngModeFragment.Toast(getResources().getString(R.string.seat_row_col), getActivity());
					row = 0;
					col = 0;
				}
				break;

			case R.id.canclebtn:
				showreset(SystemInfo.getSeatArrangement());
				break;

			case R.id.addfinishbtn:
				if (row == 0 || col == 0) {
					EngModeFragment.Toast(getResources().getString(R.string.seat_add), getActivity());
				} else {
					if (!TextUtils.isEmpty(ip) && GoldingLauncherActivity.getValue() == 0) {
						arrangementSet();
					} else{
						EngModeFragment.Toast(getResources().getString(R.string.netdisc), getActivity());
					}
				}
				break;
			}
		}
	};
	
	private void planSet() {
		WindowManager windowManager = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	    View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_dialog, null);
		LinearLayout plan_lay = (LinearLayout) view.findViewById(R.id.dialog_lay);
		dialog = new Dialog(getActivity(), R.style.AlertDialogStyle);
		dialog.setCancelable(false);
		plan_lay.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));
		ListView listview = (ListView) view.findViewById(R.id.dialog_listview);
		listview.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.seat_plan)));
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					showreset(HKBUS);
					break;

				default:
					break;
				}
				dialog.dismiss();
			}
		});
		Button btn_dialog_cancel = (Button) view.findViewById(R.id.btn_cancle);
		btn_dialog_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}
	
	private void arrangementSet() {
		String[] mButtonString = new String[row * col];
		for (int i = 0; i < row * col; i++) {
			Button button = (Button)view.findViewById(i+ID);
			String mStr = button.getText().toString();
			if (i < row * col - 1) {
				mStr += "#";
			}
			mButtonString[i] = mStr;
		}
		final StringBuilder sb = new StringBuilder();
		for (String str : mButtonString) {
			sb.append(str);
		}
		
		mSeatString = String.valueOf(row)+"&"+String.valueOf(col)+"&"+sb.toString();
		int tipTextId;
		if (mSeatString.equals(SystemInfo.getSeatArrangement())) {
			tipTextId = R.string.seat_nochange;
		} else {
			tipTextId = R.string.seat_change;
		}
		
		WindowManager windowManager = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	    View view = LayoutInflater.from(getActivity()).inflate(R.layout.tips_alertdialog, null);
		LinearLayout lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
		dialog = new Dialog(getActivity(), R.style.AlertDialogStyle);
		dialog.setCancelable(false);
		lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));
		TextView tipTextView = (TextView) view.findViewById(R.id.tipstext);
		tipTextView.setText(tipTextId);
		Button btn_dialog_resolve = (Button) view.findViewById(R.id.btn_ok);
		btn_dialog_resolve.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SystemInfo.SaveSeatArrangement(String.valueOf(row)+"&"+String.valueOf(col)+"&"+sb.toString());// 本地存储下数据
				Intent sendSeatIntent = new Intent(Contant.Actions.CONVERSATION_SEND);
				sendSeatIntent.putExtra("conversation", "arrangement"+"@"+mSeatString);
				getActivity().sendBroadcast(sendSeatIntent);
				dialog.dismiss();
			}
		});
		Button btn_dialog_cancel = (Button) view.findViewById(R.id.btn_cancle);
		btn_dialog_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
		
	}

	public static Boolean CheckSeat(String seat){
		String seatnum;
		Boolean thesame = false;
		if(!seat.equals("")){
			for(int i = 0;i < seatlist.size();i++){
				seatnum = seatlist.get(i);
				Log.e("seatnum",seatnum);
				if(seatnum.equals(seat)){
					Log.e("check","true");
					thesame = true;
					break;
				}else{
					thesame = false;
				}
			}
		}
		return thesame;
	}
}



