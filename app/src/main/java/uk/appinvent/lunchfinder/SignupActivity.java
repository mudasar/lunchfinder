package uk.appinvent.lunchfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import uk.appinvent.lunchfinder.data.User;

public class SignupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText mNameText;
    private EditText mEmailText;
    private EditText mPhoneText;
    private TextInputLayout mInputLayoutName, mInputLayoutEmail, mInputLayoutPhone;
    private Button mBtnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNameText = (EditText) findViewById(R.id.name_text);
        mEmailText = (EditText) findViewById(R.id.email_text);
        mPhoneText = (EditText) findViewById(R.id.phone_text);
        mNameText.addTextChangedListener(new TextChangeWatcher(mNameText));
        mPhoneText.addTextChangedListener(new TextChangeWatcher(mPhoneText));
        mEmailText.addTextChangedListener(new TextChangeWatcher(mEmailText));

        mInputLayoutName = (TextInputLayout) findViewById(R.id.name_input_layout);
        mInputLayoutEmail = (TextInputLayout) findViewById(R.id.email_input_layout);
        mInputLayoutPhone = (TextInputLayout) findViewById(R.id.phone_input_layout);

        mBtnSignUp = (Button) findViewById(R.id.signup_button);
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });
    }

    private void saveUser() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePhone()) {
            return;
        }

        //TODO: update the code to save data in database

        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
        User user = new User(mNameText.getText().toString(), mEmailText.getText().toString(), mPhoneText.getText().toString());
        user.save();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.is_user_registered), true);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class TextChangeWatcher implements TextWatcher{

        private View mView;

        public TextChangeWatcher(View mView) {
            this.mView = mView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (mView.getId()){
                case R.id.name_text:
                    validateName();
                    break;
                case R.id.phone_text:
                    validatePhone();
                    break;
                case R.id.email_text:
                    validateEmail();
                    break;
            }
        }
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateEmail(){
        String email = mEmailText.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {

            mInputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(mEmailText);
            return false;
        } else {
            mInputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        if (mPhoneText.getText().toString().trim().isEmpty()) {
            mInputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(mPhoneText);
            return false;
        } else {
            mInputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateName() {
        if (mNameText.getText().toString().trim().isEmpty()) {
            mInputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(mNameText);
            return false;
        } else {
            mInputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
