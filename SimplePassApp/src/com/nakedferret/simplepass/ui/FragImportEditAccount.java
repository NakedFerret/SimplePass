package com.nakedferret.simplepass.ui;

import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ViewSwitcher;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.Category;
import com.nakedferret.simplepass.ImportManager;
import com.nakedferret.simplepass.ImportManager.ImportAccount;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.utils.Utils;

@EFragment(R.layout.frag_import_edit_account)
@OptionsMenu(R.menu.ac_frag_import_edit_account)
public class FragImportEditAccount extends Fragment implements
		LoaderCallbacks<Cursor> {

	@FragmentArg
	long selectedAccount;

	@Bean
	ImportManager importManager;

	@ViewById
	EditText nameInput, usernameInput, passwordInput, categoryInput;

	@ViewById
	Spinner categorySpinner;

	@ViewById
	ViewSwitcher categoryEditSwitcher;

	private ActImport activity;
	private SimpleCursorAdapter categoryAdapter;
	private Long similarCategoryID;

	public FragImportEditAccount() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		activity = (ActImport) a;
	}

	@AfterViews
	void init() {
		prepareCategorySpinner();
		getLoaderManager().initLoader(0, null, this);
	}

	private void prepareCategorySpinner() {
		String[] from = { "name" };
		int[] to = { android.R.id.text1 };

		categoryAdapter = new SimpleCursorAdapter(activity,
				android.R.layout.simple_spinner_item, null, from, to, 0);
		categoryAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(categoryAdapter);
	}

	@Click(R.id.editButton)
	void onEdit() {
		categoryEditSwitcher.showNext();
	}

	@Click(R.id.doneButton)
	void onDone() {
		categoryEditSwitcher.showNext();
	}

	@OptionsItem(R.id.action_confirm)
	void confirmChanges() {
		activity.finishAccountEdit();
	}

	@OptionsItem(R.id.action_discard)
	void discardChanges() {
		activity.finishAccountEdit();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri categoryUri = ContentProvider.createUri(Category.class, null);
		return new CursorLoader(activity, categoryUri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		categoryAdapter.changeCursor(data);
		findSimilarCategory();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		categoryAdapter.changeCursor(null);
	}

	@Background
	void findSimilarCategory() {
		ImportAccount a = importManager.getSelectedAccount(selectedAccount);
		Category cat = new Select().from(Category.class)
				.where("name = ? collate nocase", a.getCategory())
				.executeSingle();
		Utils.log(this, "category: " + cat);

		if (cat == null)
			return;

		similarCategoryID = cat.getId();

		for (int i = 0; i < categorySpinner.getCount(); i++) {
			if (categorySpinner.getItemIdAtPosition(i) == similarCategoryID) {
				Utils.log(this, "findSimilarCategory");
				setSpinnerSelection(i);
				break;
			}
		}
	}

	@UiThread
	void setSpinnerSelection(int categoryPosition) {
		categorySpinner.setSelection(categoryPosition);
	}

}
