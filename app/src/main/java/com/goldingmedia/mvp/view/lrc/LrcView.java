package com.goldingmedia.mvp.view.lrc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.goldingmedia.mvp.view.lrc.LrcProcess.LrcContent;

import java.util.ArrayList;
import java.util.List;

public class LrcView extends TextView {

	private float width;
	private float high;
	private Paint CurrentPaint;
	private Paint NotCurrentPaint;
	private float TextHigh = 35;
	private float TextSize = 24;
	private float CurrentTextSize = 28;
	private Paint TextPaint;
	private int Index = 0;
	private Context mContext;
	private Typeface localTypeface;
	private List<LrcContent> mSentenceEntities = new ArrayList<LrcContent>();

	public void setSentenceEntities(List<LrcContent> mSentenceEntities, boolean isWide ,boolean full) {
		this.mSentenceEntities = mSentenceEntities;
		TextHigh = 35;
		TextSize = 24;
		CurrentTextSize = 28;
		if(!isWide && !full){
			TextSize = TextSize*8/10;
			TextHigh = TextHigh*8/10 + 1;
			CurrentTextSize = CurrentTextSize*8/10;
		}
		if(full){
			TextHigh = 34;
			TextSize = 23;
			CurrentTextSize = 26;
		}
	}

	public LrcView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public LrcView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		setFocusable(true);
		localTypeface = Typeface.createFromAsset(mContext.getAssets(), "font/zfgy.otf");
		CurrentPaint = new Paint();
		CurrentPaint.setAntiAlias(true);
		CurrentPaint.setTextAlign(Paint.Align.CENTER);
		NotCurrentPaint = new Paint();
		NotCurrentPaint.setAntiAlias(true);
		NotCurrentPaint.setTextAlign(Paint.Align.CENTER);
		TextPaint = new Paint();
		TextPaint.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (canvas == null) {
			return;
		}
		CurrentPaint.setColor(Color.argb(210, 251, 248, 29));
		NotCurrentPaint.setColor(Color.argb(140, 255, 255, 255));
		CurrentPaint.setTextSize(CurrentTextSize);
		CurrentPaint.setTypeface(Typeface.MONOSPACE);
		NotCurrentPaint.setTextSize(TextSize);
		NotCurrentPaint.setTypeface(localTypeface);

		try {
			setText("");
			canvas.drawText(mSentenceEntities.get(Index).getLrc(), width / 2, high / 2, CurrentPaint);

			float tempY = high / 2;
			for (int i = Index - 1; i >= 0; i--) {
				tempY = tempY - TextHigh;
				canvas.drawText(mSentenceEntities.get(i).getLrc(), width / 2, tempY, NotCurrentPaint);
			}
			tempY = high / 2;
			for (int i = Index + 1; i < mSentenceEntities.size(); i++) {
				tempY = tempY + TextHigh;
				canvas.drawText(mSentenceEntities.get(i).getLrc(), width / 2,tempY, NotCurrentPaint);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		this.width = w;
		this.high = h;
	}

	public void SetIndex(int index) {
		this.Index = index;
	}
}
