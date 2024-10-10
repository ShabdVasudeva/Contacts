package org.codeaurora.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BlockedNumberContract;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.google.android.material.elevation.SurfaceColors;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import org.codeaurora.contacts.databinding.LayoutContactInfoBinding;

public class ContactInfo extends AppCompatActivity {
    private LayoutContactInfoBinding binding;
    private Contact contact;
    private Uri photoUri;
    private Random random = new Random();
    private String[] messages = {
        "Hello, how are you?",
        "Have a great day!",
        "Stay positive!",
        "Keep going!",
        "You're doing great!",
        "Believe in yourself!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutContactInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        contact = getIntent().getParcelableExtra("contact");
        int colorSurfaceContainer =
                getMaterialColor(this, com.google.android.material.R.attr.colorSurfaceContainer);
        int colorSurface = SurfaceColors.SURFACE_2.getColor(this);
        Window window = getWindow();
        window.setNavigationBarColor(colorSurface);
        window.setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        binding.toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });
        binding.infoCard.setWapClickListener(
                v -> {
                    startWhatsAppVideoCall(contact.getPhoneNumber());
                });
        binding.infoCard.setTextClickListener(
                v -> {
                    int index = random.nextInt(messages.length);
                    String message = messages[index];
                    sendSMS(contact.getPhoneNumber(), message);
                });
        binding.infoCard.setNumberAsText(contact.getPhoneNumber());
        String mailText = (contact.getEmail() != null) ? contact.getEmail() : "No Email Found";
        binding.infoCard.setEmailText(mailText);
        binding.infoCard.setEmailOnClickListener(
                v -> {
                    if (contact.getEmail() != null) {
                        composeEmail(contact.getEmail());
                    } else {
                        Toast.makeText(
                                        this,
                                        "this contact may not have any email data saved......",
                                        Toast.LENGTH_LONG)
                                .show();
                    }
                });
        binding.bunch.setVideoClickListener(
                v -> {
                    startWhatsAppVideoCall(contact.getPhoneNumber());
                });
        binding.bunch.setCallClickListener(
                v -> {
                    makePhoneCall(contact.getPhoneNumber());
                });

        binding.bunch.setTextClickListener(
                v -> {
                    int index = random.nextInt(messages.length);
                    String message = messages[index];
                    sendSMS(contact.getPhoneNumber(), message);
                });

        binding.bottomNavigation.setEditOnClickListener(
                v -> {
                    Cursor cursor =
                            getContentResolver()
                                    .query(
                                            Uri.withAppendedPath(
                                                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                                                    Uri.encode(contact.getPhoneNumber())),
                                            new String[] {ContactsContract.PhoneLookup._ID},
                                            null,
                                            null,
                                            null);

                    if (cursor != null && cursor.moveToFirst()) {
                        String contactId =
                                cursor.getString(
                                        cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                        Uri contactUri =
                                ContentUris.withAppendedId(
                                        ContactsContract.Contacts.CONTENT_URI,
                                        Long.parseLong(contactId));

                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setData(contactUri);
                        intent.putExtra("finishActivityOnSaveCompleted", true);
                        startActivity(intent);

                        cursor.close();
                    }
                });
        binding.bottomNavigation.setEmailOnClickListener(
                v -> {
                    if (contact.getEmail() != null) {
                        composeEmail(contact.getEmail());
                    } else {
                        Toast.makeText(
                                        this,
                                        "this contact may not have any email data saved......",
                                        Toast.LENGTH_LONG)
                                .show();
                    }
                });
        binding.bottomNavigation.applyConditionalIcon(
                new ApwBottomNavigation.SetIconOnCondition() {
                    @Override
                    public boolean shouldFav() {
                        if (contact.getStarred()) {
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public Drawable trueIcon() {
                        return getResources().getDrawable(R.drawable.true_favourite);
                    }

                    @Override
                    public Drawable falseIcon() {
                        return getResources().getDrawable(R.drawable.favourite);
                    }
                });
        binding.bottomNavigation.setShareOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String email =
                                (contact.getEmail() != null)
                                        ? contact.getEmail()
                                        : "Email not found";
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Information");
                        intent.putExtra(
                                Intent.EXTRA_TEXT,
                                "Name: "
                                        + contact.getName()
                                        + "\nPhone: "
                                        + contact.getPhoneNumber()
                                        + "\nEmail: "
                                        + email);
                        startActivity(Intent.createChooser(intent, "Send contact to..."));
                    }
                });
        binding.bottomNavigation.setFavOnClickListener(
                v -> {
                    if (contact.getStarred()) {
                        unstarRawContact(contact.getName());
                        contact.setStarred(false);
                    } else if (!contact.getStarred()) {
                        starRawContact(contact.getName());
                        contact.setStarred(true);
                    }
                    binding.bottomNavigation.applyConditionalIcon(
                            new ApwBottomNavigation.SetIconOnCondition() {
                                @Override
                                public boolean shouldFav() {
                                    if (contact.getStarred()) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }

                                @Override
                                public Drawable trueIcon() {
                                    return getResources().getDrawable(R.drawable.true_favourite);
                                }

                                @Override
                                public Drawable falseIcon() {
                                    return getResources().getDrawable(R.drawable.favourite);
                                }
                            });
                });
        if (contact != null) {
            binding.name.setText(contact.getName());
            Glide.with(this)
                    .load(getContactPhoto(contact.getPhoneNumber()))
                    .placeholder(R.drawable.contactplace)
                    .into(binding.photo);
        }
    }

    public void sendSMS(String phoneNumber, String message) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + phoneNumber)); // Only SMS apps should handle this
        smsIntent.putExtra("sms_body", message); // Pre-fill the message
        try {
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(this, "No messaging app found", Toast.LENGTH_SHORT).show();
        }
    }

    public void makePhoneCall(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 1);
            } else {
                startCall(phoneNumber);
            }
        } else {
            startCall(phoneNumber);
        }
    }

    public void startWhatsAppVideoCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://wa.me/" + phoneNumber));
        intent.setPackage("com.whatsapp");
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        try {
            startActivity(callIntent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission to make calls is required", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getContactPhoto(String phoneNumber) {
        try {
            ContentResolver cr = getContentResolver();
            Uri uri =
                    Uri.withAppendedPath(
                            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                            Uri.encode(phoneNumber));
            Cursor cursor =
                    cr.query(
                            uri,
                            new String[] {ContactsContract.PhoneLookup.PHOTO_URI},
                            null,
                            null,
                            null);
            if (cursor == null) {
                return null;
            }
            String contactImage = null;
            if (cursor.moveToFirst()) {
                contactImage =
                        cursor.getString(
                                cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));
            }

            if (!cursor.isClosed()) {
                cursor.close();
            }
            return Uri.parse(contactImage);
        } catch (Exception err) {
            return contact.getPhotoUri();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(contact.getPhoneNumber());
            } else {
                Toast.makeText(this, "Permission DENIED to make calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteContactById(String id) {
        Cursor cur =
                getContentResolver()
                        .query(
                                ContactsContract.Contacts.CONTENT_URI,
                                null,
                                ContactsContract.Contacts.DISPLAY_NAME + "=" + id,
                                null,
                                null);
        if (cur != null) {
            while (cur.moveToNext()) {
                try {
                    String lookupKey =
                            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri =
                            Uri.withAppendedPath(
                                    ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                    getContentResolver()
                            .delete(uri, ContactsContract.Contacts.DISPLAY_NAME + "=" + id, null);
                    Toast.makeText(
                                    getApplicationContext(),
                                    "Contact Removed Successfully changes may appear next turn",
                                    Toast.LENGTH_LONG)
                            .show();
                } catch (Exception e) {
                    Log.e("Remover", "deleteContactById: ", e);
                }
            }
            cur.close();
        }
    }

    private void starRawContact(String contactId) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Contacts.STARRED, 1);
        getContentResolver()
                .update(
                        ContactsContract.Contacts.CONTENT_URI,
                        values,
                        ContactsContract.Contacts.DISPLAY_NAME + "= ?",
                        new String[] {contactId});
        Toast.makeText(
                        getApplicationContext(),
                        "Contact Has beed added in Favourites",
                        Toast.LENGTH_LONG)
                .show();
    }

    private void unstarRawContact(String contactId) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Contacts.STARRED, 0);
        getContentResolver()
                .update(
                        ContactsContract.Contacts.CONTENT_URI,
                        values,
                        ContactsContract.Contacts.DISPLAY_NAME + "= ?",
                        new String[] {contactId});
        Toast.makeText(
                        getApplicationContext(),
                        "Contact Has been Removed From Favourites",
                        Toast.LENGTH_LONG)
                .show();
    }

    public int getMaterialColor(Context context, int colorAttr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(colorAttr, typedValue, true);
        return typedValue.data;
    }

    private void composeEmail(String emailAddress) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        intent.putExtra(Intent.EXTRA_TEXT, "Body of the email");
        try {
            startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (Exception err) {
            Toast.makeText(
                            this,
                            "This contact may not have any email data saved.",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu arg0) {
        getMenuInflater().inflate(R.menu.info, arg0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem arg0) {
        if (arg0.getItemId() == R.id.share) {
            String email = (contact.getEmail() != null) ? contact.getEmail() : "Email not found";
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Information");
            intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Name: "
                            + contact.getName()
                            + "\nPhone: "
                            + contact.getPhoneNumber()
                            + "\nEmail: "
                            + email);
            startActivity(Intent.createChooser(intent, "Send contact to..."));
            return true;
        } else if (arg0.getItemId() == R.id.delete) {
            deleteContactById(contact.getName());
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
