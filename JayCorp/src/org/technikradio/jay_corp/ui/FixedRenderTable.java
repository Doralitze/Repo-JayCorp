package org.technikradio.jay_corp.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class FixedRenderTable extends JPanel {
	private static final long serialVersionUID = 2019095826654681316L;

	private Font f;
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
			for (int i = 0; i < head.length; i++) {
				//Add a with textlenght of head[i]
			}
		} else
			minWidth = 50;

	}

	@Override
	public void paintComponent(Graphics g) {
		// Draw the header

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
