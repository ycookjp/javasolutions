package test.myproject.java.utils.swing;


import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JTextPane;

import myproject.java.utils.SwingUtils;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class ApplicationWindow {

	private JFrame frame;
	private final Action buttonAction = new SwingAction();

	/**
	 * Launch the application.
	 * @param args コマンドラインの引数。何も指定しない。
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApplicationWindow window = new ApplicationWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ApplicationWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);
		
		JTextPane textPane = new JTextPane();
		textPane.setName("textPane");
		scrollPane.setViewportView(textPane);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JButton button1 = new JButton("Button 1");
		button1.setAction(buttonAction);
		button1.setName("buttonName1");
		panel.add(button1);
		
		JButton button2 = new JButton("Button 2");
		button2.setAction(buttonAction);
		button2.setName("buttonName2");
		panel.add(button2);
	}
    private class SwingAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        public SwingAction() {
            putValue(NAME, "SwingAction");
            putValue(SHORT_DESCRIPTION, "Some short description");
        }
        public void actionPerformed(ActionEvent e) {
            Component eventSource = (Component) e.getSource();
            JTextPane textPane = (JTextPane) SwingUtils.getComponentByName(frame.getContentPane(), "textPane");
            if (textPane != null) {
                textPane.setText(textPane.getText() + System.getProperty("line.separator")
                        + "click from " + eventSource.getName());
            }
        }
    }
}
