package org.technikradio.jay_corp.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.technikradio.jay_corp.ui.helpers.MoreColors;

public class FixedRenderTable extends JPanel {
	private static final long serialVersionUID = 2019095826654681316L;

	private Font f;
	private FontMetrics lastFM;
	private String[][] data;
	private String[] head;
	private int minWidth = 0;
	private int minHeight = 0;

	public FixedRenderTable() {
		super(true);
		setup();
	}

	public FixedRenderTable(LayoutManager layout) {
		super(layout);
		setup();
	}

	public FixedRenderTable(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		setup();
	}

	public FixedRenderTable(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		setup();
	}

	private void setup() {
		f = new Font("Arial", 12, 0);
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (data != null)
			minHeight = 20 * data.length + 25;
		else
			minHeight = 25;
		if (head != null) {
			int a = 15;
			if (lastFM != null)
				for (int i = 0; i < head.length; i++) {
					// Add a with textlenght of head[i]
					a += lastFM.stringWidth(head[i]);
				}
			else
				for (int i = 0; i < head.length; i++) {
					a += head[i].length() * 6;
				}
		} else
			minWidth = 50;

	}

	@Override
	public void paintComponent(Graphics g) {
		if (f != null)
			g.setFont(f);
		lastFM = g.getFontMetrics();
		// Draw the header (Or use a special component for it)
		// Draw the body
		for (int i = 0; i < data.length; i++) {
			if (i % 2 == 0)
				g.setColor(Color.LIGHT_GRAY);
			else
				g.setColor(MoreColors.VERRY_LIGHT_GRAY);
			g.fillRect(0, i * 25, this.getWidth(), 25);
			g.setColor(Color.BLACK);
			g.drawLine(0, i * 25, this.getWidth(), i * 25);
			int am = 15;
			for (int j = 0; j < data[i].length; j++) {
				g.drawString(data[i][j], am, (i * 25) + 12);
				am += lastFM.stringWidth(data[i][j]);
			}
		}

	}

	public String[][] getData() {
		return data;
	}

	public void setData(String[][] data) {
		this.data = data;
		this.updateUI();
	}

	/**
	 * @return the head
	 */
	public String[] getHead() {
		return head;
	}

	/**
	 * @param head
	 *            the head to set
	 */
	public void setHead(String[] head) {
		this.head = head;
	}

}
