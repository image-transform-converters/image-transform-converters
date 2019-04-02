package itc.transforms.elastix;

import org.scijava.plugin.Parameter;

public class ElastixEulerTransform extends ElastixTransform {
    // EulerTransform specific
    @Parameter
    public Double[] CenterOfRotationPoint;
}
