package com.example.foodspy;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

public class EstablishmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment);
        new AsyncDetails().execute();

    }
    private void getEstDetails(String response){
        try {
            JSONObject jObject = new JSONObject(response);
            JSONObject jPlaces = jObject.getJSONObject("result");

            String[] listItems = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
            if (jPlaces.has("name")) {
                listItems[0] = (jPlaces.getString("name"));
            }
            if (jPlaces.has("formatted_address")) {
                listItems[1] = (jPlaces.getString("formatted_address"));
            } else {
                listItems[2] = "Address not available";
            }
            if (jPlaces.has("formatted_phone_number")) {
                listItems[2] = (jPlaces.getString("formatted_phone_number"));
            } else {
                listItems[2] = "Phone number not available";
            }
            listItems[3] = "Opening Hours:";
            if (jPlaces.has("opening_hours")) {
                JSONObject openingHours = jPlaces.getJSONObject("opening_hours");
                JSONArray weekdayTimes = openingHours.getJSONArray("weekday_text");
                for (int i = 0; i < weekdayTimes.length(); i++) {
                    listItems[i + 4] = weekdayTimes.getString(i);
                }
            } else {
                listItems[4] = "Not available";
                listItems[5] = "Not available";
                listItems[6] = "Not available";
                listItems[7] = "Not available";
                listItems[8] = "Not available";
                listItems[9] = "Not available";
                listItems[10] = "Not available";
            }
            if (jPlaces.has("rating")) {
                listItems[11] = "Rating: " + (jPlaces.getString("rating"));
            } else {
                listItems[11] = "No rating";
            }
            listItems[12] = "Reviews:";
            if (jPlaces.has("reviews")) {
                JSONArray reviewObject = jPlaces.getJSONArray("reviews");
                Log.d("Reviews", reviewObject.toString());
                int reviews = reviewObject.length() < 1 ? 2 : 1;
                for (int i = 0; i < reviews; i++) {
                    listItems[13 + i] = reviewObject.getJSONObject(i).getString("author_name") + " - " + reviewObject.getJSONObject(i).getString("text");
                }
            } else {
                listItems[13] = "No review available";
                listItems[14] = "No review available";
            }
            ListView list = (ListView) findViewById(R.id.placeInfo);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), R.layout.list_item, R.id.place, listItems);
            list.setAdapter(adapter);
        }catch (Exception e) {
            Log.e("Map", e.getMessage());
        }

    }
    class AsyncDetails extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {
            final String response = new HttpClient().getRestaurantDetails(getIntent().getExtras().getString("placeID"));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getEstDetails(response);
                }
            });

            return null;
        }
    }
}

