package test.myproject.java.utils;

import java.io.Reader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
    public void testCsvIterator() {
        logger.debug("***** BEGIN testCsvIterator");

        StringBuilder csv = new StringBuilder();
        csv.append("1,abc,def,あいう,かきく\r\n");
        csv.append("2,\"abc\",\"def\",\"あいう\",\"かきく\"\r\n");
        csv.append("3,\"abc,xyz\",\"def\r\nuvw,\",\"あいう,らりる\",\"かきく\"\"\r\n\"\"やゆよ,\"\r\n");
        csv.append("4, \"abc\", \"\"def, \"あいう\", \"\"かきく\r\n");
        csv.append("5,\"abc,xyz\" ,\"def\r\nuvw,\" \"\",\"あいう,らりゆ\" ,\"かきく\"\"\r\n\"\"やゆよ,\" \"\"");
        logger.debug("csv: " + csv);

        Reader in = new StringReader(csv.toString());
        try {
            int count = 0;
            for (List<String> rowdata: new CsvIterator(in)) {
                count ++;
                String[] line = rowdata.toArray(new String[0]);
                if ("1".equals(line[0])) {
                    logger.debug("[" + count + "]rowdata: " + rowdata);
                    Assert.assertEquals("CSV項目の値が英数字であること", "abc", line[1]);
                    Assert.assertEquals("CSV項目の値が英数字であること", "def", line[2]);
                    Assert.assertEquals("CSV項目の値が全角文字であること", "あいう", line[3]);
                    Assert.assertEquals("CSV項目の値が全角文字であること", "かきく", line[4]);
                } else if ("2".equals(line[0])) {
                    logger.debug("[" + count + "]rowdata: " + rowdata);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた英数字であること",
                            "abc", line[1]);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた英数字であること",
                            "def", line[2]);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた全角文字であること",
                            "あいう", line[3]);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた全角文字であること",
                            "かきく", line[4]);
                } else if ("3".equals(line[0])) {
                    logger.debug("[" + count + "]rowdata: " + rowdata);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（英数字）にコンマが含まれること",
                            "abc,xyz", line[1]);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（英数字）にコンマや改行が含まれること",
                            "def\nuvw,", line[2]);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（全角文字）にコンマが含まれること",
                            "あいう,らりる", line[3]);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（全角文字）にコンマや改行、連続するダブルクォートでエスケープされたダブルクォートが含まれること",
                            "かきく\"\n\"やゆよ,", line[4]);
                } else if ("4".equals(line[0])) {
                    logger.debug("[" + count + "]rowdata: " + rowdata);
                    Assert.assertEquals("スペースの後にダブルクォートで囲まれた英習字のCSV項目の値はダブルクォートが残ること",
                            " \"abc\"", line[1]);
                    Assert.assertEquals("スペースの後ろに続く連続するダブルクォートのCSV項目は連続するダブルクォートがエスケープされないこと",
                            " \"\"def", line[2]);
                    Assert.assertEquals("\"スペースの後にダブルクォートで囲まれた全角文字のCSV項目の値はダブルクォートが残ること",
                            " \"あいう\"", line[3]);
                    Assert.assertEquals("スペースの後ろに続く連続するダブルクォートのCSV項目は連続するダブルクォートがエスケープされないこと",
                            " \"\"かきく", line[4]);
                } else if ("5".equals(line[0])) {
                    logger.debug("[" + count + "]rowdata: " + rowdata);
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
            logger.debug("***** END testCsvIterator");
        }
    }

    /**
     * {@link #testCsvIterator()} と同じ内容のCSVをファイルから読み込み、CSVの項
     * 目が正しく主と腐れることをテストする。
     */
    @Test
    public void testCsvIteratorFileReader() {
        logger.debug("***** BEGIN testCsvIteratorFileReader");

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(
                    ClassLoader.getSystemResourceAsStream("test/myproject/java/utils/csv_ierator.csv"),
                    "UTF-8");
            int count = 0;
            for (List<String> rowdata: new CsvIterator(reader)) {
                count ++;
                String[] line = rowdata.toArray(new String[0]);
                if ("1".equals(line[0])) {
                    logger.debug("[" + count + "]rowdata: " + rowdata);
                    Assert.assertEquals("CSV項目の値が英数字であること", "abc", line[1]);
                    Assert.assertEquals("CSV項目の値が英数字であること", "def", line[2]);
                    Assert.assertEquals("CSV項目の値が全角文字であること", "あいう", line[3]);
                    Assert.assertEquals("CSV項目の値が全角文字であること", "かきく", line[4]);
                } else if ("2".equals(line[0])) {
                    logger.debug("[" + count + "]rowdata: " + rowdata);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた英数字であること",
                            "abc", line[1]);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた英数字であること",
                            "def", line[2]);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた全角文字であること",
                            "あいう", line[3]);
                    Assert.assertEquals("CSV項目の値が両端のダブルクォートが覗かれた全角文字であること",
                            "かきく", line[4]);
                } else if ("3".equals(line[0])) {
                    logger.debug("[" + count + "]rowdata: " + rowdata);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（英数字）にコンマが含まれること",
                            "abc,xyz", line[1]);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（英数字）にコンマや改行が含まれること",
                            "def\nuvw,", line[2]);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（全角文字）にコンマが含まれること",
                            "あいう,らりる", line[3]);
                    Assert.assertEquals("ダブルクォートで囲まれた場合はCSV項目の値（全角文字）にコンマや改行、連続するダブルクォートでエスケープされたダブルクォートが含まれること",
                            "かきく\"\n\"やゆよ,", line[4]);
                } else if ("4".equals(line[0])) {
                    logger.debug("[" + count + "]rowdata: " + rowdata);
                    Assert.assertEquals("スペースの後にダブルクォートで囲まれた英習字のCSV項目の値はダブルクォートが残ること",
                            " \"abc\"", line[1]);
                    Assert.assertEquals("スペースの後ろに続く連続するダブルクォートのCSV項目は連続するダブルクォートがエスケープされないこと",
                            " \"\"def", line[2]);
                    Assert.assertEquals("\"スペースの後にダブルクォートで囲まれた全角文字のCSV項目の値はダブルクォートが残ること",
                            " \"あいう\"", line[3]);
                    Assert.assertEquals("スペースの後ろに続く連続するダブルクォートのCSV項目は連続するダブルクォートがエスケープされないこと",
                            " \"\"かきく", line[4]);
                } else if ("5".equals(line[0])) {
                    logger.debug("[" + count + "]rowdata: " + rowdata);
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            logger.debug("***** END testCsvIteratorFileReader");
        }
    }

    /**
     * 空（０文字）のデータを読み込むと、Iteratorは何も要素を返さないことを
     * テストする。
     */
    @Test
    public void testCsvIteratorNull() {
        logger.debug("***** BEGIN testCsvIteratorNull");

        StringBuilder csv = new StringBuilder();
        Reader in = new StringReader(csv.toString());
        int count = 0;
        for (List<String> rowdata: new CsvIterator(in)) {
            count ++;
        }
        
        Assert.assertEquals("Listの行数は0であること。", 0, count);
        
        logger.debug("***** END testCsvIteratorNull");
    }

    /**
     * １文字で改行なしのデータを読み込むと、要素数１の{@link List}が１行
     * 返されることをテストする。
     */
    @Test
    public void testCsvIteratorOneChar() {
        logger.debug("***** BEGIN testCsvIteratorOneChar");

        StringBuilder csv = new StringBuilder("z");
        Reader in = new StringReader(csv.toString());
        int count = 0;
        for (List<String> rowdata: new CsvIterator(in)) {
            count ++;
            logger.debug("[" + count + "]rowdata: " + rowdata);
            String[] line = rowdata.toArray(new String[0]);
            Assert.assertEquals("Listの要素数は1であること", 1, line.length);
            Assert.assertEquals("読み込んだ１文字がCSV項目の値に設定されること",
                    "z", line[0]);
        }

        Assert.assertEquals("Listの行数は1であること。", 1, count);

        logger.debug("***** END testCsvIteratorOneChar");
    }

    /**
     * <ol>
     * <li>
     *   改行のみのデータを読み込むと、要素数1の{@link List}が１行返されることを
     *   テストする。
     * </li>
     * <li>
     *   １行のCSV項目数は１つで、値が長さ０の文字列であることをテストする。
     * </li>
     * </ol>
     */
    @Test
    public void testCsvIteratorNullLine() {
        logger.debug("***** BEGIN testCsvIteratorNullLine");

        StringBuilder csv = new StringBuilder("\r\n");
        Reader in = new StringReader(csv.toString());
        int count = 0;
        for (List<String> rowdata: new CsvIterator(in)) {
            count ++;
            logger.debug("[" + count + "]rowdata: " + rowdata);
            String[] line = rowdata.toArray(new String[0]);
            Assert.assertEquals("Listの要素数は0であること", 0, line.length);
        }

        Assert.assertEquals("Listの行数は1であること。", 1, count);

        logger.debug("***** END testCsvIteratorNullLine");
    }

    /**
     * 以下のCSVデータを読み込む。
     * <pre>
     * ,
     * 
     * END(改行なし)
     * </pre>
     * <ol>
     * <li>１行目のCSV項目数は２であること。</li>
     * <li>１行目の１番目、２番目の項目の値は共に長さ０の文字列であること。</li>
     * <li>２行目のCSV項目数は0であること。</li>
     * <li>３行目のCSV項目数は１であること。</li>
     * <li>３行目の１番目の項目の値は「END」であること。</li>
     * </ol>
     */
    @Test
    public void testCsvIteratorCommaOnly() {
        logger.debug("***** BEGIN testCsvIteratorCommaOnly");

        StringBuilder csv = new StringBuilder();
        csv.append(",\r\n");
        csv.append("\r\n");
        csv.append("END");
        Reader in = new StringReader(csv.toString());
        int count = 0;
        for (List<String> rowdata: new CsvIterator(in)) {
            logger.debug("rowdata: " + rowdata);
            count ++;
            String[] line = rowdata.toArray(new String[0]);
            if (count == 1) {
                logger.debug("[" + count + "]rowdata: " + rowdata);
                Assert.assertEquals("１行目のCSV項目の個数は２であること。",
                        2, line.length);
                Assert.assertEquals("１行目の１番目の値は長さ０の文字列であること。",
                        0, line[0].length());
                Assert.assertEquals("１行目の２番目の値は長さ０の文字列であること。",
                        0, line[1].length());
            } else if (count == 2) {
                logger.debug("[" + count + "]rowdata: " + rowdata);
                Assert.assertEquals("２行目のCSV項目数は０であること。",
                        0, line.length);
            } else if (count == 3) {
                logger.debug("[" + count + "]rowdata: " + rowdata);
                Assert.assertEquals("３行目のCSV項目数は１であること。",
                        1, line.length);
                Assert.assertEquals("３行目の１番目の値は「END」であること。",
                        "END", line[0]);
            }
        }
        Assert.assertEquals("Listの行数は３であること",
                3, count);

        logger.debug("***** END testCsvIteratorCommaOnly");
    }

    /**
     * 以下のCSVデータを読み込む。
     * <pre>
     * "abc","あいう
     * （改行なし）
     * </pre>
     * <ul>
     * <li>CSV項目数は２であること。</li>
     * <li>１番目の項目値は「abc」であること。</li>
     * <li>２番目の項目値は「\"あいう\n」であること。</li>
     * </ul>
     */
    @Test
    public void testCsvIteratorIllegal() {
        logger.debug("***** BEGIN testCsvIteratorIrregal");

        StringBuilder csv = new StringBuilder("\"abc\",\"あいう\r\n");
        Reader in = new StringReader(csv.toString());
        int count = 0;
        for (List<String> rowdata: new CsvIterator(in)) {
            count ++;
            logger.debug("[" + count + "]rowdata: " + rowdata);
            String[] line = rowdata.toArray(new String[0]);
            Assert.assertEquals("Listの要素数は2であること", 2, line.length);
            Assert.assertEquals("１番目の項目の値が「abc」であること。",
                    "abc", line[0]);
            Assert.assertEquals("２番目の項目の値が「\"あいう\n」であること",
                    "\"あいう\n", line[1]);
        }

        Assert.assertEquals("Listの行数は1であること。", 1, count);

        logger.debug("***** END testCsvIteratorIrregal");
    }
}
