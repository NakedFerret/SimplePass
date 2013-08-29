package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.nakedferret.simplepass.ServicePassword;

public class FragPasswordListener extends Fragment {

	public FragPasswordListener() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		LocalBroadcastManager m = LocalBroadcastManager.getInstance(activity);
		m.registerReceiver(passwordReceiver, new IntentFilter(
				ServicePassword.VAULT_LOCKED));
		m.registerReceiver(passwordReceiver, new IntentFilter(
				ServicePassword.VAULT_UNLOCKED));

	}

	private BroadcastReceiver passwordReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent i) {
			Uri vaultUri = null;
			byte[] vaultKey = null;
			byte[] vaultIv = null;

			String uriString = i
					.getStringExtra(ServicePassword.EXTRA_VAULT_URI);
			vaultUri = Uri.parse(uriString);

			if (ServicePassword.VAULT_LOCKED.equals(i.getAction())) {
				onVaultLocked(vaultUri);

			} else if (ServicePassword.VAULT_UNLOCKED.equals(i.getAction())) {

				vaultKey = i.getByteArrayExtra(ServicePassword.EXTRA_VAULT_KEY);
				vaultIv = i.getByteArrayExtra(ServicePassword.EXTRA_VAULT_IV);
				onVaultUnlocked(vaultUri, vaultKey, vaultIv);
			}
		}
	};

	void onVaultUnlocked(Uri vault, byte[] key, byte[] iv) {

	}

	void onVaultLocked(Uri vault) {

	}

}
