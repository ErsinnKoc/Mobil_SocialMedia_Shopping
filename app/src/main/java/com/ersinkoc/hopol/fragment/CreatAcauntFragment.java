package com.ersinkoc.hopol.fragment;

import static android.app.ProgressDialog.show;

import android.content.Intent;
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

import com.ersinkoc.hopol.MainActivity;
import com.ersinkoc.hopol.R;
import com.ersinkoc.hopol.ReplacerActivty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreatAcauntFragment extends Fragment {
    private EditText nameFCA,emailFCA,passwordFCA,confirmPasswordFCA;
    private TextView loginBackFCA;
    private Button singInButtonFCA;
    private FirebaseAuth auth;
    private ProgressBar progressBar1;

    public static final String EMAIL_REGEX="^(.+)@(.+)$";


    public CreatAcauntFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_creat_acaunt, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        clickListener();
    }
    private void init(View view){

        nameFCA= view.findViewById(R.id.nameFCA);
        emailFCA= view.findViewById(R.id.emailFCA);
        passwordFCA= view.findViewById(R.id.passwordFCA);
        confirmPasswordFCA= view.findViewById(R.id.confirmPasswordFCA);
        loginBackFCA= view.findViewById(R.id.loginBackFCA);
        singInButtonFCA= view.findViewById(R.id.singInButtonFCA);
        progressBar1=view.findViewById(R.id.progressBar1);

        auth=FirebaseAuth.getInstance();

    }
    private void clickListener(){

        loginBackFCA.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                ((ReplacerActivty) getActivity()).setFragment(new loginFragment());



            }
        });
        singInButtonFCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameFCA.getText().toString();
                String email = emailFCA.getText().toString();
                String password = passwordFCA.getText().toString();
                String confirmps = confirmPasswordFCA.getText().toString();
                if(name.isEmpty()||name.equals(" ")){
                    nameFCA.setError("Name is incorrect");
                    return;
                }
                if(email.isEmpty()|| !email.matches(EMAIL_REGEX)){
                    emailFCA.setError("Email is incorrect");
                    return;
                }

                if(password.isEmpty()||password.length()<6){
                    passwordFCA.setError("Name is incorrect");
                    return;
                }

                if(!password.equals(confirmps)){
                    confirmPasswordFCA.setError("This is not match");
                    return;
                }
                progressBar1.setVisibility(View.VISIBLE);

                creatAcaunt(name,email,password);


            }
        });
    }
    private void creatAcaunt(String name,String email,String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser user = auth.getCurrentUser();

                    UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                    request.setDisplayName(name);
                    user.updateProfile(request.build());

                    user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(getContext(), "Email verification link send", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                    uploadUser(user,name,email);




                }else{
                    progressBar1.setVisibility(View.GONE);
                   String exception = task.getException().getMessage();
                    Toast.makeText(getContext(),"erorr"+exception,Toast.LENGTH_SHORT).show();



                }



            }
        });

    }

    private void uploadUser(FirebaseUser user, String name, String email){
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();

        Map<String, Object> map=new HashMap<>();

        map.put("name",name);
        map.put("email", email);
        map.put("profileImage"," ");
        map.put("uid",user.getUid());
        map.put("status", " ");
        map.put("search",name.toLowerCase());
        map.put("follower", list);
        map.put("following", list1);


        FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    assert getActivity() != null;
                    progressBar1.setVisibility(View.GONE);
                    startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                    getActivity().finish();



                }else{
                    progressBar1.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"Eror"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();




                }





            }
        });



    }



}