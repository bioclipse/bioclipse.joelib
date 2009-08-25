package joelib2.feature.fields.potentials;

public class Coulomb implements ElectrostaticPotential {

	double eps_r;
	public Coulomb(double dielectric)
	{
		eps_r = dielectric;
	}

	public double getElectrostaticPotential(double charge1, double charge2,	float distance) {
		double d = charge1*charge2;
		//eps_r = 1.0/distance;
		double t = 4.0*Math.PI*eps_r*(double)(distance);
		return d/t;
	}

}
