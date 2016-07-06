package uk.appinvent.lunchfinder;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.appinvent.lunchfinder.data.DishLoader;
import uk.appinvent.lunchfinder.data.LunchContract;
import uk.appinvent.lunchfinder.data.User;
import uk.appinvent.lunchfinder.sync.LunchFinderSyncAdapter;

public class SignupActivity extends AppCompatActivity {

    private final String LOG_TAG = SignupActivity.class.getSimpleName();

    private Toolbar toolbar;
    private EditText mNameText;
    private EditText mEmailText;
    private EditText mPhoneText;
    private TextInputLayout mInputLayoutName, mInputLayoutEmail, mInputLayoutPhone;
    private Button mBtnSignUp;

    private User user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    boolean is_user_registered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();


        //TODO Check if user is configured   if not redirect ot signup activity
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        is_user_registered = sharedPreferences.getBoolean(getString(R.string.is_user_registered), false);

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

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    if (user == null){
                        user = new User();
                        user.setUid(firebaseUser.getUid());
                    }
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        if (is_user_registered){
            // user is registered
            // load user data
            try {
                // First, check if the location with this city name exists in the db
                Cursor userCursor = getApplicationContext().getContentResolver().query(
                        LunchContract.UserEntry.CONTENT_URI,
                        DishLoader.UserQuery.PROJECTION,
                        null,
                        null,
                        null);

                if(userCursor != null)
                {
                    if (userCursor.moveToFirst()) {
                        user = User.fromCursor(userCursor);
                        String user_string = user.getName() + ":" + user.getPhone();
                        mNameText.setText(user.getName());
                        mPhoneText.setText(user.getPhone());
                        mEmailText.setText(user.getEmail());
                        mBtnSignUp.setText("Update Details");
                    }
                }
            }catch (Exception e){
                //TODO: remave strack trace when realese
                e.printStackTrace();
                Log.d(LOG_TAG,e.getMessage());
            }
        }

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

        if (user == null){
            user = new User();
            user.setUid("updated");
        }

        user.setEmail(mEmailText.getText().toString());
        user.setName(mNameText.getText().toString());
        user.setPhone(mPhoneText.getText().toString());
        user.setPassword(user.generatePasword());

        ContentValues userValues = new ContentValues();

        // Then add the data, along with the corresponding name of the data type,
        // so the content provider knows what kind of value is being inserted.
        userValues.put(LunchContract.UserEntry.COLUMN_NAME, user.getName());
        userValues.put(LunchContract.UserEntry.COLUMN_EMAIL, user.getEmail());
        userValues.put(LunchContract.UserEntry.COLUMN_PHONE, user.getPhone());
        userValues.put(LunchContract.UserEntry.COLUMN_PASSWORD, user.getPassword());
        userValues.put(LunchContract.UserEntry.COLUMN_UID, user.getUid());


        if (!is_user_registered){
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(LOG_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }

        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(LOG_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (is_user_registered){
            //TODO: update user

            // Finally,update data into the database.
          String where =  LunchContract.UserEntry.TABLE_NAME +
                    "." + LunchContract.UserEntry._ID + " = ? ";

            long userId  = getApplicationContext().getContentResolver().
                    update(LunchContract.UserEntry.CONTENT_URI,userValues,where,new String[]{Long.toString(user.getId())});

            Log.d(LOG_TAG, "User is updated with id " + userId);

        }else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.



            // Finally, insert location data into the database.
            Uri insertedUri = getApplicationContext().getContentResolver().insert(
                    LunchContract.UserEntry.CONTENT_URI,
                    userValues
            );

            long userId = ContentUris.parseId(insertedUri);

            Log.d(LOG_TAG, "User is saved with id " + userId);

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.is_user_registered), true);
            editor.commit();
        }
        //sync data
        loadData();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void loadData() {

        LunchFinderSyncAdapter.syncImmediately(this);
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


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
