package com.chaitupenjudcoder;

import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityBucksBinding;
import com.chaitupenjudcoder.datapojos.CategoriesAmount;
import com.chaitupenjudcoder.firebasehelpers.BucksWidgetHelper;
import com.chaitupenjudcoder.firebasehelpers.FirebaseCategoriesHelper;
import com.chaitupenjudcoder.firebasehelpers.SharedPreferencesHelper;
import com.chaitupenjudcoder.recyclerviews.BucksOverviewRecycler;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class BucksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    com.github.clans.fab.FloatingActionButton incomeFab, expenseFab;
    FloatingActionMenu fab;
    ActivityBucksBinding bucks;
    NavigationView navigationView;

    FirebaseAuth mAuth;
    FirebaseUser user;

    private static final boolean BUCKS_INCOME = true;
    public static final String BUCKS_STRING_IS_INCOME_EXTRA = "income_extra";
    private static final boolean BUCKS_EXPENSE = false;

    public static final String INCOME_EXPENSE_OBJECT_EXTRA = "income_expense_extra";
    String currencySymbol;
    int totalIncome, totalExpense;

    TextView username, usermail;

    TextView income, expense, balance;
    Intent addIncExp;

    Spinner categories;
    RecyclerView rv_categories;

    SharedPreferencesHelper h;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bucks = DataBindingUtil.setContentView(this, R.layout.activity_bucks);
        PreferenceManager.setDefaultValues(this, R.xml.bucks_preferences, true);
        h = new SharedPreferencesHelper(this);

        new BucksWidgetHelper().callIntentService(this, h.getWidgetOptionPref("Last Income"));

        //get Firebase authentication instance and user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        income = findViewById(R.id.tv_income_amount);
        expense = findViewById(R.id.tv_expense_amount);
        balance = findViewById(R.id.tv_balance_amount);

        categories = findViewById(R.id.spi_categories_choose);
        // populate spinner function which populates with two classifications of categories
        populateCategorySpinner();

        rv_categories = findViewById(R.id.rv_categories_amount);

        Toolbar toolbar = findViewById(R.id.toolbar);

        //floating action buttons for rvIncome and rvExpense adding activities
        incomeFab = findViewById(R.id.fab_add_income);
        expenseFab = findViewById(R.id.fab_add_expnese);

        //fab on click listeners, opens same activity and changes title and data insertion of activity based on fab selection
        incomeFab.setOnClickListener(v -> {
            fab.close(true);
            addIncExp = new Intent(new Intent(BucksActivity.this, AddIncomeExpenseActivity.class));
            addIncExp.putExtra(BUCKS_STRING_IS_INCOME_EXTRA, BUCKS_INCOME);
            startActivity(addIncExp);
        });

        expenseFab.setOnClickListener(v -> {
            fab.close(true);
            addIncExp = new Intent(new Intent(BucksActivity.this, AddIncomeExpenseActivity.class));
            addIncExp.putExtra(BUCKS_STRING_IS_INCOME_EXTRA, BUCKS_EXPENSE);
            startActivity(addIncExp);
        });

        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
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

    //gets the total rvIncome and rvExpense from firebase
    private void setTotalIncomeAndExpense() {
        //  initialize categories helper class
        final FirebaseCategoriesHelper helper = new FirebaseCategoriesHelper();

        //  Get two totals one inside the other and set them to textviews
        helper.getCategoryTotal(total1 -> helper.getCategoryTotal(total2 -> {
            totalIncome = total1;
            totalExpense = total2;
            //set all the totals
            setCurrencyAndTotal(total1, total2);
        }, "expense"), "income");
    }

    public void setCurrencyAndTotal(int totalIncome, int totalExpense) {
        Resources res = getResources();
        currencySymbol = h.getCurrencyPref("R");
        income.setText(res.getString(R.string.currency_symbol, currencySymbol, totalIncome));
        expense.setText(res.getString(R.string.currency_symbol, currencySymbol, totalExpense));
        balance.setText(res.getString(R.string.currency_symbol, currencySymbol, totalIncome - totalExpense));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  set first option as always checked when app resumes or comes back from another activity
        navigationView.getMenu().getItem(0).setChecked(true);
        setCurrencyAndTotal(totalIncome, totalExpense);
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

        Intent in = new Intent(BucksActivity.this, BucksTransactions.class);
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_week:
                in.putExtra("WEEK", 7L);
                startActivity(in);
                return true;
            case R.id.action_month:
                in.putExtra("MONTH", 30L);
                startActivity(in);
                return true;
            case R.id.action_date_range:
                startActivity(new Intent(new Intent(BucksActivity.this, DateChooserActivity.class)));
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

    private void populateCategorySpinner() {
        final String[] categorie = {"INCOME", "EXPENSE"};
        ArrayAdapter<String> cats = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_dropdown_item, categorie);
        categories.setAdapter(cats);

        final FirebaseCategoriesHelper categoriesHelper = new FirebaseCategoriesHelper();
        categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                categoriesHelper.getCategoryTotal(total -> {
//                        Log.d("abcde", "inside categories total"+total);
                    categoriesHelper.getAllCategories(categoriesList -> getIncomeExpenseCategoriesTotal(categoriesList, total), categorie[position].toLowerCase());
                }, categorie[position].toLowerCase());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getIncomeExpenseCategoriesTotal(final ArrayList<String> cats, final int total) {
        Query categorySalary = FirebaseDatabase.getInstance().getReference("data/" + user.getUid() + "/spendings");
        final ArrayList<CategoriesAmount> catAmount = new ArrayList<>();

        categorySalary.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int[] amounts = new int[cats.size()];
                int cnt = 0;
                String[] categories;
                categories = cats.toArray(new String[0]);
                for (String category : categories) {
                    for (DataSnapshot catShot : dataSnapshot.getChildren()) {
                        if (catShot.child("category").getValue(String.class).equals(category)) {
                            amounts[cnt] += Integer.valueOf(catShot.child("amount").getValue(String.class));
                        }
                    }
                    catAmount.add(new CategoriesAmount(category, String.valueOf(amounts[cnt]), ((float) amounts[cnt] / total) * 100));
                    cnt++;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    catAmount.sort((CategoriesAmount first, CategoriesAmount second) -> (int) (second.getPercentage() - first.getPercentage()));
                } else {
                    Collections.sort(catAmount, (o1, o2) -> (int) (o2.getPercentage() - o1.getPercentage()));
                }
                BucksOverviewRecycler overviewRecycler = new BucksOverviewRecycler(getApplicationContext(), catAmount);
                RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
                rv_categories.setLayoutManager(manager);
                rv_categories.setNestedScrollingEnabled(false);
                rv_categories.setAdapter(overviewRecycler);
                //  set bottom to top layout animation
                rv_categories.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.categorywise_card_bottom_animation));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
