package org.codeaurora.contacts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.material.card.MaterialCardView;

public class ButtonsBunch extends MaterialCardView {
    private LinearLayout call, text, video;

    public ButtonsBunch(Context context) {
        super(context);
        initialize(context);
    }

    public ButtonsBunch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context ctxt) {
        LayoutInflater.from(ctxt).inflate(R.layout.buttons_bunch,this,true);
        call = findViewById(R.id.call);
        text = findViewById(R.id.text);
        video = findViewById(R.id.video);
    }

    public void setCallClickListener(OnClickListener listener) {
        call.setOnClickListener(listener);
    }

    public void setTextClickListener(OnClickListener listener) {
        text.setOnClickListener(listener);
    }

    public void setVideoClickListener(OnClickListener listener) {
        video.setOnClickListener(listener);
    }
}
