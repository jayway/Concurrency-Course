package com.sony;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LabReentrantLock {

	public static class SharedResource {
		private final Lock mLock = new ReentrantLock();
		private int mCount = 0;

		public void write() {
			mLock.lock();
			try {
				for (int i = 0; i < 1E5; i++) {
					mCount++;
				}
				System.out.println(mCount);
			} finally {
				mLock.unlock();
			}
		}

		public int read() {
			mLock.lock();
			try {
				for (int i = 0; i < mCount; i++) {
					Math.sin(i);
				}
				return mCount;
			} finally {
				mLock.unlock();
			}
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
