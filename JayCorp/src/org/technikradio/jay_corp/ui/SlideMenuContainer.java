package org.technikradio.jay_corp.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
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
				if (panels.get(index).getClass().isAnnotationPresent(AdvancedPage.class)) {
					SetupNotifier s = (SetupNotifier) panels.get(index);
					if (s.canGoForward())
						sml.goForeward();
					else
						JOptionPane.showMessageDialog(null, "Die aktuelle Seite ist noch nicht ausgefüllt.",
								"Sie sind noch nicht fertig..." + toString(), JOptionPane.INFORMATION_MESSAGE);
				} else
					sml.goForeward();
			}
		});
		container.setBackground(Color.GRAY);
		this.goTo(0);
	}

	public void goTo(int index) {
		if (index + 1 > panels.size() || index < 0)
			return;
		panels.get(this.index).setVisible(false);
		if (panels.get(index).getClass().isAnnotationPresent(AdvancedPage.class)) {
			SetupNotifier s = (SetupNotifier) panels.get(index);
			s.leaveFocus();
		}
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
		if (panels.get(index).getClass().isAnnotationPresent(AdvancedPage.class)) {
			SetupNotifier s = (SetupNotifier) panels.get(index);
			s.activateCurrentPage();
		}
	}

	public void addPanel(JPanel p) {
		panels.add(p);
		p.setBounds(container.getBounds());
		p.setMinimumSize(container.getMinimumSize());
		p.setVisible(false);
		container.add(p);
		// p.setSize(container.getSize());
		if (p.getClass().isAnnotationPresent(AdvancedPage.class)) {
			SetupNotifier s = (SetupNotifier) panels.get(index);
			s.addedToSlider();
		}
		if (panels.size() == 1)
			goTo(0);
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
		this.abortButton.setBounds(this.getWidth() - 195, this.getHeight() - 25, 80, 20);
		for (JPanel p : panels) {
			p.setBounds(container.getBounds());
			p.setMinimumSize(container.getMinimumSize());
		}
		this.setVisible(true);
		this.repaint();
	}

	public int getIndex() {
		return index;
	}

}
