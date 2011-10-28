package com.powerje.nyan.sprites;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.powerje.nyan.NyanUtils;
import com.powerje.nyan.R;

public class Rainbow {
	final Context mContext;
	final ArrayList<Bitmap> mFrames;
	final private int rainbowWidth;

	private int mCenterX;
	private int mCenterY;
	private int mOffset;

	private int currentFrame;
	final private Paint mPaint;

	public Rainbow(final Context c, final int maxDim, final Paint paint, String image) {
		mContext = c;

		mPaint = paint;

		mFrames = new ArrayList<Bitmap>();
		if (image.equals("neapolitan")) {
			mFrames.add(NyanUtils.maxHeightResourceToBitmap(c,
					R.drawable.neapolitan_rainbow_frame0, maxDim));
			mFrames.add(NyanUtils.maxHeightResourceToBitmap(c,
					R.drawable.neapolitan_rainbow_frame1, maxDim));
		} else {
			mFrames.add(NyanUtils.maxHeightResourceToBitmap(c,
					R.drawable.rainbow_frame0, maxDim));
			mFrames.add(NyanUtils.maxHeightResourceToBitmap(c,
					R.drawable.rainbow_frame1, maxDim));
		}
		rainbowWidth = mFrames.get(0).getWidth();
	}

	public void draw(Canvas c, boolean animate) {
		int numberRainbowsFromCenter = (c.getWidth() / 2) / rainbowWidth;

		for (int i = 0; i < numberRainbowsFromCenter; i++) {
			Bitmap toDraw = mFrames.get((currentFrame + (i % 2)) % 2);
			c.drawBitmap(toDraw, mCenterX - (toDraw.getWidth() / 2) - mOffset
					- (i * rainbowWidth), (mCenterY - toDraw.getHeight() / 2),
					mPaint);
		}
		if (animate) {
			currentFrame++;
			currentFrame %= 2;
		}
	}

	public void setOffset(int offset) {
		mOffset = offset;
	}

	public int getFrameWidth() {
		return mFrames.get(0).getWidth();
	}

	public void setCenter(int x, int y) {
		mCenterX = x;
		mCenterY = y;
	}
}
