package com.nakedferret.simplepass.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.content.ContentProvider;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Vault;
import com.nakedferret.simplepass.utils.Utils;

@EFragment
@OptionsMenu(R.menu.frag_list_vault)
public class FragListVault extends ListFragment implements OnItemClickListener,
		LoaderCallbacks<Cursor> {

	private static Uri URI = null;
	private Object listener;
	private SimpleCursorAdapter adapter;
	// Interfaces the listener implements
	private static final Class[] LINTERFACES = new Class[] {
			LIRequestCreateVault.class, LIVaultSelection.class };

	public FragListVault() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = Utils.proxyListener(activity, LINTERFACES);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IFragListener");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		URI = ContentProvider.createUri(Vault.class, null);

		setListShown(false);
		setEmptyText(getText(R.string.no_vaults_message));

		adapter = getAdapter();
		getListView().setOnItemClickListener(this);

		setActionMode();
		getLoaderManager().initLoader(0, null, this);
	}

	private void setActionMode() {
		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			private List<Long> selectedItems = new ArrayList<Long>();

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.action_delete_vault:
					deleteVaults(selectedItems, mode);
				default:
					return false;
				}

			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.ac_frag_list_vault, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {

			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {

				if (checked)
					selectedItems.add(id);
				else
					selectedItems.remove(id);
			}
		});
	}

	@Background
	void deleteVaults(List<Long> selectedItems, ActionMode mode) {
		ActiveAndroid.beginTransaction();
		for (Long l : selectedItems) {
			Vault.delete(Vault.class, l);
		}
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
		exitMode(mode);
	}

	@UiThread
	void exitMode(ActionMode mode) {
		mode.finish();
	}

	private SimpleCursorAdapter getAdapter() {
		final int LAYOUT = android.R.layout.simple_list_item_activated_1;
		final String[] PROJECTION = { "name" };
		final int[] VIEWS = { android.R.id.text1 };

		return new SimpleCursorAdapter(getActivity(), LAYOUT, null, PROJECTION,
				VIEWS, 0);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.frag_list_vault_title);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		setListShown(true);
		setListAdapter(adapter);
		adapter.changeCursor(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.changeCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
		if (listener == null)
			return;

		((LIVaultSelection) listener).onVaultSelected(id);
	}

	@OptionsItem(R.id.action_add_vault)
	void actionAddVault() {
		((LIRequestCreateVault) listener).requestCreateVault();
	}

	public interface LIRequestCreateVault {
		void requestCreateVault();
	}

	public interface LIVaultSelection {
		void onVaultSelected(Long id);
	}

}
