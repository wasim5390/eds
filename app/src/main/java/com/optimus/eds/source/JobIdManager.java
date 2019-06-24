package com.optimus.eds.source;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

public class JobIdManager {

    public static final int JOB_TYPE_MASTER_UPLOAD = 1;
    public static final int JOB_TYPE_CHANNEL_METADATA = 2;
    public static final int JOB_TYPE_CHANNEL_DELETION = 3;
    public static final int JOB_TYPE_CHANNEL_LOGGER = 4;

    public static final int JOB_TYPE_USER_PREFS = 11;
    public static final int JOB_TYPE_USER_BEHAVIOR = 21;

    @IntDef(value = {
            JOB_TYPE_MASTER_UPLOAD,
            JOB_TYPE_CHANNEL_METADATA,
            JOB_TYPE_CHANNEL_DELETION,
            JOB_TYPE_CHANNEL_LOGGER,
            JOB_TYPE_USER_PREFS,
            JOB_TYPE_USER_BEHAVIOR
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface JobType {
    }

    //16-1 for short. Adjust per your needs
    private static final int JOB_TYPE_SHIFTS = 20;

    public static int getJobId(@JobType int jobType, int objectId) {
        if ( 0 < objectId && objectId < (1<< JOB_TYPE_SHIFTS) ) {
            return (jobType << JOB_TYPE_SHIFTS) + objectId;
        } else {
            String err = String.format("objectId %s must be between %s and %s",
                    objectId,0,(1<<JOB_TYPE_SHIFTS));
            throw new IllegalArgumentException(err);
        }
    }
}