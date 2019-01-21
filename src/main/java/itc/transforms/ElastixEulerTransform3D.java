package itc.transforms;

import itc.transforms.elastix.ElastixTransform;

public class ElastixEulerTransform3D extends ElastixTransform
{
	/**
	 * Euler angles in rad, in order X, Y, Z
	 */
	private double[] rotationAnglesInRadians;


	/**
	 * translationInMillimeters in millimeters
	 */
	private double[] translationInMillimeters;


	/**
	 * rotation center in millimeters
	 */
	private double[] rotationCenterInMillimeters;


	public ElastixEulerTransform3D(
			double[] rotationAnglesInRadians,
			double[] translationInMillimeters,
			double[] rotationCenterInMillimeters )
	{
		this.rotationAnglesInRadians = rotationAnglesInRadians;
		this.translationInMillimeters = translationInMillimeters;
		this.rotationCenterInMillimeters = rotationCenterInMillimeters;
	}


	public ElastixEulerTransform3D( String transform, String rotationCenter )
	{

	}

	public double[] getRotationAnglesInRadians()
	{
		return rotationAnglesInRadians;
	}

	public double[] getTranslationInMillimeters()
	{
		return translationInMillimeters;
	}

	public double[] getRotationCenterInMillimeters()
	{
		return rotationCenterInMillimeters;
	}
}
