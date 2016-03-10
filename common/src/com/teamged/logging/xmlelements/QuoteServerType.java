package com.teamged.logging.xmlelements;

import java.math.BigDecimal;
import java.math.BigInteger;

public class QuoteServerType extends LogType {

    protected long timestamp;
    protected String server;
    protected BigInteger transactionNum;
    protected BigDecimal price;
    protected String stockSymbol;
    protected String username;
    protected BigInteger quoteServerTime;
    protected String cryptokey;
    protected XmlElements cmdType;

    public QuoteServerType() {
        this.cmdType = XmlElements.QuoteServerType;
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
     * Gets the value of the price property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setPrice(BigDecimal value) {
        this.price = value;
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
     * Gets the value of the quoteServerTime property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getQuoteServerTime() {
        return quoteServerTime;
    }

    /**
     * Sets the value of the quoteServerTime property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setQuoteServerTime(BigInteger value) {
        this.quoteServerTime = value;
    }

    /**
     * Gets the value of the cryptokey property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCryptokey() {
        return cryptokey;
    }

    /**
     * Sets the value of the cryptokey property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCryptokey(String value) {
        this.cryptokey = value;
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
        sb.append(this.getPrice().toPlainString());
        sb.append(",");
        sb.append(this.getStockSymbol());
        sb.append(",");
        sb.append(this.getUsername());
        sb.append(",");
        sb.append(this.getQuoteServerTime().toString());
        sb.append(",");
        sb.append(this.getCryptokey());

        return sb.toString();
    }
}
