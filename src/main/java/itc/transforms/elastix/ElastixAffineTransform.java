package itc.transforms.elastix;

import org.scijava.plugin.Parameter;

public class ElastixAffineTransform extends ElastixTransform {
    // AdvancedAffineTransform specific
    @Parameter
    public Double[] CenterOfRotationPoint;
}
