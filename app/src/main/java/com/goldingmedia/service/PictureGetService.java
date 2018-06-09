package com.goldingmedia.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.goldingmedia.temporary.SystemInfo;

import java.io.FileOutputStream;

public class PictureGetService extends Service{
	private Camera camera = null;
	private Context mContext;
	private SurfaceView preview = null;
	private WindowManager wm ;
	private WindowManager.LayoutParams params ;

	private void register(){
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.golding.take.photo");
		registerReceiver(receiver, filter);
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e("","===== receive === "+action);
			if("com.golding.take.photo".equals(action)){
				onTakePhotoClicked(null);
			}
		}
	};


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams( 1, 1, WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0, PixelFormat.UNKNOWN);
		register();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	public void onTakePhotoClicked(View view) {
		preview = new SurfaceView(mContext);
		SurfaceHolder holder = preview.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (camera == null) {
						camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
						camera.setPreviewDisplay(holder);
						Camera.Parameters parameters = camera.getParameters();
						parameters.setExposureCompensation(3);
						camera.setParameters(parameters);
						camera.startPreview();
						camera.takePicture(null, null, pictureCallback);

					}
				} catch (Exception e) {
					if (camera != null) {
						camera.release();
					}
					camera = null;
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (camera != null) {
					camera.release();
				}
				camera = null;
			}
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
		});
		wm.addView(preview, params);
	}

	private final Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if(null == data){
				return;
			}
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			camera.stopPreview();
			if (camera != null) {
				camera.release();
			}
			Matrix matrix = new Matrix();
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

			int bright = getBright(bitmap);
			Log.i("", "-- bitmap bright = " + getBright(bitmap));

			if (bright<150) {
				Bitmap bitmap2 = bitmap;
				ToneLayer mToneLayer = new ToneLayer(mContext);
				mToneLayer.setLum(128+((150-bright)>127?127:(150-bright)));
				bitmap = mToneLayer.handleImage(bitmap2, 2);

				Log.i("", "-- bitmap2 bright = " + getBright(bitmap));

				if(bitmap2 != null)
					bitmap2.recycle();
			}
			bitmap = toChange(bitmap, 320, 240);

			try {
				FileOutputStream outputStreamOriginal = new FileOutputStream(Environment
						.getExternalStorageDirectory().toString()+"/"+ SystemInfo.getSeatString()+".jpg");
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStreamOriginal);
				outputStreamOriginal.close();
				wm.removeView(preview);
				Intent mIntent = new Intent("com.golding.sendfile");
				mContext.sendBroadcast(mIntent);
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(bitmap != null)
				bitmap.recycle();
		}
	};

	public static class ToneLayer {

		/**
		 * 饱和度标识
		 */
		public static final int FLAG_SATURATION = 0x0;

		/**
		 * 色相标识
		 */
		public static final int FLAG_HUE = 0x1;

		/**
		 * 亮度标识
		 */
		public static final int FLAG_LUM = 0x2;

		private LinearLayout mParent;

		private ColorMatrix mLightnessMatrix;
		private ColorMatrix mSaturationMatrix;
		private ColorMatrix mHueMatrix;
		private ColorMatrix mAllMatrix;

		/**
		 * 亮度
		 */
		private float mLumValue = 0F;

		/**
		 * 饱和度
		 */
		private float mSaturationValue = 0F;

		/**
		 * 色相
		 */
		private float mHueValue = 1F;

		/**
		 * SeekBar的中间值
		 */
		private static final int MIDDLE_VALUE = 127;

		/**
		 * SeekBar的最大值
		 */
		private static final int MAX_VALUE = 255;


		public ToneLayer(Context context) {
		}


		public View getParentView() {
			return mParent;
		}

		/**
		 * 设置饱和度值
		 * @param saturation
		 */
		public void setSaturation(int saturation) {
			mSaturationValue = saturation * 1.0F / MIDDLE_VALUE;
		}

		/**
		 * 设置色相值
		 * @param hue
		 */
		public void setHue(int hue) {
			mHueValue = (hue - MIDDLE_VALUE) * 1.0F / MIDDLE_VALUE * 180;
		}

		/**
		 * 设置亮度值
		 * @param lum
		 */
		public void setLum(int lum) {
			mLumValue = lum * 1.0F / MIDDLE_VALUE;
		}

		/**
		 *
		 * @param flag
		 *            比特位0 表示是否改变色相，比位1表示是否改变饱和度,比特位2表示是否改变明亮度
		 */
		public Bitmap handleImage(Bitmap bm, int flag) {
			Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
					Bitmap.Config.ARGB_8888);
			// 创建一个相同尺寸的可变的位图区,用于绘制调色后的图片
			Canvas canvas = new Canvas(bmp);// 得到画笔对象
			Paint paint = new Paint();// 新建paint
			paint.setAntiAlias(true);// 设置抗锯齿,也即是边缘做平滑处理
			if (null == mAllMatrix) {
				mAllMatrix = new ColorMatrix();
			}

			if (null == mLightnessMatrix) {
				mLightnessMatrix = new ColorMatrix();// 用于颜色变换的矩阵，android位图颜色变化处理主要是靠该对象完成
			}

			if (null == mSaturationMatrix) {
				mSaturationMatrix = new ColorMatrix();
			}

			if (null == mHueMatrix) {
				mHueMatrix = new ColorMatrix();
			}

			switch (flag) {
				case FLAG_HUE: // 需要改变色相
					// hueColor就是色轮旋转的角度,正值表示顺时针旋转，负值表示逆时针旋转
					mHueMatrix.reset();// 设为默认值
					mHueMatrix.setRotate(0, mHueValue);// 控制让红色区在色轮上旋转的角度
					mHueMatrix.setRotate(1, mHueValue);// 控制让绿红色区在色轮上旋转的角度
					mHueMatrix.setRotate(2, mHueValue);// 控制让蓝色区在色轮上旋转的角度
					// 这里相当于改变的是全图的色相
					break;
				case FLAG_SATURATION: // 需要改变饱和度
					// saturation 饱和度值，最小可设为0，此时对应的是灰度图(也就是俗话的“黑白图”)，
					// 为1表示饱和度不变，设置大于1，就显示过饱和
					mSaturationMatrix.reset();
					mSaturationMatrix.setSaturation(mSaturationValue);
					break;
				case FLAG_LUM: // 亮度
					mLightnessMatrix.reset();
					mLightnessMatrix.setScale(mLumValue, mLumValue, mLumValue, 1);// 红、绿、蓝三分量按相同的比例,最后一个参数1表示透明度不做变化，此函数详细说明参考
					// // android
					// doc
					break;
			}
			mAllMatrix.reset();
			mAllMatrix.postConcat(mHueMatrix);
			mAllMatrix.postConcat(mSaturationMatrix);// 效果叠加
			mAllMatrix.postConcat(mLightnessMatrix);// 效果叠加

			paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));// 设置颜色变换效果
			canvas.drawBitmap(bm, 0, 0, paint);// 将颜色变化后的图片输出到新创建的位图区
			// 返回新的位图，也即调色处理后的图片
			return bmp;
		}
	}

	private int getBright(Bitmap bitmap) {
		int r;
		int g;
		int b;
		int number = 0;
		double bright = 0;
		Integer localTemp;

		for (int i = 0; i < bitmap.getHeight(); i++) {
			for (int j = 0; j < bitmap.getWidth(); j++) {
				number++;
				localTemp = (Integer) bitmap.getPixel(j, i);
				r = (localTemp | 0xff00ffff) >> 16 & 0x00ff;
				g = (localTemp | 0xffff00ff) >> 8 & 0x0000ff;
				b = (localTemp | 0xffffff00) & 0x0000ff;

				bright = bright + 0.299 * r + 0.587 * g + 0.114 * b;
//                Log.i("xiao", "bright = " + bright); 
			}
		}
		return (int)(bright / number);
	}

	public Bitmap toChange(Bitmap bigimage,int newWidth,int newHeight){
		int width = bigimage.getWidth();
		int height = bigimage.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) newWidth)/width;
		float scaleHeight = ((float) newHeight)/height;
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bigimage, 0, 0, width, height, matrix, true);
		bigimage.recycle();
		return bitmap;
	}
}