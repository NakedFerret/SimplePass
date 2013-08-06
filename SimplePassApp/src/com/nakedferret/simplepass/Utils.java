package com.nakedferret.simplepass;

import android.net.Uri;

public class Utils {

	public static Uri buildContentUri(String table, long id) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.authority(PasswordStorageProvider.AUTHORITY);
		builder.appendPath(table);
		if (id > 0)
			builder.appendPath(Long.toString(id));

		return builder.build();
	}

	public static Uri buildContentUri(String table) {
		return buildContentUri(table, 0);
	}

}
