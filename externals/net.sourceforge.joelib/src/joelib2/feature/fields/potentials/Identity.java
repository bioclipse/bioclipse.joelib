package joelib2.feature.fields.potentials;

public class Identity implements StericPotential {

	public double getStericPotential(double distance)
	{
//		double d = 0.0;
//		if(distance < 1.0) d = 1.0;
//		return d;
		
		double d = distance*distance/2.0;
		return Math.exp(-d);
		
	}

}
