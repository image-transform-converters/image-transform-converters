package itc.converters;

import itc.converterinterfaces.ImageTransformConverterA;
import itc.transforms.AffineAsFlatString;
import net.imglib2.realtransform.AffineTransform3D;

// TODO there should be a AffineFlatString class 
public class FlatStringToAffineTransform3D implements ImageTransformConverterA< AffineAsFlatString, AffineTransform3D > {

	@Override
	public AffineTransform3D convert(AffineAsFlatString input) {
		AffineTransform3D affine = new AffineTransform3D();
		affine.set( input.paramsToDoubleArray() );
		return affine;
	}

	@Override
	public Class<AffineAsFlatString> inputType() {
		return AffineAsFlatString.class;
	}

	@Override
	public Class<AffineTransform3D> outputType() {
		return AffineTransform3D.class;
	}

}
