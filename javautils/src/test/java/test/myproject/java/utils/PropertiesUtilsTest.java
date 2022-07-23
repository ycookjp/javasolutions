package test.myproject.java.utils;

import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myproject.java.utils.PropertiesUtils;

/**
 * {@link PropertiesUtils}クラスのテストを実行します。
 * @author ycookjp my project
 */
public class PropertiesUtilsTest {
    /** Logger. */
    private Logger logger = LoggerFactory.getLogger(PropertiesUtilsTest.class);
    /**
     * {@link PropertiesUtils#loadResource(String)}メソッドのテストを実行します。
     * <ul>
     * <li>ASCII文字の値「value1」を取得できること</li>
     * <li>日本語の値「値その２」を取得できること</li>
     * <li>引数に存在しないリソースを指定すると{@link IOException}が発生すること</li>
     * <li>引数にnullを指定するとnullが返ること</li>
     * </ul>
     */
    @Test
    public void testLoadResource() {
        logger.debug("***** START testLoadResource *****");
        try {
            logger.debug("PropertiesUtils.loadResource(\"test/myproject/java/utils/testprop.properties\")");
            Properties prop = PropertiesUtils.loadResource(
                    "test/myproject/java/utils/testprop.properties");

            logger.debug("prop.get(\"key1\") ==> {}", prop.get("key1"));
            Assert.assertEquals("ASCII文字の値「value1」を取得できること",
                    "value1", prop.get("key1"));

            logger.debug("prop.get(\"key2\") ==> {}", prop.get("key2"));
            Assert.assertEquals("日本語の値「値その２」を取得できること",
                    "値その２", prop.get("key2"));

            try {
                prop = PropertiesUtils.loadResource("/test/myproject/java/utils/testprop.properties");
                Assert.fail("存在しないリソースを指定すると{@link IOException}が発生すること：例外が発生しない");
            } catch (Exception e) {
                if (!(e instanceof IOException)) {
                    Assert.fail("存在しないリソースを指定すると{@link IOException}が発生すること：IOException以外の例外が発生");
                }
            }

            prop = PropertiesUtils.loadResource(null);
            Assert.assertNull("引数にnullを指定するとnullが返ること", prop);
        } catch (IOException ioe) {
            Assert.fail(ioe.toString());
        } catch (Exception e) {
            Assert.fail(e.toString());
        } finally {
            logger.debug("***** END testLoadResource *****");
        }
    }

    /**
     * {@link myproject.java.utils.PropertiesUtils#replaceKeyword(Properties)}メソッドのテストを実行します。
     * <ul>
     * <li>値の「${key3}」はkey3の値に変換されること</li>
     * <li>「${java.runtime.name}」はシステムプロパティ「java.runtime.name」の値に返還されること</li>
     * <li>引数にnullを指定しても例外が発生しないこと</li>
     * </ul>
     */
    @Test
    public void testReplaceKeyword() {
        logger.debug("***** BEGIN testReplaceKeyword *****");
        try {
            logger.debug("PropertiesUtils.loadResource(\"test/myproject/java/utils/testprop.properties\")");
            Properties prop = PropertiesUtils.loadResource("test/myproject/java/utils/testprop.properties");
            PropertiesUtils.replaceKeyword(prop);

            logger.debug("prop.getProperty(\"key4\") ==> {}", prop.getProperty("key4"));
            Assert.assertEquals("値の「${key3}」はkey3の値に変換されること",
                    "key3の値は\"" + prop.getProperty("key3") + "\"です", prop.getProperty("key4"));

            logger.debug("prop.getProperty(\"key5\") ==> {}", prop.getProperty("key5"));
            Assert.assertEquals("「${java.runtime.name}」はシステムプロパティ「java.runtime.name」の値に返還されること",
                    "java.runtime.name=" + System.getProperty("java.runtime.name"), prop.getProperty("key5"));

            try {
                PropertiesUtils.replaceKeyword(null);
                // OK
            } catch (Exception e) {
                Assert.fail("引数にnullを指定しても例外が発生しないこと：例外が発生");
            }
        } catch (IOException ioe) {
            Assert.fail(ioe.toString());
        } catch (Exception e) {
            Assert.fail(e.toString());
        } finally {
            logger.debug("***** END testReplaceKeyword *****");
        }
    }
}
