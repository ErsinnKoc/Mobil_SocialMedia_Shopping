package com.ersinkoc.hopol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class BuyLobi extends AppCompatActivity  {

    private Button buyProduct;
    private EditText cardName, CardNumber, LastTime, Cvv;

    private TextView totalPriceTextView,backCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_lobi);

        buyProduct = findViewById(R.id.buyProduct);
        cardName = findViewById(R.id.cardName);
        CardNumber = findViewById(R.id.CardNumber);
        LastTime = findViewById(R.id.LastTime);
        Cvv = findViewById(R.id.Cvv);
        backCart= findViewById(R.id.backCart);

        setupCardNumber(CardNumber);
        setupLastTime(LastTime);
        setupCvv(Cvv);

        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        // Intent'ten toplam fiyatı al
        String totalPrice = getIntent().getStringExtra("TOTAL_PRICE");
        // Eğer toplam fiyat null değilse, totalPriceTextView'a ata
        if(totalPrice != null) {
            totalPriceTextView.setText(totalPrice);
        }

        backCart.setOnClickListener(v -> {

            Intent intent = new Intent(BuyLobi.this,ShopBasket.class);
            startActivity(intent);
        });

        buyProduct.setOnClickListener(v -> {
            if (TextUtils.isEmpty(cardName.getText())
                    || TextUtils.isEmpty(CardNumber.getText())
                    || TextUtils.isEmpty(LastTime.getText())
                    || TextUtils.isEmpty(Cvv.getText())) {
                showToast("Lütfen tüm alanları doldurun");
            } else {
                // Sepeti temizle ve sonra ödeme işlemini gerçekleştir
                ShopBasket.clearCart();
                showToast("Başarıyla ürününüz satın alınmıştır");
                Intent intent = new Intent(BuyLobi.this, ShopBasket.class);
                startActivity(intent);
            }
        });



    }


    private void setupCardNumber(EditText cardNumber) {
        cardNumber.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }
            }
        });

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(19);
        cardNumber.setFilters(filters);
    }

    private void setupLastTime(EditText lastTime) {
        lastTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2 && !s.toString().contains("/")) {
                    s.append("/");
                }
            }
        });

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(5);
        lastTime.setFilters(filters);
    }

    private void setupCvv(EditText cvv) {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(3);
        cvv.setFilters(filters);

        cvv.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
