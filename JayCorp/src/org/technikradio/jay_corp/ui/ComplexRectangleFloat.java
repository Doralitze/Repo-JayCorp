package org.technikradio.jay_corp.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import java.lang.Float;

public class ComplexRectangleFloat extends Rectangle {

	private static final long serialVersionUID = -503814612310787162L;
	private final Vector<java.lang.Float> values;
	
	
	public ComplexRectangleFloat() {
		super();
		values = new Vector<java.lang.Float>();
	}
	public ComplexRectangleFloat(Dimension d) {
		super(d);
		values = new Vector<java.lang.Float>();
	}
	public ComplexRectangleFloat(int x, int y, int width, int height) {
		super(x, y, width, height);
		values = new Vector<java.lang.Float>();
	}
	public ComplexRectangleFloat(int width, int height) {
		super(width, height);
		values = new Vector<java.lang.Float>();
	}
	public ComplexRectangleFloat(Point p, Dimension d) {
		super(p, d);
		values = new Vector<java.lang.Float>();
	}
	public ComplexRectangleFloat(Point p) {
		super(p);
		values = new Vector<java.lang.Float>();
	}
	public ComplexRectangleFloat(Rectangle r) {
		super(r);
		values = new Vector<java.lang.Float>();
	}
	
	public float getAt(int position){
		if(position > values.size())
			return 0;
		return  values.get(position).floatValue();
	}
	
	public boolean resize(int size){
		if(size < values.size())
			return false;
		values.setSize(size);
		return true;
	}
	
	public float setAt(int position, float value){
		if(position > values.size())
			values.setSize(position);
		java.lang.Float i = values.set(position, new java.lang.Float(value));
		if(i != null)
			return i.floatValue();
		else
			return 0;
	}
	
}
