package com.ersinkoc.hopol.fragment;

import static android.app.Activity.RESULT_OK;

import static com.ersinkoc.hopol.utils.Constants.PREF_DIRECTORY;
import static com.ersinkoc.hopol.utils.Constants.PREF_NAME;
import static com.ersinkoc.hopol.utils.Constants.PREF_STORED;
import static com.ersinkoc.hopol.utils.Constants.PREF_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ersinkoc.hopol.MainActivity;
import com.ersinkoc.hopol.Model.PostImageModel;
import com.ersinkoc.hopol.R;
import com.ersinkoc.hopol.ShopBasket;
import com.ersinkoc.hopol.SplashActivity;
import com.ersinkoc.hopol.chat.chatActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {
    private TextView nameHm, followingCountHm, followersCountHm, postsCountHm;
    private CircleImageView profileImage;

    private Button followbtn,startChatBtn,editProfilBtn ;
    private RecyclerView recyclerView;

    private ImageView settingsHm;

    boolean isFollowed;

    DocumentReference userRef, myRef;
    private LinearLayout countLayout;

    private FirebaseUser user;

    boolean isMyProfile = true;

    String userUID;

    FirestoreRecyclerAdapter<PostImageModel, PostImageHolder> adapter;

    List<Object> followerList, followingList, followingList_2;

    public Profile() {
        // Boş yapıcı metot
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            myRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        } else {

        }

        if (MainActivity.IS_SEARCHED_USER) {
            isMyProfile = false;
            userUID = MainActivity.USER_ID;
            loadData();
        } else {
            isMyProfile = true;
            userUID = user.getUid();
        }

        if (isMyProfile) {
            editProfilBtn.setVisibility(View.VISIBLE);
            followbtn.setVisibility(View.GONE);
            countLayout.setVisibility(View.VISIBLE);
            startChatBtn.setVisibility(View.GONE);
        } else {
            editProfilBtn.setVisibility(View.GONE);
            followbtn.setVisibility(View.VISIBLE);
            startChatBtn.setVisibility(View.VISIBLE);
        }

        userRef = FirebaseFirestore.getInstance().collection("Users").document(userUID);

        loadBasicData();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        loadPostImages();

        recyclerView.setAdapter(adapter);

        clickListener();

        view.findViewById(R.id.settingsHm).setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut(); // Firebase Authentication'dan çıkış yap
            startActivity(new Intent(getActivity(), SplashActivity.class)); // SplashActivity'ye git
            getActivity().finish();



        });
    }

    private void loadData() {
        myRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Tag_b", error.getMessage());
                    return;
                }

                if (value == null || !value.exists()) {
                    return;
                }

                followingList_2 = (List<Object>) value.get("following");
            }
        });
    }

    private void clickListener() {
        followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFollowed) {
                    followerList.remove(user.getUid());
                    followingList_2.remove(userUID);

                    final Map<String, Object> map_2 = new HashMap<>();
                    map_2.put("following", followingList_2);

                    Map<String, Object> map = new HashMap<>();
                    map.put("follower", followerList);

                    userRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                followbtn.setText("Follow");

                                myRef.update(map_2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Unfollowed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("Tag_2", task.getException().getMessage());
                                        }
                                    }
                                });

                            } else {
                                Log.e("Tag", "" + task.getException().getMessage());
                            }
                        }
                    });
                } else {
                    followerList.add(user.getUid());
                    followingList_2.add(userUID);

                    Map<String, Object> map_2 = new HashMap<>();
                    map_2.put("following", followingList_2);

                    Map<String, Object> map = new HashMap<>();
                    map.put("follower", followerList);

                    userRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                followbtn.setText("Unfollow");

                                myRef.update(map_2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Followed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("tag_3", task.getException().getMessage());
                                        }
                                    }
                                });
                            } else {
                                Log.e("Tag", "" + task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });

        assert getContext() != null;

        editProfilBtn.setOnClickListener(v -> CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getContext(), Profile.this));

        startChatBtn.setOnClickListener(v -> {
            queryChat();
        });



    }
    void queryChat() {

        assert getContext() != null;
        StylishAlertDialog alertDialog = new StylishAlertDialog(getContext(), StylishAlertDialog.PROGRESS);
        alertDialog.setTitleText("Starting Chat...");
        alertDialog.setCancellable(false);
        alertDialog.show();

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");
        reference.whereArrayContains("uid", userUID)
                .get().addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        QuerySnapshot snapshot = task.getResult();

                        if (snapshot.isEmpty()) {
                            startChat(alertDialog);
                        } else {
                            //get chatId and pass
                            alertDialog.dismissWithAnimation();
                            for (DocumentSnapshot snapshotChat : snapshot) {

                                Intent intent = new Intent(getActivity(), chatActivity.class);
                                intent.putExtra("uid", userUID);
                                intent.putExtra("id", snapshotChat.getId()); //return doc id
                                startActivity(intent);
                            }


                        }

                    } else
                        alertDialog.dismissWithAnimation();

                });

    }

    void startChat(StylishAlertDialog alertDialog) {


        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");

        List<String> list = new ArrayList<>();

        list.add(0, user.getUid());
        list.add(1, userUID);

        String pushID = reference.document().getId();

        Map<String, Object> map = new HashMap<>();
        map.put("id", pushID);
        map.put("lastMessage", "Hi");
        map.put("time", FieldValue.serverTimestamp());
        map.put("uid", list);

        reference.document(pushID).update(map).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                reference.document(pushID).set(map);
            }
        });



        CollectionReference messageRef = FirebaseFirestore.getInstance()
                .collection("Messages")
                .document(pushID)
                .collection("Messages");

        String messageID = messageRef.document().getId();




        new Handler().postDelayed(() -> {

            alertDialog.dismissWithAnimation();

            Intent intent = new Intent(getActivity(), chatActivity.class);
            intent.putExtra("uid", userUID);
            intent.putExtra("id", pushID);
            startActivity(intent);

        }, 3000);

    }
    @SuppressLint("WrongViewCast")
    private void init(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        assert getActivity() != null;
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        nameHm = view.findViewById(R.id.nameHm);
        followingCountHm = view.findViewById(R.id.followingCountHm);
        followersCountHm = view.findViewById(R.id.followersCountHm);
        postsCountHm = view.findViewById(R.id.postsCountHm);
        profileImage = view.findViewById(R.id.profileImage);
        followbtn = view.findViewById(R.id.followbtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        countLayout = view.findViewById(R.id.countLayout);
        editProfilBtn = view.findViewById(R.id.editProfilBtn);
        startChatBtn = view.findViewById(R.id.startChatBtn);
        settingsHm = view.findViewById(R.id.settingsHm);


    }

    private void loadBasicData() {
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Tag 0", error.getMessage());
                    return;
                }

                assert value != null;
                if (value.exists()) {
                    String name = value.getString("name");
                    String status = value.getString("status");
                    String profileURL = value.getString("profileImage");

                    nameHm.setText(name);

                    followerList = (List<Object>) value.get("follower");
                    followingList = (List<Object>) value.get("following");
                    followersCountHm.setText("" + followerList.size());
                    followingCountHm.setText("" + followingList.size());

                    try {
                        Glide.with(getContext().getApplicationContext())
                                .load(profileURL)
                                .placeholder(R.drawable.baseline_person_24)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                        storeProfileImage(bitmap, profileURL);

                                        return false;
                                    }
                                })
                                .timeout(7000)
                                .into(profileImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (followerList.contains(user.getUid())) {
                        followbtn.setText("Unfollow");
                        isFollowed = true;
                        startChatBtn.setVisibility(View.VISIBLE);
                    } else {
                        isFollowed = false;
                        followbtn.setText("Follow");
                        startChatBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void storeProfileImage(Bitmap bitmap, String url) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isStored = preferences.getBoolean(PREF_STORED, false);
        String urlString = preferences.getString(PREF_URL, "");
        SharedPreferences.Editor editor = preferences.edit();

        if (isStored && urlString.equals(url)) {
            return;
        }

        ContextWrapper contextWrapper = new ContextWrapper(getContext().getApplicationContext());
        File directory = contextWrapper.getDir("image_data", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File path = new File(directory, "profile.png");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                assert outputStream != null;
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        editor.putBoolean(PREF_STORED, true);
        editor.putString(PREF_URL, url);
        editor.putString(PREF_DIRECTORY, directory.getAbsolutePath());
        editor.apply();
    }

    private void loadPostImages() {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(userUID);
        Query query = reference.collection("Post Images");

        FirestoreRecyclerOptions<PostImageModel> options = new FirestoreRecyclerOptions.Builder<PostImageModel>()
                .setQuery(query, PostImageModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PostImageModel, PostImageHolder>(options) {
            @NonNull
            @Override
            public PostImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_items, parent, false);
                return new PostImageHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostImageHolder holder, int position, @NonNull PostImageModel model) {
                Glide.with(holder.itemView.getContext().getApplicationContext())
                        .load(model.getImageUrl())
                        .into(holder.imageView);

                postsCountHm.setText("" + getItemCount());
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };
    }

    private static class PostImageHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public PostImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri uri = result.getUri();
            uploadImage(uri);
        }
    }

    private void uploadImage(Uri uri) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                StorageReference reference = FirebaseStorage.getInstance().getReference().child("Profile_Images");

                reference.putFile(uri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl()
                                        .addOnSuccessListener(uri1 -> {
                                            String imageUrl = uri1.toString();

                                            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                                            request.setPhotoUri(uri1);

                                            user.updateProfile(request.build());
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("profileImage", imageUrl);
                                            FirebaseFirestore.getInstance().collection("Users")
                                                    .document(user.getUid())
                                                    .update(map).addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            Toast.makeText(requireContext(), "Updated Successful", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(requireContext(), "Error:" + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        });
                            } else {
                                Toast.makeText(requireContext(), "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }


}


