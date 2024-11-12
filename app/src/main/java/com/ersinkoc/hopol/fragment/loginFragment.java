package com.ersinkoc.hopol.fragment;



import static com.ersinkoc.hopol.fragment.CreatAcauntFragment.EMAIL_REGEX;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class loginFragment extends Fragment {

    private EditText emailFCA,passwordFCA;
    private TextView signIngo, forgotpaassFL;
    private Button loginButton, googleSignIButton;


    private ProgressBar progressBar1;

    private FirebaseAuth auth;

    private static final int  RC_SIGN_IN = 1;

    GoogleSignInClient mGoogleSignInClient;





    public loginFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);


        clickListener();
    }
    private void init(View view){

        emailFCA = view.findViewById(R.id.emailFCA);
        passwordFCA = view.findViewById(R.id.passwordFCA);
        loginButton = view.findViewById(R.id.loginButton);
        signIngo = view.findViewById(R.id.signIngo);
        forgotpaassFL = view.findViewById(R.id.forgotpaassFL);
        progressBar1 = view.findViewById(R.id.progressBar1);
        googleSignIButton =view.findViewById(R.id.googleSignIButton);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(getActivity(),googleSignInOptions);




    }

    private void clickListener(){
        forgotpaassFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReplacerActivty) getActivity()).setFragment(new ForgotPassword());
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailFCA.getText().toString();
                String password = passwordFCA.getText().toString();

                if (email.isEmpty()|| !email.matches(EMAIL_REGEX)){
                    emailFCA.setError("Email is not corracte");
                    return;

                }
                if (password.isEmpty()|| password.length()< 6){
                    passwordFCA.setError("Password is not corracte");
                    return;
                }
                progressBar1.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            if (!user.isEmailVerified()){
                                Toast.makeText(getContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                            }
                            sendUserToMainActivity();
                        }else {
                            String exception = "Error"+task.getException().getMessage();
                            Toast.makeText(getContext(), exception, Toast.LENGTH_SHORT).show();
                            progressBar1.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        googleSignIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIN();

            }
        });
        signIngo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReplacerActivty) getActivity()).setFragment(new CreatAcauntFragment());
            }
        });





    }
    private  void sendUserToMainActivity(){

        if (getActivity() == null)
            return;

        progressBar1.setVisibility(View.GONE);
        startActivity(new Intent(getContext().getApplicationContext(), MainActivity.class));
        getActivity().finish();


    }

    private void signIN(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account !=null;
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e){
              e.printStackTrace();

            }



        }



    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            updateUi(user);
                        }else {
                            Log.w("TAG","signInWithCredential:failure",task.getException());
                        }

                    }
                });


    }
    private void updateUi(FirebaseUser user){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        Map<String, String> map=new HashMap<>();
        map.put("name",account.getDisplayName());
        map.put("email", account.getEmail());
        map.put("profileImage",String.valueOf(account.getPhotoUrl()));
        map.put("uid",user.getUid());
        map.put("following", String.valueOf(0));
        map.put("followers", String.valueOf(0));
        map.put("status", " ");

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    assert getActivity() != null;
                    progressBar1.setVisibility(View.GONE);
                    sendUserToMainActivity();



                }else{
                    progressBar1.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"Eror"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();




                }





            }
        });
    }







}