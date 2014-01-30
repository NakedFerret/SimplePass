package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.ImportManager;
import com.nakedferret.simplepass.ImportManager.ImportAccount;
import com.nakedferret.simplepass.R;

@EFragment(R.layout.frag_import_edit_account)
@OptionsMenu(R.menu.ac_frag_import_edit_account)
public class FragImportEditAccount extends Fragment implements OnTouchListener {

	@FragmentArg
	long selectedAccountID;

	@Bean
	ImportManager importManager;

	@ViewById
	EditText nameInput, usernameInput, passwordInput, categoryInput;

	@ViewById
	ImageButton showUsernameButton, showPasswordButton;

	private ActImport activity;
	private ImportAccount selectedAccount;
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
		selectedAccount = importManager.getSelectedAccount(selectedAccountID);

		// Set name, username, and password to the EditText's
		nameInput.setText(selectedAccount.name);
		usernameInput.setText(selectedAccount.username);
		passwordInput.setText(selectedAccount.password);
		categoryInput.setText(selectedAccount.category);

		showUsernameButton.setOnTouchListener(this);
		showPasswordButton.setOnTouchListener(this);
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
	public boolean onTouch(View v, MotionEvent e) {

		EditText input = (v.getId() == showPasswordButton.getId()) ? passwordInput
				: usernameInput;

		switch (e.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			input.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_NORMAL);
			return true;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			input.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			return true;
		default:
			return false;
		}
	}
}
