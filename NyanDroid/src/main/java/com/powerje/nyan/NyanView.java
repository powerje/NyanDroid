package com.powerje.nyan;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.powerje.nyan.sprites.NyanDroid;
import com.powerje.nyan.sprites.Rainbow;
import com.powerje.nyan.sprites.Stars;

/**
 * NyanView draws NyanDroid flying through space distributing Ice Cream Nyanwich.
 * @author powerj
 *
 */
public class NyanView extends SurfaceView implements SurfaceHolder.Callback, OnSharedPreferenceChangeListener {

	/** Paint to draw with. */
	private final Paint mPaint = new Paint();
	/** True iff dimensions have been setup. */
	private boolean hasSetup;

	private SharedPreferences mPrefs;
	private boolean mPreferencesChanged;
	private String mDroidImage;
	private String mRainbowImage;
	private String mStarImage;
    private boolean mShowDroid;
    private boolean mShowRainbow;
    private boolean mShowStars;
	private int mMaxDim;
    private int mAnimationSpeed;
    private int mSizeMod;
	/** Animated NyanDroid. */
	private NyanDroid mNyanDroid;
	/** Animated rainbow. */
	private Rainbow mRainbow;
	/** Animated star field. */
	private Stars mStars;
	/** Count number of elapsed frames to time animations. */
	private int frameCount;
	
	private Context mContext;

	private DrawingThread mThread;

	public NyanView(Context context, int scaleBy) {
		super(context);
		mPaint.setColor(0xffffffff);
		mContext = context;

		init(scaleBy);
	}

	private void init(int scaleBy) {
		mPrefs = mContext.getSharedPreferences(NyanPaper.SHARED_PREFS_NAME, 0);
		mPrefs.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(mPrefs, null);
		setupPrefs();
		
		getHolder().addCallback(this);
		hasSetup = false;

		setupAnimations();
	}

    public void cancel() {
        // Lazy way to ensure in post 4.0 that this executes after the asynctask in the start method
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mThread.setRunning(false);
                mNyanDroid.recycle();
                mStars.recycle();
                mRainbow.recycle();
            }
        }.execute();
    }

    public void start() {

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Do image loading off the main thread
                setupAnimations();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mThread.setRunning(true);
            }
        }.execute();

    }

	private void setupAnimations() {
        mMaxDim = 64 * mSizeMod;

        int width = this.getContext().getResources().getDisplayMetrics().widthPixels;
        mMaxDim = mMaxDim < width ? mMaxDim : width - 64;

		mNyanDroid = new NyanDroid(mContext, mMaxDim, mPaint, mDroidImage);

		// initialize Rainbow
		mMaxDim =  (int) (mNyanDroid.getFrameHeight() * .4);
		mRainbow = new Rainbow(mContext, mMaxDim, mPaint, mRainbowImage);

		// remember offset for when drawing rainbows
		mRainbow.setOffset((mNyanDroid.getFrameWidth() / 2)
				- mRainbow.getFrameWidth());

		mStars = new Stars(mContext, mMaxDim, mPaint, mStarImage, mAnimationSpeed);
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs,
			String key) {
		setupPrefs();
		mPreferencesChanged = true;
	}

    private void setupPrefs() {
        mDroidImage = mPrefs.getString("droid_image", "nyanwich");
        mRainbowImage = mPrefs.getString("rainbow_image", "rainbow");
        mStarImage = mPrefs.getString("star_image", "white");
        mSizeMod = mPrefs.getInt("size_mod", 5);
        mAnimationSpeed = mPrefs.getInt("animation_speed", 3);
        mShowDroid = !"none".equals(mDroidImage);
        mShowRainbow = !"none".equals(mRainbowImage);
        mShowStars = !"none".equals(mStarImage);
    }
	
	public void surfaceCreated(SurfaceHolder holder) {
		mThread = new DrawingThread(getHolder(), this);
		mThread.setRunning(true);
		mThread.start();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("NyanView", "Surface destroyed");
		boolean retry = true;
		mThread.setRunning(false);
		while (retry) {
			try {
				mThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void draw(Canvas c) {
		frameCount++;
		if (c != null) {
			if (mPreferencesChanged) {
				setupAnimations();
				mPreferencesChanged = false;
				//must reset centers
				hasSetup = false;
			}
			
			if (!hasSetup) {
				mRainbow.setCenter(c.getWidth() / 2, c.getHeight() / 2);
				mNyanDroid.setCenter(c.getWidth() / 2, c.getHeight() / 2);
				hasSetup = true;
			}

			c.drawColor(getResources().getColor(R.color.nyanblue));
            if (mShowStars) {
			    mStars.draw(c);
            }

            boolean animateFrame = frameCount == 3;

            if (mShowRainbow) {
                mRainbow.draw(c, animateFrame);
            }

            if (mShowDroid) {
                mNyanDroid.draw(c, animateFrame);
            }
		}
		frameCount %= 3;
	}

	public static class DrawingThread extends Thread {

		private SurfaceHolder myThreadSurfaceHolder;
		private NyanView myThreadSurfaceView;
		private boolean myThreadRun = false;

		public DrawingThread(SurfaceHolder surfaceHolder, NyanView surfaceView) {
			myThreadSurfaceHolder = surfaceHolder;
			myThreadSurfaceView = surfaceView;
		}

		public void setRunning(boolean b) {
			myThreadRun = b;
		}

		@Override
		public void run() {
			while (myThreadRun) {
				Canvas c = null;
				try {
					c = myThreadSurfaceHolder.lockCanvas(null);
					synchronized (myThreadSurfaceHolder) {
						myThreadSurfaceView.draw(c);
					}

					sleep(1000 / 30);

				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					if (c != null) {
						myThreadSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}
}