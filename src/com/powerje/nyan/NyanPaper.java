package com.powerje.nyan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class NyanPaper extends WallpaperService {

	private final Handler mDroidHandler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new NyanEngine();
	}

	class NyanEngine extends Engine {
		private final Paint mPaint = new Paint();

		private boolean mVisible;
		private boolean hasSetup = false;

		private NyanDroid mNyanDroid;
		private Rainbow mRainbow;
		private Stars mStars;

		private final Runnable mDrawFrame = new Runnable() {
			public void run() {
				drawFrame();
			}
		};

		NyanEngine() {
			mPaint.setColor(0xffffffff);
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

			hasSetup = false;
			int maxDim = width / 10;
			Context c = getApplicationContext();

			mNyanDroid = new NyanDroid(c, maxDim, mPaint);

			// initialize Rainbow
			maxDim = (int) (mNyanDroid.getFrameHeight() * .4);
			mRainbow = new Rainbow(c, maxDim, mPaint);

			// remember offset for when drawing rainbows
			mRainbow.setOffset((mNyanDroid.getFrameWidth() / 2)
					- mRainbow.getFrameWidth());

			mStars = new Stars(c, maxDim, mPaint);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mDroidHandler.removeCallbacks(mDrawFrame);
		}

		private int frameCount;

		/**
		 * Draw a single animation frame.
		 */
		void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();

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
				mDroidHandler.postDelayed(mDrawFrame, 1000 / 30);
			}
		}
	}
}