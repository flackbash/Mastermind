package com.prangesoftwaresolutions.mastermind.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.prangesoftwaresolutions.mastermind.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferences.registerOnSharedPreferenceChangeListener(mPrefListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPreferences.unregisterOnSharedPreferenceChangeListener(mPrefListener);
    }

    public static class MastermindPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource
            setPreferencesFromResource(R.xml.settings, rootKey);
            validatePreferences();
            initSummary(getPreferenceScreen());
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.settings_duplicate_colors_key))
                    || key.equals(getString(R.string.settings_number_slots_key))
                    || key.equals(getString(R.string.settings_number_colors_key))) {
                validatePreferences();
            }
            Preference pref = findPreference(key);
            updateSummary(pref);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        private void validatePreferences() {
            // If the number of slots is greater than the number of colors, then duplicate colors
            // needs to be enabled
            final SwitchPreference duplicateColorsSP = findPreference(getString(R.string.settings_duplicate_colors_key));
            final ListPreference numSlotsLP = findPreference(getString(R.string.settings_number_slots_key));
            final ListPreference numColorsLP = findPreference(getString(R.string.settings_number_colors_key));
            int numSlots = 0;
            if (numSlotsLP != null) {
                numSlots = Integer.parseInt(numSlotsLP.getValue());
            }
            int numColors = 0;
            if (numColorsLP != null) {
                numColors = Integer.parseInt(numColorsLP.getValue());
            }

            if (duplicateColorsSP != null && !duplicateColorsSP.isChecked() && numSlots > numColors) {
                duplicateColorsSP.setChecked(true);
                Toast.makeText(getContext(), R.string.warning_duplicate_colors_enabled, Toast.LENGTH_LONG).show();
            }
        }

        private void initSummary(Preference pref) {
            if (pref instanceof PreferenceGroup) {
                PreferenceGroup pGrp = (PreferenceGroup) pref;
                for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                    initSummary(pGrp.getPreference(i));
                }
            } else {
                updateSummary(pref);
            }
        }

        private void updateSummary(Preference pref) {
            if (pref instanceof ListPreference) {
                pref.setSummary(((ListPreference)pref).getValue());
            }
        }
    }
}
