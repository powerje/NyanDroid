package com.powerje.nyan.sprites;

import java.util.ArrayList;

import com.powerje.nyan.NyanUtils;
import com.powerje.nyan.R;
import com.powerje.nyan.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NyanDroid is a class for drawing an animated nyan droid.
 * 
 * @author powerj
 * 
 */
public class NyanDroid {
	/** Context NyanDroid is being drawn in. */
	final Context mContext;
	/** NyanDroid frames. */
	final ArrayList<Bitmap> mFrames;

	/** Current y offset. */
	private int yOffset;
	/** True iff NyanDroid is moving upwards */
	boolean mMovingUp;
	/** Center x coordinate. */
	private int mCenterX;
	/** Center y coordinate. */
	private int mCenterY;

	/** Paint with which to draw. */
	private Paint mPaint;
	/** Current frame NyanDroid is in. */
	private int currentFrame;

	/**
	 * Construct NyanDroid.
	 * 
	 * @param c
	 *            context to be drawn in.
	 * @param maxDim
	 *            maximize size of a side.
	 * @param paint
	 *            used to draw.
	 */
	public NyanDroid(Context c, int maxDim, Paint paint, String image) {
		mContext = c;
		mPaint = paint;
		Bitmap repeatingFrame;
		
		mFrames = new ArrayList<Bitmap>();
		if (image.equals("droidtv")) {
			repeatingFrame = NyanUtils.imageResourceToBitmap(c, R.drawable.superman_gtv0,
					maxDim);
			mFrames.add(repeatingFrame);
			mFrames.add(repeatingFrame);
			mFrames.add(repeatingFrame);
			repeatingFrame = NyanUtils.imageResourceToBitmap(c, R.drawable.superman_gtv1,
					maxDim);
			mFrames.add(repeatingFrame);
			mFrames.add(repeatingFrame);
			mFrames.add(repeatingFrame);
		} else {
			mFrames.add(NyanUtils.imageResourceToBitmap(c, R.drawable.frame0,
					maxDim));
			mFrames.add(NyanUtils.imageResourceToBitmap(c, R.drawable.frame1,
					maxDim));
			mFrames.add(NyanUtils.imageResourceToBitmap(c, R.drawable.frame2,
					maxDim));
			mFrames.add(NyanUtils.imageResourceToBitmap(c, R.drawable.frame3,
					maxDim));
			mFrames.add(NyanUtils.imageResourceToBitmap(c,
					R.drawable.superman0, maxDim));

			repeatingFrame = NyanUtils.imageResourceToBitmap(c,
					R.drawable.superman1, maxDim);
			mFrames.add(repeatingFrame);
			mFrames.add(repeatingFrame);
			mFrames.add(repeatingFrame);

			mFrames.add(NyanUtils.imageResourceToBitmap(c, R.drawable.frame4,
					maxDim));
			mFrames.add(NyanUtils.imageResourceToBitmap(c, R.drawable.frame5,
					maxDim));
			mFrames.add(NyanUtils.imageResourceToBitmap(c, R.drawable.frame6,
					maxDim));
			mFrames.add(NyanUtils.imageResourceToBitmap(c, R.drawable.frame7,
					maxDim));
		} 
	}

	/**
	 * Draw NyanDroid.
	 * 
	 * @param c
	 *            canvas to draw on.
	 * @param animate
	 *            move to next frame after drawing.
	 */
	public void draw(Canvas c, boolean animate) {
		Bitmap toDraw = mFrames.get(currentFrame);
		c.drawBitmap(toDraw, mCenterX - (toDraw.getWidth() / 2),
				(mCenterY - toDraw.getHeight() / 2) + yOffset, mPaint);
		if (animate) {
			currentFrame = (currentFrame == mFrames.size() - 1) ? 0
					: (currentFrame + 1);

			if (mMovingUp) {
				yOffset += 6;
				if (yOffset > 5)
					mMovingUp = false;
			} else {
				yOffset -= 6;
				if (yOffset < -5)
					mMovingUp = true;
			}
		}
	}

	/**
	 * @return the height of an individual frame.
	 */
	public int getFrameHeight() {
		return mFrames.get(0).getHeight();
	}

	/**
	 * @return the width of an individual frame.
	 */
	public int getFrameWidth() {
		return mFrames.get(0).getWidth();
	}

	/**
	 * Set the coordinates with which to center the drawing.
	 * 
	 * @param x
	 *            center x coordinate
	 * @param y
	 *            center y coordinate
	 */
	public void setCenter(int x, int y) {
		mCenterX = x;
		mCenterY = y;
	}
}