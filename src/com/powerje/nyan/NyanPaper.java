package com.powerje.nyan;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.powerje.nyan.sprites.NyanDroid;
import com.powerje.nyan.sprites.Rainbow;
import com.powerje.nyan.sprites.Stars;

public class NyanPaper extends WallpaperService {

	public static final String SHARED_PREFS_NAME = "nyandroidsettings";
	private static final String TAG = "NyanPaper";
	private final Handler mDroidHandler = new Handler();
	private static int mWidth;
	
	@Override
	public Engine onCreateEngine() {
		return new NyanEngine();
	}

	class NyanEngine extends Engine implements
			SharedPreferences.OnSharedPreferenceChangeListener {
		private final Paint mPaint = new Paint();

		private boolean mVisible;
		private boolean hasSetup;
		private SharedPreferences mPrefs;
		private boolean mPreferencesChanged;

		private NyanDroid mNyanDroid;
		private Rainbow mRainbow;
		private Stars mStars;
		
		private String mDroidImage;
		private String mRainbowImage;
		private String mStarImage;
		
		private int mSizeMod;
		private int mMaxDim;
		private int mAnimationSpeed;
		
		private final Runnable mDrawFrame = new Runnable() {
			public void run() {
				drawFrame();
			}
		};

		NyanEngine() {
			mPaint.setColor(0xffffffff);

			mPrefs = NyanPaper.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
			mPrefs.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPrefs, null);
			setupPrefs();
		}

		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key) {
			Log.d(TAG, "prefs changed");
			setupPrefs();
			mPreferencesChanged = true;
		}

		private void setupPrefs() {
			mDroidImage = mPrefs.getString("droid_image", "nyanwich");
			mRainbowImage = mPrefs.getString("rainbow_image", "rainbow");
			mStarImage = mPrefs.getString("star_image", "white");
			mSizeMod = mPrefs.getInt("size_mod", 10);
			mAnimationSpeed = mPrefs.getInt("animation_speed", 3); 
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
			mDroidHandler.removeCallbacks(mDrawFrame);
		}
		

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				drawFrame();
			} else {
				mDroidHandler.removeCallbacks(mDrawFrame);
			}
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			mWidth = width;
			hasSetup = false;
			setupAnimations();
		}

		private void setupAnimations() {
			Context c = getApplicationContext();
			mMaxDim = mWidth /((70 - (mSizeMod * 20)) + 1);
			Log.d(TAG, "mMaxDim: " + mMaxDim);
			mNyanDroid = new NyanDroid(c, mMaxDim, mPaint, mDroidImage);

			// initialize Rainbow
			mMaxDim = (int) (mNyanDroid.getFrameHeight() * .4);
			mRainbow = new Rainbow(c, mMaxDim, mPaint, mRainbowImage);

			// remember offset for when drawing rainbows
			mRainbow.setOffset((mNyanDroid.getFrameWidth() / 2)
					- mRainbow.getFrameWidth());

			mStars = new Stars(c, mMaxDim, mPaint, mStarImage);
		}
		
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			Log.d(TAG, "onSurfaceDestroyed");
			mVisible = false;
			mDroidHandler.removeCallbacks(mDrawFrame);
		}

		private int frameCount;

		/**
		 * Draw a single animation frame.
		 */
		void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();

			if (mPreferencesChanged) {
				setupAnimations();
				mPreferencesChanged = false;
				//must reset centers
				hasSetup = false;
			}
			
			Canvas c = null;
			try {
				c = holder.lockCanvas();
				synchronized (holder) {
					frameCount++;
					if (c != null) {
						if (!hasSetup) {
							mRainbow.setCenter(c.getWidth() / 2,
									c.getHeight() / 2);
							mNyanDroid.setCenter(c.getWidth() / 2,
									c.getHeight() / 2);
							hasSetup = true;
						}

						c.drawColor(getResources().getColor(R.color.nyanblue));
						mStars.draw(c);
						if (frameCount == 3) {
							mRainbow.draw(c, true);
							mNyanDroid.draw(c, true);
						} else {
							mRainbow.draw(c, false);
							mNyanDroid.draw(c, false);
						}
					}
					frameCount %= 3;
				}
			} finally {
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			// Reschedule the next redraw
			mDroidHandler.removeCallbacks(mDrawFrame);
			if (mVisible) {
				// approx 30 fps
				mDroidHandler.postDelayed(mDrawFrame, 1000 / (mAnimationSpeed * 10));
			}
		}
	}
}