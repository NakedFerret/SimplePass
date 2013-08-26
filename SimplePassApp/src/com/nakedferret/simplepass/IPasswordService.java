package com.nakedferret.simplepass;

import android.net.Uri;

interface IPasswordService {
	void registerListener(IPasswordListener listener);

	void unregisterListener(IPasswordListener listener);

	void getPassword(Uri vault);
}
