package com.ersinkoc.hopol.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ersinkoc.hopol.Model.HomeModel;
import com.ersinkoc.hopol.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ersinkoc.hopol.Model.HomeModel;
import com.ersinkoc.hopol.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {

    private List<HomeModel> list;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public HomeAdapter(List<HomeModel> list, Context context) {
        this.list = list;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();

    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_items, parent, false);
        return new HomeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        HomeModel homeModel = list.get(position);


        Glide.with(context)
                .load(homeModel.getProfileImage())
                .placeholder(R.drawable.baseline_person_24)
                .into(holder.profileImage);

        Glide.with(context)
                .load(homeModel.getImageUrl())
                .placeholder(new ColorDrawable(randomColor()))
                .into(holder.imageView);

        holder.buttonNavigation.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(context, holder.buttonNavigation);
            popupMenu.getMenuInflater().inflate(R.menu.buttonx, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_item_1) {
                    addToCart(homeModel);
                }
                return false;
            });

            popupMenu.show();
        });


        holder.nameHm.setText(homeModel.getUserName());
        holder.timeHm.setText(String.valueOf(homeModel.getTimestamp()));
        holder.productName.setText(homeModel.getProductName());
        holder.description.setText(homeModel.getDescription());
        holder.price.setText(String.format("%s TL", homeModel.getPrice()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void addToCart(HomeModel homeModel) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("productName", homeModel.getProductName());
            cartItem.put("description", homeModel.getDescription());
            cartItem.put("price", homeModel.getPrice());
            cartItem.put("imageUrl", homeModel.getImageUrl()); // Düzeltilmiş

            db.collection("Users")
                    .document(userId)
                    .collection("cart")
                    .add(cartItem) // Firestore otomatik olarak belge kimliği oluşturur
                    .addOnSuccessListener(documentReference -> showToast("Ürün sepete eklendi"))
                    .addOnFailureListener(e -> showToast("Hata: " + e.getMessage()));
        } else {
            showToast("Giriş yapmanız gerekiyor");
        }
    }




    static class HomeHolder extends RecyclerView.ViewHolder {
        private ImageView profileImage;
        private ImageView imageView;
        private TextView nameHm;
        private TextView timeHm;
        private TextView productName;
        private TextView description;
        private TextView price;
        private ImageView buttonNavigation;

        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            imageView = itemView.findViewById(R.id.imageView);
            nameHm = itemView.findViewById(R.id.nameHm);
            timeHm = itemView.findViewById(R.id.timeHm);
            productName = itemView.findViewById(R.id.productName);
            description = itemView.findViewById(R.id.descriptionHm);
            price = itemView.findViewById(R.id.price);
            buttonNavigation = itemView.findViewById(R.id.button_navigationbar);
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

