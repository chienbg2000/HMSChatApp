package com.example.huaweichat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView txt_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (AGConnectAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), ChatActivity.class));
            finish();
        }
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        txt_signup = findViewById(R.id.txt_signup);

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProcess();
            }
        });
    }

    private void loginProcess() {
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Please wait...");
        pd.show();
        AGConnectAuthCredential credential = EmailAuthProvider
                .credentialWithPassword(email.getText().toString(), password.getText().toString());
        AGConnectAuth.getInstance().signIn(credential)
                .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                    @Override
                    public void onSuccess(SignInResult signInResult) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Successfully Login", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        pd.dismiss();
                        Log.e("Login", e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}