package com.powerje.nyan;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class NyanActivity extends Activity {
	private NyanView mRoot = null;
	private MediaPlayer mPlayer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		// scale by whichever is larger, width or height
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int h = metrics.heightPixels;
		int w = metrics.widthPixels;
		int largest = h > w ? h : w;

		mRoot = new NyanView(this, largest);
		setContentView(mRoot);
	}

	@Override
	public void onResume() {
		super.onResume();
		mPlayer = MediaPlayer.create(this, R.raw.dyan_loop);
		mPlayer.setLooping(true);
		mPlayer.setScreenOnWhilePlaying(true);

		try {
			mPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
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
	public void onStop() {
		super.onStop();
		mPlayer.release();
		mPlayer = null;
	}

	@Override
	public void onPause() {
		super.onPause();
		mPlayer.pause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mRoot = null;
	}

}