package com.example.foodspy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashSet;
import java.util.Set;

public class NearbyActivity extends AppCompatActivity {
    String[] places;
    String[] placeIDs;
    Set<String> favouritesList;
    Set<String> recentList = new HashSet<>();
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        try {
            getNearby(new AsyncNearby().execute().get());
        }catch (Exception e){

        }
        ListView nearby = (ListView)findViewById(R.id.listView3);
        registerForContextMenu(nearby);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("feedme", Context.MODE_PRIVATE);
        favouritesList = pref.getStringSet("favourites", null);
        if(favouritesList == null){
            favouritesList = new HashSet<>();
        }
        recentList = pref.getStringSet("recents", null);
        if(recentList == null){
            recentList = new HashSet<>();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu m, View v, ContextMenu.ContextMenuInfo i){
        if(v.getId()==R.id.listView3){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) i;
            m.setHeaderTitle("Choose Action");
            m.add(Menu.NONE, 0, 0, "View Details");
            if (favouritesList.contains(placeIDs[info.position]+"|"+places[info.position])) {
                m.add(Menu.NONE, 1, 1, "Remove from Favourites");
            } else {
                m.add(Menu.NONE, 1, 1, "Add to Favourites");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if (item.getItemId() == 0){
            Intent i = new Intent(getApplication(), EstablishmentActivity.class);
            i.putExtra("placeID", placeIDs[index]);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("feedme", Context.MODE_PRIVATE);
            editor = pref.edit();
            recentList = pref.getStringSet("recents", null);
            recentList.add(placeIDs[index]+"|"+places[index]);
            Set<String> set = new HashSet<String>(recentList);
            editor.putStringSet("recents", set);
            editor.commit();
            startActivity(i);
        }else if(item.getItemId() == 1){
            if (favouritesList.contains(placeIDs[info.position]+"|"+places[info.position])) {
                favouritesList.remove(placeIDs[info.position]+"|"+places[info.position]);
            } else {
                favouritesList.add(placeIDs[info.position]+"|"+places[info.position]);
            }
            SharedPreferences pref = getApplicationContext().getSharedPreferences("feedme", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            Set<String> mySet = new HashSet<String>(favouritesList);
            editor.putStringSet("favourites", mySet);
            editor.commit();
        }
        return true;
    }

    private void getNearby(String api){
        try {
            JSONObject jObject = new JSONObject(api);
            JSONArray jPlaces = jObject.getJSONArray("results");
            int placesCount = jPlaces.length();
            places = new String[placesCount];
            placeIDs = new String[placesCount];
            for (int i = 0; i < placesCount; i++) {
                JSONObject p = (JSONObject) jPlaces.get(i);
                placeIDs[i] = p.getString("place_id");
                places[i] = p.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ListView list = (ListView)findViewById(R.id.listView3);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), R.layout.list_item, R.id.place, places);
        list.setAdapter(adapter);
    }
    class AsyncNearby extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... urls) {
            return new HttpClient().getNearbyRestaurants(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude"), getIntent().getExtras().getString("estType"));
        }
    }
}

