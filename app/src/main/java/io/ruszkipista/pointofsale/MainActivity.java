package io.ruszkipista.pointofsale;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mNameTextView, mQuantityTextView, mDateTextView;
    private Item mCurrentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // boiler plate code , don't mess with it
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//      grab text viewa of item details
        mNameTextView = findViewById(R.id.name_text);
        mQuantityTextView = findViewById(R.id.quantity_text);
        mDateTextView = findViewById((R.id.date_text));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentItem = Item.getDefaultItem();
                showCurrentItem();
            }
        });
    }

    private void showCurrentItem() {
        mNameTextView.setText(getString(R.string.name_format, mCurrentItem.getName()));
        mQuantityTextView.setText(getString(R.string.quantity_format, mCurrentItem.getQuantity()));
        mDateTextView.setText(getString(R.string.date_format,mCurrentItem.getDeliveryDateString()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_reset:
                final Item mSavedItem = mCurrentItem;
                mCurrentItem = new Item();
                showCurrentItem();
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),R.string.confirmation_snack_removeone,Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentItem = mSavedItem;
                        showCurrentItem();
                        Snackbar.make(findViewById(R.id.coordinator_layout),R.string.confirmation_snack_restored,Snackbar.LENGTH_LONG).show();
                    }
                });
                snackbar.show();
                return true;

            case R.id.action_settings:
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                return true;
        };
        return super.onOptionsItemSelected(item);
    }
}
