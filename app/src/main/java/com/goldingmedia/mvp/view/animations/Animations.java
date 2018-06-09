package com.goldingmedia.mvp.view.animations;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageSwitcher;

public class Animations {
	private static long time1 = 0;
	private static long s1 = 0;
	private static long time2 = 0;
	private static long s2 = 0;
	private static long timeL2 = 0;

	public static void leftHidden(View view, long durationMillis) {
		Animation anim = new TranslateAnimation(0.0f, -60.0f, 0.0f, 0.0f);
		anim.setDuration(durationMillis);
		view.startAnimation(anim);
	}

	public static void leftOpen(View view, long durationMillis) {
		Animation anim = new TranslateAnimation(-60.0f,0.0f, 0.0f, 0.0f);
		anim.setDuration(durationMillis);
		view.startAnimation(anim);
	}

	public static void rightHidden(View view, long durationMillis) {
		Animation anim = new TranslateAnimation(0.0f, 60.0f, 0.0f, 0.0f);
		anim.setDuration(durationMillis);
		view.startAnimation(anim);
	}

	public static void  rightOpen(View view, long durationMillis) {
		Animation anim = new TranslateAnimation(60.0f, 0.0f,0.0f, 0.0f);
		anim.setDuration(durationMillis);
		view.startAnimation(anim);
	}
	
	public static void playbarHiddenWide(View view, long durationMillis) {
		Animation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, 104.0f);
		anim.setDuration(durationMillis);
		view.startAnimation(anim);
		view.setVisibility(View.GONE);
	}

	public static void playbarHiddenNormal(View view, long durationMillis) {
		long timeL = System.currentTimeMillis()-time1;
		if(timeL < 0){
			timeL = 0;
		}
		if(timeL>durationMillis) {
			s1 = 0;
		} else if(timeL>durationMillis/2) {
			s1 = (s1-20)-(timeL*(s1-20)/durationMillis);
		} else {
			s1 = (s1+20)-(timeL*(s1+20)/durationMillis);
		}
		Animation anim = new TranslateAnimation(0.0f, 0.0f, s1, 81.0f);
		anim.setDuration(timeL>durationMillis?durationMillis:timeL);
		view.startAnimation(anim);
		time1 = System.currentTimeMillis();
		view.setVisibility(View.GONE);
	}
	
	
	public static void playbarOpenWide(View view, long durationMillis) {
		Animation anim = new TranslateAnimation(0.0f, 0.0f, 104.0f, 0.0f);
		anim.setDuration(durationMillis);
		view.startAnimation(anim);
		view.setVisibility(View.VISIBLE);
	}

	public static void playbarOpenNormal(View view, long durationMillis) {
		long timeL = System.currentTimeMillis()-time1;
		if(timeL < 0)
			timeL = 0;
		
		if(timeL>durationMillis) {
			s1 = 81;
		} else {
			s1 = 81-(timeL*s1/durationMillis);
		}
		Animation anim = new TranslateAnimation(0.0f, 0.0f, s1, 0.0f);
		anim.setDuration(durationMillis);
		view.startAnimation(anim);
		time1 = System.currentTimeMillis();
		view.setVisibility(View.VISIBLE);
	}

/* ************************************************************************************* */
	public static void movielistHidden(View view, long durationMillis, int left) {
		timeL2 = System.currentTimeMillis()-time2;
		if(timeL2 < 0)
			timeL2 = 0;
		if(timeL2>durationMillis) {
			s2 = 0;
		} else if(timeL2>durationMillis/2) {
			s2 = (s2-20)-(timeL2*(s2-20)/durationMillis);
		} else {
			s2 = (s2+20)-(timeL2*(s2+20)/durationMillis);
		}
		Animation anim = new TranslateAnimation(0.0f - s2, 0.0f - left, 0.0f, 0.0f);
		anim.setDuration(timeL2>durationMillis?durationMillis:timeL2);
		view.setVisibility(View.GONE);
		view.startAnimation(anim);
		time2 = System.currentTimeMillis();
	}
	
	public static void movielistOpenWide(View view, long durationMillis) {
		Animation anim = new TranslateAnimation(-180.0f, 0.0f, 0.0f, 0.0f);
		anim.setDuration(durationMillis);
		view.startAnimation(anim);
		view.setVisibility(View.VISIBLE);
	}

	public static void movielistOpenNormal(View view, long durationMillis) {
		long timeL = System.currentTimeMillis()-time2;
		if(timeL < 0)
			timeL = 0;
		if(timeL>durationMillis) {
			s2 = 300;
		} else {
			s2 = 300-(timeL*s2/durationMillis);
		}
		Animation anim = new TranslateAnimation(-s2, 0.0f, 0.0f, 0.0f);
		anim.setDuration(durationMillis);
		view.startAnimation(anim);
		view.setVisibility(View.VISIBLE);
		time2 = System.currentTimeMillis();
	}
	
	private static Animation inChange0() {
		Animation anim = new TranslateAnimation(0.0f, 0.0f, 247.5f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange0() {
		Animation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -247.5f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange0_2() {
		Animation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, 247.5f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation inChange1() {
		Animation anim = new TranslateAnimation(-600.0f, 0.0f, 247.5f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange1() {
		Animation anim = new TranslateAnimation(0.0f, 600.0f, 0.0f, -247.5f);
		anim.setDuration(1000);
		return anim;
	}

	private static Animation outChange1_2() {
		Animation anim = new TranslateAnimation(0.0f, -600.0f, 0.0f, 247.5f);
		anim.setDuration(1000);
		return anim;
	}

	private static Animation inChange2() {
		Animation anim = new TranslateAnimation(-600.0f, 0.0f, 0.0f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange2() {
		Animation anim = new TranslateAnimation(0.0f, 600.0f, 0.0f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange2_2() {
		Animation anim = new TranslateAnimation(0.0f, -600.0f, 0.0f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation inChange3() {
		Animation anim = new TranslateAnimation(600.0f, 0.0f, 0.0f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange3() {
		Animation anim = new TranslateAnimation(0.0f, -600.0f, 0.0f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange3_2() {
		Animation anim = new TranslateAnimation(0.0f, 600.0f, 0.0f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation inChange4() {
		Animation anim = new TranslateAnimation(600.0f, 0.0f, 90.0f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange4() {
		Animation anim = new TranslateAnimation(0.0f, -600.0f, 0.0f, -247.5f);
		anim.setDuration(1000);
		return anim;
	}

	private static Animation outChange4_2() {
		Animation anim = new TranslateAnimation(0.0f, 600.0f, 0.0f, 247.5f);
		anim.setDuration(1000);
		return anim;
	}

	private static Animation inChange5() {
		Animation anim = new TranslateAnimation(0.0f, 0.0f, -247.5f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange5() {
		Animation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, 247.5f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange5_2() {
		Animation anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -247.5f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation inChange6() {
		Animation anim = new TranslateAnimation(-600.0f, 0.0f, -247.5f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange6() {
		Animation anim = new TranslateAnimation(0.0f, 600.0f, 00.0f, 247.5f);
		anim.setDuration(1000);
		return anim;
	}

	private static Animation outChange6_2() {
		Animation anim = new TranslateAnimation(0.0f, -600.0f, 00.0f, -247.5f);
		anim.setDuration(1000);
		return anim;
	}

	private static Animation inChange7() {
		Animation anim = new TranslateAnimation(600.0f, 0.0f, -247.5f, 0.0f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation outChange7() {
		Animation anim = new TranslateAnimation(0.0f, -600.0f, 0.0f, 247.5f);
		anim.setDuration(1000);
		return anim;
	}

	private static Animation outChange7_2() {
		Animation anim = new TranslateAnimation(0.0f, 600.0f, 0.0f, -247.5f);
		anim.setDuration(1000);
		return anim;
	}

	private static Animation inChange8() {
		Animation anim = new AlphaAnimation(0.1f, 1.0f);
		anim.setDuration(1300);
		return anim;
	}

	private static Animation outChange8() {
		Animation anim = new AlphaAnimation(1.0f, 0.1f);
		anim.setDuration(1500);
		return anim;
	}

	private static Animation inChange9() {
		Animation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 300.0f,
				40.0f);
		anim.setDuration(800);
		return anim;
	}

	private static Animation outChange9() {
		Animation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 300.0f,
				40.0f);
		anim.setDuration(400);
		return anim;
	}

	public static void startAnim(ImageSwitcher is, boolean display, int key) {
		if (display) {
			if (key == -1) {
				key = (int) (Math.random() * 10 * 2);
				if (key < 1) {
					key = 1;
				} else if (key > 18) {
					key = 18;
				}
			}
			switch (key) {
			case 1:
				is.setInAnimation(inChange0());
				is.setOutAnimation(outChange0());
				break;

			case 2:
				is.setInAnimation(inChange1());
				is.setOutAnimation(outChange1());
				break;

			case 3:
				is.setInAnimation(inChange2());
				is.setOutAnimation(outChange2());
				break;

			case 4:
				is.setInAnimation(inChange3());
				is.setOutAnimation(outChange3());
				break;

			case 5:
				is.setInAnimation(inChange4());
				is.setOutAnimation(outChange4());
				break;

			case 6:
				is.setInAnimation(inChange5());
				is.setOutAnimation(outChange5());
				break;

			case 7:
				is.setInAnimation(inChange6());
				is.setOutAnimation(outChange6());
				break;

			case 8:
				is.setInAnimation(inChange7());
				is.setOutAnimation(outChange7());
				break;

			case 9:
				is.setInAnimation(inChange8());
				is.setOutAnimation(outChange8());
				break;

			case 10:
				is.setInAnimation(inChange7());
				is.setOutAnimation(outChange7_2());
				break;

			case 11:
				is.setInAnimation(inChange0());
				is.setOutAnimation(outChange0_2());
				break;

			case 12:
				is.setInAnimation(inChange1());
				is.setOutAnimation(outChange1_2());
				break;

			case 13:
				is.setInAnimation(inChange2());
				is.setOutAnimation(outChange2_2());
				break;

			case 14:
				is.setInAnimation(inChange3());
				is.setOutAnimation(outChange3_2());
				break;

			case 15:
				is.setInAnimation(inChange4());
				is.setOutAnimation(outChange4_2());
				break;

			case 16:
				is.setInAnimation(inChange5());
				is.setOutAnimation(outChange5_2());
				break;

			case 17:
				is.setInAnimation(inChange6());
				is.setOutAnimation(outChange6_2());
				break;

			case 18:
				is.setInAnimation(inChange9());
				is.setOutAnimation(outChange9());
				break;
			}
		}
	}

}
