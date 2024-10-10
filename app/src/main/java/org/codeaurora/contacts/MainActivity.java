package org.codeaurora.contacts;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.HashSet;
import java.util.Set;
import android.provider.ContactsContract;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import org.codeaurora.contacts.ApwSearch;
import org.codeaurora.contacts.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;
    public ContactsAdapter adapter;
    private ProgressDialog progressDialog;
    private List<Contact> contactList;
    public ContactsData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.recyclerView.setVisibility(View.GONE);
        Toast.makeText(this, "Loading Contacts", Toast.LENGTH_LONG).show();
        new Handler()
                .postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                binding.progress.setVisibility(View.GONE);
                                binding.recyclerView.setVisibility(View.VISIBLE);
                            }
                        },
                        1000);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
        binding.scroll.setOnScrollChangeListener(
                new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(
                            NestedScrollView nested,
                            int scrollX,
                            int scrollY,
                            int oldScrollX,
                            int oldScrollY) {
                        if (scrollY > oldScrollY && binding.fab.isExtended()) {
                            binding.fab.shrink();
                        } else if (scrollY < oldScrollY && !binding.fab.isExtended()) {
                            binding.fab.extend();
                        }
                    }
                });
        binding.fab.setOnClickListener(
                v -> {
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    try {
                        startActivity(intent);
                    } catch (Exception err) {
                        Toast.makeText(this, "An error occured", Toast.LENGTH_LONG).show();
                    }
                });
        binding.search.setSearchViewListener(
                new ApwSearch.SearchViewListener() {
                    @Override
                    public void onQueryTextChange(String query) {
                        adapter.filter(query);
                    }

                    @Override
                    public void onQueryTextSubmit(String query) {}
                });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                                this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    },
                    PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            loadContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
            } else {
                Toast.makeText(this, "Please Give contacts permission first", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public void loadContacts() {
        data = new ContactsData(this);
        contactList = data.getContacts();
        new LoadContactsTask().execute();
    }

    private class LoadContactsTask extends AsyncTask<Void, Void, List<Contact>> {
        @Override
        protected List<Contact> doInBackground(Void... voids) {
            List<Contact> contacts = contactList;
            return contacts;
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            if (contacts != null) {
                adapter = new ContactsAdapter(getApplicationContext(), contacts);
                binding.recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(MainActivity.this, "Error while loading contacts", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private Uri getContactPhoto(String contactId) {
        try {
            Cursor cursor =
                    getContentResolver()
                            .query(
                                    ContactsContract.Data.CONTENT_URI,
                                    null,
                                    ContactsContract.Data.CONTACT_ID
                                            + "=? AND "
                                            + ContactsContract.Data.MIMETYPE
                                            + "='"
                                            + ContactsContract.CommonDataKinds.Photo
                                                    .CONTENT_ITEM_TYPE
                                            + "'",
                                    new String[] {contactId},
                                    null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String photoUriString =
                            cursor.getString(
                                    cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI));
                    cursor.close();
                    if (photoUriString != null) {
                        return Uri.parse(photoUriString);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.progress.setVisibility(View.VISIBLE);
            ContactsData newData = new ContactsData(this);
            List<Contact> newList = newData.getContacts();
            adapter.updateContacts(newList);
            new Handler()
                    .postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    binding.progress.setVisibility(View.GONE);
                                    binding.recyclerView.setVisibility(View.VISIBLE);
                                }
                            },
                            5000);
            return true;
        } else if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
