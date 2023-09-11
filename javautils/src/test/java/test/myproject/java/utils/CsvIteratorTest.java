package test.myproject.java.utils;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myproject.java.utils.CsvIterator;

public class CsvIteratorTest {
    /** Logger. */
    private static Logger logger = LoggerFactory.getLogger(CsvIteratorTest.class);

    /**
     * CSVデータを読み込み、CSVの項目を格納したListを１行ずつ返す
     * テストを実行する。
     * 
     * <ol>
     * <li>
     *   以下のCSVデータを読み込む
     *   <pre>
     * 1,abc,def,あいう,かきく
     * 2,"abc","def","あいう","かきく"
     * 3,"abc,xyz","def
     * uvw,","あいう,らりる","かきく""
     * ""やゆよ,"
     * 4, "abc", ""def, "あいう", ""かきく
     * 5,"abc,xyz" ,"def
     * uvw," "","あいう,らりゆ" ,"かきく""
     * ""やゆよ," ""
     *   </pre>
     *   <ul>
     *   <li>
     *     カンマで区切らた文字がCSVの項目となること。<br/>  
     *     1,abc,def,あいう,かきく\r\n<br/>
     *     => ['1', 'abc', 'def', 'あいう', 'かきく']
     *   </li>
     *   <li>
     *     カンマで区切られた文字が「"」で始まり「"」で終わる場合は、
     *     CSV項目の値は文字列の前後の「"」が削除されていること。<br/>
     *     2,"abc","def","あいう","かきく"\r\n<br/>  
     *     => ['2', 'abc', 'def', 'あいう', 'かきく']
     *   </li>
     *   <li>
     *     「"」で囲まれた文字列の中に改行、「,」が含まれる場合は、それらを
     *      含め「"」で囲まれた部分がCSV項目の値となること。<br/>
     *      …,"def\r\nuvw,",…<br/>
     *      => […, 'def\r\nuvw,', …]
     *   </li>
     *   <li>
     *     カンマで区切られた文字列が「"」で始まり、「"」で終わる場合で、
     *     その文字列の中に「""」が存在する場合は、「""」は「"」として
     *     CSV項目に取り込まれること。<br/>
     *     … ,"かきく""\r\n""やゆよ,"\r\n<br/>
     *     => […, 'かきく"\r\n"やゆよ,']
     *   </li>
     *   <li>
     *     カンマの後ろが「"」以外の文字があり、その後に「"」で囲まれる文字列
     *     があった場合は、「"」も含めてCSV項目に取り込まれること。<br/>
     *     …, "あいう", ""かきく\r\n<br/>
     *     => […, ' "あいう"', ' ""かきく']
     *   </li>
     *   <li>
     *     カンマの後ろが「"」で囲まれているが、後ろの「"」の後に「"」以外の
     *     文字列が存在する場合は、「"」を含めてCSV項目に取り込まれること。<br/>
     *     …,らりゆ" ,…<br/>
     *     => […, 'らりゆ" ', …]
     *   </li>
     *   <li>
     *     カンマの後ろに以下の文字列が続く場合、
     *     <ul>
     *     <li>「"」で囲まれており、その中に「""」、改行が存在する</li>
     *     <li>その後ろが「"」、改行以外の文字である</li>
     *     <li>さらにその後に「""」が続いて終わる</li>
     *     </ul>
     *    
     *     文字列の前後の「"」が削除され、「""」が「"」に痴漢されたものが
     *     CSV項目の値となること。<br/>
     *     …,"かきく""\r\n""やゆよ," ""\r\n<br/>
     *     => […, 'かきく"\r\n"やゆよ," "']
     *   </li>
     *   </ul>
     * </li>
     * </ol>
     **/
    @Test
    public void testIterator() {
        logger.debug("***** BEGIN testIterator");
        
        StringBuilder csv = new StringBuilder();
        csv.append("1,abc,def,あいう,かきく\r\n");
        csv.append("2,\"abc\",\"def\",\"あいう\",\"かきく\"\r\n");
        csv.append("3,\"abc,xyz\",\"def\r\nuvw,\",\"あいう,らりる\",\"かきく\"\"\r\n\"\"やゆよ,\"\r\n");
        csv.append("4, \"abc\", \"\"def, \"あいう\", \"\"かきく\r\n");
        csv.append("5,\"abc,xyz\" ,\"def\r\nuvw,\" \"\",\"あいう,らりゆ\" ,\"かきく\"\"\r\n\"\"やゆよ,\" \"\"");
        logger.debug("csv: " + csv);
        
        Reader in = new StringReader(csv.toString());
        try {
            for (List<String> rowdata: new CsvIterator(in)) {
                String[] line = rowdata.toArray(new String[0]);
                if ("1".equals(line[0])) {
                    logger.debug("rowdata: " + rowdata);
                    Assert.assertEquals("CSV項目の値が英数字であること", "abc", line[1]);
                    Assert.assertEquals("CSV項目の値が英数字であること", "def", line[2]);
                    Assert.assertEquals("CSV項目の値が全角文字であること", "あいう", line[3]);
                    Assert.assertEquals("CSV項目の値が全角文字であること", "かきく", line[4]);
                } else if ("2".equals(line[0])) {
                    logger.debug("rowdata: " + rowdata);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた英数字であること",
                            "abc", line[1]);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた英数字であること",
                            "def", line[2]);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた全角文字であること",
                            "あいう", line[3]);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた全角文字であること",
                            "かきく", line[4]);
                } else if ("3".equals(line[0])) {
                    logger.debug("rowdata: " + rowdata);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（英数字）にコンマが含まれること",
                            "abc,xyz", line[1]);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（英数字）にコンマや改行が含まれること",
                            "def\nuvw,", line[2]);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（全角文字）にコンマが含まれること",
                            "あいう,らりる", line[3]);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（全角文字）にコンマや改行、連続するダブルクォートでエスケープされたダブルクォートが含まれること",
                            "かきく\"\n\"やゆよ,", line[4]);
                } else if ("4".equals(line[0])) {
                    logger.debug("rowdata: " + rowdata);
                    Assert.assertEquals("スペースの後にダブルクォートで囲まれた英習字のCSV項目の値はダブルクォートが残ること",
                            " \"abc\"", line[1]);
                    Assert.assertEquals("スペースの後ろに続く連続するダブルクォートのCSV項目は連続するダブルクォートがエスケープされないこと",
                            " \"\"def", line[2]);
                    Assert.assertEquals("\"スペースの後にダブルクォートで囲まれた全角文字のCSV項目の値はダブルクォートが残ること",
                            " \"あいう\"", line[3]);
                    Assert.assertEquals("スペースの後ろに続く連続するダブルクォートのCSV項目は連続するダブルクォートがエスケープされないこと",
                            " \"\"かきく", line[4]);
                } else if ("5".equals(line[0])) {
                    logger.debug("rowdata: " + rowdata);
                    Assert.assertEquals("ダブルクォートで囲まれた英習字の後にスペースがあるCSV項目の値はダブルクォートが残ること",
                            "\"abc,xyz\" ", line[1]);
                    Assert.assertEquals("ダブルクォートで囲まれた改行を含む英習字の後にスペースと連続するダブルクォートが続きCSV項目は両端のダブルクォートのみが省かれること",
                            "def\nuvw,\" \"", line[2]);
                    Assert.assertEquals("ダブルクォートで囲まれたコンマを含む全角文字の後にスペースがあるCSV項目の値はダブルクォートが残ること",
                            "\"あいう,らりゆ\" ", line[3]);
                    Assert.assertEquals("ダブルクォートで囲まれた改行、コンマ、エスケープされたダブルクォートを含む全角文字の後にスペースと連続する打bるクォートがあるCSV項目の値は両端のダブルクォートと連続する２つのダブルクォートのエスケープが行われること",
                            "かきく\"\n\"やゆよ,\" \"", line[4]);
                }
            }
        } finally {
            logger.debug("***** END testIterator");
        }
        
    }
}
