package org.technikradio.jay_corp.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class SlideMenuContainer extends JComponent {

	private static final long serialVersionUID = 3907952867719102783L;

	private int index = 0;

	private ArrayList<JPanel> panels;
	private SetupMovingListener sml;
	private JPanel container;
	private JButton abortButton;
	private JButton backButton;
	private JButton nextButton;

	public SlideMenuContainer(SetupMovingListener s) {
		setup();
		sml = s;
	}

	private void setup() {
		panels = new ArrayList<JPanel>();
		container = new JPanel();
		abortButton = new JButton("Abbrechen");
		backButton = new JButton("Zurück");
		nextButton = new JButton("Weiter");

		this.add(this.container);
		this.add(this.abortButton);
		this.add(this.backButton);
		this.add(this.nextButton);

		abortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sml.abort();
			}
		});
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sml.goBack();
			}
		});
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sml.goForeward();
			}
		});
	}

	public void goTo(int index) {
		if (index + 1 > panels.size() || index < 0)
			return;
		panels.get(this.index).setVisible(false);
		this.index = index;
		panels.get(this.index).setVisible(true);
		if (index - 1 == panels.size()) {
			nextButton.setText("Fertigstellen");
			abortButton.setEnabled(false);
		} else {
			nextButton.setText("Weiter");
			abortButton.setEnabled(true);
		}
		if (index == 0) {
			backButton.setEnabled(false);
		} else
			backButton.setEnabled(true);
	}

	public void addPanel(JPanel p) {
		panels.add(p);
		p.setVisible(false);
		container.add(p);
	}

	public int getCardSize() {
		return panels.size();
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		this.container.setBounds(new Rectangle(0, 0, this.getWidth(), this.getHeight() - 30));
		this.nextButton.setBounds(this.getWidth() - 55, this.getHeight() - 25, 50, 20);
		this.backButton.setBounds(this.getWidth() - 110, this.getHeight() - 25, 50, 20);
		this.abortButton.setBounds(this.getWidth() - 165, this.getHeight() - 25, 50, 20);
		System.out.println(new Rectangle(this.getWidth() - 165, this.getHeight() - 25, 50, 20));
		for (JPanel p : panels) {
			p.setBounds(0, 0, container.getWidth(), container.getHeight());
		}
		this.setVisible(true);
		this.repaint();
	}

	public int getIndex() {
		return index;
	}

}
