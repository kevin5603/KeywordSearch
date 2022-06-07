package org.example.exception;


/**
 * 設定檔參數遺漏時跳出此錯誤
 * @author liyanting
 */
public class ConfigException extends RuntimeException{

    public ConfigException(String message) {
        super(message);
    }
}
