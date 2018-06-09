package com.goldingmedia.mvp.view.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Text {
	private Paint paint;
	private String content;
	private float x;
	private float y;
	private float stepX;
	private float contentWidth;
	private float displayWidth;
	private int textColor;
	private Context context;

	public void setText(String value) {
		this.content = value;
		if (this.paint == null)
			paint = new Paint();
		if (this.paint != null)
			this.contentWidth = paint.measureText(content);
	}
	
	public String getText(){
		return content;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getContentWidth() {
		return contentWidth;
	}

	public void setTextColor(int nColor) {
		this.textColor = nColor;

		if (this.paint == null)
			paint = new Paint();

		if (this.paint != null)
			this.paint.setColor(nColor);
	}

	public void setTextSize(float size) {
		if (this.paint == null)
			paint = new Paint();

		if (this.paint != null)
			this.paint.setTextSize(size);
	}

	public void setDisplayWidth(int nWidth) {
		this.displayWidth = nWidth; // 设置屏幕宽度
	}

	public Text(String content, float x, float y, float stepX, Context context) {
		this.context = context;
		this.content = content;
		this.x = x;
		this.y = y;
		this.stepX = stepX;
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(30f);
		paint.setAntiAlias(true);
		this.contentWidth = paint.measureText(content);
	}

	public Text(float x, float y, float textSize, float stepX, Context context) {
		this.context = context;
		this.x = x;
		this.y = y;
		this.stepX = stepX;
		paint = new Paint();
		paint.setTextSize(textSize);
		paint.setAntiAlias(true);
	}

	public void move() {
		x -= stepX;
		if (x < -contentWidth) {
			if (displayWidth > 0)
				x = displayWidth;
			else
				x = 1024;

			Intent mIntentViewGone = new Intent("com.marqueetextView.next");
			mIntentViewGone.putExtra("view", 0);
			context.sendBroadcast(mIntentViewGone);
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawText(content, x, y, paint);
	}
}
