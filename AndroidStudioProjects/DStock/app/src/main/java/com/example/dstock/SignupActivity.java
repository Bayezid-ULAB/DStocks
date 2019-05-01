package com.example.dstock;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.keepMeLoggedIn) CheckBox keepMeLoggedIn;
  //  @BindView(R.id.input_address) EditText _addressText;
    @BindView(R.id.input_email) EditText _emailText;
 //   @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
  //  @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    @BindView(R.id.input_password_reEnter) EditText _reEnterPassword;
    private boolean exist;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);


        String name = _nameText.getText().toString();
        // String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
        //String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordHashedSalted=null;
        try{

            passwordHashedSalted=Hashing.generateStrongPasswordHash(password);
        }catch (Exception e){

        }
        SharedPreferences sharedPreferences=getSharedPreferences("obj",MODE_PRIVATE);

        User newUser=new User(name.toLowerCase(),passwordHashedSalted,email);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson string=new Gson();
        String json=string.toJson(newUser);
        editor.putString(name.toLowerCase(),json);
        editor.apply();
        if(keepMeLoggedIn.isChecked()){
            sharedPreferences=getSharedPreferences("obj",MODE_PRIVATE);
            editor=sharedPreferences.edit();
            editor.putString("lastuser",name);
            editor.apply();
        }

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent a=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(a);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onSignupFailed() {
        if(exist==true){
            Toast.makeText(getBaseContext(), "Username exists", Toast.LENGTH_LONG).show();
        }

        else Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
       // String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
       // String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordReEntered = _reEnterPassword.getText().toString();
        //String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

       // if (address.isEmpty()) {
      //      _addressText.setError("Enter Valid Address");
      //      valid = false;
      //  } else {
      //      _addressText.setError(null);
      //  }


        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
         //if(email.isEmpty()){
             _emailText.setError("enter a valid email");
            valid = false;
        } else {
            _emailText.setError(null);
        }


        if (password.isEmpty() || password.length() < 6) {
            _passwordText.setError("Atleast 6 Characters long");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        if((password.isEmpty()||!password.equals(passwordReEntered))||passwordReEntered.isEmpty()){
            _reEnterPassword.setError("Passwords do not match");
            valid=false;
        }else{
            _reEnterPassword.setError(null);
        }
        SharedPreferences sharedPreferences=getSharedPreferences("obj",MODE_PRIVATE);
        String existing=sharedPreferences.getString(name.toLowerCase(),null);
        if(existing!=null){
            exist=true;
            valid=false;
        }

        return valid;
    }
}