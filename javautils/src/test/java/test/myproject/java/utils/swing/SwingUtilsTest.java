package test.myproject.java.utils.swing;

import java.awt.Component;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import myproject.java.utils.SwingUtils;

/**
 * {@link SwingUtils}クラスのテストを実行します。
 * @author cook
 *
 */
public class SwingUtilsTest implements Runnable {
    /** logger. */
    private static Logger logger = LoggerFactory.getLogger(SwingUtilsTest.class);

    /** window object. */
    private ApplicationWindow applicationWindow = null;
    /** frame object. */
    private JFrame frame = null;

    /** lock object. */
    Object lock = new Object();

    /**
     * テストメソッド実行前の前処理を実行します。
     * <p>
     * 別スレッドでウィンドウアプリケーションを起動し、その初期化が終わるまで待機します。
     */
    @Before
    public void setUp() {
        synchronized(this.lock) {
            try {
                // 別スレッドでウィンドウアプリケーションを起動する
                new Thread(this).start();
                // ウィンドウアプリケーションの初期化が終わるまで待つ
                this.lock.wait();
            } catch (InterruptedException e) { }
        }
    }

    /**
     * ウィンドウアプリケーションを起動し、ウィンドウを可視化します。
     */
    @Override
    public void run() {
        try {
            this.applicationWindow = new ApplicationWindow();

            // JFrameを取得する
            Field frameField = applicationWindow.getClass().getDeclaredField("frame");
            frameField.setAccessible(true);
            this.frame = (JFrame) frameField.get(this.applicationWindow);

            // ウィンドウを可視化する
            this.frame.setVisible(true);
            synchronized(this.lock) {
                // メインスレッドの実行を再開する
                this.lock.notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            synchronized(this.lock) {
                // 例外発生時はメインスレッドの実行を再開する
                this.lock.notifyAll();
            }
        }
    }

    /**
     * テストメソッド実行後の後処理を実行します。
     * <p>
     * 別スレッドで実行されたウィンドウアプリケーションを終了します。
     */
    @After
    public void tearDown() {
        if (this.frame != null) {
            this.frame.dispose();
            this.frame = null;
            this.applicationWindow = null;
        }
    }

    /**
     * {@link SwingUtils#getComponentByName(java.awt.Container, String)}メソッドのテストを実行します。
     * <p>
     * ウィンドウアプリケーションの中に配置されたコンポーネントを取得し、取得結果について以下の確認をします。
     * <ul>
     * <li>取得したコンポーネントがnullでないこと</li>
     * <li>取得したコンポーネントの名前がメソッドで指定したものと一致すること</li>
     * <li>取得したコンポーネントのクラスがウィンドウに配置されたコンポーネントのものと一致すること</li>
     * </ul>
     */
    @Test
    public void testGetComponentByName() {
        Component buttonName1 = SwingUtils.getComponentByName(this.frame.getContentPane(), "buttonName1");
        logger.debug("SwingUtils.getComponentByName(this.frame.getContentPane(), \"buttonName1\") ==> {}", "JButton");
        Assert.assertNotNull("buttonName1 component is not null", buttonName1);
        Assert.assertEquals("Component name is \"buttonName1\"", "buttonName1", buttonName1.getName());
        Assert.assertEquals("Component class is JButton", JButton.class, buttonName1.getClass());

        Component buttonName2 = SwingUtils.getComponentByName(this.frame.getContentPane(), "buttonName2");
        logger.debug("SwingUtils.getComponentByName(this.frame.getContentPane(), \"buttonName2\") ==> {}", "JButton");
        Assert.assertNotNull("buttonName2 component is not null", buttonName2);
        Assert.assertEquals("Component name is \"buttonName2\"", "buttonName2", buttonName2.getName());
        Assert.assertEquals("Component class is JButton", JButton.class, buttonName2.getClass());

        Component textPane = SwingUtils.getComponentByName(this.frame.getContentPane(), "textPane");
        logger.debug("SwingUtils.getComponentByName(this.frame.getContentPane(), \"textPane\") ==> {}", "JTextPane");
        Assert.assertNotNull("textPane component is not null", textPane);
        Assert.assertEquals("Component name is \"textPane\"", "textPane", textPane.getName());
        Assert.assertEquals("Component class is JTextPane", JTextPane.class, textPane.getClass());
    }

    /**
     * ボタンクリックイベントが発生した時の動作を確認します。
     * <p>
     * ボタンクリックイベントを発生させると、JTextPaneのテキストの最後に
     * "&lt;改行&gt;click from &lt;ボタンのnameプロパティ&gt;" が追加されることを確認します。
     */
    @Test
    public void testButtonClick() {
        JTextPane textPane = (JTextPane) SwingUtils.getComponentByName(this.frame.getContentPane(), "textPane");
        JButton buttonName1 = (JButton) SwingUtils.getComponentByName(this.frame.getContentPane(), "buttonName1");
        JButton buttonName2 = (JButton) SwingUtils.getComponentByName(this.frame.getContentPane(), "buttonName2");
        String expectedText = "";
        String textPaneText = "";

        buttonName1.doClick();
        expectedText = textPaneText + System.getProperty("line.separator") + "click from " + buttonName1.getName();
        logger.debug("JTextPane.getText() ==> {}", "click from " + buttonName1.getName());
        textPaneText = textPane.getText();
        Assert.assertEquals(expectedText, textPaneText);

        buttonName2.doClick();
        expectedText = textPaneText + System.getProperty("line.separator") + "click from " + buttonName2.getName();
        logger.debug("JTextPane.getText() ==> {}", expectedText);
        textPaneText = textPane.getText();
        Assert.assertEquals(expectedText, textPaneText);
    }
}
