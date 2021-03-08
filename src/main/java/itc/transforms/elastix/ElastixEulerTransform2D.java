/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2021 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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

/**
 * For 2D transform, the transformation is extended to 3D space assuming:
 * - the rotation occurs along the Z axis,
 * - and Z coordinates are set to 0 by default
 */

public class ElastixEulerTransform2D extends ElastixEulerTransform {

    public double[] getRotationAnglesInRadians()
    {
        final double[] rotation = {0,0,TransformParameters[ 0 ]}; // Rotation around Z only - returns 0 in both X and Y axis
        return rotation;
    }

    public double[] getTranslationInMillimeters()
    {
        final double[] translation = { TransformParameters[ 1 ], TransformParameters[ 2 ], 0 }; // 0 in Z coordinate
        return translation;
    }

    public double[] getRotationCenterInMillimeters()
    {
        final double[] rotationCenter = { CenterOfRotationPoint[ 0 ], CenterOfRotationPoint[ 1 ], 0 }; // 0 in Z coordinate
        return rotationCenter;
    }
}
