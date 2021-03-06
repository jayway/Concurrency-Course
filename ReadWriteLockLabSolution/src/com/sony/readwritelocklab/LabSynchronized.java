package com.sony.readwritelocklab;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LabSynchronized {

	public static class SharedResource {

		private int mCount = 0;

		public synchronized void write() {

			for (int i = 0; i < 1E5; i++) {
				mCount++;
			}
			System.out.println(mCount);

		}

		public synchronized int read() {

			for (int i = 0; i < mCount; i++) {
				Math.sin(i);
			}
			return mCount;

		}
	}

	public static class Reader implements Callable<Void> {
		private SharedResource mData;

		public Reader(SharedResource data) {
			this.mData = data;
		}

		@Override
		public Void call() throws Exception {
			while (mData.read() < 1E6) {
				Thread.sleep(1);
			}
			return null;
		}
	}

	public static class Writer implements Callable<Void> {
		private SharedResource mData;

		public Writer(SharedResource data) {
			this.mData = data;
		}

		@Override
		public Void call() throws Exception {
			mData.write();
			return null;
		}
	}

	public static void main(String[] args) {
		SharedResource data = new SharedResource();
		ExecutorService executor = Executors.newCachedThreadPool();

		Set<Callable<Void>> callables = new HashSet<Callable<Void>>();

		for (int i = 0; i < 100; i++) {
			callables.add(new Reader(data));
		}

		for (int i = 0; i < 10; i++) {
			callables.add(new Writer(data));
		}

		try {
			long start = System.currentTimeMillis();
			executor.invokeAll(callables);
			System.out.println("execution time: "
					+ (System.currentTimeMillis() - start));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		executor.shutdown();

		try {
			executor.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
