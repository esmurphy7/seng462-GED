package com.teamged.logging.xmlelements;

/**
 * Logging class template for servers logging to the audit server.
 */
public abstract class LogType {
    /**
     * Gets the enumerated XML element type of the log.
     *
     * @return The type of the log.
     */
    public abstract XmlElements getXmlElementType();

    /**
     * Serializes the log in a simple format, using a comma separated string ending with a semicolon to represent
     * all values. Missing optional values will simply be an empty string, so sequential commas indicate an absent
     * optional parameter. Parameter order is preserved from the logfile.xsd file.
     *
     * @return A simple serialized String representation of the log.
     */
    public abstract String simpleSerialize();
}
