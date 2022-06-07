package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 解析後資訊封裝在此物件方便後續處理
 * @author liyanting
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Info {

    /** 完整絕對路徑 預設不會顯示該資訊 */
    private String fullPath;
    /** 檔名 */
    private String fileName;

    /** 行數 */
    private Integer lineCount;
    /** 關鍵字 */
    private String keyword;
    /** 整行內容 */
    private String line;

    @Override
    public String toString() {
        String format = "檔名:%s, 行數:%d, 關鍵字: %s, 內文: %s";
        return String.format(format, fileName, lineCount, keyword, line);
    }
}
