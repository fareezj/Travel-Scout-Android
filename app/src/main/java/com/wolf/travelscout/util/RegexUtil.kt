package com.wolf.travelscout.util

import java.util.regex.Pattern

object RegexUtil {

    const val EMAIL_REGEX = "^[A-Za-z0-9](.*)([@]{1})(.{1,})(\\.)(.{1,})$"

    fun validateEmailAddress(email: String): Boolean {
        if(email.isNotEmpty()){
            return EMAIL_REGEX.toRegex().matches(email)
        }
        return false
    }
}