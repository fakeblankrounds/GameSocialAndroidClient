package com.fbrs.novl.social.sync;

import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.StatusUpdates;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

import com.fbrs.novl.social.R;

/**
 * Class for managing contacts sync related mOperations
 */
public class ContactManager {

	/**
	 * Custom IM protocol used when storing status messages.
	 */
	public static final String CUSTOM_IM_PROTOCOL = "SampleSyncAdapter";

	private static final String TAG = "ContactManager";

	/**
	 * Synchronize raw contacts
	 * 
	 * @param context The context of Authenticator Activity
	 * @param account The username for the account
	 * @param users The list of users
	 */
	public static synchronized void syncContacts(Context context, String account, List<User> users) {

		long userId;
		long rawContactId = 0;
		final ContentResolver resolver = context.getContentResolver();
		final BatchOperation batchOperation = new BatchOperation(context, resolver);
		Log.d(TAG, "In SyncContacts");
		for (final User user : users) {
			userId = user.getUserId();
			// Check to see if the contact needs to be inserted or updated
			rawContactId = lookupRawContact(resolver, userId);
			if (rawContactId != 0) {
				if (!user.isDeleted()) {
					// update contact
					updateContact(context, resolver, account, user, rawContactId, batchOperation);
				} else {
					// delete contact
					deleteContact(context, rawContactId, batchOperation);
				}
			} else {
				// add new contact
				Log.d(TAG, "In addContact");
				if (!user.isDeleted()) {
					addContact(context, account, user, batchOperation);
				}
			}
			// A sync adapter should batch operations on multiple contacts,
			// because it will make a dramatic performance difference.
			if (batchOperation.size() >= 50) {
				batchOperation.execute();
			}
		}
		batchOperation.execute();
	}

	/**
	 * Add a list of status messages to the contacts provider.
	 * 
	 * @param context the context to use
	 * @param accountName the username of the logged in user
	 * @param statuses the list of statuses to store
	 */
	/*    public static void insertStatuses(Context context, String username, List<User.Status> list) {

        final ContentValues values = new ContentValues();
        final ContentResolver resolver = context.getContentResolver();
        final BatchOperation batchOperation = new BatchOperation(context, resolver);
        for (final User.Status status : list) {
            // Look up the user's sample SyncAdapter data row
            final long userId = status.getUserId();
            final long profileId = lookupProfile(resolver, userId);
            // Insert the activity into the stream
            if (profileId > 0) {
                values.put(StatusUpdates.DATA_ID, profileId);
                values.put(StatusUpdates.STATUS, status.getStatus());
                values.put(StatusUpdates.PROTOCOL, Im.PROTOCOL_CUSTOM);
                values.put(StatusUpdates.CUSTOM_PROTOCOL, CUSTOM_IM_PROTOCOL);
                values.put(StatusUpdates.IM_ACCOUNT, username);
                values.put(StatusUpdates.IM_HANDLE, status.getUserId());
                values.put(StatusUpdates.STATUS_RES_PACKAGE, context.getPackageName());
                values.put(StatusUpdates.STATUS_ICON, R.drawable.icon);
                values.put(StatusUpdates.STATUS_LABEL, R.string.label);
                batchOperation.add(ContactOperations.newInsertCpo(StatusUpdates.CONTENT_URI, true)
                    .withValues(values).build());
                // A sync adapter should batch operations on multiple contacts,
                // because it will make a dramatic performance difference.
                if (batchOperation.size() >= 50) {
                    batchOperation.execute();
                }
            }
        }
        batchOperation.execute();
    }
	 */
	/**
	 * Adds a single contact to the platform contacts provider.
	 * 
	 * @param context the Authenticator Activity context
	 * @param accountName the account the contact belongs to
	 * @param user the sample SyncAdapter User object
	 */
	private static void addContact(Context context, String accountName, User user,
			BatchOperation batchOperation) {

		// Put the data in the contacts provider
		final ContactOperations contactOp =
			ContactOperations.createNewContact(context, user.getUserId(), accountName,
					batchOperation);
		contactOp.addUsername(user.getUserName());
	}

	/**
	 * Updates a single contact to the platform contacts provider.
	 * 
	 * @param context the Authenticator Activity context
	 * @param resolver the ContentResolver to use
	 * @param accountName the account the contact belongs to
	 * @param user the sample SyncAdapter contact object.
	 * @param rawContactId the unique Id for this rawContact in contacts
	 *        provider
	 */
	private static void updateContact(Context context, ContentResolver resolver,
			String accountName, User user, long rawContactId, BatchOperation batchOperation) {

		Uri uri;
		String Username = null;
		String online = null;
		final Cursor c =
			resolver.query(Data.CONTENT_URI, DataQuery.PROJECTION, DataQuery.SELECTION,
					new String[] {String.valueOf(rawContactId)}, null);
		final ContactOperations contactOp =
			ContactOperations.updateExistingContact(context, rawContactId, batchOperation);
		try {
			while (c.moveToNext()) {
				final long id = c.getLong(DataQuery.COLUMN_ID);
				final String mimeType = c.getString(DataQuery.COLUMN_MIMETYPE);
				uri = ContentUris.withAppendedId(Data.CONTENT_URI, id);
				if (mimeType.equals(NovlContactType.CONTENT_ITEM_TYPE)) {
					Username = c.getString(DataQuery.COLUMN_USERNAME);
					contactOp.updateName(uri, Username, user.getUserName());
				} else if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
					final int type = c.getInt(DataQuery.COLUMN_ONLINE);

					online = c.getString(DataQuery.COLUMN_ONLINE);
					contactOp.updateOnline(user.getOnline(), online, uri);
				}
			} // while
		} finally {
			c.close();
		}
		// Add the cell phone, if present and not updated above
		if (Username == null) {
			contactOp.addUsername(user.getUserName());
		}
		// Add the other phone, if present and not updated above
		if (online == null) {
			contactOp.addOnline(user.getOnline());
		}
	}

	/**
	 * Deletes a contact from the platform contacts provider.
	 * 
	 * @param context the Authenticator Activity context
	 * @param rawContactId the unique Id for this rawContact in contacts
	 *        provider
	 */
	private static void deleteContact(Context context, long rawContactId,
			BatchOperation batchOperation) {

		batchOperation.add(ContactOperations.newDeleteCpo(
				ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId), true).build());
	}

	/**
	 * Returns the RawContact id for a sample SyncAdapter contact, or 0 if the
	 * sample SyncAdapter user isn't found.
	 * 
	 * @param context the Authenticator Activity context
	 * @param userId the sample SyncAdapter user ID to lookup
	 * @return the RawContact id, or 0 if not found
	 */
	private static long lookupRawContact(ContentResolver resolver, long userId) {

		long authorId = 0;
		final Cursor c =
			resolver.query(RawContacts.CONTENT_URI, UserIdQuery.PROJECTION, UserIdQuery.SELECTION,
					new String[] {String.valueOf(userId)}, null);
		try {
			if (c.moveToFirst()) {
				authorId = c.getLong(UserIdQuery.COLUMN_ID);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return authorId;
	}

	/**
	 * Returns the Data id for a sample SyncAdapter contact's profile row, or 0
	 * if the sample SyncAdapter user isn't found.
	 * 
	 * @param resolver a content resolver
	 * @param userId the sample SyncAdapter user ID to lookup
	 * @return the profile Data row id, or 0 if not found
	 */
	private static long lookupProfile(ContentResolver resolver, long userId) {

		long profileId = 0;
		final Cursor c =
			resolver.query(Data.CONTENT_URI, ProfileQuery.PROJECTION, ProfileQuery.SELECTION,
					new String[] {String.valueOf(userId)}, null);
		try {
			if (c != null && c.moveToFirst()) {
				profileId = c.getLong(ProfileQuery.COLUMN_ID);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return profileId;
	}

	/**
	 * Constants for a query to find a contact given a sample SyncAdapter user
	 * ID.
	 */
	final private static class ProfileQuery {

		private ProfileQuery() {
		}

		public final static String[] PROJECTION = new String[] {Data._ID};

		public final static int COLUMN_ID = 0;

		public static final String SELECTION =
			Data.MIMETYPE + "='" + NovlSyncAdapterColumns.MIME_PROFILE + "' AND "
			+ NovlSyncAdapterColumns.DATA_PID + "=?";
	}

	/**
	 * Constants for a query to find a contact given a sample SyncAdapter user
	 * ID.
	 */
	final private static class UserIdQuery {

		private UserIdQuery() {
		}

		public final static String[] PROJECTION = new String[] {RawContacts._ID};

		public final static int COLUMN_ID = 0;

		public static final String SELECTION =
			RawContacts.ACCOUNT_TYPE + "='" + Constants.ACCOUNT_TYPE + "' AND "
			+ RawContacts.SOURCE_ID + "=?";
	}

	/**
	 * Constants for a query to get contact data for a given rawContactId
	 */
	final private static class DataQuery {

		private DataQuery() {
		}

		public static final String[] PROJECTION =
			new String[] {Data._ID, Data.MIMETYPE, Data.DATA1, Data.DATA2, Data.DATA3,};

		public static final int COLUMN_ID = 0;

		public static final int COLUMN_MIMETYPE = 1;

		public static final int COLUMN_DATA1 = 2;

		public static final int COLUMN_DATA2 = 3;

		public static final int COLUMN_DATA3 = 4;

		public static final int COLUMN_USERNAME = COLUMN_DATA1;

		// public static final int COLUMN_PHONE_TYPE = COLUMN_DATA2;

		public static final int COLUMN_ONLINE = COLUMN_DATA1;

		public static final int COLUMN_STATUS = COLUMN_DATA2;

		public static final int COLUMN_GAME = COLUMN_DATA3;

		//public static final int COLUMN_FAMILY_NAME = COLUMN_DATA3;

		public static final String SELECTION = Data.RAW_CONTACT_ID + "=?";
	}
}
