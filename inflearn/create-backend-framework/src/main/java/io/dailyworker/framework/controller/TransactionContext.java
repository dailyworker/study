package io.dailyworker.framework.controller;

import io.dailyworker.framework.db.SQLiteJdbcTransaction;
import io.dailyworker.framework.db.Transaction;

import java.sql.SQLException;

public class TransactionContext {

    private static final ThreadLocal<Transaction> THREAD_LOCAL = new ThreadLocal<>();

    private TransactionContext() {}

    public static Transaction get() {
        Transaction transaction = THREAD_LOCAL.get();
        if(hasTransaction(transaction)) {
            return transaction;
        }
        transaction = createDefaultTransaction();
        load(transaction);
        return transaction;
    }

    static void load(Transaction transaction) {
        THREAD_LOCAL.set(transaction);
    }

    private static Transaction createDefaultTransaction() {
        return new SQLiteJdbcTransaction();
    }

    private static boolean hasTransaction(Transaction transaction) {
        return transaction != null;
    }

    static void unload() {
        Transaction transaction = THREAD_LOCAL.get();
        if(hasTransaction(transaction)) {
            THREAD_LOCAL.get().closeConnection();
        }
        THREAD_LOCAL.remove();
    }

    public static void commit() throws SQLException {
        Transaction transaction = THREAD_LOCAL.get();
        if(!hasTransaction(transaction)) {
            return;
        }
        transaction.commit();
    }

    public static void rollback() throws SQLException {
        Transaction transaction = THREAD_LOCAL.get();
        if(!hasTransaction(transaction)) {
            return;
        }
        transaction.rollback();
    }
}
