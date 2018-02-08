package com.mapme.mapme.mapme.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.activity.FavoritesActivity;
import com.mapme.mapme.mapme.activity.MapActivity;
import com.mapme.mapme.mapme.activity.SettingsActivity;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * Created by Yitschak on 07/12/2017.
 */

public class DrawerManager {

    static Drawer drawer;

    public static void makeDrawer(final Activity activity) {
        new DrawerBuilder().withActivity(activity).build();

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.logo_green)
                .build();

        PrimaryDrawerItem map = new PrimaryDrawerItem().withName(R.string.map);
        SecondaryDrawerItem favorites = new SecondaryDrawerItem().withName(R.string.favorites);
        SecondaryDrawerItem settings = new SecondaryDrawerItem().withName(R.string.settings);

        drawer = new DrawerBuilder()

                .withAccountHeader(headerResult)
                .withActivity(activity)


                .addDrawerItems(map,
                        new DividerDrawerItem(),
                        favorites, settings)

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                Intent mapIntent = new Intent(activity.getBaseContext(), MapActivity.class);
                                activity.startActivity(mapIntent);
                                break;
                            case 3:
                                Intent favoritesIntent = new Intent(activity.getBaseContext(), FavoritesActivity.class);
                                activity.startActivity(favoritesIntent);
                                break;
                            case 4:
                                Intent settingsIntent = new Intent(activity.getBaseContext(), SettingsActivity.class);
                                activity.startActivity(settingsIntent);
                                break;

                        }

                        return false;
                    }
                })
                .build();


    }

    public static void openDrawer() {

        drawer.openDrawer();

    }


}



