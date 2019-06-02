package com.example.foodspy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavouritesActivity extends AppCompatActivity{
    Set<String> favouritesList;
    List<String> favourites = new ArrayList<>();
    String[] placeIDs;
    Set<String> recentList = new HashSet<>();
    SharedPreferences.Editor editor;
    int count = 0;

    ArrayAdapter<String> adapter;
    // Search EditText
    EditText inputSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        updateList();
        inputSearch = (EditText) findViewById(R.id.search);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu m, View v, ContextMenu.ContextMenuInfo i){
        if(v.getId()==R.id.listView2){
            m.setHeaderTitle("Choose Action");
            m.add(Menu.NONE, 0, 0, "View Details");
            m.add(Menu.NONE, 1, 1, "Remove from Favourites");
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
            recentList.add(placeIDs[index]+"|"+favourites.get(index));
            if(recentList == null){
                recentList = new HashSet<>();
            }
            Set<String> set = new HashSet<>(recentList);

            editor.putStringSet("recents", set);
            editor.commit();
            startActivity(i);
        }else if(item.getItemId() == 1){
            favouritesList.remove(placeIDs[info.position]+"|"+favourites.get(info.position));
            SharedPreferences pref = getApplicationContext().getSharedPreferences("feedme", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            Set<String> mySet = new HashSet<>(favouritesList);
            editor.putStringSet("favourites", mySet);
            editor.commit();
            Log.d("Favourites", "Removed from favourites: "+favourites.get(info.position));
            updateList();
        }
        return true;
    }
    private void updateList(){
        count=0;
        favourites.clear();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("feedme", Context.MODE_PRIVATE);
        favouritesList = pref.getStringSet("favourites", null);
        if(favouritesList != null) {
            placeIDs = new String[favouritesList.size()];
            for (String details : favouritesList) {
                String[] placeDetails = details.split("\\|");
                favourites.add(count, placeDetails[1]);
                placeIDs[count] = placeDetails[0];
                count++;
            }
            ListView list = (ListView) findViewById(R.id.listView2);
            list.setAdapter(null);
            adapter = new ArrayAdapter<>(getApplication(), R.layout.list_item, R.id.place, favourites);
            list.setAdapter(adapter);
        }
        recentList = pref.getStringSet("recents", null);
        if(recentList == null){
            recentList = new HashSet<>();
        }
        ListView favouritesList = (ListView)findViewById(R.id.listView2);
        registerForContextMenu(favouritesList);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EditText searchBox = (EditText) findViewById(R.id.search);
        String search = searchBox.getText().toString();
        SharedPreferences.Editor editor = getSharedPreferences("feedme", 0).edit();
        editor.putString("search", search);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EditText searchBox = (EditText) findViewById(R.id.search);
        String search = searchBox.getText().toString();
        SharedPreferences.Editor editor = getSharedPreferences("feedme", 0).edit();
        editor.putString("search", search);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText searchBox = (EditText) findViewById(R.id.search);
        SharedPreferences editor = getSharedPreferences("feedme", 0);
        if(editor.getString("search", null) != null) {
            searchBox.setText(editor.getString("search", null));
        }
    }
}
