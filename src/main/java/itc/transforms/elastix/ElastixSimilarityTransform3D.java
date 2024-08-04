/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2024 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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
package itc.transforms.elastix;

import static java.lang.Math.sqrt;
import static net.imglib2.util.LinAlgHelpers.quaternionToR;

public class ElastixSimilarityTransform3D extends ElastixSimilarityTransform {

    private double getNorm( double[] vector )
    {
        double norm = 0;
        for ( int i=0; i<vector.length; i++ ) {
            norm += vector[i]*vector[i];
        }

        if (norm > 0)
        {
            norm = sqrt(norm);
        }

        return norm;
    }

    public double[][] getRotationMatrix()
    {
        // transform parameters only gives the first 3 parts (x, y, z) of the unit quaternion - we must calculate
        // the scalar part (w) from this
        // based on elastix / itk source code:
        // https://github.com/SuperElastix/elastix/blob/c3a254562d6801f1a34a2de0ec324c10632b5884/Common/Transforms/itkAdvancedSimilarity3DTransform.hxx#L126

        double[] axis = new double[3];
        axis[0] = TransformParameters[0];
        axis[1] = TransformParameters[1];
        axis[2] = TransformParameters[2];
        double norm = getNorm( axis );

        double epsilon = 1e-10;
        if (norm >= 1.0 - epsilon)
        {
            for ( int i=0; i<axis.length; i++ ) {
                axis[i] = axis[i] / (norm + epsilon * norm);
            }
        }

        // https://github.com/InsightSoftwareConsortium/ITK/blob/master/Modules/Core/Common/include/itkVersor.hxx#L444
        // https://itk.org/Doxygen/html/classitk_1_1Versor.html#a72a0cf968370f95dac5bac84a01f93a0

        double sinangle2 = getNorm( axis );
        double cosangle2 = sqrt( 1 - sinangle2 * sinangle2);

        double[] unitQuaternion = new double[]{ cosangle2, axis[0], axis[1], axis[2] };

        final double[][] matrix = new double[3][3];
        quaternionToR( unitQuaternion, matrix );

        return matrix;
    }

    public double getScalingFactor()
    {
        final double scalingFactor = TransformParameters[6];
        return scalingFactor;
    }

    public double[] getTranslationInMillimeters()
    {
        final double[] translation = { TransformParameters[ 3 ], TransformParameters[ 4 ], TransformParameters[ 5 ]};
        return translation;
    }

    public double[] getRotationCenterInMillimeters()
    {
        final double[] rotationCenter = { CenterOfRotationPoint[ 0 ], CenterOfRotationPoint[ 1 ], CenterOfRotationPoint[ 2 ]};
        return rotationCenter;
    }
}
