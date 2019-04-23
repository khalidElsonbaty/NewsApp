package com.example.news.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.AlteredCharSequence;
import android.util.Base64;
import android.util.Base64DataException;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.helper.Validation;
import com.example.news.sql.DataBaseHelper;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Parameter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Policy;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    Validation validation = new Validation();
    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    @BindView(R.id.Login_EditText_UserName)
    EditText LoginEditTextUserName;
    @BindView(R.id.Login_EditText_Password)
    EditText LoginEditTextPassword;
    @BindView(R.id.Login_CheckBox_RememberMe)
    CheckBox LoginCheckBoxRememberMe;
    private String userName;
    private String password;

    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();


        loginButton = (LoginButton) findViewById(R.id.Login_Button_Facebook);
        loginButton.setReadPermissions(Arrays.asList(EMAIL, PUBLIC_PROFILE));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        printKeyHash();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                Toast.makeText(LoginActivity.this, "User Logged Out", Toast.LENGTH_LONG).show();
            } else {
                loadUserProfile(currentAccessToken);
            }
        }
    };

    private void loadUserProfile(AccessToken newAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.fasebook.com/" + id + "/picture?type=normal";

                            Toast.makeText(LoginActivity.this,first_name+" "+last_name,Toast.LENGTH_LONG).show();
                            
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void printKeyHash() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.example.news", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    @OnClick({R.id.Login_Button_ForgetPassword, R.id.Login_button_Sign_in, R.id.Login_TextView_Sign_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Login_Button_ForgetPassword:
                break;
            case R.id.Login_button_Sign_in:
                onSignIn();
                break;
            case R.id.Login_TextView_Sign_out:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private void onSignIn() {
        userName = LoginEditTextUserName.getText().toString().trim();
        password = LoginEditTextPassword.getText().toString().trim();
        boolean allowSave = true;
        if (validation.isNullOrEmpty(userName)) {
            LoginEditTextUserName.setError("Not a valid UserName!");
            allowSave = false;
        }

        if (validation.isNullOrEmpty(password) || validation.isValidPassword(password, true)) {
            LoginEditTextPassword.setError("Not a valid Password!");
            allowSave = false;
        }

        if (allowSave) {
            successLogin();
        } else {
            Toast.makeText(this, "The Account not Found", Toast.LENGTH_LONG).show();
            LoginEditTextUserName.setText(null);
            LoginEditTextPassword.setText(null);
        }
    }

    private void successLogin() {
        if (dataBaseHelper.checkUser(userName, password)) {
            Intent accountIntent = new Intent(this, HomeActivity.class);
            accountIntent.putExtra("UserName", userName);
            startActivity(accountIntent);

        } else {
            Toast.makeText(this, "The Account is wrong Try again", Toast.LENGTH_LONG).show();
            LoginEditTextUserName.setText(null);
            LoginEditTextPassword.setText(null);
        }
    }
}
