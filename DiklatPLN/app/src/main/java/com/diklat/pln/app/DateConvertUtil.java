package com.diklat.pln.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Fandy Aditya on 7/14/2017.
 */

public class DateConvertUtil extends SimpleDateFormat {
    public String convert(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat secondSdf = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        Date returnDate;
        String secondDate;
        String returnVal = "";
        try {
            returnDate = sdf.parse(date);
            secondDate = secondSdf.format(returnDate);
            returnVal = secondDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnVal;
    }
    public String reverseConvert(String date){
        SimpleDateFormat secondSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);
        Date returnDate;
        String secondDate;
        String returnVal = "";
        try {
            returnDate = sdf.parse(date);
            secondDate = secondSdf.format(returnDate);
            returnVal = secondDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnVal;
    }
}
