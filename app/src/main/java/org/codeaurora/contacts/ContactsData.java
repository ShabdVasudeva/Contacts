package org.codeaurora.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsData {
    private static Context context;

    public ContactsData(Context ctxt) {
        this.context = ctxt.getApplicationContext();
    }

    public List<Contact> getContacts() {
        List<Contact> contactList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor =
                contentResolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        new String[] {
                            ContactsContract.Data.CONTACT_ID,
                            ContactsContract.RawContacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Email.ADDRESS,
                            ContactsContract.Contacts.LOOKUP_KEY,
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.Contacts.STARRED
                        },
                        ContactsContract.Data.MIMETYPE + " IN (?, ?)",
                        new String[] {
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                        },
                        ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            try {
                String currentContactId = "";
                String name = "";
                String phoneNumber = null;
                String email = null;
                Uri photoUri = null;
                boolean isStarred = false;
                String currentRawId = "";

                while (cursor.moveToNext()) {
                    String contactId =
                            cursor.getString(
                                    cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                    String rawId =
                            cursor.getString(
                                    cursor.getColumnIndex(ContactsContract.RawContacts._ID));
                    String mimeType =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                    if (!contactId.equals(currentContactId)) {
                        if (!currentContactId.isEmpty()) {
                            Contact contact =
                                    new Contact(
                                            contactId,
                                            name,
                                            phoneNumber,
                                            photoUri,
                                            email,
                                            isStarred,
                                            rawId);
                            contactList.add(contact);
                        }
                        currentContactId = contactId;
                        currentRawId = rawId;
                        name =
                                cursor.getString(
                                        cursor.getColumnIndex(
                                                ContactsContract.Contacts.DISPLAY_NAME));
                        isStarred =
                                (cursor.getString(
                                                        cursor.getColumnIndex(
                                                                ContactsContract.Contacts.STARRED))
                                                .equals("1"))
                                        ? true
                                        : false;
                        phoneNumber = null;
                        email = null;
                    }
                    if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimeType)) {
                        phoneNumber =
                                cursor.getString(
                                        cursor.getColumnIndex(
                                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                    } else if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(
                            mimeType)) {
                        email =
                                cursor.getString(
                                        cursor.getColumnIndex(
                                                ContactsContract.CommonDataKinds.Email.ADDRESS));
                    }
                }
                if (!currentContactId.isEmpty()) {
                    Contact contact =
                            new Contact(
                                    currentContactId,
                                    name,
                                    phoneNumber,
                                    photoUri,
                                    email,
                                    isStarred,
                                    currentRawId);
                    contactList.add(contact);
                }

            } finally {
                cursor.close();
            }
        }
        return contactList;
    }
}
