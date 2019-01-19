package itc.transforms;

import net.imglib2.realtransform.RealTransform;

/**
 * A transformation
 * 
 */
public class WrappedCalibratedTransform {

	private String units;
	private RealTransform transform;

	public WrappedCalibratedTransform( RealTransform transform, String units ) {
		this.transform = transform;
		this.units = units;
	}
}
