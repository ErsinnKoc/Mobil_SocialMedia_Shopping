package com.ersinkoc.hopol.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ersinkoc.hopol.Model.GalleryImageModel;
import com.ersinkoc.hopol.R;
import com.ersinkoc.hopol.adapter.GalleryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Add extends Fragment {

    private EditText descFA, productNameFA, priceFA;
    private ImageView imageView;
    private RecyclerView recyclerView;

    private ImageButton backBtn, nextBtn;
    private Button imageViewBtn;

    private GalleryAdapter adapter;
    private List<GalleryImageModel> list;

    private Uri imageUri;

    private FirebaseUser user;

    private Dialog dialog;
    private boolean isUploading = false;

    public Add() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new GalleryAdapter(list);
        recyclerView.setAdapter(adapter);

        clickListener();

        imageViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getContext(), Add.this);
            }
        });

        checkPermissions();
    }

    private void clickListener() {
        adapter.SendImage(new GalleryAdapter.SendImage() {
            @Override
            public void onSend(Uri picUri) {
                CropImage.activity(picUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(4, 3)
                        .start(getContext(), Add.this);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUploading) {
                    isUploading = true;
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    final StorageReference storageReference = storage.getReference().child("Post Images/" + System.currentTimeMillis());

                    dialog.show();

                    storageReference.putFile(imageUri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                uploadData(uri.toString());
                                            }
                                        });
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Failed to upload post", Toast.LENGTH_SHORT).show();
                                        isUploading = false;
                                    }
                                }
                            });
                }
            }
        });
    }

    private void uploadData(String imageURL) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid()).collection("Post Images");

        String id = reference.document().getId();
        String description = descFA.getText().toString();
        String productName = productNameFA.getText().toString();
        String price = priceFA.getText().toString();

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("description", description);
        map.put("imageUrl", imageURL);
        map.put("timestamp", FieldValue.serverTimestamp());
        map.put("userName", user.getDisplayName());
        map.put("profileImage", String.valueOf(user.getPhotoUrl())); // Bu mevcut
        map.put("likeCount", 0);
        map.put("comments", "");
        map.put("uid", user.getUid());
        map.put("productName", productName);
        map.put("price", price);

        // Profil resmi URI'sini kaydet
        map.put("profileImageUri", String.valueOf(user.getPhotoUrl()));

        reference.document(id).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                        isUploading = false;
                    }
                });
    }

    private void init(View view) {
        descFA = view.findViewById(R.id.descriptionFA);
        productNameFA = view.findViewById(R.id.productNameFA);
        priceFA = view.findViewById(R.id.priceFA);
        imageView = view.findViewById(R.id.imageView);
        recyclerView = view.findViewById(R.id.recyclerView);
        nextBtn = view.findViewById(R.id.nextBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.laoding_dialog);
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dialog_bg, null));
        dialog.setCancelable(false);
        imageViewBtn = view.findViewById(R.id.imageViewBtn);
    }

    private void checkPermissions() {
        Dexter.withContext(getContext())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            loadImages();
                        } else {
                            Toast.makeText(getContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadImages() {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Download");
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (file1.getAbsolutePath().endsWith(".jpg") || file1.getAbsolutePath().endsWith(".png")) {
                        list.add(new GalleryImageModel(Uri.fromFile(file1)));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;
                imageUri = result.getUri();
                Glide.with(getContext())
                        .load(imageUri)
                        .into(imageView);

                imageView.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
            }
        }
    }
}
