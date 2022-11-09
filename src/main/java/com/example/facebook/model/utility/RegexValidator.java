package com.example.facebook.model.utility;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class RegexValidator {

    /*
    PASSWORD
    At least 8 chars
    Contains at least one digit
    Contains at least one lower alpha char and one upper alpha char
    Contains at least one char within a set of special chars (@#%$^ etc.)
    Does not contain space, tab, etc.
    */
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])" +
                                                        "(?=.*[@#$%^&+=])(?=\\S+$).{8,35}$";
    public static final String FIRST_LAST_NAME_REGEX = "([A-Z][a-z]*)([\\\\s\\\\\\'-][A-Z][a-z]*)*";

    public static final String DATE_REGEX = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";

    public static final String EMAIL_REGEX = "([A-Za-z0-9-_.]+@[A-Za-z0-9-_]+(?:\\.[A-Za-z0-9]+)+)";

    public static final String PHONE_NUMBER_REGEX = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})" +
                                                            "[- .]?\\d{3,4}[- .]?\\d{4}$";

    public static boolean patternNames(String name) {
        return !Pattern.compile(FIRST_LAST_NAME_REGEX)
                .matcher(name)
                .matches();
    }
    public static boolean patternEmails(String email) {
        return !Pattern.compile(EMAIL_REGEX)
                .matcher(email)
                .matches();
    }
    public static boolean patternPhoneNumber(String number) {
        return !Pattern.compile(PHONE_NUMBER_REGEX)
                .matcher(number)
                .matches();
    }
    public static boolean patternPassword(String pass) {
        return !Pattern.compile(PASSWORD_REGEX)
                .matcher(pass)
                .matches();
    }
    public static boolean patternDate(LocalDate date) {
        String d = date.toString();
        return !Pattern.compile(DATE_REGEX)
                .matcher(d)
                .matches();
    }
}
