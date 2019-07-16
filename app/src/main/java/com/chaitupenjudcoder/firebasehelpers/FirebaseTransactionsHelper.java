package com.chaitupenjudcoder.firebasehelpers;

import com.chaitupenjudcoder.datapojos.IncomeExpense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseTransactionsHelper {

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
