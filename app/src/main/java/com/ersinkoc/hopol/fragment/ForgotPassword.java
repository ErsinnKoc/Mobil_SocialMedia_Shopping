package com.ersinkoc.hopol.fragment;

import static com.ersinkoc.hopol.fragment.CreatAcauntFragment.EMAIL_REGEX;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ersinkoc.hopol.R;
import com.ersinkoc.hopol.ReplacerActivty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends Fragment {


    private TextView loginBackFCA;
    private Button recoverBtn;

    private EditText emailFCA;

    private FirebaseAuth auth;


    private ProgressBar progressBar1;



    public ForgotPassword() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        clickListener();
    }

    private void init(View view){

        loginBackFCA = view.findViewById(R.id.loginBackFCA);
        emailFCA = view.findViewById(R.id.emailFCA);
        recoverBtn = view.findViewById(R.id.recoverBtn);
        auth = FirebaseAuth.getInstance();
        progressBar1 = view.findViewById(R.id.progressBar1);




    }

    private void clickListener(){
        loginBackFCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReplacerActivty) getActivity()).setFragment(new loginFragment());
            }
        });

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailFCA.getText().toString();

                if(email.isEmpty()|| !email.matches(EMAIL_REGEX)){
                    emailFCA.setError("Email is incorrect");
                    return;
                }

                progressBar1.setVisibility(View.VISIBLE);


                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(),"password reset email sent",Toast.LENGTH_SHORT).show();

                                    emailFCA.setText("");

                                }else {

                                    String errMsg =task.getException().getMessage();
                                    Toast.makeText(getContext(),"Error:"+errMsg,Toast.LENGTH_SHORT).show();
                                }
                                progressBar1.setVisibility(View.GONE);
                            }
                        });
            }
        });



    }
}