package jplot3d;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ColorDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ColorDialog(Frame frame, Color color) {
		super(frame, "Color Chooser", true);
		cancelled = false;
		Container container = getContentPane();
		container.setLayout(new BoxLayout(container, 1));
		container.add(colorChooser);
		colorChooser.setColor(color);
		colorChooser.setAlignmentX(1.0F);
		Box box = new Box(0);
		box.add(btnOk = new JButton("Ok"));
		btnOk.setMnemonic('O');
		box.add(Box.createHorizontalStrut(3));
		box.add(btnCancel = new JButton("Cancel"));
		btnCancel.setMnemonic('C');
		ActionListener actionlistener = new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				if (actionevent.getSource() == btnCancel)
					cancelled = true;
				dispose();
			}

		};
		btnOk.addActionListener(actionlistener);
		btnCancel.addActionListener(actionlistener);
		box.setAlignmentX(1.0F);
		container.add(box);
		getRootPane().setDefaultButton(btnOk);
		pack();
		setLocationRelativeTo(frame);
		setResizable(false);
	}

	public Color getColor() {
		return colorChooser.getColor();
	}

	public boolean cancelled;
	private final JColorChooser colorChooser = new JColorChooser();
	private JButton btnOk;
	private JButton btnCancel;

}
