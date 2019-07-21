package com.chaitupenjudcoder.firebasehelpers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.chaitupenjudcoder.datapojos.IncomeExpense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FirebaseTransactionsHelper {
    public static final String WEEK_EXTRA = "week";
    public static final String MONTH_EXTRA = "month";
    public static final String BETWEEN_TWO_DATES_EXTRA = "between_two_dates";
    public static final String DATE_ONE_EXTRA = "date1";
    public static final String DATE_TWO_EXTRA = "date2";

    private DatabaseReference mTransactionsRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String uid;
    private ArrayList<IncomeExpense> transactions = new ArrayList<>();

    public FirebaseTransactionsHelper() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        uid = mUser.getUid();
        mTransactionsRef = FirebaseDatabase.getInstance().getReference("data/" + uid + "/spendings");
    }

    public interface GetAllTransactions {
        void allTransactions(ArrayList<IncomeExpense> transactions);
    }

    public interface AddTransaction {
        void addTransaction(String response);
    }

    public interface UpdateTransaction {
        void updateTransaction(String response);
    }

    public interface DeleteTransaction {
        void deleteTransaction(String response);
    }

    public interface WeekMonthTransactions {
        void getWeekMonthTransactions(ArrayList<IncomeExpense> transactions);
    }

    public interface TransactionsBetweenTwoDates {
        void getTransactionsBetweenTwoDates(ArrayList<IncomeExpense> transactions);
    }

    public void getTransactionsBwTwoDates(final TransactionsBetweenTwoDates bwTwoDates, String date1, String date2, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat + " HH:mm:ss.SSS", Locale.ENGLISH);
        SimpleDateFormat defFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.ENGLISH);
        Date startDate = null, endDate = null;
        long start, end;
        try {
            startDate = format.parse(date1 + " 00:00:00.000");
            endDate = format.parse(date2 + " 00:00:00.000");
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        Log.d("XXXXX", startDate + "   " + endDate);

        start = startDate.getTime() / 1000;
        end = endDate.getTime() / 1000;

        ValueEventListener listener = new ValueEventListener() {
            long d;
            String date = null;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dateShot : dataSnapshot.getChildren()) {
                    IncomeExpense ie = dateShot.getValue(IncomeExpense.class);
                    date = ie.getDate();

                    try {
                        d = defFormat.parse(date + " 00:00:00.000").getTime() / 1000;
                    } catch (ParseException pe) {
                        pe.printStackTrace();
                    }
                    if (start <= d && d <= end) {
                        transactions.add(ie);
                    }
                }
                bwTwoDates.getTransactionsBetweenTwoDates(transactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        };
        mTransactionsRef.addListenerForSingleValueEvent(listener);


    }

    private long getDaysDiff(String date1) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        String[] split = date1.split("-");
        cal2.set(Integer.valueOf(split[2]), Integer.valueOf(split[1]) - 1, Integer.valueOf(split[0]));
        long diff = cal1.getTimeInMillis() - cal2.getTimeInMillis();
        return diff / (24 * 60 * 60 * 1000);
    }

    public void getWeekOrMonthTransactions(final WeekMonthTransactions wmTransactions, long worm) {

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot spendingShot : dataSnapshot.getChildren()) {
                    IncomeExpense ie = spendingShot.getValue(IncomeExpense.class);
                    String date = ie.getDate();
                    long daysDiff = getDaysDiff(date);
                    if (daysDiff > 0 && daysDiff < worm) {
                        transactions.add(ie);
                    }
                }
                wmTransactions.getWeekMonthTransactions(transactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mTransactionsRef.addListenerForSingleValueEvent(listener);
    }

    public void deleteATransaction(final DeleteTransaction transaction, String key) {
        mTransactionsRef.child(key).setValue(null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                transaction.deleteTransaction("Data is Deleted!");
            } else {
                transaction.deleteTransaction("Something Went Wrong!!!");
            }
        });
    }

    public void updateATransaction(final UpdateTransaction transaction, String key, IncomeExpense ie) {
        ie.setId(key);
        mTransactionsRef.child(key).setValue(ie).addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                transaction.updateTransaction("Data Updated Successfully");
            } else {
                transaction.updateTransaction("Something went Wrong!!!");
            }
        });
    }

    public void addATransaction(final AddTransaction transaction, IncomeExpense ie) {
        String id = mTransactionsRef.push().getKey();
        ie.setId(id);
        mTransactionsRef.child(ie.getId()).setValue(ie).addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                transaction.addTransaction("Data Saved Successfully");
            } else {
                transaction.addTransaction("Something went Wrong!!!");
            }
        });
    }

    public void getAllTransactions(final GetAllTransactions allTransactions) {

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot incomeExpenseShot : dataSnapshot.getChildren()) {
                    IncomeExpense expenseIncome = incomeExpenseShot.getValue(IncomeExpense.class);
                    transactions.add(expenseIncome);
                }
                allTransactions.allTransactions(transactions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
            }
        };
        mTransactionsRef.addValueEventListener(postListener);
    }
}
