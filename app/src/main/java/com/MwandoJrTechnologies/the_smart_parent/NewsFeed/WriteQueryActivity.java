package com.MwandoJrTechnologies.the_smart_parent.NewsFeed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WriteQueryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private DatabaseReference postsReference;

    private EditText editTextWriteQuery;
    private Button buttonPost;

    final static int galleryPick = 1;

    //all strings
    private String post;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;
    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_query);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        postsReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        //cast to views
        editTextWriteQuery = findViewById(R.id.edit_text_write_query);
        buttonPost = findViewById(R.id.button_post);
        progressDialog = new ProgressDialog(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Write query");

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInformation();
            }
        });

    }

    //activate back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);

    }


    //check that a query must be written
    private void ValidatePostInformation() {
        post = editTextWriteQuery.getText().toString();
        if (TextUtils.isEmpty(post)) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please ask a question", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {

            //show progress dialog
            progressDialog.setTitle("Adding new Post");
            progressDialog.setMessage("Updating post, Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            StoreImageToFireBaseStorage();
            SavingPostInformationToDatabase();
        }
    }

    private void StoreImageToFireBaseStorage() {
        //setting current date and time to generate random keys for the users images posted
        //setting current date
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        //setting current date
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

    }

    //saves details of image to fireBase storage
    private void SavingPostInformationToDatabase() {
        usersReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //validation if child exists then we execute
                if (dataSnapshot.exists()) {
                    String userFullName = dataSnapshot.child("fullName").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", currentUserID);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", post);
                    postsMap.put("profileImage", userProfileImage);
                    postsMap.put("fullName", userFullName);
                    //now save inside fireBase database
                    postsReference.child(currentUserID + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        SendUserToMainActivity();
                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New Question Updated Successfully.", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        progressDialog.dismiss();
                                    } else {
                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please try again. An error occurred", Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(WriteQueryActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }

}
