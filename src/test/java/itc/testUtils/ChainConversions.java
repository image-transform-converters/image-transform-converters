/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2022 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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
package itc.testUtils;
import java.util.function.BiFunction;

import itc.converterinterfaces.ImageTransformConverterA;

public class ChainConversions<F, R> {

	private ImageTransformConverterA<R, F> reverseConverter;
	private ImageTransformConverterA<F, R> forwardConverter;

	private BiFunction<F, F, Boolean> forwardEquality;
	private BiFunction<R, R, Boolean> reverseEquality;

	public ChainConversions(final ImageTransformConverterA<F, R> forwardConverter,
			final ImageTransformConverterA<R, F> reverseConverter) {
		this.forwardConverter = forwardConverter;
		this.reverseConverter = reverseConverter;

		forwardEquality = new BiFunction<F, F, Boolean>() {
			@Override
			public Boolean apply(F t, F u) {
				return t.equals(u);
			}
		};

		reverseEquality = new BiFunction<R, R, Boolean>() {
			@Override
			public Boolean apply(R t, R u) {
				return t.equals(u);
			}
		};
	}

	public void setEqualityForward(BiFunction<F, F, Boolean> forwardEquality) {
		this.forwardEquality = forwardEquality;
	}

	public void setEqualityReverse(BiFunction<R, R, Boolean> reverseEquality) {
		this.reverseEquality = reverseEquality;
	}

	public ChainConversionResult test(R r) {
		F r2f = reverseConverter.convert(r);
		R r2f2r = forwardConverter.convert(r2f);
		F r2f2r2f = reverseConverter.convert(r2f2r);

		return new ChainConversionResult(reverseEquality.apply(r, r2f2r), forwardEquality.apply(r2f, r2f2r2f));
	}

	public static class ChainConversionResult {
		public final boolean firstCircleSuccess;
		public final boolean secondCircleSuccess;

		public ChainConversionResult(boolean firstCircleSuccess, boolean secondCircleSuccess) {
			this.firstCircleSuccess = firstCircleSuccess;
			this.secondCircleSuccess = secondCircleSuccess;
		}

		public boolean success() {
			return firstCircleSuccess && secondCircleSuccess;
		}

	}

	public static void main(String[] args) {

		// Object a;
		//
		// modify( a )
		// convert( a, b );
		// b = convert( a );
		// convert( b, c );

	}
}
