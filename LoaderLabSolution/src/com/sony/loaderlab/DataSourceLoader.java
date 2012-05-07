package com.sony.loaderlab;

import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class DataSourceLoader extends AsyncTaskLoader<List<String>> implements
		DataSourceChangedListener {

	private final DataSource source;

	public DataSourceLoader(Context context) {
		super(context);
		source = new DataSource(this);
	}

	@Override
	protected void onStartLoading() {
		source.startSource();
		forceLoad();
	}
	
	@Override
	protected void onStopLoading() {
		source.stopSource();
	}

	@Override
	public void onDataSourceChanged() {
		onContentChanged();
	}

	/**
	 * This is the long running operation
	 */
	@Override
	public List<String> loadInBackground() {
		return source.getData();
	}
}
