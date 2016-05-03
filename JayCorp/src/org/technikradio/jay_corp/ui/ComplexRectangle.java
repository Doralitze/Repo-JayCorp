package org.technikradio.jay_corp.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

public class ComplexRectangle extends Rectangle {

	private static final long serialVersionUID = -503814612310787162L;
	private final Vector<Integer> values;
	
	
	public ComplexRectangle() {
		super();
		values = new Vector<Integer>();
	}
	public ComplexRectangle(Dimension d) {
		super(d);
		values = new Vector<Integer>();
	}
	public ComplexRectangle(int x, int y, int width, int height) {
		super(x, y, width, height);
		values = new Vector<Integer>();
	}
	public ComplexRectangle(int width, int height) {
		super(width, height);
		values = new Vector<Integer>();
	}
	public ComplexRectangle(Point p, Dimension d) {
		super(p, d);
		values = new Vector<Integer>();
	}
	public ComplexRectangle(Point p) {
		super(p);
		values = new Vector<Integer>();
	}
	public ComplexRectangle(Rectangle r) {
		super(r);
		values = new Vector<Integer>();
	}
	
	public int getAt(int position){
		if(position > values.size())
			return 0;
		return  values.get(position).intValue();
	}
	
	public boolean resize(int size){
		if(size < values.size())
			return false;
		values.setSize(size);
		return true;
	}
	
	public int setAt(int position, int value){
		if(position > values.size())
			values.setSize(position);
		Integer i = values.set(position, new Integer(value));
		if(i != null)
			return i.intValue();
		else
			return 0;
	}
	
}
