package com.sony.readwritelocklab;

import java.util.concurrent.Callable;

public class LabUnsynchronizedSkeleton {

	public static class SharedResource {
		private int mCount = 0;

		public void write() {
			for (int i = 0; i < 1E5; i++) {
				mCount++;
			}
			System.out.println(mCount);
		}

		public int read() {
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

		// TODO:
		// Create an Executor that uses a cached thread pool.
		// Instantiate 100 Readers and 10 Writers with the SharedResource and
		// add them all to a Set.

		long start = System.currentTimeMillis();

		// TODO:
		// Invoke all Readers and Writers in the Set

		System.out.println("execution time: "
				+ (System.currentTimeMillis() - start));

		// TODO:
		// Shut down the executor.
		// Await termination of all Writers and Readers.

	}
}
