package com.teamged.logging.xmlelements;

import java.math.BigDecimal;
import java.math.BigInteger;


public class AccountTransactionType extends LogType {
    protected long timestamp;
    protected String server;
    protected BigInteger transactionNum;
    protected String action;
    protected String username;
    protected BigDecimal funds;
    protected XmlElements cmdType;

    public AccountTransactionType() {
        this.cmdType = XmlElements.AccountTransactionType;
    }

    /**
     * Gets the value of the timestamp property.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     */
    public void setTimestamp(long value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the server property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setServer(String value) {
        this.server = value;
    }

    /**
     * Gets the value of the transactionNum property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getTransactionNum() {
        return transactionNum;
    }

    /**
     * Sets the value of the transactionNum property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setTransactionNum(BigInteger value) {
        this.transactionNum = value;
    }

    /**
     * Gets the value of the action property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Gets the value of the username property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the funds property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getFunds() {
        return funds;
    }

    /**
     * Sets the value of the funds property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setFunds(BigDecimal value) {
        this.funds = value;
    }

    @Override
    public XmlElements getXmlElementType() {
        return this.cmdType;
    }

    @Override
    public String simpleSerialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(getXmlElementType().ordinal());
        sb.append(",");
        sb.append(this.getTimestamp());
        sb.append(",");
        sb.append(this.getServer());
        sb.append(",");
        sb.append(this.getTransactionNum().toString());
        sb.append(",");
        sb.append(this.getAction());
        sb.append(",");
        sb.append(this.getUsername());
        sb.append(",");
        sb.append(this.getFunds().toPlainString());

        return sb.toString();
    }
}
