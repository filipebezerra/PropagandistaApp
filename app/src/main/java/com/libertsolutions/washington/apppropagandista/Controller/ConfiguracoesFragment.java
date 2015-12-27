package com.libertsolutions.washington.apppropagandista.Controller;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import com.libertsolutions.washington.apppropagandista.R;

import static com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils.PREF_AUTH_KEY;
import static com.libertsolutions.washington.apppropagandista.Util.PreferencesUtils.PREF_SERVER_URL;

/**
 * Tela das configurações.
 *
 * @author Filipe Bezerra
 * @version 1.0, 20/11/2015
 * @since 1.0
 */
public class ConfiguracoesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        bindPreferenceSummaryToValue(findPreference(PREF_SERVER_URL));
        bindPreferenceSummaryToValue(findPreference(PREF_AUTH_KEY));
    }

    private static Preference.OnPreferenceChangeListener sOnPreferenceChangeListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String strinValue = o.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                final int index = listPreference.findIndexOfValue(strinValue);
                preference.setSummary(index >= 0 ?
                        listPreference.getEntries()[index] : null);
            } else {
                preference.setSummary(strinValue);
            }

            return true;
        }
    };

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sOnPreferenceChangeListener);
        sOnPreferenceChangeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
