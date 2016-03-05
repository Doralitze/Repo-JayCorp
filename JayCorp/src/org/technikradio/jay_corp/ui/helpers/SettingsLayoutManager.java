/*
JayCorp-Client/SettingsLayoutManager.java
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
package org.technikradio.jay_corp.ui.helpers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;

public class SettingsLayoutManager implements LayoutManager2 {

	public static final String POSITION_CONTAINER = "CONTAINER"; //$NON-NLS-1$
	public static final String POSITION_FOOTER = "FOOTER"; //$NON-NLS-1$

	private ArrayList<Component> mainComponents;
	private ArrayList<Component> footerComponents;
	@SuppressWarnings("unused")
	// Support will be added later
	private boolean sizeUnknown = true;

	public SettingsLayoutManager() {
		super();
		mainComponents = new ArrayList<Component>();
		footerComponents = new ArrayList<Component>();
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		if (name == SettingsLayoutManager.POSITION_FOOTER) {
			footerComponents.add(comp);
		} else
			mainComponents.add(comp);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		mainComponents.remove(comp);
		footerComponents.remove(comp);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		sizeUnknown = false;
		boolean g = false;
		Dimension d = null;
		int w = 0, h = 0;
		for (Component c : mainComponents) {
			if (c.getPreferredSize().width > w) {
				w = c.getPreferredSize().width;
			}
			h += c.getPreferredSize().height;
		}
		for (Component c : footerComponents) {
			if (c.getPreferredSize().width > w) {
				w = c.getPreferredSize().width;
			}
			h += c.getPreferredSize().height;
		}
		d = new Dimension(w, h);
		if (d.getWidth() < parent.getWidth()
				&& d.getHeight() < parent.getHeight()) {
			g = true;
		}
		if (g)
			return parent.getSize();
		else
			return d;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		int w = 0, h = 0;
		sizeUnknown = false;
		for (Component c : mainComponents) {
			if (c.getWidth() > w) {
				w = c.getWidth();
			}
			h += c.getHeight();
		}
		for (Component c : footerComponents) {
			if (c.getWidth() > w) {
				w = c.getWidth();
			}
			h += c.getHeight();
		}
		return new Dimension(w, h);
	}

	@Override
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();
		Dimension dimension = parent.getSize();
		if (dimension.width < minimumLayoutSize(parent).getWidth()) {
			parent.setSize((int) minimumLayoutSize(parent).getWidth(),
					parent.getHeight());
		}
		if (dimension.height < minimumLayoutSize(parent).getHeight()) {
			parent.setSize(parent.getWidth(), (int) minimumLayoutSize(parent)
					.getHeight());
		}
		int x = insets.right, y = insets.top;
		for (Component c : mainComponents) {
			c.setLocation(x, y);
			y += c.getHeight() + 5;
		}
		y += dimension.getWidth();
		for (Component c : footerComponents) {
			y -= c.getWidth() + 5;
			c.setLocation(x, y);
		}
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {

	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return preferredLayoutSize(target);
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0.5f;
	}

	@Override
	public void invalidateLayout(Container target) {

	}

	@Override
	public String toString() {
		return "SettingsLayoutManager"; //$NON-NLS-1$
	}

}
