package com.example.finalproject.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.R;
import com.example.finalproject.SharedViewModel;
import com.example.finalproject.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private EditText editUsername;
    private EditText editFullName;
    private Button changePicB;
    private Button saveB;

    private Uri selectedImageUri = null;

    private SharedViewModel model;

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    profileImage.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImage = findViewById(R.id.profileImageView);
        editUsername = findViewById(R.id.usernameET);
        editFullName = findViewById(R.id.fullNameET);
        changePicB = findViewById(R.id.changePictureB);
        saveB = findViewById(R.id.saveProfileB);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        model = new ViewModelProvider(this).get(SharedViewModel.class);
        model.fetchUserData(uid);
        model.getUser().observe(this, user -> {
            if (user != null) {
                editUsername.setText(user.getUserName());
                editFullName.setText(user.getFullName());
                loadCurrentProfilePicture(user);
            }
        });
        changePicB.setOnClickListener(v -> openGallery());
        saveB.setOnClickListener(v -> saveChanges());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void loadCurrentProfilePicture(User user) {
        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()){
            return;
        }
        StorageReference imgRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(user.getProfileImageUrl());

        final long MAX_SIZE = 300 * 1024;

        imgRef.getBytes(MAX_SIZE)
                .addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profileImage.setImageBitmap(bitmap);
                });
    }

    private void saveChanges() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        String newUsername = editUsername.getText().toString().trim();
        String newFullName = editFullName.getText().toString().trim();

        User currentUser = model.getUser().getValue();
        if (currentUser == null) {
            Toast.makeText(this, "Error: user data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldUsername = currentUser.getUserName();
        String oldFullName = currentUser.getFullName();

        // Username Error Handling

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newUsername.contains(" ")) {
            editUsername.setError("Username cannot contain spaces");
            return;
        }

        if (newUsername.length() < 3) {
            editUsername.setError("Username must be at least 3 characters");
            return;
        }

        if (!newUsername.matches("[A-Za-z0-9_.]+")) {
            editUsername.setError("Only letters, numbers, _, and . is allowed");
            return;
        }

        // If no changes were made. EX. only changed pfp
        boolean sameUsername = newUsername.equals(oldUsername);
        boolean sameName = newFullName.isEmpty() || newFullName.equals(oldFullName);
        boolean samePicture = (selectedImageUri == null);

        if (sameUsername && sameName && samePicture) {
            finish();
            return;
        }
        // If username didn't change
        if (sameUsername) {
            updateRealName(uid, newFullName, oldFullName);
            uploadPicture(uid);
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if username IS being changed, we check to see if the username already exists
        FirebaseDatabase.getInstance().getReference("Users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    boolean taken = false;

                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (!child.getKey().equals(uid)) {
                            String existing = child.child("userName").getValue(String.class);
                            if (newUsername.equalsIgnoreCase(existing)) {
                                taken = true;
                                break;
                            }
                        }
                    }

                    if (taken) {
                        Toast.makeText(this, "Username already taken!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(uid)
                            .child("userName")
                            .setValue(newUsername)
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show());


                    updateRealName(uid, newFullName, oldFullName);
                    uploadPicture(uid);

                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking usernames", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateRealName(String uid, String newName, String oldName) {

        if (!newName.isEmpty() && !newName.equals(oldName)) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(uid)
                    .child("fullName")
                    .setValue(newName);
        }
    }

    private void uploadPicture(String uid) {
        if (selectedImageUri == null){
            return;
        }

        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("profileImages/" + uid + ".jpg");

        ref.putFile(selectedImageUri)
                .addOnSuccessListener(task ->
                        ref.getDownloadUrl().addOnSuccessListener(uri ->
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(uid)
                                        .child("profileImageUrl")
                                        .setValue(uri.toString())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Failed to update picture URL", Toast.LENGTH_SHORT).show()
                                        )
                        ).addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to get uploaded image URL", Toast.LENGTH_SHORT).show()
                        )
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to upload picture", Toast.LENGTH_SHORT).show()
                );
    }
}
