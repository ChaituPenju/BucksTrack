package com.chaitupenjudcoder.firebasehelpers;

import android.support.design.widget.TextInputLayout;

public class BucksInputValidationHelper {

    public boolean inputValidator(TextInputLayout layout, int length) {
        String input = layout.getEditText().getText().toString();
        if (input.isEmpty()) {
            layout.setError("Field can\'t be empty");
            return false;
        } else if (input.length() > length){
            layout.setError("Field " + layout.getHint() + "\'s length can\'t be greater than " + length);
            return false;
        }else {
            layout.setError(null);
            return true;
        }
    }
}
