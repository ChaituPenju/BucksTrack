package com.chaitupenjudcoder.firebasehelpers

import com.google.android.material.textfield.TextInputLayout

class BucksInputValidationHelper {
    fun inputValidator(layout: TextInputLayout, length: Int): Boolean {
        val input = layout.editText!!.text.toString()

        return when {
            input.isEmpty() -> {
                layout.error = "Field can\'t be empty"
                false
            }

            input.length > length -> {
                layout.error = "Field " + layout.hint + "\'s length can\'t be greater than " + length
                false
            }

            else -> {
                layout.error = null
                true
            }
        }
    }

    fun passwordValidator(first: TextInputLayout, second: TextInputLayout): Boolean {
        val firstInput = first.editText?.text.toString().trim { it <= ' ' }
        val secondInput = second.editText?.text.toString().trim { it <= ' ' }

        return if (firstInput == secondInput) {
            second.error = null
            true
        } else {
            second.error = "Password and Confirm Password must match"
            false
        }
    }
}