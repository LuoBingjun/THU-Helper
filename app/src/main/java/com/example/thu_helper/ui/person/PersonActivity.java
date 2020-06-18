package com.example.thu_helper.ui.person;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.thu_helper.R;
import com.example.thu_helper.data.LoginRepository;
import com.example.thu_helper.data.model.LoggedInUser;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private EditTextPreference mUsernameEdit;
        private EditTextPreference mEmailEdit;
        private EditTextPreference mNicknameEdit;
        private EditTextPreference mPhoneEdit;
        LoginRepository loginRepository = LoginRepository.getInstance();

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            initPreferences();
        }

        private void initPreferences(){
            LoggedInUser loggedInUser = loginRepository.getUser();

            mUsernameEdit = getPreferenceManager().findPreference("username");
            if (loggedInUser != null){
                mUsernameEdit.setText(loggedInUser.getUserId());
            }
            else{
                mUsernameEdit.setText("");
            }


            mEmailEdit = getPreferenceManager().findPreference("email");
            if (loggedInUser != null){
                mEmailEdit.setText(loggedInUser.getDisplayName());
            }
            else{
                mEmailEdit.setText("");
            }

            mNicknameEdit = getPreferenceManager().findPreference("nickname");
            if (loggedInUser != null){
                mNicknameEdit.setText(loggedInUser.getDisplayName());
            }
            else{
                mNicknameEdit.setText("");
            }
            mNicknameEdit.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getContext(), "昵称修改为" + newValue, Toast.LENGTH_LONG);
                    return true;
                }
            });

            mPhoneEdit = getPreferenceManager().findPreference("phone");
            if (loggedInUser != null){
                mPhoneEdit.setText(loggedInUser.getDisplayName());
            }
            else{
                mPhoneEdit.setText("");
            }
            mPhoneEdit.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getContext(), "电话修改为" + newValue, Toast.LENGTH_LONG);
                    return true;
                }
            });
        }
    }
}