package org.codeaurora.contacts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.google.android.material.button.MaterialButton;

public class ApwBottomNavigation extends LinearLayout {

    private MaterialButton edit, share, favourite, email;
    private SetIconOnCondition setConditionalIcon;

    public ApwBottomNavigation(Context context) {
        super(context);
        initialize(context);
    }

    public ApwBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ApwBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context ctxt) {
        LayoutInflater.from(ctxt).inflate(R.layout.apw_bottom, this, true);
        edit = findViewById(R.id.edit);
        share = findViewById(R.id.share);
        favourite = findViewById(R.id.fav);
        email = findViewById(R.id.email);
    }

    public void setEditOnClickListener(OnClickListener listener) {
        edit.setOnClickListener(listener);
    }

    public void setShareOnClickListener(OnClickListener listener) {
        share.setOnClickListener(listener);
    }

    public void setFavOnClickListener(OnClickListener listener) {
        favourite.setOnClickListener(listener);
    }

    public void setEmailOnClickListener(OnClickListener listener) {
        email.setOnClickListener(listener);
    }

    public void setIconOnCondition(MaterialButton button, SetIconOnCondition condition) {
        if (condition.shouldFav()) {
            button.setIcon(condition.trueIcon());
        } else {
            button.setIcon(condition.falseIcon());
        }
    }

    public void applyConditionalIcon(SetIconOnCondition favCondition) {
        setIconOnCondition(favourite, favCondition);
    }

    public interface SetIconOnCondition {
        boolean shouldFav();

        Drawable trueIcon();

        Drawable falseIcon();
    }
}
