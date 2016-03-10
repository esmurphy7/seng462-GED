package com.teamged.logging.xmlelements;

import java.math.BigInteger;

public class TransactionCompleteType extends LogType {
    protected BigInteger transactionNum;
    protected XmlElements cmdType;

    public TransactionCompleteType() {
        this.cmdType = XmlElements.TransactionCompleteType;
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

    @Override
    public XmlElements getXmlElementType() {
        return this.cmdType;
    }

    @Override
    public String simpleSerialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(getXmlElementType().ordinal());
        sb.append(",");
        sb.append(this.getTransactionNum().toString());

        return sb.toString();
    }
}
