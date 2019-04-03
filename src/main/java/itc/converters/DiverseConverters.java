package itc.converters;


import itc.transforms.elastix.ElastixEulerTransform3D;
import itc.utilities.TransformUtils;
import net.imglib2.realtransform.AffineTransform3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public abstract class DiverseConverters
{

	/**
	 * Amira allows the user to interactively specify
	 * an Euler (rotation & translation) transformation.
	 * The user can read the transformation parameters on the screen.
	 * The units in Amira could be anything(?),
	 * but here, currently, micrometer units are required
	 * for the conversion.
	 *
	 * @param amiraRotationAxis
	 * @param amiraRotationAngleInDegrees
	 * @param amiraTranslationVectorInMicrometer
	 * @param targetImageVoxelSizeInMicrometer
	 * @param targetImageCenterInPixels
	 * @return
	 */
	public static AffineTransform3D amiraEulerInMicrometerToAffineTransform3DInPixels(
			double[] amiraRotationAxis,
			double amiraRotationAngleInDegrees,
			double[] amiraTranslationVectorInMicrometer,
			double[] targetImageVoxelSizeInMicrometer,
			double[] targetImageCenterInPixels // this is the center of the rotation
	)
	{

		// rotate
		//
		final Vector3D axis = new Vector3D(
				amiraRotationAxis[ 0 ],
				amiraRotationAxis[ 1 ],
				amiraRotationAxis[ 2 ] );

		double angle = amiraRotationAngleInDegrees / 180.0 * Math.PI;

		final double[][] rotationMatrix = TransformUtils.rotationMatrix( axis, angle );

		final AffineTransform3D transform3D =
				TransformUtils.rotationAroundImageCenterTransform(
						rotationMatrix,
						targetImageCenterInPixels );

		// translate
		//
		double[] translationInPixels = new double[ 3 ];

		for ( int d = 0; d < 3; ++d )
		{
			translationInPixels[ d ] = amiraTranslationVectorInMicrometer[ d ] / targetImageVoxelSizeInMicrometer[ d ];
		}

		transform3D.translate( translationInPixels );

		return transform3D;
	}


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
	 * @param affineTransform3D
	 * @param voxelSizeInMillimeter
	 * @return string as it appears in the Transformation.txt output of elastix
	 */
	public static String affineTransform3DToElastixAffine3DString(
			AffineTransform3D affineTransform3D,
			double voxelSizeInMillimeter )
	{

		String out = "";
		for ( int row = 0; row < 3; ++row )
			for ( int col = 0; col < 3; ++col )
				out += affineTransform3D.get( row, col ) + " ";

		out += voxelSizeInMillimeter * affineTransform3D.get( 0, 3 ) + " ";
		out += voxelSizeInMillimeter * affineTransform3D.get( 1, 3 ) + " ";
		out += voxelSizeInMillimeter * affineTransform3D.get( 2, 3 );

		return out;
	}

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
	 * @param affineTransform3D
	 * @return string as it appears in the *.xml files of the BigDataViewer
	 * SpimData files
	 */
	public static String affineTransform3DToBigDataViewerAffine3DString( AffineTransform3D affineTransform3D )
	{
		String out = "";

		for ( int row = 0; row < 3; ++row )
			for ( int col = 0; col < 4; ++col )
				out += String.format( "%.4f",  affineTransform3D.get( row, col ) ) + " ";

		return out;
	}

	/**
	 * TODO
	 * @return
	 */
	public static AffineTransform3D getElastixSimilarityAsBdvAffine()
	{
		// TODO.
		// (Transform "SimilarityTransform")
		//(NumberOfParameters 7)
		//(TransformParameters -0.008415 0.004752 -0.001727 -0.002337 -0.001490 0.003296 0.987273)
		//(InitialTransformParametersFileName "/Users/tischer/Documents/rachel-mellwig-em-prospr-registration/transformations/prospr-to-segmentation/TransformParameters.ManualPreAlignment-Affine_actuallyOnlyEuler.txt")
		//(HowToCombineTransforms "Compose")
		//
		//// Image specific
		//(FixedImageDimension 3)
		//(MovingImageDimension 3)
		//(FixedInternalImagePixelType "float")
		//(MovingInternalImagePixelType "float")
		//(Size 550 518 570)
		//(Index 0 0 0)
		//(Spacing 0.0005000000 0.0005000000 0.0005000000)
		//(Origin 0.0000000000 0.0000000000 0.0000000000)
		//(Direction 1.0000000000 0.0000000000 0.0000000000 0.0000000000 1.0000000000 0.0000000000 0.0000000000 0.0000000000 1.0000000000)
		//(UseDirectionCosines "false")
		//
		//// SimilarityTransform specific
		//(CenterOfRotationPoint 0.0132582577 0.0387138401 0.1074694348)


		return null;
	}

	/**
	 * From elastix-4.9.0 manual:
	 * Rigid: (EulerTransform) A rigid transformation is defined as:
	 * Tμ(x) = R(x − c) + t + c,
	 * with the matrix R a rotation matrix (i.e. orthonormal and proper),
	 * c the centre of rotation, and t translation again.
	 * The image is treated as a rigid body, which can translate and rotate,
	 * but cannot be scaled/stretched.
	 * The rotation matrix is parameterised by the Euler angles (one in 2D, three in 3D).
	 * The parameter vector μ consists of the Euler angles (in rad)
	 * and the translation vector.
	 * In 2D, this gives a vector of length 3: μ = (θz , tx , ty )T ,
	 * where θz denotes the rotation around the axis normal to the image.
	 * In 3D, this gives a vector of length 6: μ = (θx,θy,θz,tx,ty,tz)T .
	 * The centre of rotation is not part of μ; it is a fixed setting,
	 * usually the centre of the image.
	 *
	 * Note: Elastix units are always millimeters
	 *
	 * @param transform string as printed in elastix output file
	 * @param rotationCentre string as printed in elastix output file
	 * @param imageVoxelSizeInMicrometer
	 * @return
	 */
	public static AffineTransform3D elastixEuler3DStringsAsAffineTransform3DInPixelUnits(
			String transform,
			String rotationCentre,
			double[] imageVoxelSizeInMicrometer )
	{
		String[] split = transform.split( " " );

		final double[] angles = new double[ 3 ];

		for ( int d = 0; d < 3; ++d )
		{
			angles[ d ] = Double.parseDouble( split[ d ] );
		}

		final double[] translationInPixels = new double[ 3 ];

		for ( int d = 0; d < 3; ++d )
		{
			translationInPixels[ d ] = Double.parseDouble( split[ d + 3 ] ) / ( 0.001 * imageVoxelSizeInMicrometer[ d ] );
		}

		split = rotationCentre.split( " " );

		final double[] rotationCentreVectorInPixelsPositive = new double[ 3 ];
		final double[] rotationCentreVectorInPixelsNegative = new double[ 3 ];

		for ( int d = 0; d < 3; ++d )
		{
			rotationCentreVectorInPixelsPositive[ d ] = Double.parseDouble( split[ d  ] ) / ( 0.001 * imageVoxelSizeInMicrometer[ d ] );
			rotationCentreVectorInPixelsNegative[ d ] = - Double.parseDouble( split[ d  ] ) / ( 0.001 * imageVoxelSizeInMicrometer[ d ] );
		}


		final AffineTransform3D transform3D = new AffineTransform3D();

		// rotate around rotation centre
		//
		transform3D.translate( rotationCentreVectorInPixelsNegative ); // + or - ??
		for ( int d = 0; d < 3; ++d)
		{
			transform3D.rotate( d, angles[ d ]);
		}
		final AffineTransform3D translateBackFromRotationCentre = new AffineTransform3D();
		translateBackFromRotationCentre.translate( rotationCentreVectorInPixelsPositive );
		transform3D.preConcatenate( translateBackFromRotationCentre );

		// translate
		//
		transform3D.translate( translationInPixels );

		return transform3D;
	}

	/**
	 * Converts text string as they occur in the elastix output files
	 * into an {@link ElastixEulerTransform3D} object.
	 * @param transform
	 * @param rotationCentre
	 * @return elastix euler transform {@link ElastixEulerTransform3D }
	 */
	public static ElastixEulerTransform3D elastixEuler3DStringsToElastixEulerTransform3D(
			String transform,
			String rotationCentre )
	{
//		String[] split = transform.split( " " );
//
//		final double[] angles = new double[ 3 ];
//
//		for ( int d = 0; d < 3; ++d )
//		{
//			angles[ d ] = Double.parseDouble( split[ d ] );
//		}
//
//		final double[] translation = new double[ 3 ];
//
//		for ( int d = 0; d < 3; ++d )
//		{
//			translation[ d ] = Double.parseDouble( split[ d + 3 ] );
//		}
//
//		split = rotationCentre.split( " " );
//
//		final double[] rotations = new double[ 3 ];
//
//		for ( int d = 0; d < 3; ++d )
//		{
//			rotations[ d ] = Double.parseDouble( split[ d ] );
//		}
//
		return null; //ElastixEulerTransform3D( angles, translation, rotations );
	}


	/**
	 * From elastix-4.9.0 manual:
	 * Rigid: (EulerTransform) A rigid transformation is defined as:
	 * Tμ(x) = R(x − c) + t + c,
	 * with the matrix R a rotation matrix (i.e. orthonormal and proper),
	 * c the centre of rotation, and t translation again.
	 * The image is treated as a rigid body, which can translate and rotate,
	 * but cannot be scaled/stretched.
	 * The rotation matrix is parameterised by the Euler angles (one in 2D, three in 3D).
	 * The parameter vector μ consists of the Euler angles (in rad)
	 * and the translation vector.
	 * In 2D, this gives a vector of length 3: μ = (θz , tx , ty )T ,
	 * where θz denotes the rotation around the axis normal to the image.
	 * In 3D, this gives a vector of length 6: μ = (θx,θy,θz,tx,ty,tz)T .
	 * The centre of rotation is not part of μ; it is a fixed setting,
	 * usually the centre of the image.
	 *
	 * Note: Elastix units are always millimeters
	 *
	 * @param elastixEulerTransform3D
	 * @param imageVoxelSizeInMicrometer
	 * @return
	 */
	public static AffineTransform3D elastixEulerTransform3DToAffineTransform3DInPixelUnits(
			ElastixEulerTransform3D elastixEulerTransform3D,
			double[] imageVoxelSizeInMicrometer )
	{

		final double[] angles = elastixEulerTransform3D.getRotationAnglesInRadians();

		final double[] translationInMillimeters = elastixEulerTransform3D.getTranslationInMillimeters();
		final double[] translationInPixels = new double[ 3 ];
		for ( int d = 0; d < 3; ++d )
		{
			translationInPixels[ d ] = translationInMillimeters[ d ] / ( 0.001 * imageVoxelSizeInMicrometer[ d ] );
		}


		final double[] rotationCenterInMillimeters = elastixEulerTransform3D.getRotationCenterInMillimeters();
		final double[] rotationCentreVectorInPixelsPositive = new double[ 3 ];
		final double[] rotationCentreVectorInPixelsNegative = new double[ 3 ];

		for ( int d = 0; d < 3; ++d )
		{
			rotationCentreVectorInPixelsPositive[ d ] = rotationCenterInMillimeters[ d ] / ( 0.001 * imageVoxelSizeInMicrometer[ d ] );
			rotationCentreVectorInPixelsNegative[ d ] = - rotationCenterInMillimeters[ d ] / ( 0.001 * imageVoxelSizeInMicrometer[ d ] );
		}


		final AffineTransform3D transform3D = new AffineTransform3D();

		// rotate around rotation centre
		//
		transform3D.translate( rotationCentreVectorInPixelsNegative ); // + or - ??
		for ( int d = 0; d < 3; ++d)
		{
			transform3D.rotate( d, angles[ d ]);
		}
		final AffineTransform3D translateBackFromRotationCentre = new AffineTransform3D();
		translateBackFromRotationCentre.translate( rotationCentreVectorInPixelsPositive );
		transform3D.preConcatenate( translateBackFromRotationCentre );

		// translate
		//
		transform3D.translate( translationInPixels );

		return transform3D;
	}
}
