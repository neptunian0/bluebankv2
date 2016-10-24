package com.lloydtucker.bluebankv2.pojos;

import com.lloydtucker.bluebankv2.helpers.ApiAdapterType;
import com.lloydtucker.bluebankv2.helpers.PaymentStatus;

/**
 * Created by lloydtucker on 24/10/2016.
 */

public class Payments {
    private String id;
    private String fromAccountId;
    private String toAccountNumber;
    private String toSortCode;
    private String paymentReference;
    private double paymentAmount;
    private String otpCode;
    private PaymentStatus paymentStatus;
    private ApiAdapterType apiAdapterType;

    public Payments(String fromAccountId,
                    String toAccountNumber,
                    String toSortCode,
                    String paymentReference,
                    double paymentAmount) {
        this.fromAccountId = fromAccountId;
        this.toAccountNumber = toAccountNumber;
        this.toSortCode = toSortCode;
        this.paymentReference = paymentReference;
        this.paymentAmount = paymentAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public String getToSortCode() {
        return toSortCode;
    }

    public void setToSortCode(String toSortCode) {
        this.toSortCode = toSortCode;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public ApiAdapterType getApiAdapterType() {
        return apiAdapterType;
    }

    public void setApiAdapterType(ApiAdapterType apiAdapterType) {
        this.apiAdapterType = apiAdapterType;
    }
}
