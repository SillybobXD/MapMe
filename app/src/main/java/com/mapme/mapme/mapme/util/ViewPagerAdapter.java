package com.mapme.mapme.mapme.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.util.data.obj.Photo;

import java.util.ArrayList;

/**
 * Created by Sillybob on 2/4/2018.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Photo> images;
    private String photoRef;

    public ViewPagerAdapter(Context context, ArrayList<Photo> images) {
        this.context = context;
        this.images = images;
    }

    public ViewPagerAdapter(Context context, String photoRef) {
        this.context = context;
        this.photoRef = photoRef;
    }

    @Override
    public int getCount() {
        if (images != null)
            return images.size();
        else
            return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.viewpager_image, null);
        final ImageView imageView = view.findViewById(R.id.iv_viewpager);

        if (images != null) {
            Photo photo = images.get(position);
            GoogleAPIManager.getPlacePhoto(photo.getReference(), photo.getMaxWidth(),
                    new GoogleAPIManager.IGetPhotoResponse() {
                        @Override
                        public void onResponse(Bitmap photo) {
                            imageView.setImageBitmap(photo);
                        }

                        @Override
                        public void onFailuer(VolleyError error) {

                        }
                    });
        } else {
            imageView.setImageBitmap(new ImageSaver(context).load(photoRef));
        }

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
