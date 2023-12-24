package com.mad.localist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GroceryListAdapter groceryListAdapter;
    private ListView groceryListView;
    private Toolbar toolbar;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.groceryMenuBar);
        setSupportActionBar(toolbar);

        GroceryEntry entry = new GroceryEntry("Hallo", "Test", "AMOGUS", "Tits", "meow", "");
        List<GroceryEntry> groceryEntryList = new ArrayList<>();
        groceryEntryList.add(entry);

        groceryListAdapter = new GroceryListAdapter(groceryEntryList);
        groceryListView = findViewById(R.id.groceryListView);
        groceryListView.setAdapter(groceryListAdapter);

        groceryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("TEST", "TEST");
                return false;
            }
        });


        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String entryName = result.getData().getStringExtra("groceryEntryName");
                        String entityQuantity = result.getData().getStringExtra("groceryEntryQuantity");
                        // Extract other data fields similarly

                        // Use the extracted data to create a new GroceryEntry
                        GroceryEntry newEntry = new GroceryEntry(
                                entryName, entityQuantity, "", "","",""
                        );

                        int position = result.getData().getIntExtra("groceryEntryPosition", -1);
                        if(position >= 0){
                            groceryEntryList.remove(position);
                            groceryEntryList.add(newEntry);
                        }
                        else{
                            groceryEntryList.add(newEntry);
                        }

                        // Assuming groceryEntryList is accessible here
                        groceryListAdapter.notifyDataSetChanged(); // Correct method name
                    } else {
                        Log.e("MainActivity", "Failed to get data from NewEntryActivity");
                    }
                }
        );

        groceryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroceryEntry groceryEntry = groceryListAdapter.data.get(position);
                String entryName = groceryEntry.getName();

                Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
                intent.putExtra("groceryEntryName", entryName);
                intent.putExtra("groceryEntryQuantity", groceryEntry.getQuantity());
                intent.putExtra("groceryEntryPosition", position);

                launcher.launch(intent);
            }
        });
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