package com.ersinkoc.hopol.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class discoverFragment extends Fragment {

    private RecyclerView recyclerView;

    private HomeAdapter adapter;
    private List<HomeModel> userList;

    public discoverFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userList = new ArrayList<>();
        adapter = new HomeAdapter(userList, getContext());
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

    private void loadDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("Users")
                                        .document(document.getId())
                                        .collection("Post Images")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                        HomeModel model = snapshot.toObject(HomeModel.class);
                                                        userList.add(model);
                                                    }

                                                    adapter.notifyDataSetChanged();
                                                } else {
                                                    Log.e("Firestore", "Error getting post images: ", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
