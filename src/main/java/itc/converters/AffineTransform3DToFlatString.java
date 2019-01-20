package itc.converters;

import java.util.Arrays;
import java.util.stream.Collectors;

import itc.converterinterfaces.ImageTransformConverterA;
import itc.transforms.AffineAsFlatString;
import net.imglib2.realtransform.AffineTransform3D;

// TODO there should be a AffineFlatString class 
public class AffineTransform3DToFlatString implements ImageTransformConverterA< AffineTransform3D, AffineAsFlatString > {
	
	private String delimiter = " ";

	public AffineTransform3DToFlatString(){}

	public AffineTransform3DToFlatString( String delimiter )
	{
		this.delimiter = delimiter;
	}

	@Override
	public AffineAsFlatString convert(AffineTransform3D input) {
		 return new AffineAsFlatString( 
					 Arrays.stream( input.getRowPackedCopy() )
						.mapToObj( Double::toString )
						.collect(Collectors.joining(delimiter)),
					delimiter);
	}

	@Override
	public Class<AffineTransform3D> inputType() {
		return AffineTransform3D.class;
	}

	@Override
	public Class<AffineAsFlatString> outputType() {
		return AffineAsFlatString.class;
	}

}
