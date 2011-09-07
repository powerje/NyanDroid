package com.powerje.nyan;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class NyanActivity extends Activity {

	List<AnimationDrawable> mAnimations = null;
	List<ImageView> mImages = null;
	LinearLayout mRainbow = null;
	RelativeLayout mCat = null;
	RelativeLayout mCombined = null;

	private static final int RAINBOW_HEIGHT = 70;
	private static final int RAINBOW_WIDTH = 40;
	
	private static final String TAG = "NyanDroid";
	private static boolean DEBUG = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mAnimations = new ArrayList<AnimationDrawable>();
		mImages = new ArrayList<ImageView>();
		mCat = (RelativeLayout) findViewById(R.id.cat);
		mRainbow = (LinearLayout) findViewById(R.id.rainbow);
		mCombined = (RelativeLayout) findViewById(R.id.combined);

		setupAnimation();
	}

	private int calcCenter() {
		int width = getWindowManager().getDefaultDisplay().getWidth();
		return ((width / 2) / (RAINBOW_WIDTH * 2));
	}

	private void log(String log) {
		if (DEBUG) {
			Log.d(TAG, log);
		}
	}
	
	private void setupAnimation() {
		for (int i = 0; i < calcCenter(); i++) {
			addFrame(R.drawable.rainbow0, mRainbow);
			addFrame(R.drawable.rainbow1, mRainbow);
		}
		addFrame(R.drawable.nyandroid, mCat);
	}

	private void addFrame(int frame_drawable, ViewGroup vg) {
		final ImageView img = new ImageView(this);
		mImages.add(img);
		img.setAdjustViewBounds(true);
		img.setScaleType(ImageView.ScaleType.FIT_XY);
		if (frame_drawable == R.drawable.nyandroid) {
			img.setMaxHeight(200);
			img.setMaxWidth(175);
		} else {
			img.setMaxHeight(RAINBOW_HEIGHT);
			img.setMaxWidth(RAINBOW_WIDTH);
		}

		img.setImageResource(frame_drawable);
		vg.addView(img, generateDefaultLayoutParams());
		mAnimations.add((AnimationDrawable) img.getDrawable());
	}

	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) mCat
				.getLayoutParams();
		lp.setMargins(mCat.getLeft() - mRainbow.getWidth(), 0, 0, 0);

		if (hasFocus) {
			for (AnimationDrawable anim : mAnimations) {
				anim.start();
			}
		} else {
			for (AnimationDrawable anim : mAnimations) {
				anim.stop();
			}
		}
	}

	private void clearDrawables(ViewGroup v) {
		int children = v.getChildCount();
		for (int i=0;i < children; i++) {
			((ImageView) v.getChildAt(i)).setImageDrawable(null);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		for (ImageView img : mImages) {
			img.getDrawable().setCallback(null);
		}
		
		for (AnimationDrawable anim : mAnimations) {
			anim.setCallback(null);
		}
		
		clearDrawables(mCat);
		clearDrawables(mRainbow);
		
		mCombined.removeAllViews();
		mImages.clear();
		mAnimations.clear();
		
		mCat = null;
		mRainbow = null;
		mCombined = null;
	}
}