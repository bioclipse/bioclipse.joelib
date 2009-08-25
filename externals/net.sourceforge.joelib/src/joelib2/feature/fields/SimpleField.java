package joelib2.feature.fields;

import javax.vecmath.Point3f;

public class SimpleField 
{
	Point3f[][][] grid;
	float[][][] vals;
	double minPot;
	double maxPot;
	
	public SimpleField(Point3f[][][] grid, float[][][] vals, double min, double max)
	{
		this.grid = grid;
		this.vals = vals;
		this.minPot = min;
		this.maxPot = max;
	}	
}
