package com.example.foodspy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecentlyViewed extends AppCompatActivity {
    List<String> recents = new ArrayList<>();
    String[] placeIDs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_viewed);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("feedme", Context.MODE_PRIVATE);
        Set<String> recentList = pref.getStringSet("recents", null);
        if(recentList != null) {
            int count=0;
            placeIDs = new String[recentList.size()];
            for (String details : recentList) {
                String[] placeDetails = details.split("\\|");
                recents.add(count, placeDetails[1]);
                placeIDs[count] = placeDetails[0];
                count++;
            }
            try {
                ListView list = (ListView) findViewById(R.id.listView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), R.layout.list_item, R.id.place, recents);
                list.setAdapter(adapter);
            }catch (Exception e){
                e.getMessage();
            }
        }
        ListView lv = (ListView)findViewById(R.id.listView);
        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu m, View v, ContextMenu.ContextMenuInfo i){
        if(v.getId()==R.id.listView){
            m.setHeaderTitle("Choose Action");
            m.add(Menu.NONE, 0, 0, "View Details");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if (item.getItemId() == 0){
            Intent i = new Intent(getApplication(), EstablishmentActivity.class);
            i.putExtra("placeID", placeIDs[index]);
            startActivity(i);
        }
        return true;
    }
}
