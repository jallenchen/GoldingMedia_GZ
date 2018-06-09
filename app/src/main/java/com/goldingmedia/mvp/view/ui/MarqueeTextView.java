package com.goldingmedia.mvp.view.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {
	private Thread thread;
	private Text text;
	private int mBgColor = Color.WHITE;
	private boolean move = true;

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		text = new Text(0, 38, 28, 1, context); // 调用新的构造函数。暂时没有指定文字内容，颜色和大小
	}

	public void setX(float textX) {
		text.setX(textX);
	}

	// 设置走马灯的文字内容
	public void setText(String content) {
		text.setText(content);
	}

	public void setDisplayWidth(int nWidth) {
		text.setDisplayWidth(nWidth);
	}

	// 设置走马灯的文字大小
	public void setTextSize(float size) {
		if (text != null)
			text.setTextSize(size);
	}

	// 设置走马灯的文字颜色
	public void setTextColor(int nColor) {
		if (text != null)
			text.setTextColor(nColor);
	}

	// 设置走马灯的文字后面的背景颜色。有时候不一定是白色的
	public void setBackgroundColor(int nColor) {
		mBgColor = nColor;
	}

	public void setTextCenter(){// 文字居中显示
		if(text.getContentWidth() <= 1024f) {
			text.setX((1024f - text.getContentWidth()) / 2);
		}
		postInvalidate();
	}

	public void resumeMove() {
		thread.notifyAll();
	}

	public void waitMove() {
		try {
			thread.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void controlMove(boolean move) {
		this.move = move;
	}

	protected void onDraw(Canvas canvas) {
		super.setSingleLine(true);
		long time = System.currentTimeMillis();
		if(!TextUtils.isEmpty(text.getText())){
			canvas.drawColor(mBgColor);
			text.draw(canvas);

			time = System.currentTimeMillis() - time;
			if(move){
				text.move();
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				postInvalidateDelayed(Math.abs(time - 35));
			}
		}
	}
}
