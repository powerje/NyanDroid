package com.powerje.nyan.sprites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.powerje.nyan.NyanUtils;
import com.powerje.nyan.R;

import java.util.ArrayList;

public class Rainbow {
	final Context mContext;
	final ArrayList<Bitmap> mFrames;
	final private int rainbowWidth;

	private int mCenterX;
	private int mCenterY;
	private int mOffset;
    private boolean isBlank = false;
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
        } else if (image.equals("mono")) {
            mFrames.add(NyanUtils.maxHeightResourceToBitmap(c,
                    R.drawable.monochrome_rainbow_0, maxDim));
            mFrames.add(NyanUtils.maxHeightResourceToBitmap(c,
                    R.drawable.monochrome_rainbow_1, maxDim));
		} else if (image.equals("rainbow")) {
			mFrames.add(NyanUtils.maxHeightResourceToBitmap(c,
					R.drawable.rainbow_frame0, maxDim));
			mFrames.add(NyanUtils.maxHeightResourceToBitmap(c,
					R.drawable.rainbow_frame1, maxDim));
		} else {
            isBlank = true;
            rainbowWidth = 0;
            return;
        }
		rainbowWidth = mFrames.get(0).getWidth();
	}

    public void draw(Canvas c, boolean animate) {
        synchronized (this) {
            if (isBlank) return;


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
    }

	public void setOffset(int offset) {
		mOffset = offset;
	}

	public int getFrameWidth() {
        if (isBlank) return 0;

		return mFrames.get(0).getWidth();
	}

	public void setCenter(int x, int y) {
		mCenterX = x;
		mCenterY = y;
	}

    public void recycle() {
        synchronized (this) {
            isBlank = true;
            for (Bitmap b : mFrames) {
                b.recycle();
            }
        }
    }
}
