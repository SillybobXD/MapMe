package com.mapme.mapme.mapme.util;

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

import com.mapme.mapme.mapme.DrawerManager;
import com.mapme.mapme.mapme.R;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Button btn_drawer = findViewById(R.id.btn_menu_favorites);
        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerManager.makeDrawer(FavoritesActivity.this);
            }
        });


        ListView listView = findViewById(R.id.lv_favorites);

        ArrayAdapter adapter = new ArrayAdapter<Integer>(FavoritesActivity.this, R.layout.item_favoties_cv) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(FavoritesActivity.this).inflate(R.layout.item_favoties_cv, null);
                }

                TextView tv_title = convertView.findViewById(R.id.tv_placeName_itemFavorites);
                TextView tv_details = convertView.findViewById(R.id.tv_details_itemFavorites);
                Button btn_delete = convertView.findViewById(R.id.btn_delete_itemFavorites);
                ImageView iv_place = convertView.findViewById(R.id.iv_itemFavorites);


                return convertView;
            }
        };


        listView.setAdapter(adapter);


    }

}
