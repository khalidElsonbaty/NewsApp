package com.example.news.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.news.R;
import com.example.news.helper.Validation;
import com.example.news.data.model.User;
import com.example.news.sql.DataBaseHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.Register_EditText_UserName)
    EditText RegisterEditTextUserName;
    @BindView(R.id.Register_EditText_Phone)
    EditText RegisterEditTextPhone;
    @BindView(R.id.Register_EditText_Password)
    EditText RegisterEditTextPassword;
    @BindView(R.id.Register_EditText_RePassword)
    EditText RegisterEditTextRePassword;
    @BindView(R.id.Register_EditText_Email)
    EditText RegisterEditTextEmail;
    private String name = null;
    private String eMail = null;
    private String phone = null;
    private String password = null;
    private String rePassword = null;
    private Validation validation;
    private DataBaseHelper dataBaseHelper;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        intiObjects();
    }

    private void intiViews() {
        name = RegisterEditTextUserName.getText().toString().trim();
        eMail = RegisterEditTextEmail.getText().toString().trim();
        phone = RegisterEditTextPhone.getText().toString().trim();
        password = RegisterEditTextPassword.getText().toString().trim();
        rePassword = RegisterEditTextRePassword.getText().toString().trim();
    }

    private void intiObjects() {
        validation = new Validation();
        dataBaseHelper = new DataBaseHelper(this);
        user = new User();

    }


    @OnClick(R.id.Register_button_Send)
    public void onViewClicked() {
        postDataToSQLite();
    }


    private void postDataToSQLite() {
        intiViews();
        boolean allowSave = true;
        if (validation.isNullOrEmpty(name)) {
            RegisterEditTextUserName.setError("Forget Username");
            allowSave = false;
        }
        if (validation.isNullOrEmpty(eMail) || !validation.isValidEmail(eMail)) {
            RegisterEditTextEmail.setError("Enter Valid Email");
            allowSave = false;
        }
        if (validation.isNullOrEmpty(phone) || !validation.isNumeric(phone)) {
            RegisterEditTextPhone.setError("Enter Valid Phone number");
            allowSave = false;
        }
        if (validation.isNullOrEmpty(password) || validation.isValidPassword(password, false)) {
            RegisterEditTextPassword.setError("Enter Password Without Special Chars");
            allowSave = false;
        }
        if (validation.isNullOrEmpty(rePassword) || !rePassword.equals(password)) {
            RegisterEditTextRePassword.setError("Please Check Your Password");
            allowSave = false;
        }

        if (allowSave) {
            if (!dataBaseHelper.checkUser(RegisterEditTextEmail.getText().toString().trim())) {
                user.setName(RegisterEditTextUserName.getText().toString().trim());
                user.setEmail(RegisterEditTextEmail.getText().toString().trim());
                user.setPhone(RegisterEditTextPhone.getText().toString().trim());
                user.setPassword(RegisterEditTextPassword.getText().toString().trim());
                dataBaseHelper.addUser(user);
                // Snack Bar to show success message that record saved successfully
                Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
                emptyInputEditText();

            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Try Again", Snackbar.LENGTH_LONG).show();
        }

    }

    private void emptyInputEditText() {
        RegisterEditTextUserName.setText(null);
        RegisterEditTextEmail.setText(null);
        RegisterEditTextPhone.setText(null);
        RegisterEditTextPassword.setText(null);
        RegisterEditTextRePassword.setText(null);
    }
}
