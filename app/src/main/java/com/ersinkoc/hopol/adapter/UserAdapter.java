package com.ersinkoc.hopol.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ersinkoc.hopol.Model.Users;
import com.ersinkoc.hopol.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private List<Users> list;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    OnUserClicked onUserClicked;

    public UserAdapter(List<Users> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_items, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, @SuppressLint("RecyclerView") int position) {

        if (list.get(position).getUid().equals(user.getUid())){
            holder.layout.setVisibility(View.GONE);

            holder.itemView.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
        }else {
            holder.layout.setVisibility(View.VISIBLE);
        }

        holder.nameHm.setText(list.get(position).getName());
        holder.status.setText(list.get(position).getStatus());

        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(list.get(position).getProfileImage())
                .placeholder(R.drawable.baseline_person_24_white)
                .timeout(7000)
                .into(holder.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClicked.onClicked(list.get(position).getUid());

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class UserHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImage;
        private TextView nameHm, status;
        private RelativeLayout layout;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            nameHm = itemView.findViewById(R.id.nameHm);
            status = itemView.findViewById(R.id.status);
            layout = itemView.findViewById(R.id.relativeLayout);
        }
    }

    public void OnUserClicked(OnUserClicked onUserCliked) {
        this.onUserClicked = onUserCliked;
    }

    public interface OnUserClicked {
        void onClicked(String uid);
    }
}
