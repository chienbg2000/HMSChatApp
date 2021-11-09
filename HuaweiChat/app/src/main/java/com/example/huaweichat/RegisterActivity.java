package com.example.huaweichat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huaweichat.model.DBConnect;
import com.example.huaweichat.model.User;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.agconnect.auth.EmailUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.agconnect.cloud.storage.core.UploadTask;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    EditText username, email, password,verifyCode;
    Button register;
    TextView txt_login;
    Button verifyEmail;
    ProgressDialog pd;
    ImageView imageAvt;
    private Uri mImageUri;
    StorageReference reference;
    DBConnect dbConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);
        verifyEmail = findViewById(R.id.verifyEmail);
        verifyCode = findViewById(R.id.verifyCode);
        imageAvt = findViewById(R.id.imageAvt);
        imageAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(RegisterActivity.this);
            }
        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        verifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyEmail();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageUri == null){
                    Toast.makeText(RegisterActivity.this,"Please chosse Image",Toast.LENGTH_SHORT).show();
                    return;
                }
                register();
            }
        });


    }

    public void register(){
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("Please wait...");
        pd.show();
        //Huawei
        EmailUser emailUser = new EmailUser.Builder()
                .setEmail(email.getText().toString())
                .setPassword(password.getText().toString())
                .setVerifyCode(verifyCode.getText().toString())
                .build();

        AGConnectAuth.getInstance().createUser(emailUser)
                .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                    @Override
                    public void onSuccess(SignInResult signInResult) {
                        Toast.makeText(getApplicationContext(), "User successfully created.", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        dbConnect = new DBConnect(RegisterActivity.this);
                        User user = new User();
                        user.setId(AGConnectAuth.getInstance().getCurrentUser().getUid());
                        user.setUser_name(username.getText().toString());
                        dbConnect.add(user);
                        uploadImage_10();
                        startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                        finish();
                    }
                }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                pd.dismiss();
                Log.e("login error",e.getMessage()+"|"+email.getText().toString()+"|"+password.getText().toString()+"|"+verifyCode.getText().toString());
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void verifyEmail() {
        VerifyCodeSettings verifyCodeSettings = VerifyCodeSettings.newBuilder()
                .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                .sendInterval(30)
                .locale(Locale.getDefault())
                .build();

        com.huawei.hmf.tasks.Task<VerifyCodeResult> task = EmailAuthProvider.requestVerifyCode(email.getText().toString(), verifyCodeSettings);
        task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
            @Override
            public void onSuccess(VerifyCodeResult verifyCodeResult) {
                Toast.makeText(getApplicationContext(), "Please check your e-mail", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("register", e.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            imageAvt.setImageURI(mImageUri);
        }
    }

    private void uploadImage_10(){

        if (mImageUri != null){
            reference = AGCStorageManagement.getInstance().getStorageReference("avt\\"
                    + AGConnectAuth.getInstance().getCurrentUser().getUid() +".png");
            UploadTask task = reference.putFile(new File( mImageUri.getPath() ));
            task.addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.UploadResult>(){
                @Override
                public void onSuccess(UploadTask.UploadResult uploadResult) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            User user = new User();
                            user.setId(AGConnectAuth.getInstance().getCurrentUser().getUid());
                            user.setUser_name(username.getText().toString());
                            user.setImgage_url(uri.toString());
                            dbConnect.add(user);
                        }
                    });

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.UploadResult>() {
                @Override
                public void onComplete(Task<UploadTask.UploadResult> task) {
                    pd.dismiss();
                }
            });

        } else {
            Toast.makeText(RegisterActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}