package com.teamged.logging.xmlelements;

import java.math.BigDecimal;
import java.math.BigInteger;

public class UserCommandType extends LogType {
    protected long timestamp;
    protected String server;
    protected BigInteger transactionNum;
    protected CommandType command;
    protected String username;
    protected String stockSymbol;
    protected String filename;
    protected BigDecimal funds;
    protected XmlElements cmdType;

    public UserCommandType() {
        this.cmdType = XmlElements.UserCommandType;
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
     * Gets the value of the command property.
     *
     * @return possible object is
     * {@link CommandType }
     */
    public CommandType getCommand() {
        return command;
    }

    /**
     * Sets the value of the command property.
     *
     * @param value allowed object is
     *              {@link CommandType }
     */
    public void setCommand(CommandType value) {
        this.command = value;
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
     * Gets the value of the stockSymbol property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStockSymbol() {
        return stockSymbol;
    }

    /**
     * Sets the value of the stockSymbol property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStockSymbol(String value) {
        this.stockSymbol = value;
    }

    /**
     * Gets the value of the filename property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setFilename(String value) {
        this.filename = value;
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

    @SuppressWarnings("Duplicates")
    @Override
    public String simpleSerialize() {
        StringBuilder sb = new StringBuilder(getXmlElementType().ordinal());
        sb.append(",");
        sb.append(this.getTimestamp());
        sb.append(",");
        sb.append(this.getServer());
        sb.append(",");
        sb.append(this.getTransactionNum().toString());
        sb.append(",");
        sb.append(this.getCommand().value());
        sb.append(",");
        if (this.getUsername() != null) {
            sb.append(this.getUsername());
        }
        sb.append(",");
        if (this.getStockSymbol() != null) {
            sb.append(this.getStockSymbol());
        }
        sb.append(",");
        if (this.getFilename() != null) {
            sb.append(this.getFilename());
        }
        sb.append(",");
        if (this.getFunds() != null) {
            sb.append(this.getFunds().toPlainString());
        }
        sb.append(";");

        return sb.toString();
    }
}
