package com.nakedferret.simplepass.ui;

import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ViewSwitcher;

import com.activeandroid.query.Select;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.Category;
import com.nakedferret.simplepass.ImportManager;
import com.nakedferret.simplepass.ImportManager.ImportAccount;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.utils.Utils;

@EFragment(R.layout.frag_import_edit_account)
@OptionsMenu(R.menu.ac_frag_import_edit_account)
public class FragImportEditAccount extends Fragment {

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

	ActImport activity;

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
		ImportAccount a = importManager.getSelectedAccount(selectedAccount);
		Utils.log(this, "account category: " + a.getCategory());
		List<Category> list = new Select().from(Category.class)
				.where("name = ? collate nocase", a.getCategory()).execute();
		Utils.log(this, "similar groups: " + list.size());
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

}
