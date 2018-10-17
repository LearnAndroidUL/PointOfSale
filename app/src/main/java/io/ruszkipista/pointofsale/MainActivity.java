package io.ruszkipista.pointofsale;

import android.app.Dialog;
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
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

//      register "Name" view component for context menu (long press)
        registerForContextMenu(mNameTextView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputItem(false);
            }
        });

//      fill items list with 3 entries
        mItems.add(new Item("Eaxample 1", 10, new GregorianCalendar()));
        mItems.add(new Item("Eaxample 2", 15, new GregorianCalendar()));
        mItems.add(new Item("Eaxample 3", 30, new GregorianCalendar()));


        mCurrentItem = getNewItem();
        showCurrentItem();
    }

    private Item getNewItem() {
        return new Item("- - - -",0, new GregorianCalendar());
    }

    private void inputItem(final boolean isEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_additem,null,false);
        builder.setView(view);
        final EditText nameEditTextView = view.findViewById(R.id.edit_name);
        final EditText quantityEditTextView = view.findViewById(R.id.edit_quantity);
        final CalendarView deliveryDateCalendarView = view.findViewById(R.id.calendar_view);
        final GregorianCalendar deliveryDate = new GregorianCalendar();
        if (isEdit) {
            nameEditTextView.setText(mCurrentItem.getName());
            quantityEditTextView.setText( Integer.toString(mCurrentItem.getQuantity()));
            deliveryDateCalendarView.setDate(mCurrentItem.getDeliveryDateTime());
        }

        deliveryDateCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                deliveryDate.set(year,month,dayOfMonth);
            }
        });

        builder.setNegativeButton(android.R.string.cancel,null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = nameEditTextView.getText().toString();
                int itemQuantity = Integer.parseInt(quantityEditTextView.getText().toString());
                if (isEdit){
//                  update current item with changed details
                    mCurrentItem.setName(itemName);
                    mCurrentItem.setQuantity(itemQuantity);
                    mCurrentItem.setDeliveryDate(deliveryDate);
                } else {
//                  create new item with captured details
                    mCurrentItem = new Item(itemName, itemQuantity, deliveryDate);
//                  add new item to list
                    mItems.add(mCurrentItem);
                }
                showCurrentItem();
            }
        });
        builder.create().show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // Inflate the options menu; this adds items to context menu of field Name
        getMenuInflater().inflate(R.menu.menu_context_name, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem contextItem) {
        switch (contextItem.getItemId()) {
            case R.id.action_context_edit:
                inputItem(true);
                Toast.makeText(this,"TODO: edit item",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_context_remove:
                removeCurrentItem();
                return true;
        }
        return super.onContextItemSelected(contextItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle action bar (options) item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (menuItem.getItemId()){
            case R.id.action_options_removeone:
                removeCurrentItem();
                return true;

            case R.id.action_options_removeall:
                showDialogRemoveAll();
                return true;

            case R.id.action_options_search:
                showDialogItemList();
                return true;

            case R.id.action_options_settings:
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                return true;
        };
        return super.onOptionsItemSelected(menuItem);
    }

    private void removeCurrentItem() {
        final Item mSavedItem = mCurrentItem;
        mItems.remove(mCurrentItem);
        mCurrentItem = getNewItem();
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
    }

    private void showDialogRemoveAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_remove);
        builder.setMessage(R.string.confirmation_dialog_removeall);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mItems.clear();
                mCurrentItem = getNewItem();
                showCurrentItem();
            }
        });
        builder.setNegativeButton(android.R.string.cancel,null);
        builder.create().show();
    }

    private void showDialogItemList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_itemlist_title);
        builder.setItems(getItemNames(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentItem = mItems.get(which);
                showCurrentItem();
            }
        });
        builder.setNegativeButton(android.R.string.cancel,null);
        builder.create().show();
    }

    private String[] getItemNames() {
        String[] names = new String[mItems.size()];
        for (int i=0;i<mItems.size();i++){
            names[i] = mItems.get(i).getName();
        }
        return names;
    }

    private void showCurrentItem() {
        mNameTextView.setText(getString(R.string.name_format, mCurrentItem.getName()));
        mQuantityTextView.setText(getString(R.string.quantity_format, mCurrentItem.getQuantity()));
        mDateTextView.setText(getString(R.string.date_format,mCurrentItem.getDeliveryDateString()));
    }
}
