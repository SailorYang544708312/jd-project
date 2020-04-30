package com.jd.pojo;

import java.io.Serializable;
import java.util.Date;

public class TbPayLog implements Serializable {
    private String outTradeNo;

    private Date createTime;

    private Date payTime;

    private Long totalFee;

    private String userId;

    private String transactionId;

    private String tradeState;

    private String orderList;

    private String payType;

    public TbPayLog(String outTradeNo, Date createTime, Date payTime, Long totalFee, String userId, String transactionId, String tradeState, String orderList, String payType) {
        this.outTradeNo = outTradeNo;
        this.createTime = createTime;
        this.payTime = payTime;
        this.totalFee = totalFee;
        this.userId = userId;
        this.transactionId = transactionId;
        this.tradeState = tradeState;
        this.orderList = orderList;
        this.payType = payType;
    }

    public TbPayLog() {
        super();
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId == null ? null : transactionId.trim();
    }

    public String getTradeState() {
        return tradeState;
    }

    public void setTradeState(String tradeState) {
        this.tradeState = tradeState == null ? null : tradeState.trim();
    }

    public String getOrderList() {
        return orderList;
    }

    public void setOrderList(String orderList) {
        this.orderList = orderList == null ? null : orderList.trim();
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }
}