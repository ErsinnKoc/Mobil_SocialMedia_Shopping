package com.ersinkoc.hopol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ersinkoc.hopol.Model.shopModel;
import com.ersinkoc.hopol.adapter.shopAdapter;
import com.ersinkoc.hopol.fragment.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopBasket extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static shopAdapter adapter;
    private static List<shopModel> userList;
    private static TextView totalPriceTextView;
    private Button goBuyLobi,goback;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_basket);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalPriceTextView = findViewById(R.id.totalPrice);
        goBuyLobi = findViewById(R.id.goBuyLobi);
        goback= findViewById(R.id.goback);

        userList = new ArrayList<>();
        adapter = new shopAdapter(userList, this);
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();

        // goBuyLobiButton'a tıklama dinleyicisi ekleme
        // goBuyLobiButton'a tıklama dinleyicisi ekleme
        goBuyLobi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // BuyLobi Activity'sini başlatma
                Intent intent = new Intent(ShopBasket.this, BuyLobi.class);
                // totalPriceTextView'daki değeri BuyLobi sınıfına aktar
                intent.putExtra("TOTAL_PRICE", totalPriceTextView.getText().toString());
                startActivity(intent);
            }
        });


        // goback butonuna tıklama dinleyicisi ekleme
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Home Activity'sine geçiş yapma
                Intent intent = new Intent(ShopBasket.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    protected static void clearCart() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users")
                .document(currentUserId)
                .collection("cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                            // Sepet temizlendikten sonra tekrar verileri yükle
                            loadDataFromFirestore();
                        } else {
                            Log.e("Firestore", "Error deleting cart items: ", task.getException());
                        }
                    }
                });
    }




    private static void loadDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users")
                .document(currentUserId)
                .collection("cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear();
                            double totalPrice = 0.0;
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                shopModel model = snapshot.toObject(shopModel.class);
                                userList.add(model);
                                if (model.getPrice() != null) {
                                    try {
                                        double price = Double.parseDouble(model.getPrice());
                                        totalPrice += price;
                                    } catch (NumberFormatException e) {
                                        Log.e("Firestore", "Price format error: ", e);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            // Toplam fiyatı string olarak hesaplama
                            String totalPriceString = String.format("%.2f TL", totalPrice);
                            totalPriceTextView.setText(totalPriceString);
                        } else {
                            Log.e("Firestore", "Error getting cart items: ", task.getException());
                        }
                    }
                });
    }
}
