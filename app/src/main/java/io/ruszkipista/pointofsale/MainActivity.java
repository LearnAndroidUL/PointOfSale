package io.ruszkipista.pointofsale;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private TextView mNameTextView, mQuantityTextView, mDateTextView;
    private Item mCurrentItem;
    private ArrayList<Item> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // boiler plate code, don't mess with it
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//      grab text views of item details
        mNameTextView = findViewById(R.id.name_text);
        mQuantityTextView = findViewById(R.id.quantity_text);
        mDateTextView = findViewById((R.id.date_text));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
    }

    private void addItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_additem,null,false);
        builder.setView(view);
        final EditText nameEditTextView = view.findViewById(R.id.edit_name);
        final EditText quantityEditTextView = view.findViewById(R.id.edit_quantity);
        final CalendarView deliveryDateCalendarView = view.findViewById(R.id.calendar_view);
        final GregorianCalendar deliveryDate = new GregorianCalendar();

        deliveryDateCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                deliveryDate.set(year,month,dayOfMonth);
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = nameEditTextView.getText().toString();
                int itemQuantity = Integer.parseInt(quantityEditTextView.getText().toString());
                mCurrentItem = new Item(itemName,itemQuantity,deliveryDate);
//              add new item to list
                mItems.add(mCurrentItem);
                showCurrentItem();
            }
        });
        builder.setNegativeButton(android.R.string.cancel,null);
        builder.create().show();
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
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (menuItem.getItemId()){
            case R.id.action_reset:
                final Item mSavedItem = mCurrentItem;
                mItems.remove(mCurrentItem);
                mCurrentItem = new Item();
                showCurrentItem();
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),R.string.confirmation_snack_removeone,Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentItem = mSavedItem;
//                      re-add restored item to list
                        mItems.add(mCurrentItem);
                        showCurrentItem();
                        Snackbar.make(findViewById(R.id.coordinator_layout),R.string.confirmation_snack_restored,Snackbar.LENGTH_LONG).show();
                    }
                });
                snackbar.show();
                return true;

            case R.id.action_search:
                showDialogItemList();
                return true;

            case R.id.action_settings:
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                return true;
        };
        return super.onOptionsItemSelected(menuItem);
    }

    private void showDialogItemList() {

    }
}
