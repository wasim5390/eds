package com.optimus.eds.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Patterns;

import com.optimus.eds.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Util {
    public static final String DATE_FORMAT_1 = "MM/dd/yyyy hh:mm:ss";
    public static final String DATE_FORMAT_2 = "MMM dd";
    public static final String DATE_FORMAT_3 = "MMM-dd";
    public static final String DATE_FORMAT_5 = "hh:mm a";
    public static final String DATE_FORMAT_4 = "MM/dd/yyyy hh:mm a";

    private static final String TAG = "Util";

   /* public static String getAuthorizationHeader(Context context) throws UnsupportedEncodingException {
        String username = PreferenceUtil.getInstance(context).getUsername();
        String password = PreferenceUtil.getInstance(context).getPassword();

        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            return encodeBase64(username + ":" + password);
        } else {
            return null;
        }
    }*/

    public static String getAuthorizationHeader(Context context) {
        String token = PreferenceUtil.getInstance(context).getToken();
        if(token.isEmpty())
            return null;
        return token;
    }



    public static boolean isValidEmail(final String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }


    public static String encodeBase64(String s) throws UnsupportedEncodingException {
        byte[] data = s.getBytes("UTF-8");
        String encoded = Base64.encodeToString(data, Base64.NO_WRAP);
        return encoded;
    }


    public static String formatDate(String format, Long dateInMilli) {
        if(dateInMilli==null)
            return "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateInMilli);
            return simpleDateFormat.format(calendar.getTime());
        }catch (Exception e){
            return "";
        }

    }


    public static Date getDateFromMilliseconds(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return calendar.getTime();
    }

    public static Long convertDateToMilli(String date,String format){
        long timeInMilliseconds;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }finally {
            timeInMilliseconds = new Date().getTime();
        }
        return timeInMilliseconds;
    }

    public static boolean isDateToday(Long dateInMilli){

       return DateUtils.isToday(dateInMilli);

    }

    public static boolean isPastDate(Long dateInMilli){
        boolean isDatePassed=false;
        String dateString = formatDate(DATE_FORMAT_2,dateInMilli);
        try {
            if (new SimpleDateFormat("MM/yyyy").parse(dateString).before(new Date())) {
                isDatePassed=true;
            }
        } catch (ParseException e) {
            isDatePassed=false;
            e.printStackTrace();
        }catch (Exception e){
            isDatePassed=false;
        }
        return isDatePassed;
    }

    /**
     * get uri from file path
     * @param path
     * @return
     */
    public static Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    /**
     * copy stream up-to 2 mb
     * @param input
     * @param output
     * @throws IOException
     */
    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[2048];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }



    /**
     * Encode base64 string into bitmap
     *
     * @param base64 the base 64 string
     * @return the result bitmap
     */
    public static Bitmap base64ToBitmapDecode(String base64) {
        Bitmap bitmap = null;

        byte[] imageAsBytes = Base64.decode(base64.getBytes(), Base64.DEFAULT);

        bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

        return bitmap;
    }


    public static String imageFileToBase64(File file){
        if (file.exists() && file.length() > 0) {
            Bitmap bm = BitmapFactory.decodeFile(file.getPath());
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bOut);
            String encoded = Base64.encodeToString(bOut.toByteArray(), Base64.NO_WRAP);
            //encoded = "data:image/jpeg;base64,"+encoded;
            return encoded;
        }
        return null;
    }

    public static String compressBitmap(Bitmap bitmap){
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bOut);
        String encoded = Base64.encodeToString(bOut.toByteArray(), Base64.NO_WRAP);
        return encoded;
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * This function is being used in Order Booking where edittext must be empty in case of no carton/units
     * @param carton
     * @param units
     * @return
     */
    public static String convertToNullableDecimalQuantity(Integer carton, Integer units)
    {
        if(carton==null)
            carton=0;
        if(units==null)
            units=0;

        String finalVal="";
        if(carton>0 && units>0)
            finalVal= String.valueOf(carton).concat("."+String.valueOf(units));
        else if(carton>0){
            finalVal = String.valueOf(carton);
        }else if(units>0)
            finalVal = ".".concat(String.valueOf(units));


        return finalVal;
    }

    public static String convertToDecimalQuantity(Integer carton, Integer units)
    {
        if(carton==null)
            carton=0;
        if(units==null)
            units=0;

        String finalVal="";
        if(carton>0 && units>0)
            finalVal= String.valueOf(carton).concat("."+String.valueOf(units));
        else if(carton>0){
            finalVal = String.valueOf(carton);
        }else if(units>0)
            finalVal = ".".concat(String.valueOf(units));
        else
            finalVal = "0.0";

        return finalVal;
    }

    public static Integer convertToUnits(Integer carton,Integer cartonSize, Integer units)
    {
        if(carton==null)
            carton=0;
        if(units==null)
            units=0;

        Integer finalVal=0;
        if(carton<1)
            finalVal = units;
        else{
            int totalUnits = carton * cartonSize ;
            finalVal=totalUnits + units;
        }

        return finalVal;
    }


    public static Integer[] convertToLongQuantity(String qty)
    {
        if(qty.startsWith("."))
            qty = "0".concat(qty);
        Integer decimal;
        Integer fractional ;

        boolean isWhole = Util.isInt(Double.parseDouble(qty));
        if(isWhole) {
            decimal = Integer.parseInt(qty.split("\\.")[0]);
            fractional=0;
        }else {
            decimal = Integer.parseInt(qty.split("\\.")[0]);
            fractional = Integer.parseInt(qty.split("\\.")[1]);
        }
        return new Integer[]{decimal,fractional};
    }

    public static boolean isInt(double d)
    {
        return d == (long) d;
    }

    public static boolean isListEmpty(List list){
        return (list==null || list.isEmpty());
    }

    public static boolean moveFile(File file, File dir) throws Exception {
        boolean fileMoved=false;
        File newFile = new File(dir, file.getName());
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            file.delete();

            fileMoved = true;
        } catch (IOException exception){
            fileMoved = false;
        }
        finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
            return fileMoved;

        }
    }



    public static String formatCurrency(Double price){
        if(price==null)
            price=0d;

        DecimalFormat fmt = (DecimalFormat) NumberFormat.getInstance();
        Locale locale = new Locale("en", "pk");
        String symbol = Currency.getInstance(locale).getSymbol(locale);
        fmt.setGroupingUsed(true);
        fmt.setPositivePrefix(symbol + " ");
        fmt.setNegativePrefix("-" + symbol + " ");
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);
        return fmt.format(price);


    }

    public static String formatCurrency(Double price,int fractionDigit){
        if(price==null)
            price=0d;

        DecimalFormat fmt = (DecimalFormat) NumberFormat.getInstance();
        Locale locale = new Locale("en", "pk");
        String symbol = Currency.getInstance(locale).getSymbol(locale);
        fmt.setGroupingUsed(true);
        fmt.setPositivePrefix(symbol + " ");
        fmt.setNegativePrefix("-" + symbol + " ");
        fmt.setMinimumFractionDigits(fractionDigit);
        fmt.setMaximumFractionDigits(fractionDigit);
        return fmt.format(price);


    }

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
