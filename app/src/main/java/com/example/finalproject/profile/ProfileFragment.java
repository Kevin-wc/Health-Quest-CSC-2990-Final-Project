package com.example.finalproject.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.finalproject.R;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private ImageView profileImage;
    private TextView usernameTV;
    private TextView realNameTV;
    private ListView settingsList;
    private Button editProfileB;
    FirebaseAuth auth;
    Firebase db;

    public ProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        profileImage = view.findViewById(R.id.profile_image);
        usernameTV = view.findViewById(R.id.username_text);
        realNameTV = view.findViewById(R.id.realname_text);
        settingsList = view.findViewById(R.id.settings_list);
        editProfileB = view.findViewById(R.id.edit_profile_btn);

        auth = FirebaseAuth.getInstance();
     //   db = FirebaseFirestore.getInstance();

        loadUserInfo();
        setupSettingsList();
        setupProfileImage();
        setupEditProfileButton();
        return view;
    }

    private void loadUserInfo() {
        // Gets the logged in user's ID
        String uid = auth.getCurrentUser().getUid();

       // db.collection("users").document(uid).get()
               // .addOnSuccessListener(doc -> {

                  //  if (doc.exists()) {
                      //  usernameTV.setText(doc.getString("username"));
                      //  realNameTV.setText(doc.getString("fullname"));
                        // Load profile image if it exists
                       // String img = doc.getString("profileImageUrl");
                      //  if (img != null && !img.isEmpty()) {
                            // Glide efficiently loads image from a URL into an ImageView
                            //Glide.with(this).load(img).into(profileImage);
                      //  }
                   // }
              //  });
    }

        // Creates a list of settings using a custom adapter.
        // We use a ListView because it supports rows with icons and text.
        // The custom adapter allows us to control exactly how the row looks.
    private void setupSettingsList() {
        String[] items = {"Personal", "Notifications", "Friends"};
        // Corresponding icons that haven't been added yet.
        //int[] icons = {R.drawable.ic_user, R.drawable.ic_bell, R.drawable.ic_friends};

       // SettingsAdapter adapter = new SettingsAdapter(requireContext(), items, icons);
       // settingsList.setAdapter(adapter);

        settingsList.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) startActivity(new Intent(getActivity(), PersonalActivity.class));
            if (position == 1) startActivity(new Intent(getActivity(), NotificationActivity.class));
            if (position == 2) startActivity(new Intent(getActivity(), FriendsActivity.class));
        });
    }

    private void setupProfileImage() {
     //   profileImage.setOnClickListener(v -> openGallery());
    }

    private void setupEditProfileButton() {
        editProfileB.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), EditProfileActivity.class))
        );
    }
}
