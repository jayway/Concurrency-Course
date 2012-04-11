package com.sony;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReferenceArray;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class AsyncTaskLabActivity extends Activity {
	private ImageView mImageView1, mImageView2, mImageView3, mImageView4;
	private Button mLoadButton, mCancelButton;
	private ProgressBar mProgressBar;
	private DownloadImagesTask mLoadImageTask;

	private static String[] DOWNLOAD_URLS = {
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
				if (mLoadImageTask != null) {
					mLoadImageTask.cancel(false);
				}
			}
		});

		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		mProgressBar.setMax(DOWNLOAD_URLS.length);

		DataObject dataObject = (DataObject) getLastNonConfigurationInstance();

		if (dataObject != null) {
			mLoadImageTask = dataObject.task;
			if (mLoadImageTask != null) {
				mLoadImageTask.attach(this);
				if (mLoadImageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
					mLoadButton.setEnabled(false);
				}
				if (mLoadImageTask.isCancelled()) {
					mLoadButton.setEnabled(true);
				}
				setImages(mLoadImageTask.getBitmaps());
			}
		}
	}

	private static class DownloadImagesTask extends
			AsyncTask<String, Integer, Void> {
		private AsyncTaskLabActivity mActivity;
		private AtomicReferenceArray<Bitmap> mDownloadedBitmaps = new AtomicReferenceArray<Bitmap>(
				DOWNLOAD_URLS.length);

		public DownloadImagesTask(AsyncTaskLabActivity activity) {
			this.mActivity = activity;
		}

		@Override
		protected Void doInBackground(String... urls) {
			for (int i = 0; i < urls.length; i++) {
				if (!isCancelled()) {
					try {
						Bitmap bitmap = BitmapFactory
								.decodeStream((InputStream) new URL(urls[i])
										.getContent());
						mDownloadedBitmaps.set(i, bitmap);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					publishProgress(i);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mActivity.setImages(mDownloadedBitmaps);
			mActivity.mLoadButton.setEnabled(true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (!isCancelled()) {
				super.onProgressUpdate(values);
				mActivity.mProgressBar.incrementProgressBy(1);
				mActivity.mLoadButton.setText(String.valueOf(values[0]));
				mActivity.setImages(mDownloadedBitmaps);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mActivity.mLoadButton.setText("Canceled");
			mActivity.mLoadButton.setEnabled(true);
		}

		void detach() {
			mActivity = null;
		}

		void attach(AsyncTaskLabActivity activity) {
			this.mActivity = activity;
		}

		AtomicReferenceArray<Bitmap> getBitmaps() {
			return mDownloadedBitmaps;
		}

	}

	public void downloadImages() {
		mLoadButton.setEnabled(false);
		mLoadImageTask = new DownloadImagesTask(this);
		mLoadImageTask.execute(DOWNLOAD_URLS);
	}

	private void setImages(AtomicReferenceArray<Bitmap> bitmaps) {
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

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (mLoadImageTask != null) {
			mLoadImageTask.detach();
			DataObject dataObject = new DataObject();
			dataObject.task = mLoadImageTask;
			return dataObject;
		} else {
			return null;
		}
	}

	static private class DataObject {
		DownloadImagesTask task;
	}

}