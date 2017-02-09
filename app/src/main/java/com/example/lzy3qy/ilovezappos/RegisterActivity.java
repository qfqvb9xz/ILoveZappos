package com.example.lzy3qy.ilovezappos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends AppCompatActivity {
    private Context context;
    Activity _this = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        //loginCheck();
        setContentView(R.layout.acticity_login);
        Button registerButton = (Button) findViewById(R.id.RegisterButton);
        final EditText userName = (EditText) findViewById(R.id.UserName);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                if (name.length() < 1) {
                    Toast toast = Toast.makeText(_this, "Please Enter your name", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    saveUserName(name);
                    loginCheck();
                }
            }
        });

    }

    protected void saveUserName(String ID){
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_userID), ID);
        editor.apply();
    }

    protected void loginCheck() {
        Log.d("login", "working");

        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String userId = sharedPref.getString(getString(R.string.preference_userID),"");
        if (!userId.equals("")) {
            this.startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }
    }
}
