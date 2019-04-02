package itc.scijavaconverters;

import itc.transforms.elastix.ElastixAffineTransform;
import itc.transforms.elastix.ElastixTransform;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.RealTransform;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

@Plugin(type = Converter.class)
public class ElastixAffineTransformToRealTransformConverter<I extends ElastixTransform, O extends RealTransform> extends AbstractConverter<I, O> {

    /**
     * From elastix-4.9.0 manual:
     * Affine: (AffineTransform) An affine transformation is defined as:
     * Tμ(x) = A(x − c) + t + c, (2.14), where the matrix A has no restrictions.
     * This means that the image can be translated, rotated, scaled,
     * and sheared.
     * The parameter vector μ is formed by the matrix elements aij and the
     * translation vector.
     * In 2D, this gives a vector of length 6:
     * μ = (a11,a12,a21,a22,tx,ty)T.
     * In 3D, this gives a vector of length 12.
     *
     * Note: Elastix transformations are always in millimeter units.
     *
     * @param o
     * @param aClass
     * @return Affine Transform
     */
    @Override
    public <T> T convert(Object o, Class<T> aClass) {
        ElastixAffineTransform eat = (ElastixAffineTransform) o;
        // Only 2d -> 2d transform and 3d -> 3d transforms are supported
        assert eat.FixedImageDimension==eat.MovingImageDimension;
        assert (eat.FixedImageDimension==2)||(eat.FixedImageDimension==3);

        switch ( eat.FixedImageDimension )
        {
            case 2:
                final AffineTransform2D affine2D = new AffineTransform2D();
                Double[] m2D = eat.TransformParameters;
                affine2D.set(new double[][] {{m2D[0], m2D[1], m2D[2]},
                                             {m2D[3], m2D[4], m2D[5]},
                                             {0.,     0.,     1.    } });
                return (T) affine2D;
            case 3:
                final AffineTransform3D affine3D = new AffineTransform3D();
                Double[] m3D = eat.TransformParameters;
                affine3D.set(new double[][] {{m3D[0], m3D[1], m3D[ 2], m3D[ 3]},
                                             {m3D[4], m3D[5], m3D[ 6], m3D[ 7]},
                                             {m3D[8], m3D[9], m3D[10], m3D[11]},
                                             {0.,     0.,     0.,      1.     } });
                return (T) affine3D;
            default:
                System.err.println("ElastixAffineTransform: Unsupported FixedImageDimension");
            return null;
        }
    }

    @Override
    public Class<O> getOutputType() {
        return (Class<O>) RealTransform.class;
    }

    @Override
    public Class<I> getInputType() {
        return (Class<I>) ElastixAffineTransform.class;
    }


}
