package com.mad.localist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class NewEntryActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText groceryArticleEditText, groceryQuantityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        groceryArticleEditText = findViewById(R.id.groceryArticleEditText);
        groceryQuantityEditText = findViewById(R.id.groceryQuantityEditText);

        String entryName = (String) getIntent().getSerializableExtra("groceryEntryName");
        String entryQuantity = (String) getIntent().getSerializableExtra("groceryEntryQuantity");
        groceryArticleEditText.setText(entryName);
        groceryQuantityEditText.setText(entryQuantity);


        toolbar = findViewById(R.id.newEntryToolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_save_entry){
            Intent returnIntent = new Intent();
            int entryPosition = getIntent().getIntExtra("groceryEntryPosition", -1);

            returnIntent.putExtra("groceryEntryName", groceryArticleEditText.getText().toString());
            returnIntent.putExtra("groceryEntryPosition", entryPosition);
            returnIntent.putExtra("groceryEntryQuantity", groceryQuantityEditText.getText().toString());
            // Add other putExtra calls for other data

            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_entry_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}