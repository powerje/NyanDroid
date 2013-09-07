package com.powerje.nyan.sprites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.powerje.nyan.NyanUtils;
import com.powerje.nyan.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Stars {

    private final ArrayList<Bitmap> mLargeStars;
    private final ArrayList<Bitmap> mMediumStars;
    private final ArrayList<Bitmap> mSmallStars;

    private final List<Star> stars = new ArrayList<Star>();

    private final Random mRandom = new Random();

    private final Context mContext;
    private final Paint mPaint;
    private final int mSpeed;
    private static int MAX_NEW_STARS = 5;
    private boolean reverse = false;
    private boolean isBlank = false;

    final int[] whiteDrawables = { R.drawable.star0, R.drawable.star1,
            R.drawable.star2, R.drawable.star3, R.drawable.star4,
            R.drawable.star5, R.drawable.star6, R.drawable.star7,
            R.drawable.star8, R.drawable.star9 };

    final int[] yellowDrawables = { R.drawable.yellow_star0, R.drawable.yellow_star1,
            R.drawable.yellow_star2, R.drawable.yellow_star3, R.drawable.yellow_star4,
            R.drawable.yellow_star5, R.drawable.yellow_star6, R.drawable.yellow_star7,
            R.drawable.yellow_star8, R.drawable.yellow_star9 };

    final int[] noDrawable = {R.drawable.no};

    final int[] icsDrawables = {
            R.drawable.nyandroid00,
            R.drawable.nyandroid01,
            R.drawable.nyandroid02,
            R.drawable.nyandroid03,
            R.drawable.nyandroid04,
            R.drawable.nyandroid05,
            R.drawable.nyandroid06,
            R.drawable.nyandroid07,
            R.drawable.nyandroid08,
            R.drawable.nyandroid09,
            R.drawable.nyandroid10,
            R.drawable.nyandroid11,
    };

    final int mNumberOfFrames;

    static class Star {
        int x;
        int y;
        int frame;
        int speed;
        int width;
        ArrayList<Bitmap> stars;
    }

    public Stars(Context c, int maxDim, Paint paint, String image, int speed) {
        mSpeed = speed;
        mContext = c;
        mPaint = paint;
        int[] drawables;

        int dimMod = 1;
        if (image.equals("white")) {
            drawables = whiteDrawables;
        } else if (image.equals("yellow")) {
            drawables = yellowDrawables;
        } else if (image.equals("no")) {
            drawables = noDrawable;
            // The 'no' can be overwhelming
            MAX_NEW_STARS = 1;
        } else { //if (image.equals("ics_egg")) {
            drawables = icsDrawables;
            reverse = true;
            MAX_NEW_STARS = 1;
            dimMod = 0;
        }

        // Add a little bit of crowd control for slow speeds
        if (speed < 4)
            MAX_NEW_STARS = speed;

        mNumberOfFrames = drawables.length;

        mLargeStars = new ArrayList<Bitmap>();
        for (int i = 0; i < drawables.length; i++) {
            mLargeStars.add(NyanUtils.scaleWithRatio(c, drawables[i],
                    maxDim / (dimMod +1)));
        }

        mMediumStars = new ArrayList<Bitmap>();
        for (int i = 0; i < drawables.length; i++) {
            mMediumStars.add(NyanUtils.scaleWithRatio(c, drawables[i],
                    maxDim / (dimMod + 2)));
        }

        mSmallStars = new ArrayList<Bitmap>();
        for (int i = 0; i < drawables.length; i++) {
            mSmallStars.add(NyanUtils.scaleWithRatio(c, drawables[i],
                    maxDim / (dimMod + 3)));
        }
    }

    public void draw(Canvas c) {
        synchronized (this) {
            if (isBlank) return;

            int newStars = 0;
            // create some arbitrary number of stars up to a given max
            while (mRandom.nextInt(100) > 40 && newStars < MAX_NEW_STARS) {
                // create new star
                Star s = new Star();
                if (reverse) {
                    s.x = -40;
                } else {
                    s.x = c.getWidth();
                }
                s.y = mRandom.nextInt(c.getHeight());
                s.frame = mRandom.nextInt(mNumberOfFrames);

                int size = mRandom.nextInt(3);

                if (size == 0) {
                    s.speed = 10;
                    s.width = mLargeStars.get(0).getWidth();
                    s.stars = mLargeStars;
                } else if (size == 1) {
                    s.speed = 5;
                    s.width = mMediumStars.get(0).getWidth();
                    s.stars = mMediumStars;
                } else {
                    s.speed = 1;
                    s.width = mSmallStars.get(0).getWidth();
                    s.stars = mSmallStars;
                }

                s.speed += mSpeed * 5;

                stars.add(s);
                newStars++;
            }

            for (int i = 0; i < stars.size(); i++) {
                Star s = stars.get(i);
                c.drawBitmap(s.stars.get(s.frame), s.x, s.y, mPaint);
                s.frame++;
                s.frame %= mNumberOfFrames;
                if (reverse) {
                    s.x += s.speed;
                    if (s.x > c.getWidth()) {
                        stars.remove(i);
                        i--;
                    }
                } else {
                    s.x -= s.speed;
                    if (s.x < -c.getWidth()) {
                        stars.remove(i);
                        i--;
                    }
                }
            }
        }
    }

    private void recycleBitmapsInList(List<Bitmap> l) {
        for (Bitmap b : l) {
            b.recycle();
        }
    }
    public void recycle() {
        synchronized (this) {
            isBlank = true;
            recycleBitmapsInList(mLargeStars);
            recycleBitmapsInList(mMediumStars);
            recycleBitmapsInList(mSmallStars);
        }
    }
}
