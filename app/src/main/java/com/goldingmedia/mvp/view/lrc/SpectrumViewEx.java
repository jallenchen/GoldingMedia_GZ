package com.goldingmedia.mvp.view.lrc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SpectrumViewEx extends View {

	private static final int EXPRESSION_STEP = 5;
	private static final int COL_SQUARE_PADDING = 4;
	private static final int COL_LOWEST_HEIGHT = 2;
	private Visualizer mVisualizer;
	private long mLastTime;
	private int mSessionId = -1;
	
	private MediaPlayer mMediaPlayer;
	
	private static final String TAG = "SpectrumView";
	
	private Paint mColPaint;
	private Paint mSquarePaint;

	public void setMediaPlayer(MediaPlayer player){
		mMediaPlayer = player;
	}
	
	public SpectrumViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SpectrumViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SpectrumViewEx(Context context) {
		super(context);
		init();
	}

	private void init() {
		mColPaint = new Paint();
		mColPaint.setColor(0x9C2EB5E6);
		mColPaint.setStyle(Style.FILL);
		mSquarePaint = new Paint();
		mSquarePaint.setColor(0xFF2EB5E6);
		mSquarePaint.setStyle(Style.FILL);
	}

	private int mDesiredWidth;
	private int mColCount;
	private int mColWidth;
	private int mDesiredHeight;
	private Rect[] mColRects;
	private Rect[] mSquareRects;
	private int[] mLastColData;
	private int[] mLastSquareData;
	private int mColTopLower;
	
	
	public void initialize(int colcolor, int squarecolor, int colcount, 
			int colwidth, int colpadding, int height) {
		mColCount = colcount;
		mColWidth = colwidth;
		int colpaddingwidth = colwidth + colpadding + colpadding;
		mDesiredHeight = height;
		mDesiredWidth = mColCount * colpaddingwidth;
		mColRects = new Rect[mColCount];
		mSquareRects = new Rect[mColCount];
		for (int i = 0; i < mColCount; i++) {
			int left = colpaddingwidth * i + colpadding;
			mColRects[i] = new Rect(left, mDesiredHeight, 
					left + colwidth, mDesiredHeight);
			mSquareRects[i] = new Rect(left, mDesiredHeight - 18, 
					left + colwidth, mDesiredHeight - 10);
		}
		mLastColData = new int[mColCount];
		mLastSquareData = new int[mColCount];
		mColTopLower = mDesiredHeight - colwidth - COL_SQUARE_PADDING - COL_LOWEST_HEIGHT;
		mColPaint.setColor(colcolor);
		mSquarePaint.setColor(squarecolor);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 0),
				resolveSizeAndState(getSuggestedMinimumHeight(), heightMeasureSpec, 0));
	}
	
	protected int getSuggestedMinimumHeight() {
        return Math.max(super.getSuggestedMinimumHeight(), mDesiredHeight);
    }

    protected int getSuggestedMinimumWidth() {
        return Math.max(super.getSuggestedMinimumWidth(), mDesiredWidth);
    }

    public void updateVisualizer(byte[] fft)
	{
		byte[] model = new byte[fft.length / 2 + 1];
		model[0] = (byte) Math.abs(fft[0]);
		int mSpectrumNum = mColCount;//fft.length/2;
		for (int i = 2, j = 1; j < mSpectrumNum;)
		{
			model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
			i += 2;
			j++;
		}
		
		adjustColumeEx(model);
		//invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final int count = mColCount;
		int coloff = mColWidth + COL_SQUARE_PADDING;
		if (mColRects != null && mSquareRects != null) {
			for (int i = 0; i < count; i++) {
				int top = mColTopLower * (450 - mLastColData[i]) / 450 + coloff;
				if (top < coloff) {
					top = coloff;
				}
				mColRects[i].top = top;
				canvas.drawRect(mColRects[i], mColPaint);
				
				int sqtop = mColTopLower * (450 - mLastSquareData[i]) / 450;
				if (sqtop < 0) {
					sqtop = 0;
				}
				
				int a = (int)(((mLastColData[i] + mLastSquareData[i]) >>> 1) * 1.5) + 60;
				if (a > 255) {
					a = 255;
				}
				
				mSquarePaint.setAlpha(a);
				mSquareRects[i].top = sqtop + mColWidth / 2;
				mSquareRects[i].bottom = sqtop + mColWidth;
				canvas.drawRect(mSquareRects[i], mSquarePaint);
			}
		}
	}

	private byte[] mSpectrumData = new byte[32];
	private byte[] mZeroData = new byte[32];

	int type = 0;
	
	public void pause() {
		mSessionId = -1;
		if (mVisualizer != null) {
			mVisualizer.setEnabled(false);
			mVisualizer.release();
			mVisualizer = null;
			Log.d("@@", "release");
		}
	}
	
	@Override
	public void setVisibility(int visibility) {
		if (visibility != View.VISIBLE) {
			pause();
		}
		super.setVisibility(visibility);
	}
	
	public void snoop() {
		long time = System.currentTimeMillis();
		removeCallbacks(mLowerColumeRunnable);
		postDelayed(mLowerColumeRunnable, 60);
		Log.e("","+++++snoop()");
		/*if (type == 0) {
			if (mVisualizer == null || time - mLastTime > 2000) {
	*/			int session = mMediaPlayer.getAudioSessionId();//MusicUtils.getAudioSessionId();
				Log.d("@@", "init audio session id = " + session);
				if (mVisualizer != null) {
					mVisualizer.setEnabled(false);
					mVisualizer.release();
				}
				mInUse = false;
				mVisualizer = new Visualizer(session);
				if (mVisualizer.getCaptureSize() != 1024) {
					mVisualizer.setCaptureSize(1024);
				}
				final int maxCR = Visualizer.getMaxCaptureRate();
				mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
					
					@Override
					public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
							int samplingRate) {
						// TODO Auto-generated method stub
						adjustColumeEx(waveform);
					}
					
					@Override
					public void onFftDataCapture(Visualizer visualizer, byte[] fft,
							int samplingRate) {
						// TODO Auto-generated method stub
						updateVisualizer(fft);
					}
				}, maxCR/2, false, true);
				
				mSessionId = session;
			//} 
			//if (!mInUse) {
				mVisualizer.setEnabled(true);
				mInUse = true;
			//}
			
			mLastTime = time;
	}
	
	private boolean adjustColumeEx(byte[] waveform) {
		for (int i = 0; i < mColCount; i++) {
			int coldata = mLastColData[i];
			final int energy = Math.abs(waveform[i]*4);
			if (energy < coldata - 28) {
				coldata -= 14;
			} else if (energy > coldata || energy < coldata - 6) {
				coldata = (energy + coldata) >>> 1;
			} else if (energy < coldata){
				coldata -= 1;
			}
			
			if (coldata > mLastSquareData[i]) {
				mLastSquareData[i] = coldata;
			} else if (coldata < mLastSquareData[i] - EXPRESSION_STEP) {
				mLastSquareData[i] -= EXPRESSION_STEP;
				if (mLastSquareData[i] < 0) {
					mLastSquareData[i] = 0;
				}
			} else {
				mLastSquareData[i] = coldata;
			}
			mLastColData[i] = coldata;
		}
		invalidate();
		for (int i = 0; i < mColCount; i++) {
			if (mLastSquareData[i] > 0) {
				return true;
			}
		}
		return false;
	}
	
	private boolean mInUse = false;
	
	private Runnable mLowerColumeRunnable = new Runnable() {
		public void run() {
			boolean more = adjustColumeEx(mZeroData);
			if (more) {
				postDelayed(this, 30);
			} else if (mVisualizer != null) {
//				pause();
			}
		}
	};
	
	public void stop() {
		postDelayed(mLowerColumeRunnable, 0);
	}

	public int getCellCount() {
		return mColCount;
	}

}
