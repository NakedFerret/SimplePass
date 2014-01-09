package com.nakedferret.simplepass;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class MultiUriCursorLoader extends CursorLoader {

	final ForceLoadContentObserver mObserver;

	public MultiUriCursorLoader(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		super(context, uri, projection, selection, selectionArgs, sortOrder);
		mObserver = new ForceLoadContentObserver();
	}

	public void addUri(Uri uri) {
		getContext().getContentResolver().registerContentObserver(uri, true,
				mObserver);
	}

}
