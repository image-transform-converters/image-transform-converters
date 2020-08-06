/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2020 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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
package itc.converters;

import java.util.Arrays;
import java.util.stream.Collectors;

import itc.converterinterfaces.ImageTransformConverterA;
import itc.transforms.AffineAsFlatString;
import net.imglib2.realtransform.AffineTransform3D;

// TODO there should be a AffineFlatString class 
public class AffineTransform3DToFlatString
		implements ImageTransformConverterA< AffineTransform3D, AffineAsFlatString > {
	
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
