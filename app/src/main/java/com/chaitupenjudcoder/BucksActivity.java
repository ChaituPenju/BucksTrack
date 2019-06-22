package com.chaitupenjudcoder;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.CategoriesActivity;
import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityBucksBinding;
import com.chaitupenjudcoder.datapojos.AddIncomeExpense;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class BucksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean isFABOpen;
    FloatingActionButton fab1, fab2;
    ActivityBucksBinding bucks;

    FirebaseAuth mAuth;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference myRef;

    private static final String BUCKS_STRING_INCOME = "income";
    public static final String BUCKS_STRING_INCOME_EXTRA = "income_extra";
    private static final String BUCKS_STRING_EXPENSE = "expense";
    private static final String BUCKS_STRING_EXPENSE_EXTRA = "expense_extra";

    Intent addIncExp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bucks = DataBindingUtil.setContentView(this, R.layout.activity_bucks);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Toast.makeText(this, "name is "+user.getUid(), Toast.LENGTH_SHORT).show();
        Toolbar toolbar = findViewById(R.id.toolbar);
        fab1 = findViewById(R.id.fab_add_income);
        fab2 = findViewById(R.id.fab_add_expnese);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                addIncExp = new Intent(new Intent(BucksActivity.this, AddIncomeActivity.class));
                addIncExp.putExtra(BUCKS_STRING_INCOME_EXTRA, BUCKS_STRING_INCOME);
                startActivity(addIncExp);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                addIncExp = new Intent(new Intent(BucksActivity.this, AddIncomeActivity.class));
                addIncExp.putExtra(BUCKS_STRING_EXPENSE_EXTRA, BUCKS_STRING_EXPENSE);
                startActivity(addIncExp);
            }
        });
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //set first option as always checked
        navigationView.getMenu().getItem(0).setChecked(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        final EditText et = findViewById(R.id.et_firebase);
        Button btn = findViewById(R.id.btn_firebase);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("chaitnaya");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddIncomeExpense myExpense = new AddIncomeExpense("title", "4000","2019-06-15", "this is test insertion", "coding", "income");
                if (myExpense.getBucksString().equals("income")) {
                    myRef.setValue(et.getText().toString());
                }
                ArrayList<AddIncomeExpense> myExpenseList = new ArrayList<>();
                for (int i=0;i<4;i++) {
                    myExpenseList.add(new AddIncomeExpense(myExpense.getTitle()+i, myExpense.getAmount()+i, myExpense.getDate(), myExpense.getNote()+i, myExpense.getCategory()+i, "income"));
                }
                myExpenseList.add(myExpense);
                myRef.child("income").setValue(myExpenseList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(BucksActivity.this, "Its Successful.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BucksActivity.this, "Exception is -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showFABMenu() {
        isFABOpen = true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_dimen_income));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_dimen_expense));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bucks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_week:
                return true;
            case R.id.action_month:
                return true;
            case R.id.action_date_range:
                startActivity(new Intent(BucksActivity.this, DateChooserActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_overview) {

        } else if (id == R.id.nav_transactions) {
            startActivity(new Intent(BucksActivity.this, BucksTransactions.class));
        } else if (id == R.id.nav_categories) {
            startActivity(new Intent(BucksActivity.this, CategoriesActivity.class));
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            //sign out user
            mAuth.signOut();
            finish();
            startActivity(new Intent(BucksActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
