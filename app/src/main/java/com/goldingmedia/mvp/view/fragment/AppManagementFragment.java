package com.goldingmedia.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.golding.goldinglauncher.GoldingLauncherActivity;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.temporary.AppInfo;
import com.goldingmedia.temporary.AppInfoList;
import com.goldingmedia.temporary.Command;

import java.util.List;

@SuppressLint("NewApi")
public class AppManagementFragment extends Fragment {
	
	private View view;
	List<AppInfo> appInfo;
	private String ip;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.app_info_list, container,false);
		appInfo = AppInfoList.getAppInfoSort(AppInfoList.getAppInfos(getActivity()));
		Bundle bundle = getArguments();
		ip = bundle.getString("ip");
		ListView listView = (ListView) view.findViewById(R.id.listView);
		listView.setAdapter(new AppListViewAdapter(getActivity(), appInfo));
		listView.setOnItemClickListener(new ListViewItemClick());
		return view;
	}
	
	class ListViewItemClick implements OnItemClickListener {
		Toast toast;
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			final int arg = arg2;
			switch (arg0.getId()) {
			case R.id.listView:
				final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogStyle);
				dialog.setCancelable(true);
				View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_dialog, null);
				LinearLayout dialog_lay = (LinearLayout) view.findViewById(R.id.dialog_lay);
				WindowManager windowManager = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
				Display display = windowManager.getDefaultDisplay();
				dialog_lay.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));
				ListView listview = (ListView) view.findViewById(R.id.dialog_listview);
				listview.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.app_operation)));
				listview.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
					    final String pkgName = appInfo.get(arg).getPkgName();
						String value = "EngineeringAndTesting"+"@";
						final Intent wholeCarSetIntent = new Intent(Contant.Actions.CONVERSATION_SEND);
						switch (position) {
						case 0:
							value = value+"on"+"@"+"openVersion"+"&"+pkgName+"&"+appInfo.get(arg).getVersionCode()+"&"+appInfo.get(arg).getVersionName();
							wholeCarSetIntent.putExtra("conversation", value);
							getActivity().sendBroadcast(wholeCarSetIntent);
							break;
							
						case 1:
						case 2:
							try {
								if (pkgName != null) {
									boolean cannotOpen = false;
									for (int i = 0; i < cannotOpenApp.length; i++) {
									    if (cannotOpenApp[i].equals(pkgName)) {
									    	cannotOpen = true;
									    	break;
									    }
									}
									if (cannotOpen) {
										if (toast != null) {
											toast.cancel();
										}
										toast = Toast.makeText(getActivity(), getResources().getString(R.string.cannotOpenApp), Toast.LENGTH_LONG);
										toast.show();
										dialog.dismiss();
										return;
									}
									if (position == 1) {
										Intent openAppIntent = getActivity().getPackageManager().getLaunchIntentForPackage(pkgName);
										openAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
										startActivity(openAppIntent);
										getActivity().overridePendingTransition(0, R.anim.zoom_exit);
									} else {
										if (ip != null && !"".equals(ip)) {
											value = value+"openApp"+"@"+pkgName+"@"+ip;
											Command.sendCommandString(value);
										} else{
											EngModeFragment.Toast(getResources().getString(R.string.netdisc), getActivity());
										}
									}
								}
							} catch (Exception e) {
								// TODO: handle exception
								toast = Toast.makeText(getActivity(), getResources().getString(R.string.cannotOpenApp), Toast.LENGTH_LONG);
								toast.show();
							}
							break;
							
						case 3:
							Uri packageURI = Uri.parse("package:"+pkgName);
							Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
							uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
							startActivity(uninstallIntent);
							break;
							
						case 4:
			                if (!TextUtils.isEmpty(ip) && GoldingLauncherActivity.getValue() == 0) {
			    				WindowManager windowManager = (WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE);
			    				Display display = windowManager.getDefaultDisplay();
			    			    View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.password_dialog, null);
			    				LinearLayout lLayout_bg = (LinearLayout) view1.findViewById(R.id.lLayout_bg);
			    				final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogStyle);
			    				dialog.setCancelable(false);
			    				lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));
			    				TextView tipTextView = (TextView) view1.findViewById(R.id.tipstext);
			    				tipTextView.setText(R.string.whole_car_uninstall);
			    				final EditText editText = (EditText) view1.findViewById(R.id.editText);
								editText.setHint(getResources().getString(R.string.pwdAgain));
			    				Button btn_dialog_resolve = (Button) view1.findViewById(R.id.btn_ok);
			    				btn_dialog_resolve.setOnClickListener(new OnClickListener() {
			    					public void onClick(View v) {
			    						if ("828".equals(editText.getText().toString())) {
											String value1 = "EngineeringAndTesting"+"@"+"uninstallApp"+"@"+pkgName;
											wholeCarSetIntent.putExtra("conversation", value1);
											getActivity().sendBroadcast(wholeCarSetIntent);
			        						dialog.dismiss();
										} else {
											editText.setText("");
											editText.setHint(getResources().getString(R.string.pwderr));
											editText.setHintTextColor(0xffff0000);
										}
			    					}
			    				});
			    				Button btn_dialog_cancel = (Button) view1.findViewById(R.id.btn_cancle);
			    				btn_dialog_cancel.setOnClickListener(new OnClickListener() {
			    					public void onClick(View v) {
			    						 dialog.dismiss();
			    					}
			    				});
			    				dialog.setContentView(view1);
			    				dialog.show();
			                } else{
								EngModeFragment.Toast(getResources().getString(R.string.netdisc), getActivity());
			            	}
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
				break;
			}
		}
	}
	
	public static String[] cannotOpenApp={
			"com.golding.goldinglauncher",
			"com.golding.mediacenter",
			"com.golding.languageservice",
			"com.golding.systemservice",
			"com.golding.ebookservice",
	};

	public class AppListViewAdapter extends BaseAdapter {

		private Context context;
		private List<AppInfo> list;

		public AppListViewAdapter(Context context, List<AppInfo> list) {
			this.context = context;
			this.list = list;
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View view, ViewGroup parent) {

			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.listview_item_app_info, parent,false);
				holder.icon = (ImageView)view.findViewById(R.id.icon);
				holder.appNameTxt = (TextView)view.findViewById(R.id.appNameTxt);
				holder.VersionCodeTxt = (TextView)view.findViewById(R.id.VersionCodeTxt);
				holder.VersionNameTxt = (TextView)view.findViewById(R.id.VersionNameTxt);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			AppInfo appInfo = (AppInfo) getItem(position);
			holder.icon.setImageDrawable(appInfo.getAppIcon());
			holder.appNameTxt.setText(appInfo.getAppName());
			holder.VersionCodeTxt.setText("VersionCode: "+appInfo.getVersionCode());
			holder.VersionNameTxt.setText("VersionName: "+appInfo.getVersionName());

			return view;
		}

		public class ViewHolder {
			public ImageView icon;
			public TextView appNameTxt;
			public TextView VersionCodeTxt;
			public TextView VersionNameTxt;
		}
	}
}



