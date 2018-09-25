package cn.yznu.gdmapoperate.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mvp.lt.airlineview.R;

import java.util.ArrayList;
import java.util.List;



/**
 * 作者：uiho_mac
 * 时间：2018/9/4
 * 描述：
 * 版本：1.0
 * 修订历史：
 */
public class RecyclerviewGalleryAdapter extends RecyclerView.Adapter<RecyclerviewGalleryAdapter.ViewHolder> {
    private List<Integer> mList = new ArrayList<>();

    public RecyclerviewGalleryAdapter(List<Integer> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_garllery_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mImageView.setImageResource(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
        }
    }
}
