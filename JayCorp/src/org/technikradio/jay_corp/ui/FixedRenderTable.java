package org.technikradio.jay_corp.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.technikradio.jay_corp.ui.helpers.MoreColors;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class FixedRenderTable extends JPanel {
	private static final long serialVersionUID = 2019095826654681316L;

	private Font f;
	private FontMetrics lastFM;
	private String[][] data;
	private String[] head;
	private int minWidth = 0;
	private int minHeight = 0;
	private final static boolean debugMode = Boolean
			.parseBoolean(System.getProperty("org.technikradio.jay_corp.ui.debugmode"));

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
		f = new Font("Arial", 0, 12);
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
					a += 20;
				}
			else
				for (int i = 0; i < head.length; i++) {
					a += head[i].length() * 20;
				}
			minWidth = a;
		} else
			minWidth = 50;
		this.setMinimumSize(new Dimension(minWidth, minHeight));
		this.setPreferredSize(new Dimension(minWidth + 10, minHeight + 10));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (f != null)
			g.setFont(f);
		lastFM = g.getFontMetrics();
		// Draw the header (Or use a special component for it)
		// Draw the body
		if (data != null)
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
					g.drawString(data[i][j], am, (i * 25) + (int) ((25 - lastFM.getAscent()) * 1.25f));
					// Console.log(LogType.StdOut, this, "Writing text: " + am +
					// ",
					// " + i * 25 + ", " + data[i][j]);
					am += lastFM.stringWidth(data[i][j]);
					am += 10;
					g.drawLine(am, (i * 25), am, (i + 1) * 25);
					am += 10;
				}
			}
		else if(debugMode){
			Console.log(LogType.StdOut, this, "No data");
		}
	}

	public String[][] getData() {
		return data;
	}

	public void setData(String[][] data) {
		this.data = data;
		this.updateUI();
		this.repaint();
	}

	public JComponent getHeadComponent() {
		final String[] headdata = head;
		return new JComponent() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3976090048633541194L;

			private final String[] data = headdata;

			protected int maxWidth;

			@Override
			public void paintComponent(Graphics g) {
				if (isBigger(this.getMinimumSize(), this.getSize()))
					this.setSize(this.getMinimumSize());
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				int am = 10;
				/*
				 * for (int i = 0; i < data.length; i++) { g.drawString(data[i],
				 * this.getWidth() / 2 - g.getFontMetrics().getHeight(), am); am
				 * += 10; am += g.getFontMetrics().stringWidth(data[i]); }
				 */
				if (data != null)
					for (int i = 0; i < data.length; i++) {
						g.drawString(data[i], am, (i * 25) + (int) ((25 - lastFM.getAscent()) * 1.25f));
						// Console.log(LogType.StdOut, this, "Writing text: " +
						// am + ",
						// " + i * 25 + ", " + data[i][j]);
						am += lastFM.stringWidth(data[i]);
						am += 10;
						g.drawLine(am, (i * 25), am, (i + 1) * 25);
						am += 10;
					}
				am += 10;
				maxWidth = am;
				if (debugMode)
					Console.log(LogType.Information, this,
							"Renderred [" + this.getWidth() + "; " + this.getHeight() + "]");
			}

			private boolean isBigger(Dimension da, Dimension db) {
				double a = da.getWidth() * da.getHeight();
				double b = db.getWidth() * db.getHeight();
				return a < b;
			}

			@Override
			public Dimension getMinimumSize() {
				Dimension s = super.getMinimumSize();
				s.setSize(s.getWidth() + maxWidth, s.getHeight() + 25);
				return s;
			}

			@Override
			public String toString() {
				return "org.technikradio.jay_corp.ui.FixedRenderTable.HeadComponent";
			}
		};
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

	@Override
	public String toString() {
		return "org.technikradio.jay_corp.ui.FixedRenderTable";
	}

}
