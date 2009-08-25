package joelib2.feature.fields;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;


import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PointArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import joelib2.gui.render3D.math.geometry.Point3D;

public class SimpleFieldViewer extends Frame
{
	public static void main(String[] args)
	{
		//new SimpleFieldViewer();
	}

	Point3f[][][] grid;
	float[][][] vals;
	
	public SimpleFieldViewer(Point3f[][][] grid, float[][][] vals)
	{
		this.grid = grid;
		this.vals = vals;
		setTitle("PointArrayDemo");

		setLayout(new BorderLayout());
		GraphicsConfigTemplate3D tmpl = new GraphicsConfigTemplate3D();
        GraphicsEnvironment env = GraphicsEnvironment
            .getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getBestConfiguration(tmpl);
		Canvas3D c = new Canvas3D(config);
		add("Center", c);

//		UniverseBuilder u = new UniverseBuilder(c);
		BranchGroup scene = createSceneGraph();
//		
//		u.addBranchGraph(scene);
		VirtualUniverse	universe = new VirtualUniverse();
		Locale locale = new Locale(universe);
		locale.addBranchGraph(scene);
		setSize(300,400);
		show();
	}


	public BranchGroup createSceneGraph()
	{
		BranchGroup branchGroup = new BranchGroup();
		TransformGroup transformGroup = new TransformGroup();
		transformGroup.addChild(new PointCloud(grid, vals).getShape());
		branchGroup.addChild(transformGroup);
		return branchGroup;
	}
}

class UniverseBuilder extends Object
{
	Locale			locale;

	UniverseBuilder(Canvas3D c)
	{
		Transform3D t = new Transform3D();
		Transform3D t2 = new Transform3D();
		t2.setEuler( new Vector3d(-35.0*(Math.PI/180.0),45.0*(Math.PI/180.0),0.0));
		t.set(4,new Vector3d(6.0,5.0,6.0));
		t.mul(t,t2);

		VirtualUniverse	universe = new VirtualUniverse();
		locale = new Locale(universe);

		PhysicalBody body = new PhysicalBody();
		PhysicalEnvironment environment = new PhysicalEnvironment();
		BranchGroup viewPlatformBranchGroup = new BranchGroup();
		TransformGroup viewPlatformTransformGroup = new TransformGroup(t);
		ViewPlatform viewPlatform = new ViewPlatform();

		View view = new View();
		view.addCanvas3D(c);
		view.setPhysicalBody(body);
		view.setPhysicalEnvironment(environment);
		view.attachViewPlatform(viewPlatform);

		viewPlatformTransformGroup.addChild(viewPlatform);
		viewPlatformBranchGroup.addChild(viewPlatformTransformGroup);
		locale.addBranchGraph(viewPlatformBranchGroup);
	}

	void addBranchGraph(BranchGroup bg)
	{
		locale.addBranchGraph(bg);
	}
}

class PointCloud extends Object
{
//	static double verts[] = {
//		1.0, 1.0, 1.0, -1.0, 1.0, 1.0,
//		-1.0, -1.0, 1.0, 1.0, -1.0, 1.0,
//
//		1.0, 1.0, -1.0, -1.0, 1.0, -1.0,
//		-1.0, -1.0, -1.0, 1.0, -1.0, -1.0 };
//
//
//	static float[] colors = {
//		1.0f, 0.0f, 0.0f,
//		0.0f, 1.0f, 0.0f,
//		0.0f, 0.0f, 1.0f,
//		1.0f, 1.0f, 0.0f,
//		1.0f, 0.0f, 1.0f,
//		0.0f, 1.0f, 1.0f,
//		1.0f, 0.0f, 0.0f,
//		1.0f, 0.0f, 0.0f,
//	};

	private Shape3D shape;

	public PointArray points;
	
	public PointCloud(Point3f[][][] points, float[][][] vals)
	{

//		PointArray points= new PointArray(8, PointArray.COORDINATES| PointArray.COLOR_3);
//		points.setCoordinates(0,verts);
//		points.setColors(0,colors);
		int x = points.length;
		int y = points[0].length;
		int z = points[0][0].length;
		this.points = new PointArray(x*y*z, PointArray.COORDINATES| PointArray.COLOR_3);
		
		double[] tp = new double[x*y*z*3];
		float[] colors = new float[x*y*z*3];
		int a = 0;
		int b = 0;
		for(int i = 0; i < points.length; i++)
		{
			for(int j = 0; j < points[i].length; j++)
			{
				for(int k = 0; k < points[i][j].length; k++)
				{
					//tp[a] = new Point3D(points[i][j][k].x,points[i][j][k].y,points[i][j][k].z);
					colors[b] = (float)vals[i][j][k];
					if(vals[i][j][k] < 0.01)
					{
						colors[b] = 1.0f;
						colors[b+1] = 0.0f;
						colors[b+2] = 0.0f;
					}else
					{
						if(vals[i][j][k] < 0.05)
						{
							colors[b] = 0.0f;
							colors[b+1] = 1.0f;
							colors[b+2] = 0.0f;
						}else
						{
							colors[b] = 0.0f;
							colors[b+1] = 0.0f;
							colors[b+2] = 1.0f;
						}
					}
					b += 3;
					tp[a] = (points[i][j][k].x);
					a++;
					tp[a] = (points[i][j][k].y);
					a++;
					tp[a] = (points[i][j][k].z);
					a++;
				}
				
			}
			
		}
		this.points.setCoordinates(0, tp);
		this.points.setColors(0, colors);
		shape = new Shape3D(this.points, new Appearance());
	}

	public Shape3D getShape()
	{
		return shape;
	}

}
