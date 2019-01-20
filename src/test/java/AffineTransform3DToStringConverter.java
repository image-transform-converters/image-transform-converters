import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.convert.AbstractConverter;

public class AffineTransform3DToStringConverter extends AbstractConverter< AffineTransform3D, String >
{

	@Override
	public < T > T convert( Object o, Class< T > aClass )
	{
		return null;
	}

	@Override
	public Class< String > getOutputType()
	{
		return null;
	}

	@Override
	public Class< AffineTransform3D > getInputType()
	{
		return null;
	}
}
