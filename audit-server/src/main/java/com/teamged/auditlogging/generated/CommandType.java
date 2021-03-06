//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.02 at 07:58:07 PM PST 
//


package com.teamged.auditlogging.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for commandType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="commandType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADD"/>
 *     &lt;enumeration value="QUOTE"/>
 *     &lt;enumeration value="BUY"/>
 *     &lt;enumeration value="COMMIT_BUY"/>
 *     &lt;enumeration value="CANCEL_BUY"/>
 *     &lt;enumeration value="SELL"/>
 *     &lt;enumeration value="COMMIT_SELL"/>
 *     &lt;enumeration value="CANCEL_SELL"/>
 *     &lt;enumeration value="SET_BUY_AMOUNT"/>
 *     &lt;enumeration value="CANCEL_SET_BUY"/>
 *     &lt;enumeration value="SET_BUY_TRIGGER"/>
 *     &lt;enumeration value="SET_SELL_AMOUNT"/>
 *     &lt;enumeration value="SET_SELL_TRIGGER"/>
 *     &lt;enumeration value="CANCEL_SET_SELL"/>
 *     &lt;enumeration value="DUMPLOG"/>
 *     &lt;enumeration value="DISPLAY_SUMMARY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "commandType")
@XmlEnum
public enum CommandType {

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
    DISPLAY_SUMMARY;

    public String value() {
        return name();
    }

    public static CommandType fromValue(String v) {
        return valueOf(v);
    }

}
