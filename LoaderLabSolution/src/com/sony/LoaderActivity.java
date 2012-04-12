package com.sony;

import java.util.List;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class LoaderActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<List<String>> {

	private ArrayAdapter<String> mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getLoaderManager().initLoader(0, null, this);

		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		
		setListAdapter(mAdapter);
	}

	@Override
	public Loader<List<String>> onCreateLoader(int id, Bundle args) {
		return new DataSourceLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
		mAdapter.clear();
		mAdapter.addAll(data);
	}

	@Override
	public void onLoaderReset(Loader<List<String>> loader) {
		mAdapter.clear();
	}

}