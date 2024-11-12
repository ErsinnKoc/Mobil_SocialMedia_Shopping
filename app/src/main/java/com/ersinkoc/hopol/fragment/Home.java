package com.ersinkoc.hopol.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ersinkoc.hopol.Model.HomeModel;
import com.ersinkoc.hopol.R;
import com.ersinkoc.hopol.ShopBasket;
import com.ersinkoc.hopol.adapter.HomeAdapter;
import com.ersinkoc.hopol.chat.chatUsersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<HomeModel> list;
    private FirebaseUser user;

    public Home() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        list = new ArrayList<>();
        adapter = new HomeAdapter(list, requireContext());
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();

        view.findViewById(R.id.goShop).setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), chatUsersActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.gobacget).setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ShopBasket.class);
            startActivity(intent);
        });
    }

    private void init(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void loadDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> followingList = new ArrayList<>();

        followingList.add(user.getUid());

        db.collection("Users")
                .document(user.getUid())
                .collection("following")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String following = document.getId();
                            followingList.add(following);
                        }
                        retrievePosts(followingList);
                    } else {
                        Log.e("Firestore", "Error getting following list: ", task.getException());
                    }
                });
    }

    private void retrievePosts(List<String> followingList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        list.clear();

        for (String userId : followingList) {
            db.collection("Users")
                    .document(userId)
                    .collection("Post Images")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                if (snapshot.exists()) {
                                    HomeModel model = snapshot.toObject(HomeModel.class);
                                    list.add(model);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("Error", "Gönderiler alınamadı", task.getException());
                        }
                    });
        }
    }
}
