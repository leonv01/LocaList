package com.mad.localist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private GroceryListAdapter groceryListAdapter;
    private ListView groceryListView;
    private List<GroceryEntry> groceryEntryList;
    private Toolbar toolbar;
    private ActivityResultLauncher<Intent> launcher;
    private GroceryEntry deletedEntry, addedEntry;

    private HashMap<String, ArrayList<GroceryEntry>> groceryEntryHashMap;

    private final GroceryDbHelper dbHelper = new GroceryDbHelper(this);

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Geocoder geocoder;

    private GroceryNotifier groceryNotifier;
    @SuppressLint("VisibleForTests")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groceryNotifier = new GroceryNotifier(this);

        deletedEntry = null;
        addedEntry = null;

        toolbar = findViewById(R.id.groceryMenuBar);
        setSupportActionBar(toolbar);

        groceryEntryList = new ArrayList<>();

        groceryListAdapter = new GroceryListAdapter(groceryEntryList);
        groceryListView = findViewById(R.id.groceryListView);
        groceryListView.setAdapter(groceryListAdapter);

        groceryEntryHashMap = new HashMap<>();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(@NonNull com.google.android.gms.location.LocationResult locationResult) {
                for (android.location.Location location : locationResult.getLocations()) {
                    groceryEntryHashMap.forEach((key, value) -> {
                        float[] dist = new float[1];
                        Location targetLocation = getLocationFromAddress(key);
                        assert targetLocation != null;
                        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                                targetLocation.getLatitude(), targetLocation.getLongitude(), dist);
                        if (dist[0] < 100) {
                            groceryNotifier.showOrUpdateNotification(value);
                        }
                    });
                }
            }
        };

        geocoder = new Geocoder(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if(location != null){
                Log.e("Location", location.toString());
            }
            else{
                Log.e("Location", "Location is null");
            }
        });

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {


                        String entryName = result.getData().getStringExtra("groceryEntryName");
                        String entityQuantity = result.getData().getStringExtra("groceryEntryQuantity");
                        String entityPrice = result.getData().getStringExtra("groceryEntryPrice");
                        String entryDetails = result.getData().getStringExtra("groceryEntryDetails");
                        String entryLocation = result.getData().getStringExtra("groceryEntryLocation");
                        String entryImage = result.getData().getStringExtra("groceryEntryImagePath");

                        // Extract other data fields similarly

                        // Use the extracted data to create a new GroceryEntry


                        int position = result.getData().getIntExtra("groceryEntryPosition", -1);

                        Log.e("Position", String.valueOf(position));

                        GroceryEntry groceryEntry = new GroceryEntry(
                                entryName, entityQuantity, entityPrice, entryDetails,"",entryImage, entryLocation
                        );


                        if(position >= 0){
                            groceryEntryList.get(position).setData(groceryEntry);
                        }
                        else{
                            groceryEntryList.add(groceryEntry);
                        }

                        Toast toast = Toast.makeText(this, "Entry saved", Toast.LENGTH_SHORT);
                        toast.show();


                        // Assuming groceryEntryList is accessible here
                        groceryListAdapter.notifyDataSetChanged(); // Correct method name
                    } else if(result.getResultCode() == RESULT_CANCELED && result.getData() != null){
                        int position = result.getData().getIntExtra("groceryEntryPosition", -1);

                        Toast toast;
                        if(position >= 0) {
                               deletedEntry = groceryEntryList.get(position);
                            toast = Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT);
                        }
                        else
                            toast = Toast.makeText(this, "Entry canceled", Toast.LENGTH_SHORT);
                        toast.show();
                        groceryListAdapter.notifyDataSetChanged(); // Correct method name

                    }
                    else {
                        Log.e("MainActivity", "Failed to get data from NewEntryActivity");
                    }
                }

        );

        groceryListView.setOnItemClickListener((parent, view, position, id) -> {
            GroceryEntry groceryEntry = groceryListAdapter.data.get(position);
            Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
            intent.putExtra("groceryEntryName", groceryEntry.getName());
            intent.putExtra("groceryEntryQuantity", groceryEntry.getQuantity());
            intent.putExtra("groceryEntryPrice", groceryEntry.getPrice());
            intent.putExtra("groceryEntryDetails", groceryEntry.getDetails());
            intent.putExtra("groceryEntryLocation", groceryEntry.getLocation());
            intent.putExtra("groceryEntryImagePath", groceryEntry.getPhotoPath());

            intent.putExtra("groceryEntryPosition", position);

            launcher.launch(intent);
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                GroceryDbHelper.GROCERY_COL_ARTICLE,
                GroceryDbHelper.GROCERY_COL_DESCRIPTION,
                GroceryDbHelper.GROCERY_COL_QUANTITY,
                GroceryDbHelper.GROCERY_COL_LOCATION,
                GroceryDbHelper.GROCERY_COL_PRICE
        };

        Cursor cursor = db.query(GroceryDbHelper.GROCERY_TABLE, projection, null, null,
                null, null, null);



        while(cursor.moveToNext()){
            String groceryArticle = cursor.getString(cursor.getColumnIndexOrThrow(GroceryDbHelper.GROCERY_COL_ARTICLE));
            String groceryQuantity = cursor.getString(cursor.getColumnIndexOrThrow(GroceryDbHelper.GROCERY_COL_QUANTITY));
            String groceryDetails = cursor.getString(cursor.getColumnIndexOrThrow(GroceryDbHelper.GROCERY_COL_DESCRIPTION));
            String groceryLocation = cursor.getString(cursor.getColumnIndexOrThrow(GroceryDbHelper.GROCERY_COL_LOCATION));
            String groceryPrice = cursor.getString(cursor.getColumnIndexOrThrow(GroceryDbHelper.GROCERY_COL_PRICE));

            GroceryEntry entry = new GroceryEntry(
                    groceryArticle, groceryQuantity,
                    groceryPrice, groceryDetails, "", "", groceryLocation
            );

            if(!groceryEntryList.contains(entry)){
                groceryEntryList.add(entry);

            }

        }
        groceryListAdapter.notifyDataSetChanged();

        db.delete(GroceryDbHelper.GROCERY_TABLE, null, null);

        db.close();
        cursor.close();

        if(deletedEntry != null){
            groceryEntryList.remove(deletedEntry);
            deletedEntry = null;
        }

        groceryEntryHashMap.clear();
        groceryEntryList.forEach(groceryEntry -> {
            groceryEntryHashMap.computeIfAbsent(groceryEntry.getLocation(), k -> new ArrayList<GroceryEntry>());
            Objects.requireNonNull(groceryEntryHashMap.get(groceryEntry.getLocation())).add(groceryEntry);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        fusedLocationClient.removeLocationUpdates(locationCallback);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        groceryEntryList.forEach(groceryEntry -> {
            ContentValues values = new ContentValues();

            values.put(GroceryDbHelper.GROCERY_COL_ARTICLE, groceryEntry.getName());
            values.put(GroceryDbHelper.GROCERY_COL_DESCRIPTION, groceryEntry.getDetails());
            values.put(GroceryDbHelper.GROCERY_COL_QUANTITY, groceryEntry.getQuantity());
            values.put(GroceryDbHelper.GROCERY_COL_LOCATION, groceryEntry.getLocation());
            values.put(GroceryDbHelper.GROCERY_COL_PRICE, groceryEntry.getPrice());

            db.insert(GroceryDbHelper.GROCERY_TABLE, null, values);

        });

        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_add_entry){
            Intent newEntryIntent = new Intent(MainActivity.this, NewEntryActivity.class);
            launcher.launch(newEntryIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.grocery_list_menu, menu);
        return true;
    }

    private String getAddressForLocation(Location location){
        List<Address> addresses = null;
        try{
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        if(addresses != null && addresses.size() > 0){
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }
        else{
            return "No address found";
        }
    }

    private Location getLocationFromAddress(String address){
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList;
        Location location = null;

        try{
            addressList = geocoder.getFromLocationName(address, 5);
            if(addressList == null){
                return null;
            }
            Address locationAddress = addressList.get(0);
            location = new Location("Location");
            location.setLatitude(locationAddress.getLatitude());
            location.setLongitude(locationAddress.getLongitude());
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return location;
    }
}