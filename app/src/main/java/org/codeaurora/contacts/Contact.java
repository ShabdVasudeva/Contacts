package org.codeaurora.contacts;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    private String id;
    private String name;
    private String phoneNumber;
    private Uri photoUri;
    private String email;
    private boolean isStarred;
    private String rawId;

    public Contact(
            String id,
            String name,
            String phoneNumber,
            Uri photoUri,
            String email,
            boolean isStarred,
            String rawId) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photoUri = photoUri;
        this.email = email;
        this.isStarred = isStarred;
        this.rawId = rawId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getStarred() {
        return isStarred;
    }

    public void setStarred(boolean isStarred) {
        this.isStarred = isStarred;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRawId() {
        return rawId;
    }

    public void setRawId(String rawId) {
        this.rawId = rawId;
    }

    protected Contact(Parcel in) {
        id = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        photoUri = in.readParcelable(Uri.class.getClassLoader());
        email = in.readString();
        rawId = in.readString();
        isStarred = in.readBoolean();
    }

    public static final Creator<Contact> CREATOR =
            new Creator<Contact>() {
                @Override
                public Contact createFromParcel(Parcel in) {
                    return new Contact(in);
                }

                @Override
                public Contact[] newArray(int size) {
                    return new Contact[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeParcelable(photoUri, flags);
        dest.writeString(email);
        dest.writeString(rawId);
        dest.writeBoolean(isStarred);
    }
}
