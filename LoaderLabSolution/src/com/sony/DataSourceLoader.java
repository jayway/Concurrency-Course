package com.sony;

import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class DataSourceLoader extends AsyncTaskLoader<List<String>> implements
		DataSourceChangedListener {

	private final DataSource source;

	public DataSourceLoader(Context context) {
		super(context);
		source = new DataSource(this);
		source.start();
	}

	@Override
	protected void onStartLoading() {
		forceLoad();
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
