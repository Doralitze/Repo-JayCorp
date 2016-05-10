/*
JayCorp-Client/Calendar.java
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.Settings;
import org.technikradio.jay_corp.user.DayTable;
import org.technikradio.jay_corp.user.DayTable.Status;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;
import org.technikradio.universal_tools.ParaDate;
import org.technikradio.universal_tools.Time;

public class Calendar extends JComponent implements MouseListener, KeyListener {

	private static final long serialVersionUID = -1865312314642912485L;
	private static final float OVER_SIZE_DIVISOR = 1.05f;
	private DayTable content;
	private DayTable originalContent;
	private boolean editEnabled = false;
	private int maxNumDay = 0;
	private boolean advancedOutputFlag = Boolean.valueOf(Settings.getString(Settings.getString("AdvancedOutputMode"))); // $NON-NLS-2$ //$NON-NLS-1$
	private boolean alwaysDisplayShortDays = Boolean
			.valueOf(Settings.getString(Settings.getString("AlwaysUseShortDayNames"))); // $NON-NLS-2$ //$NON-NLS-1$
	private String infoMessage = ""; //$NON-NLS-1$

	// Internal data cache
	private Month currentSelectedMonth = Month.JANUARY;
	private String dsString = ""; //$NON-NLS-1$
	private String message = null;
	private int[][] cachedData; // [Month][Day] = (Ordinal) Status
	private int currentYear = 0;
	private int selectedDays = 0;
	private boolean changed = false;
	private boolean renderYearInFooter = true;

	public enum Month {
		JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULI, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER,;

		public static Month valueOf(int ordinal) throws DateException {
			if (ordinal < 0 || ordinal > 11)
				throw new DateException("Invalid month"); //$NON-NLS-1$
			switch (ordinal) {
			case 0:
				return Month.JANUARY;
			case 1:
				return Month.FEBRUARY;
			case 2:
				return Month.MARCH;
			case 3:
				return Month.APRIL;
			case 4:
				return Month.MAY;
			case 5:
				return Month.JUNE;
			case 6:
				return Month.JULI;
			case 7:
				return Month.AUGUST;
			case 8:
				return Month.SEPTEMBER;
			case 9:
				return Month.OCTOBER;
			case 10:
				return Month.NOVEMBER;
			case 11:
				return Month.DECEMBER;
			}
			return Month.JANUARY;
		}
	}

	public Calendar() {
		super();
		setup();
	}

	private void setup() {
		content = null;
		this.addMouseListener(this);
		this.addKeyListener(this);
		Console.log(LogType.StdOut, this, "Loaded font: " + Settings.getString("Calendar.UsedFont")); //$NON-NLS-1$//$NON-NLS-2$
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent arg0) {
				return;
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				return;
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				int m = 900;
				if (infoMessage != null)
					if (infoMessage.length() > 15)
						m = 1000;
				if (getWidth() < m) {
					setRenderYearInFooterHint(false);
				} else
					setRenderYearInFooterHint(true);
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				return;
			}
		});
	}

	/**
	 * This functions builds thge cache from the given daytable
	 * 
	 * @param d
	 *            The DayTable object to use
	 */
	private void calcCache(DayTable d) {
		setOriginalContent(d);
		boolean cY = false;
		int sDc = 0;
		if (d == null)
			return;
		Console.log(LogType.StdOut, this, "Amount in hashmap: " + d.getDays().size()); //$NON-NLS-1$
		cachedData = new int[13][32];
		// Set everything to undefined due to multi-db bug
		for (ParaDate pd : d.getDays().keySet()) {
			cachedData[pd.getMonth()][pd.getDay()] = Status.undefined.ordinal();
		}
		for (ParaDate pd : d.getDays().keySet()) {
			Status s = d.getDays().get(pd);
			try {
				if (cachedData[pd.getMonth()][pd.getDay()] == Status.undefined.ordinal()
				/*
				 * ||
				 * !isLower(Status.valueOf(cachedData[pd.getMonth()][pd.getDay()
				 * ]), s)
				 */) {
					// Status s = d.getDays().get(pd);
					cachedData[pd.getMonth()][pd.getDay()] = s.ordinal();
					if (!cY) {
						cY = true;
						currentYear = pd.getYear();
						dsString = Strings.getString("Calendar.YearPreHeader") + currentYear //$NON-NLS-1$
								+ Strings.getString("Calendar.YearPastHeader"); //$NON-NLS-1$
					}
					if (s == Status.selected)
						sDc++;
				}
			} catch (Exception e) {
				Console.log(LogType.Error, this, "Incompatible data format"); //$NON-NLS-1$
				e.printStackTrace();
			}
		}
		selectedDays = sDc;
		Console.log(LogType.StdOut, this, "Successfully cached raw data"); //$NON-NLS-1$
	}

	public static boolean isLower(Status key, Status comp) {
		if (comp == Status.undefined)
			return true;
		if (key.ordinal() < comp.ordinal())
			return true;
		return false;
	}

	private int getMonthLenght(int year, Month m) {
		int l = 0;
		switch (m) {
		case APRIL:
			l = 30;
			break;
		case AUGUST:
			l = 31;
			break;
		case DECEMBER:
			l = 31;
			break;
		case FEBRUARY:
			if (year % 4 == 0)
				l = 29;
			else
				l = 28;
			break;
		case JANUARY:
			l = 31;
			break;
		case JULI:
			l = 31;
			break;
		case JUNE:
			l = 30;
			break;
		case MARCH:
			l = 31;
			break;
		case MAY:
			l = 31;
			break;
		case NOVEMBER:
			l = 30;
			break;
		case OCTOBER:
			l = 31;
			break;
		case SEPTEMBER:
			l = 30;
			break;
		}
		return l;
	}

	private String getDayName(int day, int width) {
		if (width < 1500 || alwaysDisplayShortDays) {
			switch (day) {
			case 1:
				return Strings.getString("Calendar.Days.ShortMonday"); //$NON-NLS-1$
			case 2:
				return Strings.getString("Calendar.Days.ShortTuesday"); //$NON-NLS-1$
			case 3:
				return Strings.getString("Calendar.Days.ShortWednesday"); //$NON-NLS-1$
			case 4:
				return Strings.getString("Calendar.Days.ShortThursday"); //$NON-NLS-1$
			case 5:
				return Strings.getString("Calendar.Days.ShortFriday"); //$NON-NLS-1$
			case 6:
				return Strings.getString("Calendar.Days.ShortSaturday"); //$NON-NLS-1$
			case 7:
				return Strings.getString("Calendar.Days.ShortSunday"); //$NON-NLS-1$
			}
		} else {
			switch (day) {
			case 1:
				return Strings.getString("Calendar.Days.Monday"); //$NON-NLS-1$
			case 2:
				return Strings.getString("Calendar.Days.Tuesday"); //$NON-NLS-1$
			case 3:
				return Strings.getString("Calendar.Days.Wednesday"); //$NON-NLS-1$
			case 4:
				return Strings.getString("Calendar.Days.Thursday"); //$NON-NLS-1$
			case 5:
				return Strings.getString("Calendar.Days.Friday"); //$NON-NLS-1$
			case 6:
				return Strings.getString("Calendar.Days.Saturday"); //$NON-NLS-1$
			case 7:
				return Strings.getString("Calendar.Days.Sunday"); //$NON-NLS-1$
			}
		}
		return "Invalid Day"; //$NON-NLS-1$
	}

	/**
	 * Calculate the first weekday in month
	 * @param month The month to calculate for
	 * @return the first weekday in the given month (1 - 7, Mo - So)
	 */
	private int getMonthConversion(int month) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.set(currentYear, month, 1);
		int d = c.get(java.util.Calendar.DAY_OF_WEEK);
		if (d == 1)
			d = 7;
		else
			d--;
		return d;
	}

	public void setContent(DayTable d) {
		content = d;
		calcCache(d);
	}

	public DayTable getContent() {
		return content;
	}

	public static final String monthToString(Month m) {
		switch (m) {
		case APRIL:
			return Strings.getString("Calendar.Month.April"); //$NON-NLS-1$
		case AUGUST:
			return Strings.getString("Calendar.Month.August"); //$NON-NLS-1$
		case DECEMBER:
			return Strings.getString("Calendar.Month.December"); //$NON-NLS-1$
		case FEBRUARY:
			return Strings.getString("Calendar.Month.February"); //$NON-NLS-1$
		case JULI:
			return Strings.getString("Calendar.Month.Juli"); //$NON-NLS-1$
		case JUNE:
			return Strings.getString("Calendar.Month.June"); //$NON-NLS-1$
		case MAY:
			return Strings.getString("Calendar.Month.May"); //$NON-NLS-1$
		case NOVEMBER:
			return Strings.getString("Calendar.Month.November"); //$NON-NLS-1$
		case OCTOBER:
			return Strings.getString("Calendar.Month.October"); //$NON-NLS-1$
		case SEPTEMBER:
			return Strings.getString("Calendar.Month.September"); //$NON-NLS-1$
		case JANUARY:
			return Strings.getString("Calendar.Month.January"); //$NON-NLS-1$
		case MARCH:
			return Strings.getString("Calendar.Month.March"); //$NON-NLS-1$
		default:
			return Strings.getString("Calendar.Month.Default"); //$NON-NLS-1$
		}
	}

	private float getWindowMultiplicator(int width, int height) {
		float mul = 0;
		if (getMonthConversion(currentSelectedMonth.ordinal()) - 1 <= 4)
			mul = (float) width / 400;
		else
			mul = (float) (width / 400) / OVER_SIZE_DIVISOR;
		while (true)
			if ((mul * 35) + 50 > height)
				mul = (float) (mul - 0.25);
			else
				break;
		return mul;
	}

	/**
	 * This function computes the position of the day buttons
	 * @param frameWidth The width of the parent frame
	 * @param frameHeight The height of the parent frame
	 * @param month The current selected Month
	 * @param day The current day
	 * @return The position and size of the buttons
	 */
	private ComplexRectangleFloat getRenderingPosition(int frameWidth, int frameHeight, int month, int day) {
		ComplexRectangleFloat r = new ComplexRectangleFloat();
		r.resize(4);
		
		// Calculate size multiplicator
		float sizeMultiplicator = getWindowMultiplicator(frameWidth, frameHeight); //mul

		// Calculate secondary multiplicator
		float angleMultiplicator = 1; //dmul
		if (sizeMultiplicator > 2)
			angleMultiplicator = (float) (sizeMultiplicator * 0.5);

		// Output the calculated numbers so far if requested
		if (isAdvancedOutputFlag())
			Console.log(LogType.Information, this, "MUL -> " + Float.toString(sizeMultiplicator) + "; DMUL -> " //$NON-NLS-1$ //$NON-NLS-2$
					+ Float.toString(angleMultiplicator));

		// Calculate positions and size

		int weekColumn = getMonthConversion(month) - 2;
		//int monthlenght = getMonthLenght(currentYear, currentSelectedMonth);
		int row = 1;

		
		for (int i = 0; i < day; i++) {
			weekColumn++;
			if (weekColumn == 7) {
				row++;
				weekColumn = 0;
			}
		}

		int x = ((int) ((weekColumn * (35 * sizeMultiplicator)) + (frameWidth - (245 * sizeMultiplicator)) / 2)) + (int) (5 * angleMultiplicator);
		int y = ((int) ((row * (35 * sizeMultiplicator) + 30) - (35 * sizeMultiplicator * 0.5))) + (int) (5 * angleMultiplicator);

		int size = (int) (25 * sizeMultiplicator);

		int arcWidth = (int) (5 * angleMultiplicator);
		int arcHight = (int) (5 * angleMultiplicator);

		r.setLocation(new Point(x, y));
		r.setSize(size, size);
		r.setAt(0, weekColumn);
		r.setAt(1, arcWidth);
		r.setAt(2, arcHight);
		r.setAt(3, angleMultiplicator);

		if(advancedOutputFlag)
			System.out.println("Date position (" + day + "): " + x + ", " + y + " [" + size + ", " + size + "]");
		
		return r;
	}

	@Override
	protected void paintComponent(Graphics g1d) {
		Graphics2D g = (Graphics2D) g1d;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g);
		int width = this.getWidth();
		int height = this.getHeight();
		if (content == null) {
			g.drawString(Strings.getString("Calendar.Messages.LoadingData"), width / 2 - 50, height / 2 - 25); //$NON-NLS-1$
			// Render loading animation
		} else {
			// Draw the header
			g.setFont(new Font(Settings.getString("Calendar.UsedFont"), // $NON-NLS-2$ //$NON-NLS-1$
					Font.BOLD, 15));
			g.drawLine(0, 25, width, 25);
			if (renderYearInFooter)
				g.drawString(monthToString(currentSelectedMonth), 5, 15);
			else
				g.drawString(monthToString(currentSelectedMonth) + " " + Integer.toString(currentYear), 5, 15);
			{
				FontMetrics m = g.getFontMetrics();
				if (getMaxNumDay() - selectedDays < 1) {
					g.setColor(Color.RED);
					if (isAdvancedOutputFlag())
						Console.log(LogType.Information, this, "There are 0 days left"); //$NON-NLS-1$
				}
				String s = Strings.getString("Calendar.DaysLeft") //$NON-NLS-1$
						+ Integer.toString(getMaxNumDay() - selectedDays);
				g.setColor(Color.BLACK);
				int w = m.getAscent() / 2;
				g.drawString(s, (width / 2) - w, 15);
			}

			{
				// Draw month-navigators
				g.drawRoundRect(width - 50, 5, 15, 15, 3, 3);
				g.drawRoundRect(width - 25, 5, 15, 15, 3, 3);
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(width - 49, 6, 13, 13);
				g.fillRect(width - 24, 6, 13, 13);
				g.setColor(Color.BLACK);
				Polygon p = new Polygon();
				p.addPoint(width - 47, 13);
				p.addPoint(width - 37, 9);
				p.addPoint(width - 37, 17);
				g.fillPolygon(p);
				p = new Polygon();
				p.addPoint(width - 12, 13);
				p.addPoint(width - 22, 9);
				p.addPoint(width - 22, 17);
				g.fillPolygon(p);
			}

			{
				// Render calendar
				g.setFont(new Font(Settings.getString("Calendar.UsedFont"), // $NON-NLS-2$ //$NON-NLS-1$
						Font.BOLD, 20));

				float mul = getWindowMultiplicator(width, height);

				// Render the week day names
				for (int i = 1; i <= 7; i++) {
					if (i < 6)
						g.setColor(Color.BLACK);
					else
						g.setColor(Color.decode("0xb30000")); //$NON-NLS-1$
					int x = (int) ((i * (35 * mul)) + (width - (245 * mul)) / 2) - (int) (30 * mul);
					int y = (int) (35 * (mul) - 20 * mul);
					if (y < 50)
						y = 50;
					g.drawString(getDayName(i, width), x, y);
				}

				g.setFont(new Font(Settings.getString("Calendar.UsedFont"), // $NON-NLS-2$ //$NON-NLS-1$
						Font.BOLD, (int) (10 + mul * mul * 2)));
				for (int i = 1; i <= getMonthLenght(currentYear, currentSelectedMonth); i++) {
					try {
						g.setColor(getColor(Status.valueOf(cachedData[currentSelectedMonth.ordinal() + 1][i])));
					} catch (Exception e) {
						Console.log(LogType.Error, this, "An unexpected exception occured: " //$NON-NLS-1$
								+ e.getMessage());
						e.printStackTrace();
					}

					ComplexRectangleFloat pos = getRenderingPosition(width, height, currentSelectedMonth.ordinal(), i);
					int arcWidth = (int) pos.getAt(1);
					int arcHeight = (int) pos.getAt(2);
					float dmul = pos.getAt(3);
					
					g.fillRoundRect(pos.x, pos.y, pos.width, pos.height, arcWidth, arcHeight);
					if (pos.getAt(0) < 5)
						g.setColor(Color.BLACK);
					else
						g.setColor(Color.decode("0xb30000")); //$NON-NLS-1$
					g.drawRoundRect(pos.x, pos.y, pos.width, pos.height, arcWidth, arcHeight);
					g.drawRoundRect(pos.x + 1, pos.y + 1, pos.width - 2, pos.height - 2, arcWidth - 1, arcHeight - 1);
					// Render date inside rectangle
					{
						int xi = (int) ((25 * mul) / 2) - g.getFontMetrics().stringWidth(Integer.toString(i)) / 2;
						int yi = (int) ((25 * mul) / 2) + g.getFontMetrics().getHeight() / 3;
						g.drawString(Integer.toString(i), (pos.x + (int) (5 * dmul)) + xi, (pos.y + (int) (5 * dmul)) + yi);
					}

				}
				// End of calendar rendering

				{
					// Render the footer
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
					g.setColor(Color.BLACK);
					g.drawLine(0, height - 25, width, height - 25);
					{
						g.setFont(new Font(Settings.getString("Calendar.UsedFont"), // $NON-NLS-2$ //$NON-NLS-1$
								Font.BOLD, 11));
						int x = 25;
						g.setColor(getColor(Status.normal));
						g.fillRoundRect(x, height - 20, 15, 15, 3, 3);
						g.setColor(Color.BLACK);
						g.drawRoundRect(x, height - 20, 15, 15, 3, 3);
						x += 18;
						if (Protocol.getCurrentUser().getID() != 0) {
							g.drawString(Strings.getString("Calendar.Messages.InfoNormal"), x, height - 7);//$NON-NLS-1$
							x += g.getFontMetrics().stringWidth(Strings.getString("Calendar.Messages.InfoNormal")) + 10; //$NON-NLS-1$
						} else {
							g.drawString(Strings.getString("Calendar.Messages.InfoNormalRoot"), x, height - 7);//$NON-NLS-1$
							x += g.getFontMetrics().stringWidth(Strings.getString("Calendar.Messages.InfoNormalRoot")) //$NON-NLS-1$
									+ 10;
						}
						g.drawLine(x, height - 25, x, height);
						x += 10;
						g.setColor(getColor(Status.allowed));
						g.fillRoundRect(x, height - 20, 15, 15, 3, 3);
						g.setColor(Color.BLACK);
						g.drawRoundRect(x, height - 20, 15, 15, 3, 3);
						x += 18;
						if (Protocol.getCurrentUser().getID() != 0) {
							g.drawString(Strings.getString("Calendar.Messages.InfoAllowed"), x, height - 7);//$NON-NLS-1$
							x += g.getFontMetrics().stringWidth(Strings.getString("Calendar.Messages.InfoAllowed")) //$NON-NLS-1$
									+ 10;
						} else {
							g.drawString(Strings.getString("Calendar.Messages.InfoAllowedRoot"), x, height - 7);//$NON-NLS-1$
							x += g.getFontMetrics().stringWidth(Strings.getString("Calendar.Messages.InfoAllowedRoot")) //$NON-NLS-1$
									+ 10;
						}
						g.drawLine(x, height - 25, x, height);
						x += 10;
						g.setColor(getColor(Status.selected));
						g.fillRoundRect(x, height - 20, 15, 15, 3, 3);
						g.setColor(Color.BLACK);
						g.drawRoundRect(x, height - 20, 15, 15, 3, 3);
						x += 18;
						if (Protocol.getCurrentUser().getID() != 0) {
							g.drawString(Strings.getString("Calendar.Messages.InfoSelected"), x, height - 7);//$NON-NLS-1$
							x += g.getFontMetrics().stringWidth(Strings.getString("Calendar.Messages.InfoSelected")) //$NON-NLS-1$
									+ 10;
						} else {
							g.drawString(Strings.getString("Calendar.Messages.InfoSelectedRoot"), x, height - 7);//$NON-NLS-1$
							x += g.getFontMetrics().stringWidth(Strings.getString("Calendar.Messages.InfoSelectedRoot")) //$NON-NLS-1$
									+ 10;
						}
						g.drawLine(x, height - 25, x, height);
						x += 10;
						// Show document unsaved hint
						if (isChanged()) {
							g.drawString(Strings.getString("Calendar.Messages.Unsaved"), x, height - 7);//$NON-NLS-1$
							x += g.getFontMetrics().stringWidth(Strings.getString("Calendar.Messages.Unsaved")) + 10; //$NON-NLS-1$
							g.drawLine(x, height - 25, x, height);
						} else {
							g.drawString(Strings.getString("Calendar.Messages.Saved"), x, height - 7);//$NON-NLS-1$
							x += g.getFontMetrics().stringWidth(Strings.getString("Calendar.Messages.Saved")) + 10; //$NON-NLS-1$
						}
						g.drawLine(x, height - 25, x, height);
						x += 10;
						g.drawString(getInfoMessage(), x, height - 7);
					}
					// Render year
					if (renderYearInFooter) {
						g.setFont(new Font(Settings.getString("Calendar.UsedFont"), // $NON-NLS-2$ //$NON-NLS-1$
								Font.BOLD, 15));
						FontMetrics fm = g.getFontMetrics();
						int strWidth = fm.stringWidth(dsString) + 15;
						g.setColor(this.getBackground());
						g.fillRect(width - (strWidth + 5), height - 24, width, height);
						g.setColor(Color.BLACK);
						g.drawString(dsString, width - strWidth, height - 7);
					}
					{
						// Render pop-ups
						if (message != null) {
							g.setColor(new Color(192, 192, 192, 128));
							g.fillRect(0, 0, this.getWidth(), this.getHeight() - 25);
							g.setColor(new Color(128, 128, 128));
							g.fillRect((this.getWidth() / 2) - 150, (this.getHeight() / 2) - 50, 300, 100);
							g.setColor(Color.WHITE);
							g.setFont(new Font("Arial", Font.BOLD, 15));
							g.drawString(message, (this.getWidth() / 2) - (g.getFontMetrics().stringWidth(message) / 2),
									(this.getHeight() / 2) - g.getFontMetrics().getHeight() / 2);
							// System.out.println("painting");
						}
					}
				}
			}
		}
		if (isAdvancedOutputFlag())
			Console.log(LogType.Information, this, "Successfully painted control"); //$NON-NLS-1$
		if (!isEditEnabled()) {
			g.setColor(new Color(100, 100, 100, 200));
			g.fillRect(0, 26, width, height - (26 * 2) + 1);
		}
	}

	private Color getColor(Status s) {
		switch (s) {
		case allowed:
			return Color.decode(Settings.getString("Calendar.Color.StatusAllowed"));// $NON-NLS-2$ //$NON-NLS-1$
		case normal:
			return Color.decode(Settings.getString("Calendar.Color.StatusNormal"));// $NON-NLS-2$ //$NON-NLS-1$
		case selected:
			return Color.decode(Settings.getString("Calendar.Color.StatusSelected"));// $NON-NLS-2$ //$NON-NLS-1$
		default:
			return Color.BLACK;
		}
	}

	public boolean isEditEnabled() {
		return editEnabled;
	}

	public void setEditEnabled(boolean editEnabled) {
		this.editEnabled = editEnabled;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		setEditEnabled(Protocol.isEditEnabled());
		int width = this.getWidth();
		int height = this.getHeight();
		int x = e.getX();
		int y = e.getY();
		Console.log(LogType.StdOut, this, "Click: " + x + ":" + y);
		if (y <= 25) {
			if (x >= width - 50 && x <= width - 35) {
				if (y >= 5 && y <= 20) {
					if (currentSelectedMonth != Month.JANUARY) {
						try {
							currentSelectedMonth = Month.valueOf(currentSelectedMonth.ordinal() - 1);
							if (isAdvancedOutputFlag())
								Console.log(LogType.Information, this, "Pressed on rew button"); //$NON-NLS-1$
						} catch (DateException e1) {
							Console.log(LogType.Error, this, "An unexpected exception occured: " + e1.getMessage()); //$NON-NLS-1$
							e1.printStackTrace();
						}
					}
				}
			}
			if (x >= width - 25 && x <= width - 10) {
				if (y >= 5 && y <= 20) {
					if (currentSelectedMonth != Month.DECEMBER) {
						try {
							currentSelectedMonth = Month.valueOf(currentSelectedMonth.ordinal() + 1);
							if (isAdvancedOutputFlag())
								Console.log(LogType.Information, this, "Pressed on next button"); //$NON-NLS-1$
						} catch (DateException e1) {
							Console.log(LogType.Error, this, "An unexpected exception occured: " + e1.getMessage()); //$NON-NLS-1$
							e1.printStackTrace();
						}
					}
				}
			}
		} else {
			if (isEditEnabled()){
				int month = currentSelectedMonth.ordinal();
				for (int i = 1; i <= getMonthLenght(currentYear, currentSelectedMonth); i++) {
					ComplexRectangleFloat pos = getRenderingPosition(width, height, month, i);
					if((pos.x <= x) && (pos.x + pos.width) >= x){
						if(pos.y <= y && (pos.y + pos.height) >= y){
							System.out.println("hit");
							
							//Found the clicked position
							if (isAdvancedOutputFlag())
								Console.log(LogType.Information, this, "Pressed on day element: (" + i + ") "); //$NON-NLS-1$ //$NON-NLS-2$
							setChanged(true);
							if (cachedData[currentSelectedMonth.ordinal() + 1][i] == Status.allowed.ordinal()) {
								if (getMaxNumDay() - selectedDays > 0) {
									selectedDays++;
									cachedData[currentSelectedMonth.ordinal() + 1][i] = Status.selected.ordinal();
								} else {
									// No days left
									JOptionPane.showMessageDialog(this.getParent(),
											Strings.getString("Calendar.NoDaysLeft"),
											Strings.getString("Calendar.NoDaysLeftHeader"),
											JOptionPane.INFORMATION_MESSAGE);
								}
								if (isAdvancedOutputFlag())
									System.out.print("allowed found"); //$NON-NLS-1$
							} else if (cachedData[currentSelectedMonth.ordinal() + 1][i] == Status.selected.ordinal()) {
								selectedDays--;
								cachedData[currentSelectedMonth.ordinal() + 1][i] = Status.allowed.ordinal();
								if (isAdvancedOutputFlag())
									System.out.print("selected found"); //$NON-NLS-1$
							} else {
								if (isAdvancedOutputFlag())
									System.out.print("disabled found"); //$NON-NLS-1$
							}
						}
					}
					//Didn't found it this time
				}
			}
		}
		this.repaint();
				/*for (int i = 1, r = 1, s = getMonthConversion(currentSelectedMonth.ordinal()) - 1; i <= getMonthLenght(
						currentYear, currentSelectedMonth); i++) {
					float t = (float) width / 400;
					while (true)
						if ((t * 35) + 50 > height)
							t = (float) (t - 0.25);
						else
							break;
					float w = 1;
					if (t > 2)
						w = (float) (t * 0.5);
					int u = (int) ((s * (35 * t)) + (width - (245 * t)) / 2);
					int v = (int) ((r * (35 * t) + 30) - (35 * t * 0.5));
					if (x >= (u + (int) (5 * w)) && x <= ((u + (int) (5 * w)) + (int) (25 * t))) {
						if (y >= v + (int) (5 * w) && y <= (v + (int) (5 * w) + (int) (25 * t))) {
							if (isAdvancedOutputFlag())
								Console.log(LogType.Information, this, "Pressed on day element: (" + i + ") "); //$NON-NLS-1$ //$NON-NLS-2$
							setChanged(true);
							if (cachedData[currentSelectedMonth.ordinal() + 1][i] == Status.allowed.ordinal()) {
								if (getMaxNumDay() - selectedDays > 0) {
									selectedDays++;
									cachedData[currentSelectedMonth.ordinal() + 1][i] = Status.selected.ordinal();
								} else {
									// No days left
									JOptionPane.showMessageDialog(this.getParent(),
											Strings.getString("Calendar.NoDaysLeft"),
											Strings.getString("Calendar.NoDaysLeftHeader"),
											JOptionPane.INFORMATION_MESSAGE);
								}
								if (isAdvancedOutputFlag())
									System.out.print("allowed found"); //$NON-NLS-1$
							} else if (cachedData[currentSelectedMonth.ordinal() + 1][i] == Status.selected.ordinal()) {
								selectedDays--;
								cachedData[currentSelectedMonth.ordinal() + 1][i] = Status.allowed.ordinal();
								if (isAdvancedOutputFlag())
									System.out.print("selected found"); //$NON-NLS-1$
							} else {
								if (isAdvancedOutputFlag())
									System.out.print("disabled found"); //$NON-NLS-1$
							}
						}
					}
					s++;
					if (s == 7) {
						r++;
						s = 0;
					}
				}*/
		
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	public int getMaxNumDay() {
		return maxNumDay;
	}

	public void setMaxNumDay(int maxNumDay) {
		this.maxNumDay = maxNumDay;
	}

	@Override
	public String toString() {
		return "CalendarControl"; //$NON-NLS-1$
	}

	public DayTable buildFromCache() {
		DayTable dt = new DayTable();
		for (int i = 1; i <= 12; i++) {
			try {
				int ml = getMonthLenght(currentYear, Month.valueOf(i - 1));
				for (int d = 1; d <= ml; d++) {
					ParaDate pd = new ParaDate();
					Time t = new Time();
					pd.setYear(currentYear);
					pd.setMonth((short) i);
					pd.setDay((short) d);
					pd.setTime(t);
					t.setHours((short) 0);
					t.setInitedFlag();
					t.setMillis(0);
					t.setMinutes((short) 0);
					t.setNanos(0);
					t.setSeconds((short) 0);
					dt.getDays().put(pd, Status.valueOf(cachedData[i][d]));
					if (isAdvancedOutputFlag())
						System.out.println("DTC: " + Integer.toString(d) + "." + Integer.toString(i) + "." //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								+ Integer.toString(currentYear) + " -> " + Status.valueOf(cachedData[i][d]).toString()); //$NON-NLS-1$
				}
			} catch (Exception e) {
				Console.log(LogType.Error, this, "An unexpected exception occured: " + e.getMessage()); //$NON-NLS-1$
				e.printStackTrace();
			}
		}
		return dt;
	}

	public boolean isAdvancedOutputFlag() {
		return advancedOutputFlag;
	}

	public void setAdvancedOutputFlag(boolean advancedOutputFlag) {
		this.advancedOutputFlag = advancedOutputFlag;
	}

	public String getInfoMessage() {
		return infoMessage;
	}

	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
		this.repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (isAdvancedOutputFlag())
			Console.log(LogType.Information, this, "Key released: " + e.getKeyCode()); //$NON-NLS-1$
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (currentSelectedMonth != Month.JANUARY) {
				try {
					currentSelectedMonth = Month.valueOf(currentSelectedMonth.ordinal() - 1);
					if (isAdvancedOutputFlag())
						Console.log(LogType.Information, this, "Pressed on rew key"); //$NON-NLS-1$
				} catch (DateException e1) {
					Console.log(LogType.Error, this, "An unexpected exception occured: " + e1.getMessage()); //$NON-NLS-1$
					e1.printStackTrace();
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (currentSelectedMonth != Month.DECEMBER) {
				try {
					currentSelectedMonth = Month.valueOf(currentSelectedMonth.ordinal() + 1);
					if (isAdvancedOutputFlag())
						Console.log(LogType.Information, this, "Pressed on next key"); //$NON-NLS-1$
				} catch (DateException e1) {
					Console.log(LogType.Error, this, "An unexpected exception occured: " + e1.getMessage()); //$NON-NLS-1$
					e1.printStackTrace();
				}
			}
		}
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
		this.repaint();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		this.repaint();
	}

	/**
	 * @return the originalContent
	 */
	public DayTable getOriginalContent() {
		return originalContent;
	}

	/**
	 * @param originalContent
	 *            the originalContent to set
	 */
	private void setOriginalContent(DayTable originalContent) {
		this.originalContent = originalContent;
	}

	public void clearAll() {
		// Clear all selections
		try {
			for (int m = 1; m <= 12; m++) {
				int ml = getMonthLenght(currentYear, Month.valueOf(m - 1));
				for (int d = 1; d <= ml; d++) {
					Status s = Status.valueOf(cachedData[m][d]);
					switch (s) {
					case selected:
						cachedData[m][d] = Status.allowed.ordinal();
						break;
					case allowed:
					case normal:
					case undefined:
					default:
						break;

					}
				}
			}
		} catch (DateException e) {
			e.printStackTrace();
		} catch (Exception e) {

		}
	}

	/**
	 * This method markes all dates between and including start and end
	 * 
	 * @param start
	 *            The first date to mark
	 * @param end
	 *            The last date to mark
	 * @throws SelectionNotAllowedException
	 *             if there are days in between that are not selectable
	 */
	public void selectRange(ParaDate start, ParaDate end, Status wanted) throws SelectionNotAllowedException {
		try {
			long uStart, uEnd, time;
			uStart = getUnixTime(start);
			uEnd = getUnixTime(end);
			SelectionNotAllowedException e = null;
			for (int m = 1; m <= 12; m++) {
				int ml = getMonthLenght(currentYear, Month.valueOf(m - 1));
				for (int d = 1; d <= ml; d++) {
					// TODO finish
					// Check if day is selectable otherwise switch bool to throw
					// exception at the end
					Status currentState;
					try {
						currentState = Status.valueOf(cachedData[m][d]);
					} catch (Exception e1) {
						Console.log(LogType.Error, this, "Value out of date range");
						e1.printStackTrace();
						currentState = Status.undefined;
					}
					switch (wanted) {
					case allowed:
					case selected:
						switch (currentState) {
						case allowed:
							if (wanted.ordinal() == Status.selected.ordinal())
								if (getMaxNumDay() - selectedDays > 0)
									selectedDays++;
								else
									break;
							else
								selectedDays--;
						case selected:
							cachedData[m][d] = wanted.ordinal();
							break;
						case normal:
						case undefined:
						default:
							if (e == null)
								e = new SelectionNotAllowedException();
							ParaDate p = new ParaDate();
							p.setDay((short) d);
							p.setMonth((short) m);
							p.setYear(currentYear);
							e.addForbiddenDate(p);
						}
						break;
					case normal:
					case undefined:
					default:
						throw new SelectionNotAllowedException("Unallowed requested status:" + wanted.toString());
					}
				}
			}
		} catch (DateException e) {
			Console.log(LogType.Error, this, "Failed to iterate through calendar");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private long getUnixTime(ParaDate p) {
		Date d = new Date();
		d.setYear(p.getYear());
		d.setMonth(p.getMonth() - 1);
		d.setDate(p.getDay());
		d.setHours(0);
		d.setMinutes(0);
		d.setSeconds(0);
		return d.getTime() / 1000;
	}

	/**
	 * @return the renderYearInFooter
	 */
	public boolean isRenderYearInFooterEnabled() {
		return renderYearInFooter;
	}

	/**
	 * @param renderYearInFooter
	 *            the renderYearInFooter to set
	 */
	public void setRenderYearInFooterHint(boolean renderYearInFooter) {
		this.renderYearInFooter = renderYearInFooter;
		this.repaint();
	}

	public int getActiveYear() {
		return currentYear;
	}

}
