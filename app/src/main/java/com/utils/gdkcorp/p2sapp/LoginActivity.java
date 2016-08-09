package com.utils.gdkcorp.p2sapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText uname,pswd;
    private Button login_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uname = (EditText) findViewById(R.id.user_name);
        pswd = (EditText) findViewById(R.id.password);
        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(!uname.getText().toString().equalsIgnoreCase("") && !pswd.getText().toString().equalsIgnoreCase("")){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else {
            if (uname.getText().toString().equalsIgnoreCase("")) {
                uname.setError("Username can't be blank");
            }
            if (pswd.getText().toString().equalsIgnoreCase("")) {
                pswd.setError("Password can't be blank");
            }
        }
    }
}
