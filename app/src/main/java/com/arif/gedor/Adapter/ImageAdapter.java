package com.arif.gedor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arif.gedor.DB.Upload;
import com.arif.gedor.DB.UserPdg;
import com.arif.gedor.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Angga Kristiono on 21/11/2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>  {
    private Context mContext;
    private ArrayList<Upload> mUploads;
    private OnItemClickListener mListener;



    public ImageAdapter(Context context, ArrayList<Upload>uploads){
        mContext =  context;
        mUploads = uploads;
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false));
    }


    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.txtNama.setText(mUploads.get(position).getNamaProduk());
        holder.txtHarga.setText(mUploads.get(position).getHarga());
        holder.txtRincian.setText(mUploads.get(position).getRincian());
        Picasso.get()
                .load(mUploads.get(position).getUploadImage()).fit().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();

    }


    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        public TextView txtNama, txtHarga, txtRincian;
        public ImageView imageView;
        public ImageViewHolder(View itemView){
            super(itemView);

            txtNama = itemView.findViewById(R.id.nameProduk);
            txtHarga = itemView.findViewById(R.id.harga);
            txtRincian = itemView.findViewById(R.id.Rincian);
            imageView = itemView.findViewById(R.id.imageProduk);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }




        @Override
        public void onClick(View view) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Silahkan Pilih");
            MenuItem Edit = contextMenu.add(Menu.NONE,1,1,"Edit");
            MenuItem Delete = contextMenu.add(Menu.NONE,2,2,"Hapus");

            Edit.setOnMenuItemClickListener(this);
            Delete.setOnMenuItemClickListener(this);

        }



        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()){
                        case 1:
                            mListener.onEditClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onEditClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
