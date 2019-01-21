package itc.converters;

import itc.converterinterfaces.ImageTransformConverterA;
import itc.transforms.CmtkAffineTransform3D;
import net.imglib2.realtransform.AffineTransform3D;

public class AffineTransform3DToCmtkAffineConverter
		implements ImageTransformConverterA< AffineTransform3D, CmtkAffineTransform3D> {
	@Override
	public CmtkAffineTransform3D convert( AffineTransform3D input) {

		return null;
	}

	@Override
	public Class<AffineTransform3D> inputType() {
		return AffineTransform3D.class;
	}

	@Override
	public Class<CmtkAffineTransform3D> outputType() {
		return CmtkAffineTransform3D.class;
	}

}
