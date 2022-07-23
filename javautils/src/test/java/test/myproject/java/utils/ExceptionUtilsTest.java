package test.myproject.java.utils;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myproject.java.utils.ExceptionUtils;

/**
 * {@link ExceptionUtils}クラスのテストを実行します。
 * @author ycookjp my project
 */
public class ExceptionUtilsTest {
    /** Logger. */
    private static Logger logger = LoggerFactory.getLogger(ExceptionUtilsTest.class);

    /**
     * {@link ExceptionUtils#getStackTraceString(Exception)}メソッドのテストをします。
     * <ul>
     * <li>
     *   引数に{@link NullPointerException}を指定すると、戻り値の文字列の長さは、
     *   「例外のtoString()の長さ＋このクラスの完全修飾名の長さ＋メソッド名の長さ」を
     *   超えること
     * </li>
     * <li>引数にnullを指定するとnullを返すこと</li>
     * </ul>
     */
    @Test
    public void testGetStackTraceString() {
        logger.debug("***** BEGIN testGetStackTraceString");
        // NullPointerExceptionを発生させる
        String str = null;
        try {
            str.toLowerCase();
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTraceString(e);
            logger.debug("ExceptionUtils.getStackTraceString(e)\n    ==> {}", stackTrace);
            Assert.assertTrue("引数にNullPointerExceptionを指定すると、戻り値の文字列の長さは、「例外のtoString()の長さ＋このクラスの完全修飾名の長さ＋メソッド名の長さ」を超えること",
                    stackTrace.length() > e.toString().length() + ExceptionUtilsTest.class.getName().length()
                    + "testGetStackTraceString".length());

            stackTrace = ExceptionUtils.getStackTraceString(null);
            logger.debug("ExceptionUtils.getStackTraceString(null) ==> {}", stackTrace);
            Assert.assertNull("引数にnullを指定するとnullを返すこと", stackTrace);
        } finally {
            logger.debug("***** END testGetStackTraceString");
        }
    }
}
