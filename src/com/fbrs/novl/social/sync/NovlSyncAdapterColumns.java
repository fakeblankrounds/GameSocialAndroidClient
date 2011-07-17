package com.fbrs.novl.social.sync;

import android.provider.ContactsContract.Data;

public class NovlSyncAdapterColumns {
	
	private NovlSyncAdapterColumns() {
	}

	/**
	 * MIME-type used when storing a profile {@link Data} entry.
	 */
	public static final String MIME_PROFILE =
		"com.fbrs.novl/com.fbrs.novl.profile";

	public static final String DATA_PID = Data.DATA1;

	public static final String DATA_SUMMARY = Data.DATA2;

	public static final String DATA_DETAIL = Data.DATA3;
}
