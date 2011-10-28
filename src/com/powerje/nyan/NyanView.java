package com.powerje.nyan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
public class NyanView extends SurfaceView implements SurfaceHolder.Callback {

	/** Paint to draw with. */
	private final Paint mPaint = new Paint();
	/** True iff dimensions have been setup. */
	private boolean hasSetup;

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
		getHolder().addCallback(this);

		hasSetup = false;
		int maxDim = scaleBy / 10;
		Context c = mContext;

		// initialize Neandroid
		mNyanDroid = new NyanDroid(c, maxDim, mPaint, "nyanwich");

		// initialize Rainbow
		maxDim = (int) (mNyanDroid.getFrameHeight() * .4);
		mRainbow = new Rainbow(c, maxDim, mPaint, "neapolitan");

		// remember offset for when drawing rainbows
		mRainbow.setOffset((mNyanDroid.getFrameWidth() / 2)
				- mRainbow.getFrameWidth());

		mStars = new Stars(c, maxDim, mPaint, "white");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mThread = new DrawingThread(getHolder(), this);
		mThread.setRunning(true);
		mThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
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

	/**
	 * Draw a single animation frame.
	 */
	public void onDraw(Canvas c) {
		frameCount++;
		if (c != null) {
			if (!hasSetup) {
				mRainbow.setCenter(c.getWidth() / 2, c.getHeight() / 2);
				mNyanDroid.setCenter(c.getWidth() / 2, c.getHeight() / 2);
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

	public class DrawingThread extends Thread {

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
						myThreadSurfaceView.onDraw(c);
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