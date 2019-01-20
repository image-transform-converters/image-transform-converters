package itc.converterinterfaces;

public interface ImageTransformConverterB< I, O >
{
	void convert( I input, O output );

	Class inputType();

	Class outputType();
}
