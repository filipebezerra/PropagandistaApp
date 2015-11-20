package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.libertsolutions.washington.apppropagandista.R;

/**
 * Tela das configurações.
 *
 * @author Filipe Bezerra
 * @version 1.0, 20/11/2015
 * @since 1.0
 */
public class ConfiguracoesFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        findPreference("pref_url").setOnPreferenceChangeListener(this);
        findPreference("pref_auth_key").setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference instanceof EditTextPreference) {
            preference.setSummary((String)o);
        }
        return true;
    }
}
