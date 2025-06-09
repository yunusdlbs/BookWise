package com.example.bookwise.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookwise.R;
import com.example.bookwise.models.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private Context context;
    private List<UserInfo> userList;

    public UserListAdapter(Context context, List<UserInfo> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserInfo user = userList.get(position);

        holder.tvUsername.setText(user.getUsername());
        holder.tvBorrowedCount.setText("Kitap Sayısı: " + user.getBorrowedCount());
        holder.tvGrayListStatus.setText("Gri Liste: " + (user.isGraylisted() ? "Evet" : "Hayır"));
        holder.tvGrayListStatus.setTextColor(user.isGraylisted() ? Color.RED : Color.parseColor("#4CAF50"));

        holder.btnBlock.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("Users")
                    .document(user.getId())
                    .update("isBlocked", true);
            Toast.makeText(context, "Bloke edildi!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvBorrowedCount, tvGrayListStatus;
        Button btnBlock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvBorrowedCount = itemView.findViewById(R.id.tvBorrowedCount);
            tvGrayListStatus = itemView.findViewById(R.id.tvGrayListStatus);
            btnBlock = itemView.findViewById(R.id.btnBlock);
        }
    }
}

