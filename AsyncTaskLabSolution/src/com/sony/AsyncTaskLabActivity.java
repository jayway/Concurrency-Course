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
	private ImageView imageView1, imageView2, imageView3, imageView4;
	private Button loadButton, cancelButton;
	private ProgressBar progressBar;
	private DownloadImagesTask loadImageTask;

	private static String[] urls = { "http://a.fsdn.com/sd/topics/moon_64.png",
			"http://a.fsdn.com/sd/topics/government_64.png",
			"http://a.fsdn.com/sd/topics/piracy_64.png",
			"http://a.fsdn.com/sd/topics/ai_64.png" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		imageView1 = (ImageView) findViewById(R.id.imageView1);
		imageView2 = (ImageView) findViewById(R.id.imageView2);
		imageView3 = (ImageView) findViewById(R.id.imageView3);
		imageView4 = (ImageView) findViewById(R.id.imageView4);

		loadButton = (Button) findViewById(R.id.loadButton);
		loadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearImages();
				progressBar.setProgress(0);
				downloadImages();
			}
		});

		cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(loadImageTask != null){
					loadImageTask.cancel(false);
				}
			}
		});

		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setMax(urls.length);

		DataObject dataObject = (DataObject) getLastNonConfigurationInstance();

		if (dataObject != null) {
			loadImageTask = dataObject.task;
			if (loadImageTask != null) {
				loadImageTask.attach(this);
				if (loadImageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
					loadButton.setEnabled(false);
				}
				if (loadImageTask.isCancelled()){
					loadButton.setEnabled(true);
				}
				setImages(loadImageTask.getBitmaps());
			}
		}
	}

	private static class DownloadImagesTask extends
			AsyncTask<String, Integer, Void> {
		private AsyncTaskLabActivity activity;
		private AtomicReferenceArray<Bitmap> downloadedBitmaps = new AtomicReferenceArray<Bitmap>(urls.length);

		public DownloadImagesTask(AsyncTaskLabActivity activity) {
			this.activity = activity;
		}

		@Override
		protected Void doInBackground(String... urls) {
			for (int i = 0; i < urls.length; i++) {
				if (!isCancelled()) {
					try {
						Bitmap bitmap = BitmapFactory
						.decodeStream((InputStream) new URL(urls[i])
								.getContent());
						downloadedBitmaps.set(i, bitmap);
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
			activity.setImages(downloadedBitmaps);
			activity.loadButton.setEnabled(true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (!isCancelled()) {
				super.onProgressUpdate(values);
				activity.progressBar.incrementProgressBy(1);
				activity.loadButton.setText(String.valueOf(values[0]));
				activity.setImages(downloadedBitmaps);
			}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			activity.loadButton.setText("Cancelled");
			activity.loadButton.setEnabled(true);
		}

		void detach() {
			activity = null;
		}

		void attach(AsyncTaskLabActivity activity) {
			this.activity = activity;
		}

		AtomicReferenceArray<Bitmap> getBitmaps() {
			return downloadedBitmaps;
		}
		
	}

	public void downloadImages() {
		loadButton.setEnabled(false);
		loadImageTask = new DownloadImagesTask(this);
		loadImageTask.execute(urls);
	}

	private void setImages(AtomicReferenceArray<Bitmap> bitmaps) {
		imageView1.setImageBitmap(bitmaps.get(0));
		imageView2.setImageBitmap(bitmaps.get(1));
		imageView3.setImageBitmap(bitmaps.get(2));
		imageView4.setImageBitmap(bitmaps.get(3));
	}

	private void clearImages() {
		imageView1.setImageBitmap(null);
		imageView2.setImageBitmap(null);
		imageView3.setImageBitmap(null);
		imageView4.setImageBitmap(null);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (loadImageTask != null) {
			loadImageTask.detach();
			DataObject dataObject = new DataObject();
			dataObject.task = loadImageTask;
			return dataObject;
		} else {
			return null;
		}
	}

	static private class DataObject {
		DownloadImagesTask task;
	}

}