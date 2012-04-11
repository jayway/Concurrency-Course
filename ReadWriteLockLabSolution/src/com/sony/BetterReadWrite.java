package com.sony;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BetterReadWrite {

	public static class SharedResource {
		private final ReadWriteLock mLock = new ReentrantReadWriteLock();		
		private int mCount = 0;

		public void write() {
			mLock.writeLock().lock();
			try {				
				mCount++;
			} finally {
				mLock.writeLock().unlock();
			}
		}

		public int read() {
			mLock.readLock().lock();
			
			try {
				for (int i = 0; i < mCount * 1E5; i++) {
					Math.sin(i);
				}
				return mCount;
			} finally {
				mLock.readLock().unlock();
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
			while(mData.read() < 10) {
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
			callables.add(new BetterReadWrite.Reader(data));
		}

		for (int i = 0; i < 10; i++) {
			callables.add(new BetterReadWrite.Writer(data));
		}

		try {
			long start = System.currentTimeMillis();
			executor.invokeAll(callables);
			System.out.println("execution time: " + (System.currentTimeMillis() - start));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		executor.shutdown();

	}

}
