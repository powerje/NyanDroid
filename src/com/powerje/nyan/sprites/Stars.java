package com.powerje.nyan.sprites;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.powerje.nyan.NyanUtils;
import com.powerje.nyan.R;

public class Stars {
	
	final ArrayList<Bitmap> mLargeStars;
	final ArrayList<Bitmap> mMediumStars;
	final ArrayList<Bitmap> mSmallStars;

	final List<Star> stars = new ArrayList<Star>();

	final Random mRandom = new Random();

	final Context mContext;
	final private Paint mPaint;

	final private static int MAX_NEW_STARS = 5;

	final int[] whiteDrawables = { R.drawable.star0, R.drawable.star1,
			R.drawable.star2, R.drawable.star3, R.drawable.star4,
			R.drawable.star5, R.drawable.star6, R.drawable.star7,
			R.drawable.star8, R.drawable.star9 };
	
	final int[] yellowDrawables = { R.drawable.yellow_star0, R.drawable.yellow_star1,
			R.drawable.yellow_star2, R.drawable.yellow_star3, R.drawable.yellow_star4,
			R.drawable.yellow_star5, R.drawable.yellow_star6, R.drawable.yellow_star7,
			R.drawable.yellow_star8, R.drawable.yellow_star9 };
	
	final int mNumberOfFrames = whiteDrawables.length;

	boolean mMovingUp;

	static class Star {
		int x;
		int y;
		int frame;
		int speed;
		int width;
		ArrayList<Bitmap> stars;
	}

	public Stars(Context c, int maxDim, Paint paint, String image) {

		mContext = c;
		mPaint = paint;
		int[] drawables;
		
		if (image.equals("white")) {
			drawables = whiteDrawables;
		} else {
			drawables = yellowDrawables;
		}
		
		mLargeStars = new ArrayList<Bitmap>();
		for (int i = 0; i < drawables.length; i++) {
			mLargeStars.add(NyanUtils.scaleWithRatio(c, drawables[i],
					maxDim / 2));
		}

		mMediumStars = new ArrayList<Bitmap>();
		for (int i = 0; i < drawables.length; i++) {
			mMediumStars.add(NyanUtils.scaleWithRatio(c, drawables[i],
					maxDim / 3));
		}

		mSmallStars = new ArrayList<Bitmap>();
		for (int i = 0; i < drawables.length; i++) {
			mSmallStars.add(NyanUtils.scaleWithRatio(c, drawables[i],
					maxDim / 4));
		}
	}

	public void draw(Canvas c) {
		int newStars = 0;
		// create some arbitrary number of stars up to a given max
		while (mRandom.nextInt(100) > 40 && newStars < MAX_NEW_STARS) {
			// create new star
			Star s = new Star();
			s.x = c.getWidth();
			s.y = mRandom.nextInt(c.getHeight());
			s.frame = mRandom.nextInt(mNumberOfFrames);

			int size = mRandom.nextInt(3);

			if (size == 0) {
				s.speed = 30;
				s.width = mLargeStars.get(0).getWidth();
				s.stars = mLargeStars;
			} else if (size == 1) {
				s.speed = 20;
				s.width = mMediumStars.get(0).getWidth();
				s.stars = mMediumStars;
			} else {
				s.speed = 10;
				s.width = mSmallStars.get(0).getWidth();
				s.stars = mSmallStars;
			}
			stars.add(s);
			newStars++;
		}

		for (int i = 0; i < stars.size(); i++) {
			Star s = stars.get(i);
			c.drawBitmap(s.stars.get(s.frame), s.x, s.y, mPaint);
			s.frame++;
			s.frame %= mNumberOfFrames;
			s.x -= s.speed;
			if (s.x < -s.width) {
				stars.remove(i);
				i--;
			}
		}
	}
}
