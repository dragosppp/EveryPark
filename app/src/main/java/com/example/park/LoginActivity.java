package com.example.park;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.park.models.User;
import com.example.park.models.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

   private static final String TAG = "LoginActivity";

   private FirebaseAuth.AuthStateListener mAuthListener;

   private EditText email;
   private EditText password;
   private ProgressBar progressBar;
   private Button btnSignIn;
   private TextView tvRegister;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);

      email = findViewById(R.id.et_login_email);
      password = findViewById(R.id.et_login_password);
      progressBar = findViewById(R.id.pb_login);
      btnSignIn = findViewById(R.id.btn_login_sign_in);
      tvRegister = findViewById(R.id.tv_login_register);

      setupFirebaseAuth();
      btnSignIn.setOnClickListener(this);
      tvRegister.setOnClickListener(this);

      hideSoftKeyboard();
   }

   private void showDialog() {
      progressBar.setVisibility(View.VISIBLE);

   }

   private void hideDialog() {
      if (progressBar.getVisibility() == View.VISIBLE) {
         progressBar.setVisibility(View.INVISIBLE);
      }
   }

   private void hideSoftKeyboard() {
      this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
   }

   private void setupFirebaseAuth() {
      Log.d(TAG, "setupFirebaseAuth: started.");

      mAuthListener = new FirebaseAuth.AuthStateListener() {
         @Override
         public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
               Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
               Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

               Intent intent = new Intent(LoginActivity.this, MainActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
               finish();
            } else {
               Log.d(TAG, "onAuthStateChanged:signed_out");
            }
         }
      };
   }

   @Override
   public void onStart() {
      super.onStart();
      FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
   }

   @Override
   public void onStop() {
      super.onStop();
      if (mAuthListener != null) {
         FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
      }
   }

   private void signIn() {
      //check if the fields are filled out
      if (!isEmpty(email.getText().toString())
              && !isEmpty(password.getText().toString())) {
         Log.d(TAG, "onClick: attempting to authenticate.");

         showDialog();

         FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),
                 password.getText().toString())
                 .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       hideDialog();
                    }
                 }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
               hideDialog();
            }
         });
      } else {
         Toast.makeText(LoginActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
      }
   }

   @Override
   public void onClick(View view) {
      switch (view.getId()) {
         //todo use findviewbyid object for switch
         case R.id.tv_login_register: {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            break;
         }

         case R.id.btn_login_sign_in: {
            signIn();
            break;
         }
      }
   }
}