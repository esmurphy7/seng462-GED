package com.teamged.txserver.transactions;

import com.teamged.txserver.InternalLog;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by danie on 2016-02-01.
 */
public class UserQueueObject {
    private final ConcurrentHashMap<Integer, TransactionObject> requestSequence;
    private final ReentrantLock lock;
    private int nextProcessCount;
    private int nextRequestIndex;
    private boolean processingFlag;

    public UserQueueObject() {
        requestSequence = new ConcurrentHashMap<>(/* TODO: Args */);
        lock = new ReentrantLock();
        nextProcessCount = 1;
        nextRequestIndex = 1;
        processingFlag = false;
    }

    public void addTransactionObject(TransactionObject txObject) {
        InternalLog.Log("Adding txObject " + txObject.getUserSeqNum() + ", " + txObject.toString());
        requestSequence.put(txObject.getUserSeqNum(), txObject);
    }

    /**
     * @return
     */
    public TransactionObject getNextTransactionObject() {
        Integer nextInteger = nextRequestIndex;
        InternalLog.Log("Fetching txObject with sequence " + nextRequestIndex);

        /*System.out.print("Keys: ");
        for (Integer i : requestSequence.keySet()) {
            System.out.print(i.intValue() + " ");
        }
        System.out.println();*/

        TransactionObject txObject = requestSequence.remove(nextInteger);
        if (txObject != null) {
            nextRequestIndex++;
            if (nextProcessCount > 1) {
                nextProcessCount--;
            }
        } else {
            nextProcessCount++;
        }

        return txObject;
    }

    /**
     * Attempts to claim the processing flag. Will set it if it is not currently set. Use this as a lock guard before
     * accessing from a thread.
     *
     * @return Whether the processing flag was set.
     */
    public boolean tryClaimProcessingFlag() {
        boolean claimed = false;
        lock.lock();
        if (!processingFlag) {
            processingFlag = true;
            claimed = true;
        }
        lock.unlock();

        return claimed;
    }

    /**
     * Releases the processing flag. Only call this if your thread claimed the processing flag. Always call this as
     * cleanup if your thread claimed the processing flag.
     */
    public void releaseProcessingFlag() {
        lock.lock();
        processingFlag = false;
        lock.unlock();
    }

    public int getNextProcessCount() {
        return nextProcessCount;
    }

    public int getNextRequestIndex() {
        return nextRequestIndex;
    }
}
