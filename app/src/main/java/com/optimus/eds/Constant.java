package com.optimus.eds;

/**
 * Created by sidhu on 4/18/2018.
 */

public interface Constant {
    int UNASSIGNED=0;
    int ASSIGNED=1;
    int STARTED = 2;
    int PAUSED=3;
    int STOPPED=4;
    int COMPLETED=5;
    int CANCELLED=6;

    int QUERY_LIMIT=15;
    int MAX_HORIZONTAL_ACCURACY_LIMIT=24;

    String notSynced="Not synced yet.";
    String alertTitle = "Oops!";
    String missingRequiredFields = "Missing Required Fields.";
    String dontLeaveUserPassEmpty="Don't leave username and password empty.";
    String enterSamePasswordTwice="Please enter the same password twice.";
    String enterVerificationCode = "Please enter verification code, which we sent on your mobile.";
    String verificationCodeEmpty= "Verification code is empty.";
    String chooseYourCountry="Please select your country.";
    String invalidMobileNumber="Please provide valid mobile number.";
    String invalidEmail="Please provide valid email address.";
    String emptyVerificationCode="Please provide verification code to proceed.";

    /** App Modes*/
    String DEVELOPMENT="Development";
    String PRODUCTION="Production";
    String DEBUG="Debugging";

    //Status Codes
    int SUCCESS = 200;
    int BAD_REQUEST = 400;

    String KEY_SCANNER_STARTER="key_scanner_starter";
    String KEY_SCANNER_RESULT="key_scanner_result";

    interface IntentExtras {
        String ACTION_CAMERA = "action-camera";
        String ACTION_GALLERY = "action-gallery";
        String IMAGE_PATH = "image-path";
    }
    interface ProductType {
        String FREE = "free";
        String PAID = "paid";

    }
}
