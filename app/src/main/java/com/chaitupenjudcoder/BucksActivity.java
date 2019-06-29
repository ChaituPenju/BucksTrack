package com.chaitupenjudcoder;

import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chaitupenjudcoder.buckstrack.CategoriesActivity;
import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityBucksBinding;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BucksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean isFABOpen;
    com.github.clans.fab.FloatingActionButton incomeFab, expenseFab;
    FloatingActionMenu fab;
    ActivityBucksBinding bucks;

    FirebaseAuth mAuth;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference myRef;

    private static final boolean BUCKS_INCOME = true;
    public static final String BUCKS_STRING_IS_INCOME_EXTRA = "income_extra";
    private static final boolean BUCKS_EXPENSE = false;

    TextView username, usermail;

    TextView income, expense, balance;
    Intent addIncExp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bucks = DataBindingUtil.setContentView(this, R.layout.activity_bucks);


        //get Firebase authentication instance and user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        income = findViewById(R.id.tv_income_amount);
        expense = findViewById(R.id.tv_expense_amount);
        balance = findViewById(R.id.tv_balance_amount);

//        Toast.makeText(this, "time is " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
        Toolbar toolbar = findViewById(R.id.toolbar);
        //floating action buttons for income and expense adding activities
        incomeFab = findViewById(R.id.fab_add_income);
        expenseFab = findViewById(R.id.fab_add_expnese);
        //fab on click listeners, opens same activity and changes title and data insertion of activity based on fab selection
        incomeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.close(true);
                addIncExp = new Intent(new Intent(BucksActivity.this, AddIncomeExpenseActivity.class));
                addIncExp.putExtra(BUCKS_STRING_IS_INCOME_EXTRA, BUCKS_INCOME);
                startActivity(addIncExp);
            }
        });

        expenseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.close(true);
                addIncExp = new Intent(new Intent(BucksActivity.this, AddIncomeExpenseActivity.class));
                addIncExp.putExtra(BUCKS_STRING_IS_INCOME_EXTRA, BUCKS_EXPENSE);
                startActivity(addIncExp);
            }
        });

        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //set first option as always checked
        navigationView.getMenu().getItem(0).setChecked(true);

        //set username and email in nav header bucks
        username = navigationView.getHeaderView(0).findViewById(R.id.tv_user_name);
        usermail = navigationView.getHeaderView(0).findViewById(R.id.tv_user_email);

        //handles nullpointer exception and sets the value
        if (user != null) {
            for (UserInfo info : user.getProviderData()) {
                username.setText(info.getDisplayName());
                usermail.setText(info.getEmail());
            }
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //get database instance and reference


        setTotalIncomeAndExpense();
    }

    //gets the total income and expense from firebase
    private void setTotalIncomeAndExpense() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("data/" + user.getUid());

        ValueEventListener getTotals = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int incomeTotal = 0, expenseTotal = 0, balanceTotal;
                String bucksStr;
                for (DataSnapshot incomeExpenseTotal : dataSnapshot.child("spendings").getChildren()) {
                    bucksStr = incomeExpenseTotal.child("bucksString").getValue(String.class);
                    int amount = Integer.valueOf(incomeExpenseTotal.child("amount").getValue(String.class));
                    if (bucksStr != null) {
                        if (bucksStr.equals("expense")) {
                            expenseTotal += amount;
                        } else if (bucksStr.equals("income")) {
                            incomeTotal += amount;
                        }
                    }
                }
                balanceTotal = incomeTotal - expenseTotal;

                //set all the totals to the
                Resources res = getResources();

                income.setText(res.getString(R.string.indian_currency_symbol, incomeTotal));
                expense.setText(res.getString(R.string.indian_currency_symbol, expenseTotal));
                balance.setText(res.getString(R.string.indian_currency_symbol, balanceTotal));
//                Toast.makeText(BucksActivity.this, ""+expenseTotal, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.addValueEventListener(getTotals);
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
            startActivity(new Intent(BucksActivity.this, SettingsActivity.class));
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
