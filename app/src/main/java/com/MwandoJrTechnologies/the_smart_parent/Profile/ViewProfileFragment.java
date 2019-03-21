package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Displays user profile
 */
public class ViewProfileFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView textViewName;
    private TextView textViewContact;
    private TextView textViewUsername;
    private Button buttonLogout;

    ImageView profileImageView;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersReference;

    String currentUseID;

    ProgressBar progressBar;


    public ViewProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_edit_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please Log into your account", Toast.LENGTH_SHORT).show();
           // OpenLoginFragment();
        }

        profileImageView = rootView.findViewById(R.id.profileImageView);
        progressBar = rootView.findViewById(R.id.progressBar);
        textViewName = rootView.findViewById(R.id.textViewName);
        textViewContact = rootView.findViewById(R.id.textViewContact);
        textViewUsername = rootView.findViewById(R.id.textViewUsername);

        buttonLogout = rootView.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

        LoadUserInformation();

        return rootView;
    }

    private void LoadUserInformation() {
        //getting the user who is logged in as CurrentUser
        mAuth = FirebaseAuth.getInstance();
        currentUseID = mAuth.getCurrentUser().getUid();
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUseID);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProfilePics");

        //get image to profile
        UsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String image = dataSnapshot.child("profileImage").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.profile_image_placeholder)
                            .into(profileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View view) {

        firebaseAuth = FirebaseAuth.getInstance();

        //logout button pressed
        if (view == buttonLogout) {
            //logout the user
            firebaseAuth.signOut();
            //start main activity
            startActivity(new Intent(getContext(), MainActivity.class));
        }

    }


    }




