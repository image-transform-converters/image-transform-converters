package itc.converters;

import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.realtransform.AffineTransform3D;

/**
 * For convenience - the third dimension is unchanged
 */
public class AffineTransform2DToAffineTransform3D {

    public static AffineTransform3D convert(AffineTransform2D transform2D)
    {
        double[] m2D = transform2D.getRowPackedCopy();

        double[] m3D = new double[] {
                m2D[0],m2D[1],0,m2D[2],
                m2D[3],m2D[4],0,m2D[5],
                0,     0,     1,0,
                0,     0,     0,1
        };

        AffineTransform3D transform3D = new AffineTransform3D();

        transform3D.set(m3D);

        return transform3D;
    }
}
