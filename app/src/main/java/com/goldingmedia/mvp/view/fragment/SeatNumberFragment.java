package com.goldingmedia.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.golding.goldinglauncher.GoldingLauncherActivity;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.temporary.SystemInfo;

@SuppressLint("NewApi")
public class SeatNumberFragment extends Fragment {

	public TextView textView;
	private String ip;
	private ImageButton b_2, b_3, b_1;
	private ImageButton b_4, b_5, b_6, b_7, b_8, b_9, b_0, b_A, b_B, b_C, b_D, b_E, b_F,
			b_line, b_left, b_right, b_backlash, b_delete, b_enter, whole_seat_empty, whole_seat_set, whole_seat_off;

	private View view;

	private Dialog dialog;
	private Display display;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		ip = bundle.getString("ip");

		view = inflater.inflate(R.layout.seat_number, container,false);
		textView = (TextView) view.findViewById(R.id.seatnum);
		textView.setText(SystemInfo.getLocalSeat());

		b_1 = (ImageButton) view.findViewById(R.id.b_1);
		b_2 = (ImageButton) view.findViewById(R.id.b_2);
		b_3 = (ImageButton) view.findViewById(R.id.b_3);
		b_4 = (ImageButton) view.findViewById(R.id.b_4);
		b_5 = (ImageButton) view.findViewById(R.id.b_5);
		b_6 = (ImageButton) view.findViewById(R.id.b_6);
		b_7 = (ImageButton) view.findViewById(R.id.b_7);
		b_8 = (ImageButton) view.findViewById(R.id.b_8);
		b_9 = (ImageButton) view.findViewById(R.id.b_9);
		b_0 = (ImageButton) view.findViewById(R.id.b_0);
		b_A = (ImageButton) view.findViewById(R.id.b_A);
		b_B = (ImageButton) view.findViewById(R.id.b_B);
		b_C = (ImageButton) view.findViewById(R.id.b_C);
		b_D = (ImageButton) view.findViewById(R.id.b_D);
		b_E = (ImageButton) view.findViewById(R.id.b_E);
		b_F = (ImageButton) view.findViewById(R.id.b_F);
		b_line = (ImageButton) view.findViewById(R.id.b_line);
		b_backlash = (ImageButton) view.findViewById(R.id.b_backlash);
		b_left = (ImageButton) view.findViewById(R.id.b_left);
		b_right = (ImageButton) view.findViewById(R.id.b_right);
		b_delete = (ImageButton) view.findViewById(R.id.b_delete);
		b_enter = (ImageButton) view.findViewById(R.id.b_enter);
		whole_seat_empty = (ImageButton) view.findViewById(R.id.whole_seat_empty);
		whole_seat_set = (ImageButton) view.findViewById(R.id.whole_seat_set);
		whole_seat_off = (ImageButton) view.findViewById(R.id.whole_seat_off);

		b_1.setOnClickListener(onClickListener);
		b_2.setOnClickListener(onClickListener);
		b_3.setOnClickListener(onClickListener);
		b_4.setOnClickListener(onClickListener);
		b_5.setOnClickListener(onClickListener);
		b_6.setOnClickListener(onClickListener);
		b_7.setOnClickListener(onClickListener);
		b_8.setOnClickListener(onClickListener);
		b_9.setOnClickListener(onClickListener);
		b_0.setOnClickListener(onClickListener);
		b_A.setOnClickListener(onClickListener);
		b_B.setOnClickListener(onClickListener);
		b_C.setOnClickListener(onClickListener);
		b_D.setOnClickListener(onClickListener);
		b_E.setOnClickListener(onClickListener);
		b_F.setOnClickListener(onClickListener);
		b_line.setOnClickListener(onClickListener);
		b_backlash.setOnClickListener(onClickListener);
		b_left.setOnClickListener(onClickListener);
		b_right.setOnClickListener(onClickListener);
		b_delete.setOnClickListener(onClickListener);
		b_enter.setOnClickListener(onClickListener);
		whole_seat_empty.setOnClickListener(onClickListener);
		whole_seat_set.setOnClickListener(onClickListener);
		whole_seat_off.setOnClickListener(onClickListener);

		return view;
	}

	OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			String textString = textView.getText().toString();
			String string = "";

			switch (v.getId()) {
				case R.id.b_1:
					string = "1";
					break;

				case R.id.b_2:
					string = "2";
					break;

				case R.id.b_3:
					string = "3";
					break;

				case R.id.b_4:
					string = "4";
					break;

				case R.id.b_5:
					string = "5";
					break;

				case R.id.b_6:
					string = "6";
					break;

				case R.id.b_7:
					string = "7";
					break;

				case R.id.b_8:
					string = "8";
					break;

				case R.id.b_9:
					string = "9";
					break;

				case R.id.b_0:
					string = "0";
					break;

				case R.id.b_A:
					string = "A";
					break;

				case R.id.b_B:
					string = "B";
					break;

				case R.id.b_C:
					string = "C";
					break;

				case R.id.b_D:
					string = "D";
					break;

				case R.id.b_E:
					string = "E";
					break;

				case R.id.b_F:
					string = "F";
					break;

				case R.id.b_line:
					string = "-";
					break;

				case R.id.b_backlash:
					string = "/";
					break;

				case R.id.b_left:
					string = "(";
					break;

				case R.id.b_right:
					string = ")";
					break;

				case R.id.b_delete:
					textView.setText("");
					break;

				case R.id.b_enter:
					if (!TextUtils.isEmpty(ip) && GoldingLauncherActivity.getValue() == 0) {
						if (!(TextUtils.isEmpty(textString))) {
							textString = textView.getText().toString();
							Log.i("", "value   :"+ip+"#"+textString);
							String mtr =ip+"#"+textString;
//							Utils.SetLocalSeat(mtr);//设置本机座位号【测试】
							final String value = "seat"+"@"+mtr;
							Boolean existSameSeat = SystemInfo.validataSeat(mtr);
							if (existSameSeat) {
								WindowManager windowManager2 = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
								display = windowManager2.getDefaultDisplay();
								View view2 = LayoutInflater.from(getActivity()).inflate(R.layout.tips_alertdialog, null);
								LinearLayout lLayout_bg2 = (LinearLayout) view2.findViewById(R.id.lLayout_bg);
								dialog = new Dialog(getActivity(), R.style.AlertDialogStyle);
								dialog.setCancelable(false);
								lLayout_bg2.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));
								TextView tipTextView2 = (TextView) view2.findViewById(R.id.tipstext);
								tipTextView2.setText(R.string.reset);
								Button btn_dialog_resolve2 = (Button) view2.findViewById(R.id.btn_ok);
								btn_dialog_resolve2.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										Intent sendSeatIntent = new Intent(Contant.Actions.CONVERSATION_SEND);
										sendSeatIntent.putExtra("conversation", value);
										getActivity().sendBroadcast(sendSeatIntent);
										dialog.dismiss();
									}
								});
								Button btn_dialog_cancel2 = (Button) view2.findViewById(R.id.btn_cancle);
								btn_dialog_cancel2.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										dialog.dismiss();
									}
								});
								dialog.setContentView(view2);
								dialog.show();
							} else {
								Intent sendSeatIntent = new Intent(Contant.Actions.CONVERSATION_SEND);
								sendSeatIntent.putExtra("conversation", value);
								getActivity().sendBroadcast(sendSeatIntent);
							}
						} else {
							EngModeFragment.Toast(getResources().getString(R.string.seathint), getActivity());
						}
					} else{
						EngModeFragment.Toast(getResources().getString(R.string.netdisc), getActivity());
					}
					break;

				case R.id.whole_seat_empty:
					if (!TextUtils.isEmpty(ip) && GoldingLauncherActivity.getValue() == 0) {
						WindowManager windowManager = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
						display = windowManager.getDefaultDisplay();
						View view = LayoutInflater.from(getActivity()).inflate(R.layout.password_dialog, null);
						LinearLayout lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
						dialog = new Dialog(getActivity(), R.style.AlertDialogStyle);
						dialog.setCancelable(false);
						lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));
						TextView tipTextView = (TextView) view.findViewById(R.id.tipstext);
						tipTextView.setText(R.string.tips_empty);
						final EditText editText = (EditText) view.findViewById(R.id.editText);
						editText.setHint(getResources().getString(R.string.pwdAgain));
						Button btn_dialog_resolve = (Button) view.findViewById(R.id.btn_ok);
						btn_dialog_resolve.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								if ("828".equals(editText.getText().toString())) {
									SystemInfo.SetLocalSeat("");//清空全车座位号码
									String value = "seat"+"@"+ip+"#"+"exit";
									Log.i("", "Inform all of the terminal, I set the seat number is empty!");
									Intent sendSeatIntent = new Intent(Contant.Actions.CONVERSATION_SEND);
									sendSeatIntent.putExtra("conversation", value);
									getActivity().sendBroadcast(sendSeatIntent);
									textView.setText(SystemInfo.getLocalSeat());
									dialog.dismiss();
								} else {
									editText.setText("");
									editText.setHint(getResources().getString(R.string.pwderr));
									editText.setHintTextColor(0xffff0000);
								}
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
					} else{
						EngModeFragment.Toast(getResources().getString(R.string.netdisc), getActivity());
					}
					break;

				case R.id.whole_seat_set:
					if (!TextUtils.isEmpty(ip) && GoldingLauncherActivity.getValue() == 0) {
						WindowManager windowManager2 = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
						display = windowManager2.getDefaultDisplay();
						View view2 = LayoutInflater.from(getActivity()).inflate(R.layout.tips_alertdialog, null);
						LinearLayout lLayout_bg2 = (LinearLayout) view2.findViewById(R.id.lLayout_bg);
						dialog = new Dialog(getActivity(), R.style.AlertDialogStyle);
						dialog.setCancelable(false);
						lLayout_bg2.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));
						TextView tipTextView2 = (TextView) view2.findViewById(R.id.tipstext);
						tipTextView2.setText(R.string.tips_whole_seat_set);
						Button btn_dialog_resolve2 = (Button) view2.findViewById(R.id.btn_ok);
						btn_dialog_resolve2.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								String value = "EngineeringAndTesting"+"@"+"on"+"@"+"0";
								Intent wholeCarSetSeatIntent = new Intent(Contant.Actions.CONVERSATION_SEND);
								wholeCarSetSeatIntent.putExtra("conversation", value);
								getActivity().sendBroadcast(wholeCarSetSeatIntent);
								dialog.dismiss();
							}
						});
						Button btn_dialog_cancel2 = (Button) view2.findViewById(R.id.btn_cancle);
						btn_dialog_cancel2.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
						dialog.setContentView(view2);
						dialog.show();
					} else{
						EngModeFragment.Toast(getResources().getString(R.string.netdisc), getActivity());
					}
					break;

				case R.id.whole_seat_off:
					if (!TextUtils.isEmpty(ip) && GoldingLauncherActivity.getValue() == 0) {
						WindowManager windowManager3 = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
						display = windowManager3.getDefaultDisplay();
						View view3 = LayoutInflater.from(getActivity()).inflate(R.layout.tips_alertdialog, null);
						LinearLayout lLayout_bg3 = (LinearLayout) view3.findViewById(R.id.lLayout_bg);
						dialog = new Dialog(getActivity(), R.style.AlertDialogStyle);
						dialog.setCancelable(false);
						lLayout_bg3.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));
						TextView tipTextView3 = (TextView) view3.findViewById(R.id.tipstext);
						tipTextView3.setText(R.string.tips_whole_seat_off);
						Button btn_dialog_resolve3 = (Button) view3.findViewById(R.id.btn_ok);
						btn_dialog_resolve3.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								String value2 = "EngineeringAndTesting"+"@"+"off";
								Intent wholeCarOffSeatIntent = new Intent(Contant.Actions.CONVERSATION_SEND);
								wholeCarOffSeatIntent.putExtra("conversation", value2);
								getActivity().sendBroadcast(wholeCarOffSeatIntent);
								dialog.dismiss();
							}
						});
						Button btn_dialog_cancel3 = (Button) view3.findViewById(R.id.btn_cancle);
						btn_dialog_cancel3.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
						dialog.setContentView(view3);
						dialog.show();
					} else{
						EngModeFragment.Toast(getResources().getString(R.string.netdisc), getActivity());
					}
					break;
			}

			if (textString.length() >= 3)
				return;
			else {
				textView.append(string);
			}
		}
	};
}