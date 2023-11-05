/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2023 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
