package org.codeaurora.contacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsPreference extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle arg0, String arg1) {
        setPreferencesFromResource(R.xml.settings, arg1);
        Preference theme = findPreference("about");
        Preference theme2 = findPreference("app_info");
        theme2.setIconSpaceReserved(false);
        theme.setIconSpaceReserved(false);
        theme.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        Intent intent =
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://androidportworld.netlify.app/"));
                        try {
                            startActivity(intent);
                        } catch (Exception err) {
                            Toast.makeText(
                                            getActivity().getApplicationContext(),
                                            "An error occured",
                                            Toast.LENGTH_LONG)
                                    .show();
                        }
                        return true;
                    }
                });
        theme2.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(
                                Uri.fromParts(
                                        "package",
                                        getActivity().getApplicationContext().getPackageName(),
                                        null));
                        startActivity(intent);
                        return false;
                    }
                });
        PreferenceCategory cat1 = findPreference("cat1");
        cat1.setIconSpaceReserved(false);
        PreferenceCategory cat2 = findPreference("cat2");
        cat2.setIconSpaceReserved(false);
    }
}
