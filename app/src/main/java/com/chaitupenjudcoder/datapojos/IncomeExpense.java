package com.chaitupenjudcoder.datapojos;

import android.os.Parcel;
import android.os.Parcelable;

public class IncomeExpense implements Parcelable {
    private String title;
    private String amount;
    private String date;
    private String note;
    private String category;
    private String bucksString;
    private String id;

    public IncomeExpense() {
    }

    public IncomeExpense(String title, String amount, String date, String note, String category, String bucksString) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.category = category;
        this.bucksString = bucksString;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public String getCategory() {
        return category;
    }

    public String getBucksString() {
        return bucksString;
    }

    public String getId() {
        return id;
    }

    protected IncomeExpense(Parcel in) {
        title = in.readString();
        amount = in.readString();
        date = in.readString();
        note = in.readString();
        category = in.readString();
        bucksString = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(amount);
        dest.writeString(date);
        dest.writeString(note);
        dest.writeString(category);
        dest.writeString(bucksString);
        dest.writeSerializable(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<IncomeExpense> CREATOR = new Parcelable.Creator<IncomeExpense>() {
        @Override
        public IncomeExpense createFromParcel(Parcel in) {
            return new IncomeExpense(in);
        }

        @Override
        public IncomeExpense[] newArray(int size) {
            return new IncomeExpense[size];
        }
    };
}
