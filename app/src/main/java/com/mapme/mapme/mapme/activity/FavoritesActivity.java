package com.mapme.mapme.mapme.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.util.DrawerManager;
import com.mapme.mapme.mapme.util.FavoriteManager;
import com.mapme.mapme.mapme.util.ImageSaver;
import com.mapme.mapme.mapme.util.ViewPagerAdapter;
import com.mapme.mapme.mapme.util.room.PlaceFavorite;

import java.util.ArrayList;
import java.util.Arrays;

public class FavoritesActivity extends LocalizationActivity {

    private ArrayList<PlaceFavorite> favoritesItems;
    private FavoriteManager favoriteManager;
    private ArrayAdapter adapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        DrawerManager.makeDrawer(FavoritesActivity.this);
        favoriteManager = new FavoriteManager(this);
        favoritesItems = new ArrayList<>();

        Button btn_drawer = findViewById(R.id.btn_menu_favorites);
        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DrawerManager.openDrawer();

            }
        });

        favoriteManager.getAllFavorites(new FavoriteManager.IGetAllFavorites() {
            @Override
            public void onResult(PlaceFavorite[] favorites) {
                favoritesItems.addAll(Arrays.asList(favorites));
                adapter.notifyDataSetChanged();
            }
        });

        listView = findViewById(R.id.lv_favorites);

        adapter = new ArrayAdapter<PlaceFavorite>(FavoritesActivity.this, R.layout.item_favoties_cv, favoritesItems) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(FavoritesActivity.this).inflate(R.layout.item_favoties_cv, null);
                }

                final PlaceFavorite place = getItem(position);

                TextView tv_title = convertView.findViewById(R.id.tv_place_name);
                TextView tv_details = convertView.findViewById(R.id.tv_details_itemFavorites);
                ImageView iv_place = convertView.findViewById(R.id.iv_itemFavorites);

                tv_title.setText(place.getName());
                tv_details.setText(place.getAddress());
                iv_place.setImageBitmap(new ImageSaver(FavoritesActivity.this)
                        .load(place.getPhotoPath()));

                Button btn_delete = convertView.findViewById(R.id.btn_delete_itemFavorites);

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        favoriteManager.removeFavorite(place.getId());
                        favoritesItems.remove(place);
                        adapter.notifyDataSetChanged();
                    }
                });

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPlaceView(place);
                    }
                });

                return convertView;
            }
        };

        listView.setAdapter(adapter);


    }

    public void showPlaceView(final PlaceFavorite place) {
        View popupView = getLayoutInflater().inflate(R.layout.selected_place_fragment, null);

        LinearLayout mainLayout = findViewById(R.id.main_layout);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setAnimationStyle(R.style.place_popup_window);

        // show the popup window
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        //popupWindow.showAtLocation(mainLayout_land, Gravity.CENTER, 0, 0);


        // dismiss the popup window when touched
        /*popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });*/

        // Initialize more widgets from `popup_layout.xml`
        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        ViewPager images = popupView.findViewById(R.id.vp_images);
        TextView placeName = popupView.findViewById(R.id.tv_name_selected_place_fragment);
        TextView placeAddress = popupView.findViewById(R.id.tv_address_selected_place_fragment);
        TextView placeNumber = popupView.findViewById(R.id.tv_phonenum_selected_place_fragment);
        TextView placeWebsite = popupView.findViewById(R.id.tv_website_selected_place_fragment);
        RatingBar ratingBar = popupView.findViewById(R.id.ratingBar);
        ImageView fav = popupView.findViewById(R.id.btn_add_to_fav_selected_place_fragment);
        Button closeBtn = popupView.findViewById(R.id.btn_close_selectedItemFragment);

        fav.setVisibility(View.INVISIBLE);
        fav.setClickable(false);

        /*favoriteManager.isPlaceFavorite(place.getId(), new FavoriteManager.IIsFavoriteResult() {
            @Override
            public void result(boolean result) {
                if (result) {
                    if (result) {
                        fav.getDrawable().mutate().setTint(ContextCompat.getColor(FavoritesActivity.this, R.color.favorite_active));
                    } else {
                        fav.getDrawable().mutate().setTint(ContextCompat.getColor(FavoritesActivity.this, R.color.favorite_not_active));
                    }
                }
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteManager.isPlaceFavorite(place.getId(), new FavoriteManager.IIsFavoriteResult() {
                    @Override
                    public void result(boolean result) {
                        if (result) {
                            Log.d(TAG, "result: Removing from favorites");
                            favoriteManager.removeFavorite(place.getId());
                            fav.getDrawable().mutate().setTint(ContextCompat.getColor(FavoritesActivity.this, R.color.favorite_not_active));
                        } else {
                            Log.d(TAG, "result: Adding from favorites");
                            favoriteManager.addFavorite(place);
                            fav.getDrawable().mutate().setTint(ContextCompat.getColor(FavoritesActivity.this, R.color.favorite_active));
                        }
                    }
                });
            }
        });*/

        if (!place.getPhotoPath().equals("")) {
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, place.getPhotoPath());
            images.setAdapter(viewPagerAdapter);
        }

        placeName.setText(place.getName());
        placeAddress.setText(place.getAddress());
        if (place.hasPhoneNumber())
            placeNumber.setText(place.getPhoneNumber());
        if (place.hasWebsite())
            placeWebsite.setText(place.getWebsite());
        ratingBar.setRating((float) place.getRating());

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        // Using location, the PopupWindow will be displayed right under anchorView
        /*popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                location[0], location[1] + anchorView.getHeight());*/
    }

}
