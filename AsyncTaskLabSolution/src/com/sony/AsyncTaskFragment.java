package com.sony;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReferenceArray;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

public class AsyncTaskFragment extends Fragment {

	private DownloadImagesTask mAsyncTask;
	private AsyncTaskLabFragmentActivity mActivity;
	private boolean isCanceled = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mAsyncTask = new DownloadImagesTask();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (AsyncTaskLabFragmentActivity) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mActivity = null;
	}

	private class DownloadImagesTask extends AsyncTask<String, Integer, Void> {

		private AtomicReferenceArray<Bitmap> mDownloadedBitmaps = new AtomicReferenceArray<Bitmap>(
				AsyncTaskLabFragmentActivity.DOWNLOAD_URLS.length);

		@Override
		protected Void doInBackground(String... urls) {
			System.out.println("doInBackground");
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
			if (mActivity != null) {
				mActivity.setImages(mDownloadedBitmaps);
				mActivity.mLoadButton.setEnabled(true);
			}

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (!isCancelled()) {
				super.onProgressUpdate(values);
				if (mActivity != null) {
					mActivity.mProgressBar.incrementProgressBy(1);
					mActivity.mLoadButton.setText(String.valueOf(values[0]));
					mActivity.setImages(mDownloadedBitmaps);
				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (mActivity != null) {
				mActivity.mLoadButton.setText("Cancelled");
				mActivity.mLoadButton.setEnabled(true);
			}
		}

		public AtomicReferenceArray<Bitmap> getBitmaps() {
			return mDownloadedBitmaps;
		}

	}

	public void execute(String[] urls) {
		mAsyncTask = new DownloadImagesTask();
		isCanceled = false;
		mAsyncTask.execute(urls);
	}	

	public AtomicReferenceArray<Bitmap> getBitmaps() {
		return mAsyncTask.getBitmaps();
	}
	
	public void cancel(boolean b) {
		isCanceled = true;
		mAsyncTask.cancel(b);
	}

	public boolean isCanceled() {
		return isCanceled;
	}

}
