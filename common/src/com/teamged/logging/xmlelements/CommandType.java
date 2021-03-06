package com.teamged.logging.xmlelements;

public enum CommandType {
    ADD,
    QUOTE,
    BUY,
    COMMIT_BUY,
    CANCEL_BUY,
    SELL,
    COMMIT_SELL,
    CANCEL_SELL,
    SET_BUY_AMOUNT,
    CANCEL_SET_BUY,
    SET_BUY_TRIGGER,
    SET_SELL_AMOUNT,
    SET_SELL_TRIGGER,
    CANCEL_SET_SELL,
    DUMPLOG,
    DISPLAY_SUMMARY;

    public String value() {
        return name();
    }

    public static CommandType fromValue(String v) {
        return valueOf(v);
    }

}
