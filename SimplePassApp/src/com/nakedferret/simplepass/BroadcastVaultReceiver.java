package com.nakedferret.simplepass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastVaultReceiver extends BroadcastReceiver {

	public interface OnVaultInteractionListerner {
		void onVaultUnlocked(Uri vault, byte[] key, byte[] iv);

		void onVaultLocked(Uri vault);
	}

	private OnVaultInteractionListerner listener;
	private Context c;

	public BroadcastVaultReceiver(Context c,
			OnVaultInteractionListerner listener) {
		register(c);
		this.listener = listener;
	}

	private void register(Context c) {
		LocalBroadcastManager m = LocalBroadcastManager.getInstance(c);
		m.registerReceiver(this, new IntentFilter(ServicePassword.VAULT_LOCKED));
		m.registerReceiver(this, new IntentFilter(
				ServicePassword.VAULT_UNLOCKED));
	}

	@Override
	public void onReceive(Context context, Intent i) {
		Uri vaultUri = null;
		byte[] vaultKey = null;
		byte[] vaultIv = null;

		String uriString = i.getStringExtra(ServicePassword.EXTRA_VAULT_URI);
		vaultUri = Uri.parse(uriString);

		if (ServicePassword.VAULT_LOCKED.equals(i.getAction())) {
			listener.onVaultLocked(vaultUri);

		} else if (ServicePassword.VAULT_UNLOCKED.equals(i.getAction())) {

			vaultKey = i.getByteArrayExtra(ServicePassword.EXTRA_VAULT_KEY);
			vaultIv = i.getByteArrayExtra(ServicePassword.EXTRA_VAULT_IV);
			listener.onVaultUnlocked(vaultUri, vaultKey, vaultIv);
		}
	}
}
