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
package itc.transforms;
import org.junit.Test;

import itc.converters.AffineTransform3DToFlatString;
import itc.converters.FlatStringToAffineTransform3D;
import itc.testUtils.ChainConversions;
import itc.transforms.AffineAsFlatString;
import net.imglib2.realtransform.AffineTransform3D;

import static org.junit.Assert.*;

import java.util.Arrays;

public class TestAffineTransform3DToString {

	@Test
	public void test()
	{
		AffineTransform3DToFlatString toStringConverter = new AffineTransform3DToFlatString();
		FlatStringToAffineTransform3D fromStringConverter = new FlatStringToAffineTransform3D();

		ChainConversions< AffineTransform3D, AffineAsFlatString > chainTester = new ChainConversions<>( 
				toStringConverter, fromStringConverter );

		chainTester.setEqualityForward( 
				 (a,b) -> { return Arrays.equals(a.getRowPackedCopy(), b.getRowPackedCopy()); });

		AffineAsFlatString affineAsString = new AffineAsFlatString( 
				"1 0.1 0.1 100 0.2 0.2 2.0 -100 0.3 0.3 4.0 200",
				" " );

		ChainConversions.ChainConversionResult result = chainTester.test( affineAsString );
	
		System.out.println( new AffineTransform3D().equals( new AffineTransform3D()));
	
		//assertTrue("first circle", result.firstCircleSuccess);
		assertTrue("second circle", result.secondCircleSuccess);
	}
}
