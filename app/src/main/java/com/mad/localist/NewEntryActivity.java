package com.mad.localist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class NewEntryActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText groceryArticleEditText, groceryQuantityEditText, groceryPriceEditText;
    private EditText groceryDetailsMultiline, groceryLocationEditText;
    private Button showInMaps;
    private String imagePath = "";
    private ImageView imageView;

    private final int REQUEST_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_entry);

        showInMaps = (Button) findViewById(R.id.showOnMapButton);

        imageView = findViewById(R.id.imageView);

        groceryArticleEditText = findViewById(R.id.groceryArticleEditText);
        groceryQuantityEditText = findViewById(R.id.groceryQuantityEditText);
        groceryPriceEditText = findViewById(R.id.groceryPriceEditText);
        groceryDetailsMultiline = findViewById(R.id.groceryDetailsMultiline);
        groceryLocationEditText = findViewById(R.id.groceryLocationEditText);

        String entryName = (String) getIntent().getSerializableExtra("groceryEntryName");
        String entryQuantity = (String) getIntent().getSerializableExtra("groceryEntryQuantity");
        String entryPrice = (String) getIntent().getSerializableExtra("groceryEntryPrice");
        String entryDetails = (String) getIntent().getSerializableExtra("groceryEntryDetails");
        String entryLocation = (String) getIntent().getSerializableExtra("groceryEntryLocation");

        groceryArticleEditText.setText(entryName == null ? "" : entryName);
        groceryQuantityEditText.setText(entryQuantity == null ? "" : entryQuantity);
        groceryPriceEditText.setText(entryPrice == null ? "" : entryPrice);
        groceryDetailsMultiline.setText(entryDetails == null ? "" : entryDetails);
        groceryLocationEditText.setText(entryLocation == null ? "" : entryLocation);

        showInMaps.setOnClickListener(ev -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=".concat(groceryLocationEditText.getText().toString())));
            startActivity(intent);
        });


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
            returnIntent.putExtra("groceryEntryLocation", groceryLocationEditText.getText().toString());

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

    public void deleteEntry(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent returnIntent = new Intent();
                    int entryPosition = getIntent().getIntExtra("groceryEntryPosition", -1);
                    returnIntent.putExtra("groceryEntryPosition", entryPosition);
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                })
                .setNegativeButton("No", null);
        builder.create().show();
    }
}