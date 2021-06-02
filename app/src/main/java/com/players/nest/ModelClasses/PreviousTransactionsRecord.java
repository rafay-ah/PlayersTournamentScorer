package com.players.nest.ModelClasses;

public class PreviousTransactionsRecord {

    public static final String APPROVED = "APPROVED";
    public static final String PENDING = "PENDING";
    public static final String WITHDRAW = "Withdrawal";
    public static final String DEPOSITED = "Deposited";

    String userId, transactionID, date, type, transactionState, paypalEmail, govName;
    double amount, withDrawAmount;
    double accountBalance;


    public PreviousTransactionsRecord(String userId, String transactionID, String date, String type, String transactionState, String email, String govName, double amount, double withDrawAmount, double accountBalance) {
        this.userId = userId;
        this.transactionID = transactionID;
        this.date = date;
        this.type = type;
        this.transactionState = transactionState;
        this.paypalEmail = email;
        this.govName = govName;
        this.amount = amount;
        this.accountBalance = accountBalance;
        this.withDrawAmount = withDrawAmount;
    }


    public PreviousTransactionsRecord() {

    }

//    public PreviousTransactionsRecord(String userId, String transactionID, String date, double amount, String type, String transactionState) {
//        this.userId = userId;
//        this.date = date;
//        this.amount = amount;
//        this.type = type;
//        this.transactionID = transactionID;
//        this.transactionState = transactionState;
//    }

    public String getUserId() {
        return userId;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getDate() {
        return date;
    }

    public Double getAmount() {
        return amount;
    }

//    private String SerializeDoubleToString(double amount) {
//        return String.valueOf(amount);
//    }

    public String getType() {
        return type;
    }

    public String getTransactionState() {
        return transactionState;
    }


    public String getPaypalEmail() {
        return paypalEmail;
    }

    public String getGovName() {
        return govName;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public double getWithDrawAmount() {
        return withDrawAmount;
    }
}
