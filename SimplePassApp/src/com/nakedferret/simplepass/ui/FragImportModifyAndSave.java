package com.nakedferret.simplepass.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.activeandroid.content.ContentProvider;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.ImportManager;
import com.nakedferret.simplepass.ImportManager.MockAccount;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Vault;

@EFragment(R.layout.frag_import_modify_and_save)
public class FragImportModifyAndSave extends Fragment implements
		OnClickListener, LoaderCallbacks<Cursor> {

	@ViewById(android.R.id.list)
	ListView accountList;

	@Bean
	ImportManager importManager;

	private SimpleCursorAdapter vaultAdapter;
	private ArrayAdapter<MockAccount> accountAdapter;
	private AlertDialog dialog;
	private Long selectedVault;

	public FragImportModifyAndSave() {
		// Required empty public constructor
	}

	@AfterViews
	void init() {
		getLoaderManager().initLoader(0, null, this);
		vaultAdapter = getVaultAdapter();
	}

	private SimpleCursorAdapter getVaultAdapter() {
		final String[] from = new String[] { "name" };
		final int[] to = new int[] { android.R.id.text1 };

		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				getActivity(), android.R.layout.simple_list_item_1, null, from,
				to, 0);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

	@Click(R.id.chooseVaultButton)
	void onChooseVault() {
		// Show dialog to choose vault
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		b.setTitle(R.string.choose_a_vault);
		b.setAdapter(vaultAdapter, this);
		dialog = b.create();
		dialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int position) {
		selectedVault = vaultAdapter.getItemId(position);
		accountAdapter = new MockAccountAdapter(getActivity());
		accountAdapter.addAll(importManager.getSelectedAccounts());
		accountList.setAdapter(accountAdapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri vaultUri = ContentProvider.createUri(Vault.class, null);
		return new CursorLoader(getActivity(), vaultUri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		vaultAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		vaultAdapter.changeCursor(null);
	}

}
