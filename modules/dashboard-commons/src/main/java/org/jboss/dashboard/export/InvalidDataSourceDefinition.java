package org.jboss.dashboard.export;

/**
 * The datasource definition is invalid, it does not contains the required properties or are not valid.
 */
public class InvalidDataSourceDefinition extends Exception {
    
    public InvalidDataSourceDefinition(String message) {
        super(message);
    }

    public InvalidDataSourceDefinition(String message, Throwable cause) {
        super(message, cause);
    }
}
