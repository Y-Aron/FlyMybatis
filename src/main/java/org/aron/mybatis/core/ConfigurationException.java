package org.aron.mybatis.core;

/**
 * @author: Y-Aron
 * @create: 2019-03-27 20:52
 */
public class ConfigurationException extends Exception {

    private String message;

    public ConfigurationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
