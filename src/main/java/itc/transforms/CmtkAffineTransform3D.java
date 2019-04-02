package itc.transforms;

public class CmtkAffineTransform3D {

	public final String referenceStudy;

	public final String floatingStudy;

	public final double[] translationXYZ;

	/**
	 * Rotation in degrees
	 */
	public final double[] rotate;

	public final double[] scale;

	public final double[] shear;

	public final double[] center;

	public final boolean logScaleFactors = false;

	public CmtkAffineTransform3D(
			String reference_study,
			String floating_study,
			double[] xlate,
			double[] rotate,
			double[] scale,
			double[] shear,
			double[] center) {

		// TODO needs better doc for each field
		// TODO consider making getters that create defensive copies of the arrays

		this.referenceStudy = reference_study;
		this.floatingStudy = floating_study;
		this.translationXYZ = xlate;
		this.rotate = rotate;
		this.scale = scale;
		this.shear = shear;
		this.center = center;
	}

	
}
