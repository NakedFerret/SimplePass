package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
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
public class FragImportEditAccount extends Fragment implements OnTouchListener {

	@FragmentArg
	long selectedAccountID;

	@Bean
	ImportManager importManager;

	@ViewById
	EditText nameInput, usernameInput, passwordInput, categoryInput;

	@ViewById
	ImageButton showUsernameButton, showPasswordButton;

	@ViewById
	TextView progressMessage;

	@ViewById
	ProgressBar progress;

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
		setProgressState(true);
		checkForSimilarCategory(categoryInput.getText().toString());
	}

	@OptionsItem(R.id.action_discard)
	void discardChanges() {
		activity.finishAccountEdit();
	}

	@Background
	void checkForSimilarCategory(String category) {
		Category c = new Select()
				.from(Category.class)
				.where("name = ? collate nocase and name != ?", category,
						category).executeSingle();

		Utils.log(this, "checking for  similar category..");
		if (c != null)
			onSimilarCategoryFound(c);
		else
			finishAccountEdit(null);
	}

	@UiThread
	void onSimilarCategoryFound(final Category c) {
		final String savedCategory = c.name;
		final String newCategory = categoryInput.getText().toString();

		String messageFormat = getString(R.string.merge_category_message);
		String message = String.format(messageFormat, newCategory,
				savedCategory);

		AlertDialog.Builder b = new AlertDialog.Builder(activity);
		b.setTitle(R.string.merge_category_title);
		b.setMessage(message);
		b.setNegativeButton(newCategory, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finishAccountEdit(null);
			}
		});
		b.setPositiveButton(savedCategory, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finishAccountEdit(savedCategory);
			}
		});
		b.setCancelable(false);
		b.show();
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

	private void finishAccountEdit(String category) {
		Utils.log(this, "similar category found..");

		selectedAccount.name = nameInput.getText().toString();
		selectedAccount.username = usernameInput.getText().toString();
		selectedAccount.password = passwordInput.getText().toString();
		selectedAccount.category = (category != null) ? category
				: categoryInput.getText().toString();

		activity.finishAccountEdit();
	}

	private void setProgressState(boolean showingProgress) {
		nameInput.setEnabled(!showingProgress);
		usernameInput.setEnabled(!showingProgress);
		passwordInput.setEnabled(!showingProgress);
		categoryInput.setEnabled(!showingProgress);

		showUsernameButton.setEnabled(!showingProgress);
		showPasswordButton.setEnabled(!showingProgress);

		int visibility = (showingProgress) ? View.VISIBLE : View.INVISIBLE;
		progress.setVisibility(visibility);
		progressMessage.setVisibility(visibility);
	}
}
