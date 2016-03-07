package com.teamged.txserver.transactions;

/**
 * Created by DanielF on 2016-01-31.
 */
public enum UserCommand {
    NO_COMMAND,
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
    DUMPLOG_ROOT,
    DISPLAY_SUMMARY
}
