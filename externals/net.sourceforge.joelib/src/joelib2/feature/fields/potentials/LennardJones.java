package joelib2.feature.fields.potentials;

public class LennardJones implements StericPotential 
{
	double exp1;
	double exp2;
	double A;
	double B;
	public LennardJones(double A, double B, double exp1, double exp2)
	{
		this.exp1 = exp1;
		this.exp2 = exp2;
		this.A = A;
		this.B = B;
	}

	public double getStericPotential(double distance) 
	{
		double r1 = Math.pow(distance, exp1);
		double r2 = Math.pow(distance, exp2);
		return -((A/r1) - (B/r2));
	}

}
