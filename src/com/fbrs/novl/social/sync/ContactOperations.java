package com.fbrs.novl.social.sync;


import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.SyncStateContract.Constants;
import android.text.TextUtils;
import android.util.Log;

import com.fbrs.novl.social.R;



/**
 * Helper class for storing data in the platform content providers.
 */
public class ContactOperations {

    private final ContentValues mValues;

    private ContentProviderOperation.Builder mBuilder;

    private final BatchOperation mBatchOperation;

    private final Context mContext;

    private boolean mYield;

    private long mRawContactId;

    private int mBackReference;

    private boolean mIsNewContact;

    /**
     * Returns an instance of ContactOperations instance for adding new contact
     * to the platform contacts provider.
     * 
     * @param context the Authenticator Activity context
     * @param userId the userId of the sample SyncAdapter user object
     * @param accountName the username of the current login
     * @return instance of ContactOperations
     */
    public static ContactOperations createNewContact(Context context, int userId,
        String accountName, BatchOperation batchOperation) {

        return new ContactOperations(context, userId, accountName, batchOperation);
    }

    /**
     * Returns an instance of ContactOperations for updating existing contact in
     * the platform contacts provider.
     * 
     * @param context the Authenticator Activity context
     * @param rawContactId the unique Id of the existing rawContact
     * @return instance of ContactOperations
     */
    public static ContactOperations updateExistingContact(Context context, long rawContactId,
        BatchOperation batchOperation) {

        return new ContactOperations(context, rawContactId, batchOperation);
    }

    public ContactOperations(Context context, BatchOperation batchOperation) {
        mValues = new ContentValues();
        mYield = true;
        mContext = context;
        mBatchOperation = batchOperation;
    }

    public ContactOperations(Context context, int userId, String accountName,
        BatchOperation batchOperation) {

        this(context, batchOperation);
        mBackReference = mBatchOperation.size();
        mIsNewContact = true;
        mValues.put(RawContacts.SOURCE_ID, userId);
        mValues.put(RawContacts.ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
        mValues.put(RawContacts.ACCOUNT_NAME, accountName);
        mBuilder = newInsertCpo(RawContacts.CONTENT_URI, true).withValues(mValues);
        mBatchOperation.add(mBuilder.build());
    }

    public ContactOperations(Context context, long rawContactId, BatchOperation batchOperation) {
        this(context, batchOperation);
        mIsNewContact = false;
        mRawContactId = rawContactId;
    }

    /**
     * Adds a Username
     * 
     * @param phone new phone number for the contact
     * @param phoneType the type: cell, home, etc.
     * @return instance of ContactOperations
     */
    public ContactOperations addUsername(String Username) {
        mValues.clear();
        if (!TextUtils.isEmpty(Username)) {
            mValues.put(NovlContactType.USERNAME, Username);
            mValues.put(NovlContactType.MIMETYPE, NovlContactType.CONTENT_ITEM_TYPE);
            addInsertOp();
        }
        return this;
    }
    
    public ContactOperations addOnline(String online) {
        mValues.clear();
        if (!TextUtils.isEmpty(online)) {
            mValues.put(NovlContactType.ONLINE, online);
            mValues.put(NovlContactType.MIMETYPE, NovlContactType.CONTENT_ITEM_TYPE);
            addInsertOp();
        }
        return this;
    }

    /**
     * Adds a profile action
     * 
     * @param userId the userId of the sample SyncAdapter user object
     * @return instance of ContactOperations
     */
    public ContactOperations addProfileAction(long userId) {
        mValues.clear();
        if (userId != 0) {
            mValues.put(NovlSyncAdapterColumns.DATA_PID, userId);
            mValues.put(NovlSyncAdapterColumns.DATA_SUMMARY, mContext
                .getString(R.string.profile_action));
            mValues.put(NovlSyncAdapterColumns.DATA_DETAIL, mContext
                .getString(R.string.view_profile));
            mValues.put(Data.MIMETYPE, NovlSyncAdapterColumns.MIME_PROFILE);
            addInsertOp();
        }
        return this;
    }

    /**
     * Updates contact's email
     * 
     * @param email email id of the sample SyncAdapter user
     * @param uri Uri for the existing raw contact to be updated
     * @return instance of ContactOperations
     */
    public ContactOperations updateOnline(String online, String existingEmail, Uri uri) {
        if (!TextUtils.equals(existingEmail, online)) {
            mValues.clear();
            mValues.put(NovlContactType.ONLINE, online);
            addUpdateOp(uri);
        }
        return this;
    }

    /**
     * Updates contact's name
     * 
     * @param name Name of contact
     * @param existingName Name of contact stored in provider
     * @param nameType type of name: family name, given name, etc.
     * @param uri Uri for the existing raw contact to be updated
     * @return instance of ContactOperations
     */
    public ContactOperations updateName(Uri uri, String existingUsername, String Username) {

        Log.i("ContactOperations", "ef=" + existingUsername + "f="
            + Username);
        mValues.clear();
        if (!TextUtils.equals(existingUsername, Username)) {
            mValues.put(NovlContactType.USERNAME, Username);
        }
        if (mValues.size() > 0) {
            addUpdateOp(uri);
        }
        return this;
    }

    /**
     * Updates contact's profile action
     * 
     * @param userId sample SyncAdapter user id
     * @param uri Uri for the existing raw contact to be updated
     * @return instance of ContactOperations
     */
    public ContactOperations updateProfileAction(Integer userId, Uri uri) {
        mValues.clear();
        mValues.put(NovlSyncAdapterColumns.DATA_PID, userId);
        addUpdateOp(uri);
        return this;
    }

    /**
     * Adds an insert operation into the batch
     */
    private void addInsertOp() {

        if (!mIsNewContact) {
            mValues.put(Phone.RAW_CONTACT_ID, mRawContactId);
        }
        mBuilder = newInsertCpo(addCallerIsSyncAdapterParameter(Data.CONTENT_URI), mYield);
        mBuilder.withValues(mValues);
        if (mIsNewContact) {
            mBuilder.withValueBackReference(Data.RAW_CONTACT_ID, mBackReference);
        }
        mYield = false;
        mBatchOperation.add(mBuilder.build());
    }

    /**
     * Adds an update operation into the batch
     */
    private void addUpdateOp(Uri uri) {
        mBuilder = newUpdateCpo(uri, mYield).withValues(mValues);
        mYield = false;
        mBatchOperation.add(mBuilder.build());
    }

    public static ContentProviderOperation.Builder newInsertCpo(Uri uri, boolean yield) {
        return ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(uri))
            .withYieldAllowed(yield);
    }

    public static ContentProviderOperation.Builder newUpdateCpo(Uri uri, boolean yield) {
        return ContentProviderOperation.newUpdate(addCallerIsSyncAdapterParameter(uri))
            .withYieldAllowed(yield);
    }

    public static ContentProviderOperation.Builder newDeleteCpo(Uri uri, boolean yield) {
        return ContentProviderOperation.newDelete(addCallerIsSyncAdapterParameter(uri))
            .withYieldAllowed(yield);
    }

    private static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
            .build();
    }
}