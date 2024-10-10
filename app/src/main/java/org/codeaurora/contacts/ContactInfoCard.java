package org.codeaurora.contacts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ContactInfoCard extends MaterialCardView {
    private MaterialButton wap, text, email;
    private TextView number, mail;
    public ContactInfoCard(Context context) {
        super(context);
        initialize(context);
    }

    public ContactInfoCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.contact_info_card, this, true);
        wap = findViewById(R.id.wap);
        text = findViewById(R.id.txt);
        number = findViewById(R.id.number);
        email = findViewById(R.id.email);
        mail = findViewById(R.id.mailText);
    }

    public void setEmailText(String email){
        mail.setText(email);
    }
    
    public void setEmailOnClickListener(OnClickListener listener){
        email.setOnClickListener(listener);
    }
    
    public void setWapClickListener(OnClickListener listener) {
        wap.setOnClickListener(listener);
    }
    
    public void setTextClickListener(OnClickListener listener) {
        text.setOnClickListener(listener);
    }

    public void setNumberAsText(String phoneNumber) {
        number.setText(phoneNumber);
    }
}
