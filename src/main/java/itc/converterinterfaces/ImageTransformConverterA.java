package itc.converterinterfaces;

public interface ImageTransformConverterA< I, O >
{
	O convert( I input );

	Class inputType();

	Class outputType();
}
