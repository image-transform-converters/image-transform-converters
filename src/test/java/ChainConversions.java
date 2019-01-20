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
