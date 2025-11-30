package com.example.cinedex_v2.UI.AdaptersUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioResponseDto;
import com.example.cinedex_v2.R;
import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {

    private Context context;
    private List<UsuarioResponseDto> usuarios;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(UsuarioResponseDto usuario);
    }

    public UserSearchAdapter(Context context, List<UsuarioResponseDto> usuarios, OnUserClickListener listener) {
        this.context = context;
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Necesitas crear item_user_search.xml (ver abajo)
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsuarioResponseDto u = usuarios.get(position);

        holder.tvUsername.setText("@" + u.getNombreUsuario());
        holder.tvNombreReal.setText(u.getNombres() + " " + u.getApellidos());

        // Cargar avatar o default
        Glide.with(context)
                .load(u.getUrlAvatar()) // Si es null, carga el placeholder
                .placeholder(R.drawable.ic_person)
                .circleCrop()
                .into(holder.ivAvatar);

        holder.itemView.setOnClickListener(v -> listener.onUserClick(u));
    }

    @Override
    public int getItemCount() { return usuarios.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername, tvNombreReal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
            tvUsername = itemView.findViewById(R.id.tv_user_username);
            tvNombreReal = itemView.findViewById(R.id.tv_user_realname);
        }
    }
}