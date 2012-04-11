package com.sony;

import java.util.concurrent.atomic.AtomicReferenceArray;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class AsyncTaskLabFragmentActivity extends Activity {
	private ImageView mImageView1, mImageView2, mImageView3, mImageView4;
	Button mLoadButton;
	private Button mCancelButton;
	ProgressBar mProgressBar;
	private AsyncTaskFragment mAsyncTaskFragment;

	static String[] DOWNLOAD_URLS = {
			"http://a.fsdn.com/sd/topics/moon_64.png",
			"http://a.fsdn.com/sd/topics/government_64.png",
			"http://a.fsdn.com/sd/topics/piracy_64.png",
			"http://a.fsdn.com/sd/topics/ai_64.png" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mImageView1 = (ImageView) findViewById(R.id.imageView1);
		mImageView2 = (ImageView) findViewById(R.id.imageView2);
		mImageView3 = (ImageView) findViewById(R.id.imageView3);
		mImageView4 = (ImageView) findViewById(R.id.imageView4);

		mLoadButton = (Button) findViewById(R.id.loadButton);
		mLoadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearImages();
				mProgressBar.setProgress(0);
				downloadImages();
			}
		});

		mCancelButton = (Button) findViewById(R.id.cancelButton);
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mAsyncTaskFragment != null) {
					mAsyncTaskFragment.cancel(false);
				}
			}
		});

		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		mProgressBar.setMax(DOWNLOAD_URLS.length);

		// Create the fragment or get retained images
		FragmentManager manager = getFragmentManager();
		mAsyncTaskFragment = (AsyncTaskFragment) manager
				.findFragmentByTag("asynctask");

		if (mAsyncTaskFragment == null) {
			FragmentTransaction transaction = manager.beginTransaction();
			mAsyncTaskFragment = new AsyncTaskFragment();
			transaction.add(mAsyncTaskFragment, "asynctask");
			transaction.commit();
		} else {
			setImages(mAsyncTaskFragment.getBitmaps());
			if (mAsyncTaskFragment.isCanceled()) {
				mLoadButton.setText("Canceled");
				mLoadButton.setEnabled(true);
			}
		}
	}

	public void downloadImages() {
		mLoadButton.setEnabled(false);
		mAsyncTaskFragment.execute(DOWNLOAD_URLS);
	}

	void setImages(AtomicReferenceArray<Bitmap> bitmaps) {
		mImageView1.setImageBitmap(bitmaps.get(0));
		mImageView2.setImageBitmap(bitmaps.get(1));
		mImageView3.setImageBitmap(bitmaps.get(2));
		mImageView4.setImageBitmap(bitmaps.get(3));
	}

	private void clearImages() {
		mImageView1.setImageBitmap(null);
		mImageView2.setImageBitmap(null);
		mImageView3.setImageBitmap(null);
		mImageView4.setImageBitmap(null);
	}

}