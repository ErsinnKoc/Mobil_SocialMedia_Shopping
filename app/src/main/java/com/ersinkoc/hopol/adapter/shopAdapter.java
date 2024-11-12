package com.ersinkoc.hopol.adapter;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ersinkoc.hopol.Model.shopModel;
import com.ersinkoc.hopol.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class shopAdapter extends RecyclerView.Adapter<shopAdapter.shopHolder> {

    private List<shopModel> list;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;





    public shopAdapter(List<shopModel> userList, Context context) {
        this.list = userList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        fetchCartItems();


    }

    @SuppressLint("RestrictedApi")
    private void fetchCartItems() {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("Users")
                    .document(userId)
                    .collection("cart")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<shopModel> cartItems = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                shopModel cartItem = document.toObject(shopModel.class);
                                cartItems.add(cartItem);
                            }
                            list.clear();
                            list.addAll(cartItems);
                            notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } else {
            showToast("Giriş yapmanız gerekiyor");
        }
    }

    @NonNull
    @Override
    public shopAdapter.shopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_items, parent, false);
        return new shopHolder(view);
    }

    @Override


    public void onBindViewHolder(@NonNull shopHolder holder, int position) {
        shopModel shopModel1 = list.get(position);


        String imageUrl = shopModel1.getImageUrl() != null ? shopModel1.getImageUrl() : "";
        String productName = shopModel1.getProductName() != null ? shopModel1.getProductName() : "Ürün Adı Yok";
        String description = shopModel1.getDescription() != null ? shopModel1.getDescription() : "Açıklama Yok";
        String price = shopModel1.getPrice() != null ? shopModel1.getPrice() : "Fiyat Yok";


        if (!imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(new ColorDrawable(randomColor()))
                    .into(holder.imageView);
        } else {

            holder.imageView.setImageDrawable(new ColorDrawable(randomColor()));
        }


        String priceText = price + "TL";


        holder.productName.setText(productName);
        holder.description.setText(description);
        holder.price.setText(priceText);

        holder.delateProduct.setOnClickListener(v -> {
            removeFromCart(shopModel1);
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeFromCart(shopModel shopModel1) {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("Users")
                    .document(userId)
                    .collection("cart")
                    .whereEqualTo("productName", shopModel1.getProductName())
                    .whereEqualTo("description", shopModel1.getDescription())
                    .whereEqualTo("price", shopModel1.getPrice())
                    .whereEqualTo("imageUrl", shopModel1.getImageUrl())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        showToast("Ürün sepetten kaldırıldı");
                                    })
                                    .addOnFailureListener(e -> showToast("Hata: " + e.getMessage()));
                        }
                    })
                    .addOnFailureListener(e -> showToast("Hata: " + e.getMessage()));
        } else {
            showToast("Giriş yapmanız gerekiyor");
        }
    }





    public class shopHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView productName;

        private TextView description;

        private ImageView delateProduct;

        private TextView price;

        public shopHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            productName = itemView.findViewById(R.id.productName);
            description = itemView.findViewById(R.id.descriptionFA);
            delateProduct = itemView.findViewById(R.id.delateProduct);

            price = itemView.findViewById(R.id.price);
        }
    }

    private int randomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
