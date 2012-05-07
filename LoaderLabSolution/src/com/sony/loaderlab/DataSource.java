package com.sony.loaderlab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataSource {

	private final Random random = new Random();

	private final List<String> mList = Collections
			.synchronizedList(new ArrayList<String>());

	private final ScheduledExecutorService mExecutor = Executors
			.newScheduledThreadPool(3);

	private DataSourceChangedListener mListener;

	private final Runnable mPutTask = new Runnable() {

		@Override
		public void run() {
			mList.add("Random: " + random.nextInt(100));
			System.out.println(mList);
			if (mListener != null) {
				mListener.onDataSourceChanged();
			}
		}

	};

	public DataSource(DataSourceChangedListener listener) {
		this.mListener = listener;
	}

	public void startSource() {
		mExecutor.scheduleAtFixedRate(mPutTask, 1, 5, TimeUnit.SECONDS);
	}

	public void stopSource() {
		mExecutor.shutdown();
	}

	public List<String> getData() {
		try {
			Thread.sleep(random.nextInt(3000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Collections.unmodifiableList(mList);

	}

}
