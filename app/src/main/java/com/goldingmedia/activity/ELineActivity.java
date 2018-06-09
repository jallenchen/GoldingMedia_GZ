package com.goldingmedia.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.goldingmedia.BaseActivity;
import com.goldingmedia.R;
import com.goldingmedia.contant.Contant;
import com.goldingmedia.goldingcloud.TruckMediaProtos;
import com.goldingmedia.mvp.view.ui.HListView;
import com.goldingmedia.utils.Utils;

import java.util.List;

import static com.goldingmedia.mvp.view.animations.Animations.startAnim;

public class ELineActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private LayoutInflater inflater;
	private TextView elineName_content;
	private TextView profile;
	private TextView profile_content;
	private ImageButton backBtn;
	private HListView gallery;
	private ImageSwitcher imageSwitcher;

	private ImageAdapter mAdapter;
	private TruckMediaProtos.CTruckMediaNode mTruck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		Intent intent = getIntent();
		mTruck = (TruckMediaProtos.CTruckMediaNode) intent.getSerializableExtra("truck");
		String truckTitle = mTruck.getMediaInfo().getTruckMeta().getTruckTitle();
		String truckDesc = mTruck.getMediaInfo().getTruckMeta().getTruckDesc();
		String folder = mTruck.getMediaInfo().getTruckMeta().getTruckFilename()+"/"+mTruck.getMediaInfo().getTruckMeta().getTruckFilename();
		String filePath = Contant.getTruckMetaNodePath(mTruck.getCategoryId(),mTruck.getCategorySubId(),folder,true);
		List<String>  list = Utils.getFolderMsgWithFileExtension(filePath, "jpg", true);

		setContentView(R.layout.activity_eline);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initView();
		initListener();

		elineName_content.setText(truckTitle);
		profile.setText(R.string.intru);
		profile_content.setText(truckDesc);

		mAdapter = new ImageAdapter(this, list);
		mAdapter.setSelectedPosition(0);
		gallery.setAdapter(mAdapter);

		try {
			imageSwitcher.setImageURI(Uri.parse((String)(mAdapter.getSelecteItem())));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		imageSwitcher.setId(0);
		startAnim(imageSwitcher, true, 9);
	}

	private void initView() {
		elineName_content = (TextView) findViewById(R.id.elineName_content);
		profile = (TextView) findViewById(R.id.profile);
		profile_content = (TextView) findViewById(R.id.profile_content);
		backBtn = (ImageButton) findViewById(R.id.backBtn);
		gallery = (HListView) findViewById(R.id.gallery);
		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
	}

	private void initListener() {
		backBtn.setOnClickListener(this);

		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				mAdapter.setSelectedPosition(position);
				mAdapter.notifyDataSetChanged();
				imageSwitcher.setImageURI(Uri.parse((String)(mAdapter.getSelecteItem())));
				imageSwitcher.setId(position);
				startAnim(imageSwitcher, true, 9);
			}
		});

		imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
			@Override
			public View makeView() {
				ImageView image = new ImageView(mContext);
				image.setLayoutParams(new ImageSwitcher.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				return image;
			}
		});
	}

	private class ImageAdapter extends BaseAdapter {
		final Context context;
		int selectedPosition = 0;
		String mFilePath;
		private List<String> mFileNames;

		public ImageAdapter(Context context, List<String> fileNames) {
			this.context = context;
			if (fileNames != null) {
				mFileNames = fileNames;
			}
		}

		public void notifyDataSetChanged(List<String> fileNames, int position) {
			this.selectedPosition = position;
			if (fileNames != null) {
				mFileNames = fileNames;
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mFileNames.size();
		}

		@Override
		public Object getItem(int position) {
			if (mFileNames == null) return null;
			else if (position >= mFileNames.size()) return null;
			return mFileNames.get(position);
		}

		public String getFilePath() {
			return mFilePath;
		}

		public Object getSelecteItem() {
			return mFileNames.get(selectedPosition);
		}

		public void setSelectedPosition(int selectedPosition) {
			if(selectedPosition >= getCount()) {
				selectedPosition = 0;
			}
			this.selectedPosition = selectedPosition;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder ;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.eline_gallery_item, parent,false);
				holder.icon = (ImageView)convertView.findViewById(R.id.icon);
				holder.name = (TextView)convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.icon.setImageURI(Uri.parse(mFileNames.get(position)));
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	private static class ViewHolder {
		public ImageView icon;
		public TextView name;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.backBtn:
				finish();
				break;
		}
	}
}
