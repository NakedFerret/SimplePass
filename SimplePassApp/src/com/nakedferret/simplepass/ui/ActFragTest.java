package com.nakedferret.simplepass.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.SystemService;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ServicePassword;
import com.nakedferret.simplepass.ServicePassword_;
import com.nakedferret.simplepass.Utils;
import com.nakedferret.simplepass.ui.BroadcastVaultReceiver.OnVaultInteractionListerner;
import com.nakedferret.simplepass.ui.FragCreateAccount.OnAccountCreatedListener;
import com.nakedferret.simplepass.ui.FragCreateVault.OnVaultCreatedListener;
import com.nakedferret.simplepass.ui.FragListAccount.OnAccountSelectedListener;
import com.nakedferret.simplepass.ui.FragListCursor.OnItemSelectedListener;

@EActivity(R.layout.act_frag_test)
public class ActFragTest extends ActFloating implements OnItemSelectedListener,
		OnVaultCreatedListener, OnAccountCreatedListener,
		OnVaultInteractionListerner, OnAccountSelectedListener {

	private BroadcastVaultReceiver receiver;

	private ProgressDialog dialog;

	@AfterViews
	void initializeInterface() {
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

	@Override
	public void onItemSelected(Uri uri) {
		// Vault was selected
		if (Vault.TABLE_NAME.equals(uri.getPathSegments().get(0))) {
			getPasswordAndAlertService(uri);
		}

	}

	@Override
	public void onVaultCreated(Uri uri) {

	}

	@Override
	public void onAccountCreated(Uri uri) {

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
						alertService(uri, pass);
						dialog.dismiss();
					}
				});

		alert.setNegativeButton("Cancel", null);
		alert.show();
	}

	private void alertService(Uri uri, String pass) {
		Intent i = new Intent(ActFragTest.this, ServicePassword_.class);
		i.setAction(ServicePassword.UNLOCK_VAULT);
		i.putExtra(ServicePassword.EXTRA_VAULT_PASSWORD, pass);
		i.putExtra(ServicePassword.EXTRA_VAULT_URI, uri.toString());
		startService(i);
		// Listen for the service
		receiver = new BroadcastVaultReceiver(ActFragTest.this,
				ActFragTest.this);

		// Show dialog to wait
		dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.show();
	}

	@Override
	public void onVaultUnlocked(Uri vault, byte[] key, byte[] iv) {
		dialog.dismiss();
		Fragment accountListFragment = FragListAccount.newInstance(vault);

		FragmentManager m = getSupportFragmentManager();
		FragmentTransaction t = m.beginTransaction();
		t.replace(R.id.fragmentContainer, accountListFragment);
		t.commit();
	}

	@Override
	public void onVaultLocked(Uri vault) {
		dialog.dismiss();
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Wrong Password");
		alert.setPositiveButton("Ok", null);
		alert.show();
	}

	@Override
	public void onAccountSelected(Uri uri) {
		// TODO Auto-generated method stub

	}
}
