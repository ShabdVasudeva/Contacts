package org.codeaurora.contacts;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    private List<Contact> contactList;
    private List<Contact> filteredList;
    private Context context;

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView text;
        private Context context;
        private LinearLayout layout;

        public ContactsViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            text = view.findViewById(R.id.name);
            layout = view.findViewById(R.id.layout);
            context = view.getContext();
        }

        public void bind(Contact contact) {
            text.setText(contact.getName());
            if (contact.getPhotoUri() != null) {
                Glide.with(context).load(R.drawable.qy).placeholder(R.drawable.qy).into(image);
            } else {
                image.setImageResource(R.drawable.qy);
            }
        }
    }

    public ContactsAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contactList = contacts;
        this.filteredList = new ArrayList<>(contactList);
    }

    public void updateContacts(List<Contact> newContacts) {
        this.contactList.clear();
        this.contactList.addAll(newContacts);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(contactList);
        } else {
            String filePattern = query.toLowerCase().trim();
            for (Contact contact : contactList) {
                if (contact.getName().toLowerCase().contains(filePattern)
                        || (contact.getPhoneNumber() != null
                                && contact.getPhoneNumber().contains(filePattern))) {
                    filteredList.add(contact);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        View view =
                LayoutInflater.from(arg0.getContext())
                        .inflate(R.layout.contacts_adapter, arg0, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder arg0, int arg1) {
        Contact contacts = filteredList.get(arg1);
        arg0.bind(contacts);
        arg0.itemView.setOnClickListener(
                v -> {
                    Intent intent = new Intent(context,ContactInfo.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("contact", contacts);
                    context.startActivity(intent);
                });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }
}
