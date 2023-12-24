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

    private EditText groceryArticleEditText, groceryQuantityEditText, groceryPriceEditText;
    private EditText groceryDetailsMultiline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        groceryArticleEditText = findViewById(R.id.groceryArticleEditText);
        groceryQuantityEditText = findViewById(R.id.groceryQuantityEditText);
        groceryPriceEditText = findViewById(R.id.groceryPriceEditText);
        groceryDetailsMultiline = findViewById(R.id.groceryDetailsMultiline);

        String entryName = (String) getIntent().getSerializableExtra("groceryEntryName");
        String entryQuantity = (String) getIntent().getSerializableExtra("groceryEntryQuantity");
        String entryPrice = (String) getIntent().getSerializableExtra("groceryEntryPrice");
        String entryDetails = (String) getIntent().getSerializableExtra("groceryEntryDetails");

        groceryArticleEditText.setText(entryName == null ? "" : entryName);
        groceryQuantityEditText.setText(entryQuantity == null ? "" : entryQuantity);
        groceryPriceEditText.setText(entryPrice == null ? "" : entryPrice);
        groceryDetailsMultiline.setText(entryDetails == null ? "" : entryDetails);

        toolbar = findViewById(R.id.newEntryToolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_save_entry){
            Intent returnIntent = new Intent();
            int entryPosition = getIntent().getIntExtra("groceryEntryPosition", -1);

            returnIntent.putExtra("groceryEntryName", groceryArticleEditText.getText().toString());
            returnIntent.putExtra("groceryEntryQuantity", groceryQuantityEditText.getText().toString());
            returnIntent.putExtra("groceryEntryPrice", groceryPriceEditText.getText().toString());
            returnIntent.putExtra("groceryEntryDetails", groceryDetailsMultiline.getText().toString());

            returnIntent.putExtra("groceryEntryPosition", entryPosition);
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