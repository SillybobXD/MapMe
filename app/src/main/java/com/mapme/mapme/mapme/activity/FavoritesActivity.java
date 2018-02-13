package com.mapme.mapme.mapme.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.util.DrawerManager;
import com.mapme.mapme.mapme.util.FavoriteManager;
import com.mapme.mapme.mapme.util.ImageSaver;
import com.mapme.mapme.mapme.util.room.PlaceFavorite;

import java.util.ArrayList;
import java.util.Arrays;

public class FavoritesActivity extends AppCompatActivity {

    private ArrayList<PlaceFavorite> favoritesItems;
    private FavoriteManager favoriteManager;
    private ArrayAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

                final View viewAnim = convertView;
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


                return convertView;
            }
        };

        listView.setAdapter(adapter);


    }


}
