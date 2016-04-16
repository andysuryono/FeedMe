package smartfoodcluster.feedme.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import smartfoodcluster.feedme.R;
import smartfoodcluster.feedme.handlers.RestaurantGui;

public class UserSelection extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    List<RestaurantGui> restaurants=new ArrayList<RestaurantGui>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List restaurantList  = populateList();

        //ListAdapter restaurantAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,restaurantList);
        ListAdapter restaurantAdapter = new RestaurantAdapter();
        ListView restaurantListGui = (ListView)findViewById(R.id.restaurantList);
        restaurantListGui.setAdapter(restaurantAdapter);

        restaurantListGui.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RestaurantGui selectedRestaurantGui = restaurants.get(position);
                String selectedRestaurant = selectedRestaurantGui.getRestaurantName();
                Toast.makeText(UserSelection.this, selectedRestaurant, Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(), UserViewMenu.class);
                i.putExtra("restarantName", selectedRestaurant);
                i.putExtra("RestaurantIcon", selectedRestaurantGui.getRestaurantIconId());
                startActivity(i);
                setContentView(R.layout.activity_restaurant_home);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private List<RestaurantGui> populateList(){


        restaurants.add(new RestaurantGui("BoneFish", R.drawable.bonefish));
        restaurants.add(new RestaurantGui("BigBurger", R.drawable.bigburger));
        restaurants.add(new RestaurantGui("Chipotle", R.drawable.chipotle));
        restaurants.add(new RestaurantGui("McDonalds",R.drawable.mcdonalds));
        restaurants.add(new RestaurantGui("Publix", R.drawable.publix));
        restaurants.add(new RestaurantGui("Subway", R.drawable.subway));
        return restaurants;
    }


    private class RestaurantAdapter extends ArrayAdapter<RestaurantGui> {

        public RestaurantAdapter(){
            super(UserSelection.this,R.layout.restaurant_list_view,restaurants);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return super.getView(position, convertView, parent);
            View thisView = convertView;
            if(thisView==null){
                thisView=getLayoutInflater().inflate(R.layout.restaurant_list_view,parent,false);
            }

            RestaurantGui selectedRestaurant = restaurants.get(position);

            ImageView restaurantImageView = (ImageView)thisView.findViewById(R.id.restaurantIcon);
            restaurantImageView.setImageResource(selectedRestaurant.getRestaurantIconId());

            TextView restaurantNameTextView=(TextView)thisView.findViewById(R.id.restaurantName);
            restaurantNameTextView.setText(selectedRestaurant.getRestaurantName());

            return thisView;
        }

    }


}
