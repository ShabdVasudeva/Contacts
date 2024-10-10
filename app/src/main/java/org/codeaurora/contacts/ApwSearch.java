package org.codeaurora.contacts;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.codeaurora.contacts.R;

public class ApwSearch extends LinearLayout {
    private EditText searchInput;
    private ImageView searchIcon, clearIcon;
    private SearchViewListener searchListener;

    public ApwSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ApwSearch(Context context) {
        super(context);
        initialize(context);
    }

    private void initialize(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_view, this, true);
        searchInput = (EditText) view.findViewById(R.id.input);
        clearIcon = (ImageView) view.findViewById(R.id.clear);
        searchInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence arg0, int arg1, int arg2, int arg3) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            clearIcon.setVisibility(View.VISIBLE);
                            if (searchListener != null) {
                                searchListener.onQueryTextChange(s.toString());
                            }
                        } else {
                            clearIcon.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {}
                });
        clearIcon.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        searchInput.setText("");
                    }
                });
    }

    public void setSearchViewListener(SearchViewListener listener) {
        this.searchListener = listener;
    }

    public interface SearchViewListener {
        void onQueryTextSubmit(String query);

        void onQueryTextChange(String query);
    }
}
