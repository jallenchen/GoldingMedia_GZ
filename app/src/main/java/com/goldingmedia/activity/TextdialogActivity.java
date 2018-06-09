package com.goldingmedia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.R;
import com.goldingmedia.mvp.view.fragment.EngModeFragment;
import com.goldingmedia.mvp.view.fragment.SeatArrangementFragment;

import static com.goldingmedia.mvp.view.fragment.SeatArrangementFragment.CheckSeat;

public class TextdialogActivity extends BaseActivity {
	private static String bString;
	private static String deletenum;
	private static String seatnum;
	private SeatArrangementFragment seatarrange;

	
	private final static int SET_SEATNO_TYPE = 0;
	private TextView textView;

	private Button button_clear, button_0, button_1, button_2, button_3, button_4,
			button_5, button_6, button_7, button_8, button_9, button_A,
			button_B, button_C, button_D, button_E, button_F, buttonline,
			buttonleft, buttonright, buttonbacklash, buttonOk, buttoncancle;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_textdialog);
		
		LayoutParams params = getWindow().getAttributes();
		params.gravity = Gravity.CENTER;
		params.x = -50;
		params.y = -40;
		getWindow().setAttributes(params);
		
		

		String mString = getIntent().getExtras().get("buttonText").toString();
		bString = getIntent().getExtras().get("buttonText").toString();
		
		textView = (TextView) findViewById(R.id.seat_num);
		textView.setText(mString);
		button_clear = (Button) findViewById(R.id.button_clear);
		button_0 = (Button) findViewById(R.id.button_0);
		button_1 = (Button) findViewById(R.id.button_1);
		button_2 = (Button) findViewById(R.id.button_2);
		button_3 = (Button) findViewById(R.id.button_3);
		button_4 = (Button) findViewById(R.id.button_4);
		button_5 = (Button) findViewById(R.id.button_5);
		button_6 = (Button) findViewById(R.id.button_6);
		button_7 = (Button) findViewById(R.id.button_7);
		button_8 = (Button) findViewById(R.id.button_8);
		button_9 = (Button) findViewById(R.id.button_9);
		button_A = (Button) findViewById(R.id.button_A);
		button_B = (Button) findViewById(R.id.button_B);
		button_C = (Button) findViewById(R.id.button_C);
		button_D = (Button) findViewById(R.id.button_D);
		button_E = (Button) findViewById(R.id.button_E);
		button_F = (Button) findViewById(R.id.button_F);
		buttonline = (Button) findViewById(R.id.buttonline);
		buttonleft = (Button) findViewById(R.id.buttonleft);
		buttonright = (Button) findViewById(R.id.buttonright);
		buttonbacklash = (Button) findViewById(R.id.buttonbacklash);
		buttonOk = (Button) findViewById(R.id.button_OK);

		buttonOk.setOnClickListener(onClickListener);
		button_clear.setOnClickListener(onClickListener);

		buttonright.setOnClickListener(onClickListener);
		buttonline.setOnClickListener(onClickListener);
		buttonleft.setOnClickListener(onClickListener);
		buttonbacklash.setOnClickListener(onClickListener);

		button_0.setOnClickListener(onClickListener);
		button_1.setOnClickListener(onClickListener);
		button_2.setOnClickListener(onClickListener);
		button_3.setOnClickListener(onClickListener);
		button_4.setOnClickListener(onClickListener);
		button_5.setOnClickListener(onClickListener);
		button_6.setOnClickListener(onClickListener);
		button_7.setOnClickListener(onClickListener);
		button_8.setOnClickListener(onClickListener);
		button_9.setOnClickListener(onClickListener);
		button_A.setOnClickListener(onClickListener);
		button_B.setOnClickListener(onClickListener);
		button_C.setOnClickListener(onClickListener);
		button_D.setOnClickListener(onClickListener);
		button_E.setOnClickListener(onClickListener);
		button_F.setOnClickListener(onClickListener);
		
		
		
	}



	private OnClickListener onClickListener = new OnClickListener() {

		public void onClick(View v) {
			seatnum = textView.getText().toString();
			String mString = "";
			switch (v.getId()) {
			case R.id.button_clear:
				seatnum = textView.getText().toString();
				deletenum = seatnum;
				textView.setText("");
				break;

			case R.id.button_0:
				mString = "0";
				break;
			case R.id.button_1:
				mString = "1";
				break;
			case R.id.button_2:
				mString = "2";
				break;
			case R.id.button_3:
				mString = "3";
				break;
			case R.id.button_4:
				mString = "4";

				break;
			case R.id.button_5:
				mString = "5";
				break;
			case R.id.button_6:
				mString = "6";
				break;
			case R.id.button_7:
				mString = "7";
				break;
			case R.id.button_8:
				mString = "8";
				break;
			case R.id.button_9:
				mString = "9";
				break;
			case R.id.button_A:
				mString = "A";
				break;
			case R.id.button_B:
				mString = "B";
				break;
			case R.id.button_C:
				mString = "C";
				// textView.append("C");
				break;
			case R.id.button_D:
				mString = "D";
				break;

			case R.id.button_E:
				mString = "E";
				break;

			case R.id.button_F:
				mString = "F";
				break;

			case R.id.buttonline:
				mString = "-";
				break;

			case R.id.buttonleft:
				mString = "(";
				break;
			case R.id.buttonright:
				mString = ")";
				break;
			case R.id.buttonbacklash:
				mString = "/";
				break;
			case R.id.button_OK:

				seatnum = textView.getText().toString();
				if(CheckSeat(seatnum)){
					Log.e("if","if");
					Toast.makeText(TextdialogActivity.this, "it has the same number", Toast.LENGTH_SHORT).show();
				}else{
					Log.e("else","else");
					if(seatnum.equals("") && !bString.equals("")){
						seatarrange.seatlist.remove(deletenum);
					}else if(!seatnum.equals("")){
						seatarrange.seatlist.remove(bString);
						seatarrange.seatlist.add(seatnum);
					}
					//TODO
					Intent i = new Intent(TextdialogActivity.this,EngModeFragment.class);
					i.putExtra("seatnum", seatnum);
					i.putExtra("type", SET_SEATNO_TYPE);
					TextdialogActivity.this.setResult(RESULT_OK, i);
					TextdialogActivity.this.finish();
				}
				
				break;
			}
			if (seatnum.length() >= 3){

			}else {
				textView.append(mString);
			}
		}
	};

	

}
