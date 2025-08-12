package com.trustai.common_base.constants;

public class SecurityConstants {
    private static final long VALIDITY_1_HOUR   = 3600_000; // 60 * 60 * 1000
    private static final long VALIDITY_2_MINUTE = 120_000;   // 1 * 60 * 1000

    public static final int OTP_LENGTH = 6;
    public static final long OTP_TTL_MILLIS = 5 * 60 * 1000;  // 5 minutes
    public static final long JWT_EXPIRE_MILLS = VALIDITY_2_MINUTE;
}
