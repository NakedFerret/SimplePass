package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.ImportManager;
import com.nakedferret.simplepass.ImportManager.ImportAccount;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Vault;
import com.nakedferret.simplepass.ui.BeanAdapter.ListItemBinder;
import com.nakedferret.simplepass.utils.ViewHolder;

@EFragment(R.layout.frag_import_modify_and_save)
public class FragImportModifyAndSave extends Fragment implements
		OnClickListener, LoaderCallbacks<Cursor>, OnItemClickListener {

	@ViewById(android.R.id.list)
	ListView accountList;

	@Bean
	ImportManager importManager;

	private SimpleCursorAdapter vaultAdapter;
	private BeanAdapter<ImportAccount> accountAdapter;
	private AlertDialog dialog;
	private ImportAccountBinder binder = new ImportAccountBinder();
	private ActImport activity;
	private Long selectedVault;

	public FragImportModifyAndSave() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (ActImport) activity;
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
		accountAdapter = new BeanAdapter<ImportAccount>(getActivity(), binder);
		accountAdapter.addAll(importManager.getSelectedAccounts());
		accountList.setAdapter(accountAdapter);
		accountList.setOnItemClickListener(this);
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

	public static class ImportAccountBinder implements
			ListItemBinder<ImportAccount>, OnTouchListener {

		@Override
		public View newView(Context c, View convertView, ViewGroup parent) {
			return LayoutInflater.from(c).inflate(
					R.layout.listitem_mockaccount, parent, false);
		}

		@Override
		public View bindView(Context c, View v, int position, ImportAccount bean) {
			TextView text1 = ViewHolder.get(v, R.id.text1);
			TextView text2 = ViewHolder.get(v, R.id.text2);
			ImageButton showInfoButton = ViewHolder.get(v, R.id.showInfoButton);

			text1.setText(bean.getName() + "\n" + bean.getCategory());
			text2.setText(bean.getUsername() + "\n" + bean.getPassword());
			showInfoButton.setOnTouchListener(this);

			return v;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			View p;
			TextView text2;

			switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				p = (View) v.getParent();
				text2 = ViewHolder.get(p, R.id.text2);
				text2.setVisibility(View.VISIBLE);
				return true;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				p = (View) v.getParent();
				text2 = ViewHolder.get(p, R.id.text2);
				text2.setVisibility(View.INVISIBLE);
				return true;
			default:
				return false;
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		activity.editImportAccount(id);
	}

}
