package com.vms.enums;

/**
 * Enumerates the types of government-issued identity proof a visitor may present.
 *
 * <p>Used during visitor registration to record the type of identification
 * document provided for verification purposes.</p>
 */
public enum IdProofType {

    /** Aadhaar card (India’s unique identity number). */
    AADHAAR,

    /** Permanent Account Number (PAN) card. */
    PAN,

    /** International passport. */
    PASSPORT,

    /** Driving license. */
    DRIVING_LICENSE,

    /** Voter identification card. */
    VOTER_ID,

    /** Any other valid government-issued ID. */
    OTHER
}
