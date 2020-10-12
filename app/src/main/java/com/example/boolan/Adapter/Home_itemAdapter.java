package com.example.boolan.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boolan.R;
import com.example.boolan.utils.ConstantUtil;
import com.example.boolan.utils.SquaresimpleDraweeView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

public class Home_itemAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    public Context contexts;
    public String[] photo;
    private SquaresimpleDraweeView imageView;
    private String url= ConstantUtil.SERVER_ADDRESS;
    private Uri imageUri;
    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_hone_item_photo, parent, false);
        imageView=view.findViewById(R.id.imageview);
        final HomeAdapter.ViewHolder holder = new HomeAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        imageView.setPadding(0,0,0,0);
        imageUri = Uri.parse(url+"/"+photo[position]+".jpg");
        imageView.setImageURI(imageUri);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(imageUri)
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())
                .build();
        imageView.setController(controller);
    }

    @Override
    public int getItemCount() {
        return photo.length;
    }

    public Home_itemAdapter(Context context, String[] photo1) {
        photo = photo1;
        this.contexts = context;
    }
}
