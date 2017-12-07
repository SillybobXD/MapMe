package com.mapme.mapme.mapme;

import android.app.Activity;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * Created by Yitschak on 07/12/2017.
 */

public class DrawerManager {

    public static void makeDrawer(final Activity activity) {

        new DrawerBuilder().withActivity(activity).build();

        PrimaryDrawerItem map = new PrimaryDrawerItem().withIdentifier(1).withName("Map");
        SecondaryDrawerItem favorites = new SecondaryDrawerItem().withIdentifier(2).withName("Favorites");
        SecondaryDrawerItem settings = new SecondaryDrawerItem().withIdentifier(2).withName("Settings");

        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                //.withToolbar(toolbar)
                .addDrawerItems(map, favorites, settings)

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 0:

                                break;

                            case 2:

                                break;

                            case 3:

                                break;


                        }

                        return false;
                    }
                })
                .build();

    }
}



