package com.mad.localist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GroceryListAdapter groceryListAdapter;
    private ListView groceryListView;
    private List<GroceryEntry> groceryEntryList;
    private Toolbar toolbar;
    private ActivityResultLauncher<Intent> launcher;
    private GroceryEntry deletedEntry, addedEntry;

    private HashMap<String, GroceryEntry> groceryEntryHashMap;

    GroceryDbHelper dbHelper = new GroceryDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deletedEntry = null;
        addedEntry = null;

        toolbar = findViewById(R.id.groceryMenuBar);
        setSupportActionBar(toolbar);

        groceryEntryList = new ArrayList<>();

        groceryListAdapter = new GroceryListAdapter(groceryEntryList);
        groceryListView = findViewById(R.id.groceryListView);
        groceryListView.setAdapter(groceryListAdapter);

        groceryEntryHashMap = new HashMap<>();


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

        groceryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                GroceryDbHelper.GROCERY_COL_ARTICLE,
                GroceryDbHelper.GROCERY_COL_DESCRIPTION,
                GroceryDbHelper.GROCERY_COL_QUANTITY
        };

        Cursor cursor = db.query(GroceryDbHelper.GROCERY_TABLE, projection, null, null,
                null, null, null);



        while(cursor.moveToNext()){
            String groceryArticle = cursor.getString(cursor.getColumnIndexOrThrow(GroceryDbHelper.GROCERY_COL_ARTICLE));
            String groceryQuantity = cursor.getString(cursor.getColumnIndexOrThrow(GroceryDbHelper.GROCERY_COL_QUANTITY));
            String groceryDetails = cursor.getString(cursor.getColumnIndexOrThrow(GroceryDbHelper.GROCERY_COL_DESCRIPTION));
            //String groceryLocation = cursor.getString(cursor.getColumnIndexOrThrow(GroceryDbHelper.GROCERY_COL_LOCATION));

            GroceryEntry entry = new GroceryEntry(
                    groceryArticle, groceryQuantity,
                    "", groceryDetails, "", "", ""
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
            groceryEntryHashMap.put(groceryEntry.getLocation(), groceryEntry);
        });
        Log.e("Size", String.valueOf(groceryEntryHashMap.size()));
    }

    @Override
    protected void onPause() {
        super.onPause();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        groceryEntryList.forEach(groceryEntry -> {
            ContentValues values = new ContentValues();

            values.put(GroceryDbHelper.GROCERY_COL_ARTICLE, groceryEntry.getName());
            values.put(GroceryDbHelper.GROCERY_COL_DESCRIPTION, groceryEntry.getDetails());
            values.put(GroceryDbHelper.GROCERY_COL_QUANTITY, groceryEntry.getQuantity());

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
}