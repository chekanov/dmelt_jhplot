package jplot3dp;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import jplot3dp.MainComponent;
import jplot3dp.UVModeller;
import jplot3dp.Utils;

public class UVModeller extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UVModeller() {
	}

	public void init() {
		Utils.applet = this;
		getContentPane().add(mainComponent = new MainComponent(null));
	}

	private JMenuBar createMenu() {
		ActionListener actionlistener = new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				String s = ((AbstractButton) actionevent.getSource())
						.getActionCommand();
				mainComponent.handleCommand(s);
			}

		};
		JMenuBar jmenubar = new JMenuBar();
		JMenu jmenu = new JMenu("File");
		jmenu.setMnemonic(70);
		jmenubar.add(jmenu);
		JMenuItem jmenuitem = new JMenuItem("New", 78);
		jmenuitem.setAccelerator(KeyStroke.getKeyStroke(78, 2));
		jmenuitem.setActionCommand("FileNew");
		jmenuitem.addActionListener(actionlistener);
		jmenu.add(jmenuitem);
		JMenuItem jmenuitem1 = new JMenuItem("Open...", 79);
		jmenuitem1.setAccelerator(KeyStroke.getKeyStroke(79, 2));
		jmenuitem1.setActionCommand("FileOpen");
		jmenuitem1.addActionListener(actionlistener);
		jmenu.add(jmenuitem1);
		JMenuItem jmenuitem2 = new JMenuItem("Save", 83);
		jmenuitem2.setAccelerator(KeyStroke.getKeyStroke(83, 2));
		jmenuitem2.setActionCommand("FileSave");
		jmenuitem2.addActionListener(actionlistener);
		jmenu.add(jmenuitem2);
		JMenuItem jmenuitem3 = new JMenuItem("Save As...", 65);
		jmenuitem3.setAccelerator(KeyStroke.getKeyStroke(65, 2));
		jmenuitem3.setActionCommand("FileSaveAs");
		jmenuitem3.addActionListener(actionlistener);
		jmenu.add(jmenuitem3);
		jmenu.addSeparator();
		JMenuItem jmenuitem4 = new JMenuItem("Export to PNG...", 69);
		jmenuitem4.setActionCommand("ExportPNG");
		jmenuitem4.addActionListener(actionlistener);
		jmenu.add(jmenuitem4);
		jmenu.addSeparator();
		JMenuItem jmenuitem5 = new JMenuItem("Exit", 88);
		jmenuitem5.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				close();
			}

		});
		jmenu.add(jmenuitem5);
		JMenu jmenu1 = new JMenu("Scene");
		jmenu1.setMnemonic(83);
		jmenubar.add(jmenu1);
		final JCheckBoxMenuItem sceneSolid = new JCheckBoxMenuItem(
				"Toggle Solid");
		sceneSolid.setMnemonic(83);
		sceneSolid.setActionCommand("TogCulling");
		sceneSolid.addActionListener(actionlistener);
		jmenu1.add(sceneSolid);
		final JCheckBoxMenuItem sceneAxes = new JCheckBoxMenuItem("Toggle Axes");
		sceneAxes.setMnemonic(65);
		sceneAxes.setActionCommand("TogAxes");
		sceneAxes.addActionListener(actionlistener);
		jmenu1.add(sceneAxes);
		jmenu1.addSeparator();
		JMenuItem jmenuitem6 = new JMenuItem("Edit Scene...", 69);
		jmenuitem6.setAccelerator(KeyStroke.getKeyStroke(69, 2));
		jmenuitem6.setActionCommand("EditScene");
		jmenuitem6.addActionListener(actionlistener);
		jmenu1.add(jmenuitem6);
		jmenu1.addMenuListener(new MenuListener() {

			public void menuDeselected(MenuEvent menuevent) {
			}

			public void menuCanceled(MenuEvent menuevent) {
			}

			public void menuSelected(MenuEvent menuevent) {
				sceneSolid
						.setSelected(mainComponent.modelView.backCulling != 0);
				sceneAxes.setSelected(mainComponent.modelView.bShowAxes);
			}

		});
		JMenu jmenu2 = new JMenu("Help");
		jmenu2.setMnemonic(72);
		jmenubar.add(jmenu2);
		JMenuItem jmenuitem7 = new JMenuItem("Readme...", 82);
		jmenuitem7.setActionCommand("HelpReadme");
		jmenuitem7.addActionListener(actionlistener);
		jmenu2.add(jmenuitem7);
		return jmenubar;
	}

	private void close() {
		if (!mainComponent.isDirty("Do you want to save before exit?"))
			System.exit(0);
	}

	private void runApplication(String s) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception exception) {
		}
		JFrame jframe = new JFrame();
		mainComponent = new MainComponent(jframe);
		jframe.getContentPane().add(mainComponent);
		jframe.setSize(640, 480);
		jframe.setLocationRelativeTo(null);
		jframe.setIconImage(Utils.loadIcon("uvmodeller.gif").getImage());
		jframe.setDefaultCloseOperation(0);
		jframe.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent windowevent) {
				close();
			}

		});
		jframe.setJMenuBar(createMenu());
		mainComponent.loadFromFile(s);
		jframe.setVisible(true);
	}

	public static void main(String args[]) {
		(new UVModeller()).runApplication(args.length <= 0 ? null : args[0]);
	}

	private MainComponent mainComponent;

}
