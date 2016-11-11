/*
JayCorp-Client/SlideMenuContainer.java
Copyright (C) 2015-2016  Leon C. Dietrich (Doralitze)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.technikradio.jay_corp.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.technikradio.jay_corp.Application;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

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
				try {
					sml.abort();
				} catch (Exception e1) {
					e1.printStackTrace();
					Application.crash(e1);
					
				}
			}
		});
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					sml.goBack();
				} catch (Exception e1) {
					e1.printStackTrace();
					Application.crash(e1);
				}
			}
		});
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// if
				// (!(panels.get(index).getClass().isAnnotationPresent(AdvancedPage.class)))
				// {
				try {
					if (panels.get(index) instanceof SetupNotifier) {
						SetupNotifier s = (SetupNotifier) panels.get(index);
						if (s.canGoForward())
							sml.goForeward();
						else
							JOptionPane.showMessageDialog(null, "Die aktuelle Seite ist noch nicht ausgefüllt.",
									"Sie sind noch nicht fertig..." + toString(), JOptionPane.INFORMATION_MESSAGE);
					} else {
						Console.log(LogType.Warning, this,
								"Current panel [" + index + "] is not annotated with the @AdvancedPage annotation!");
						sml.goForeward();
					}
				} catch(HeadlessException e1){
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
					Application.crash(e1);
				}
			}
		});
		container.setBackground(Color.GRAY);
		this.goTo(0);
	}

	public void goTo(int index) {
		if (index + 1 > panels.size() || index < 0)
			return;
		panels.get(this.index).setVisible(false);
		if (panels.get(index) instanceof SetupNotifier) {
			// if
			// (panels.get(index).getClass().isAnnotationPresent(AdvancedPage.class))
			// {
			SetupNotifier s = (SetupNotifier) panels.get(index);
			s.leaveFocus();
		}
		this.index = index;
		panels.get(this.index).setVisible(true);
		if (index + 1 == panels.size() && panels.size() > 1) {
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
		if (p instanceof SetupNotifier) {
			SetupNotifier s = (SetupNotifier) panels.get(panels.size() - 1);
			s.addedToSlider();
			Console.log(LogType.Information, this, "Panel " + (panels.size() - 1) + " is not dumm!");
		} else
			Console.log(LogType.Warning, this, "Panel " + (panels.size() - 1) + " is dumm!");
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
		this.nextButton.setBounds(this.getWidth() - 105, this.getHeight() - 25, 100, 20);
		this.backButton.setBounds(this.getWidth() - 210, this.getHeight() - 25, 100, 20);
		this.abortButton.setBounds(this.getWidth() - 315, this.getHeight() - 25, 100, 20);
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

	@Override
	public String toString() {
		return "SlideMenuContainer";
	}

}
