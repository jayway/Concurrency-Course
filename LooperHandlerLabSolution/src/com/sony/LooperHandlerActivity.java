package com.sony;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

public class LooperHandlerActivity extends Activity {

	private final static int SHOW_PROGRESS = 1;
	private final static int HIDE_PROGRESS = 0;
	private Worker mWorker;
	private Button mButton;
	private ProgressBar mProgress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mWorker = new Worker();
		mWorker.start();

		mProgress = (ProgressBar) findViewById(R.id.progress);

		mButton = (Button) findViewById(R.id.button1);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mWorker.doWork();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWorker.exit();
	}

	private final Handler mUiHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == SHOW_PROGRESS) {
				mProgress.setVisibility(View.VISIBLE);
			} else {
				mButton.setText(String.valueOf(msg.arg1));
				mProgress.setVisibility(View.INVISIBLE);
			}
		}
	};

	private class Worker extends Thread {

		private Handler mWorkerHandler;

		public void run() {
			Looper.prepare();

			mWorkerHandler = new Handler() {

				public void handleMessage(Message msg) {
					Message uiMsg = mUiHandler.obtainMessage(SHOW_PROGRESS, 0,
							0, null);
					mUiHandler.sendMessage(uiMsg);

					Random r = new Random();
					int randomInt = r.nextInt(100);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					uiMsg = mUiHandler.obtainMessage(HIDE_PROGRESS, randomInt,
							0, null);
					mUiHandler.sendMessage(uiMsg);
				}
			};

			Looper.loop();
		}

		public void exit() {
			mWorkerHandler.getLooper().quit();
		}

		public void doWork() {
			mWorkerHandler.sendEmptyMessage(0);
		}
	}

}