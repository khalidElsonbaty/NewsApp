package com.example.news.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.news.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetEmailActivity extends AppCompatActivity {

    @BindView(R.id.Forget_Password_EditText_Phone)
    EditText ForgetPasswordEditTextPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_email);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.Forget_Password_button_Send)
    public void onViewClicked() {
    }
}
