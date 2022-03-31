package com.example.himalaya.adapters;

import android.graphics.drawable.Drawable;
import android.os.Trace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.himalaya.Base.BaseApplication;
import com.example.himalaya.DetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.utils.ImageBlur;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayerTrackPageAdapter extends PagerAdapter {

    private List<Track> mData = new ArrayList<>();

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_track_page, container, false);
        container.addView(itemView);

        ImageView item = itemView.findViewById(R.id.track_pager_item);
        Track track = mData.get(position);
        String coverUrlLarge = track.getCoverUrlLarge();

        //Glide.with(container.getContext()).load(coverUrlLarge).into(item);
        Glide.with(container.getContext()).load(coverUrlLarge)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        BaseApplication.getHandler().post(() -> ImageBlur.makeBlur(container.getContext(), resource, item));
                        return true;
                    }
                }).submit();

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<Track> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }
}
