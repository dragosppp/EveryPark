package com.example.park;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.park.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.text.TextUtils.isEmpty;
import static com.example.park.util.Check.areStringsEqual;

public class RegisterActivity extends AppCompatActivity implements
        View.OnClickListener
{
   private static final String TAG = "RegisterActivity";

   private EditText email, password, confirmPassword;
   private ProgressBar progressBar;

   //todo Hugarian notation for varaibles
   private FirebaseFirestore mDb;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_register);

      email = (EditText) findViewById(R.id.et_register_email);
      password = (EditText) findViewById(R.id.et_register_password);
      confirmPassword = (EditText) findViewById(R.id.et_register_confirm_password);
      progressBar = (ProgressBar) findViewById(R.id.pb_register);

      findViewById(R.id.btn_register).setOnClickListener(this);

      mDb = FirebaseFirestore.getInstance();

      hideSoftKeyboard();
   }

   public void registerNewEmail(final String email, String password){

      showDialog();

      FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    if (task.isSuccessful()){
                       Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                       //todo require at least 6 characters for password
                       //insert some default data
                       User user = new User();
                       user.setEmail(email);
                       user.setUsername(email.substring(0, email.indexOf("@")));
                       user.setUserId(FirebaseAuth.getInstance().getUid());

                       DocumentReference newUserRef = mDb
                               .collection(getString(R.string.collection_users))
                               .document(FirebaseAuth.getInstance().getUid());

                       newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                             hideDialog();

                             if(task.isSuccessful()){
                                redirectLoginScreen();
                             }else{
                                View parentLayout = findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                             }
                          }
                       });

                    }
                    else {
                       View parentLayout = findViewById(android.R.id.content);
                       Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                       hideDialog();
                    }
                    // ...
                 }
              });
   }

   /**
    * Redirects the user to the login screen
    */
   private void redirectLoginScreen(){
      Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");

      Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
      startActivity(intent);
      finish();
   }

   private void showDialog(){
      progressBar.setVisibility(View.VISIBLE);

   }

   private void hideDialog(){
      if(progressBar.getVisibility() == View.VISIBLE){
         progressBar.setVisibility(View.INVISIBLE);
      }
   }

   private void hideSoftKeyboard(){
      this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
   }

   @Override
   public void onClick(View view) {
      switch (view.getId()){
         case R.id.btn_register:{
            Log.d(TAG, "onClick: attempting to register.");

            //check for null valued EditText fields
            if(!isEmpty(email.getText().toString())
                    && !isEmpty(password.getText().toString())
                    && !isEmpty(confirmPassword.getText().toString())){

               //check if passwords match
               if(areStringsEqual(password.getText().toString(), confirmPassword.getText().toString())){
                  //Initiate registration task
                  registerNewEmail(email.getText().toString(), password.getText().toString());
               }else{
                  Toast.makeText(RegisterActivity.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
               }

            }else{
               Toast.makeText(RegisterActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
            }
            break;
         }
      }
   }
}
