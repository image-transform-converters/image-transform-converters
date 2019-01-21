package itc.transforms;

import java.util.Arrays;

/**
 * Stores the parameters of an affine transform as a single-line string (row-major).
 *
 */
public class AffineAsFlatString {

	final String affineParametersDelimited;
	final String delimiterRegexp;

	public AffineAsFlatString( final String affineParametersDelimited, final String delimiterRegexp )
	{
		this.affineParametersDelimited = affineParametersDelimited;
		this.delimiterRegexp = delimiterRegexp;
	}
	
	public String getString()
	{
		return affineParametersDelimited;
	}

	public String getDelimiterRegexp()
	{
		return delimiterRegexp;
	}

	public boolean equals( final AffineAsFlatString other )
	{
		return Arrays.equals( this.paramsToDoubleArray(), other.paramsToDoubleArray() );
	}
	
	public final double[] paramsToDoubleArray()
	{
		return Arrays.stream( affineParametersDelimited.split( delimiterRegexp )).mapToDouble( Double::parseDouble ).toArray();
	}
}
