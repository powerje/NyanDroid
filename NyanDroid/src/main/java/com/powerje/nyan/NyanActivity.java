package com.powerje.nyan;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import java.io.IOException;

public class NyanActivity extends Activity {
	private NyanView mRoot = null;
	private MediaPlayer mPlayer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
            getActionBar().setBackgroundDrawable(null);
        }
	}

	public void onResume() {
		super.onResume();
        // scale by whichever is larger, width or height
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int h = metrics.heightPixels;
        int w = metrics.widthPixels;
        int largest = h > w ? h : w;

        mRoot = new NyanView(this, largest);
        setContentView(mRoot);

        if (mPlayer == null) {
		    mPlayer = MediaPlayer.create(this, R.raw.dyan_loop);
		    mPlayer.setLooping(true);
        } else {
            mPlayer.start();
        }

		mPlayer.setScreenOnWhilePlaying(true);

		try {
			mPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				mPlayer.start();
			}
		});
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(NyanActivity.this, NyanSettings.class));
			break;
		}
		return true;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		mPlayer.pause();
	}

    public void onStop() {
        super.onStop();
        mRoot.cancel();
        mRoot = null;
        System.gc();
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		mRoot = null;
	}

}