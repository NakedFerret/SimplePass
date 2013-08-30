package com.nakedferret.simplepass.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.EditText;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.AccountWGroup;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ServicePassword;
import com.nakedferret.simplepass.ServicePassword_;
import com.nakedferret.simplepass.Utils;
import com.nakedferret.simplepass.ui.FragCreateAccount.OnAccountCreatedListener;
import com.nakedferret.simplepass.ui.FragCreateVault.OnVaultCreatedListener;
import com.nakedferret.simplepass.ui.FragListCursor.OnItemSelectedListener;

@EActivity(R.layout.act_frag_test)
public class ActFragTest extends ActFloating implements OnItemSelectedListener,
		OnVaultCreatedListener, OnAccountCreatedListener {

	@Click(R.id.vaultButton)
	void showVaultsFragment() {
		int layout = android.R.layout.simple_list_item_1;
		Uri uri = Utils.buildContentUri(Vault.TABLE_NAME);
		String[] projection = { Vault.COL_NAME };
		int[] views = { android.R.id.text1 };
		String searchCol = Vault.COL_NAME;

		FragListCursor vaultsList = FragListCursor.newInstance(layout, uri,
				projection, views, searchCol);

		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, vaultsList);
		t.commit();
	}

	@Click(R.id.accountsButton)
	void showAccountsFragment() {
		int layout = R.layout.listview_account;
		Uri uri = Utils.buildContentUri(AccountWGroup.TABLE_NAME);
		String[] projection = { AccountWGroup.COL_NAME,
				AccountWGroup.COL_GROUP_NAME };
		int[] views = { android.R.id.text1, android.R.id.text2 };
		String searchCol = Account.COL_NAME;

		FragListCursor accountList = FragListCursor.newInstance(layout, uri,
				projection, views, searchCol);

		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, accountList);
		t.commit();
	}

	@Click(R.id.createVault)
	void onCreateVault() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		Fragment f = new FragCreateVault_();
		t.replace(R.id.fragmentContainer, f);
		t.commit();
	}

	@Click(R.id.createAccount)
	void onCreateAccount() {
		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		Fragment f = FragCreateAccount.newInstance(1);
		t.replace(R.id.fragmentContainer, f);
		t.commit();
	}

	@Override
	public void onItemSelected(Uri uri) {
		if (Vault.TABLE_NAME.equals(uri.getPathSegments().get(0))) {
			getPasswordAndAlertService(uri);

		}

	}

	@Override
	public void onVaultSelected(Uri uri) {

	}

	@Override
	public void onFragmentInteraction(Uri uri) {

	}

	// Quick method to get the password of a vault from an alertdialog
	private void getPasswordAndAlertService(final Uri uri) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Unlock Vault");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Unlock",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String pass = input.getText().toString();

						Intent i = new Intent(ActFragTest.this,
								ServicePassword_.class);
						i.setAction(ServicePassword.UNLOCK_VAULT);
						i.putExtra(ServicePassword.EXTRA_VAULT_PASSWORD, pass);
						i.putExtra(ServicePassword.EXTRA_VAULT_URI,
								uri.toString());
						startService(i);
						dialog.dismiss();
					}
				});

		alert.setNegativeButton("Cancel", null);
		alert.show();
	}
}
