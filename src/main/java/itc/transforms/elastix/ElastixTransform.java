/*-
 * #%L
 * image-transform-converters
 * %%
 * Copyright (C) 2019 - 2020 John Bogovic, Nicolas Chiaruttini, and Christian Tischer
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

import org.scijava.plugin.Parameter;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ElastixTransform {

    public static final String EULER_TRANSFORM = "EulerTransform";
    public static final String AFFINE_TRANSFORM = "AffineTransform";
    public static final String BSPLINE_TRANSFORM = "BSplineTransform";
    public static final String SIMILARITY_TRANSFORM = "SimilarityTransform";
    public static final String TRANSLATION_TRANSFORM = "TranslationTransform";
    public static final String SPLINE_KERNEL_TRANSFORM = "SplineKernelTransform";

    public static final String DEFAULT_RESAMPLER = "DefaultResampler";

    public static final String RESULT_IMAGE_FORMAT_MHD = "mhd";

    public static final String RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_CHAR = "unsigned char";
    public static final String RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_SHORT = "unsigned short";

    public static final String FINAL_LINEAR_INTERPOLATOR = "FinalLinearInterpolator";
    public static final String FINAL_NEAREST_NEIGHBOR_INTERPOLATOR = "FinalNearestNeighborInterpolator";


    // TODO : in the toString method, put the field Transform at the first line
    @Parameter
    public String Transform;
    @Parameter
    public Integer NumberOfParameters;
    @Parameter
    public Double[] TransformParameters;
    @Parameter
    public String InitialTransformParametersFileName;
    @Parameter
    public String HowToCombineTransforms;

    // Image specific
    @Parameter
    public Integer FixedImageDimension;
    @Parameter
    public Integer MovingImageDimension;
    @Parameter
    public String FixedInternalImagePixelType; // "float", "unsigned char"
    @Parameter
    public String MovingInternalImagePixelType;
    @Parameter
    public Integer[] Size;
    @Parameter
    public Integer[] Index;
    @Parameter
    public Double[] Spacing;
    @Parameter
    public Double[] Origin;
    @Parameter
    public Double[] Direction;
    @Parameter
    public Boolean UseDirectionCosines;

    // Transform specific
    //  -> ElastixTransform subclasses

    // ResampleInterpolator specific
    @Parameter
    public String ResampleInterpolator;
    @Parameter
    public Integer FinalBSplineInterpolationOrder;

    // Resampler specific
    @Parameter
    public String Resampler;
    @Parameter
    public Double DefaultPixelValue;
    @Parameter
    public String ResultImageFormat;
    @Parameter
    public String ResultImagePixelType;
    @Parameter
    public Boolean CompressResultImage;

    /**
     * Returns a String representation of the current ElastixTransform object
     * Fit the normal Data representation in TransformParameters files provided by elastix
     * @return String representation of the ElastixTransform object
     */
    public String toString() {

        String str = "";
        Field[] fields = this.getClass().getFields();
        try {
            for (int i=0;i<fields.length;i++) {
                Field f = fields[i];
                if ((f.get(this)!=null)&&(f.isAnnotationPresent(Parameter.class))) { // Provided the field has a value and is annotated as a Scijava Parameter
                    switch (f.getType().getSimpleName()) {
                        case "String": case "Boolean":
                            str += "(" + f.getName() + " \"" + f.get(this) + "\")\n";
                            break;
                        case "Integer":  case "Double":
                            str += "(" + f.getName() + " " + f.get(this) + ")\n";
                            break;
                        case "Integer[]":
                            String integersList = Arrays.stream((Integer[]) f.get(this))
                                    .map(Object::toString)
                                    .collect(Collectors.joining(" "));
                            str += "(" + f.getName() + " " + integersList + ")\n";
                            break;
                        case "Double[]":
                            try
                            {

                                String doublesList = Arrays.stream( ( Double[] ) f.get( this ) )
                                        .map( x -> String.format("%.12f", x) )
                                        .collect( Collectors.joining( " " ) );
                                str += "(" + f.getName() + " " + doublesList + ")\n";
                            }
                            catch( Exception e )
                            {
                                System.out.println( "Parameter not found: " + f.getName() );
                            }
                            break;
                        default:
                            System.err.println("Unhandled class : " + f.getType().getSimpleName());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            str = "Error during ElastixTransform to String conversion";
            e.printStackTrace();
        }

        return str;
    }

    /**
     * Save the current ElastixTransform Object to the standard temporary directory
     * @return TransformParameters file containing all the Elastix transform properties
     */
    public File toFile() {
        BufferedWriter writer = null;
        try {
            File temp = File.createTempFile("TransformParameters.", ".txt");
            writer = new BufferedWriter(new FileWriter(temp));
            writer.write(this.toString());
            writer.close();
            temp.deleteOnExit(); // Temporary file is deleted on virtual machine exit
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Save the current ElastixTransform Object to the specified path
     * @param path
     * @return TransformParameters file containing all the Elastix transform properties
     */
    public File save(String path) {
        BufferedWriter writer = null;
        try {
            File f = new File(path);
            writer = new BufferedWriter(new FileWriter(f));
            writer.write(this.toString());
            writer.close();
            return f;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Reads a TransformParameters file generated from Elastix (f) and returns an
     * appropriate ElastixTransform object
     * @param f file generated from Elastix
     * @return ElastixTransform object, containing all properties from the file
     */
    public static ElastixTransform load(File f)
            throws IOException, UnsupportedOperationException {

        BufferedReader file = new BufferedReader(new FileReader(f));
        String line;
        String regex = "\\((\\S+)\\s(.+)(\\))";
        Pattern p = Pattern.compile(regex);

        ElastixTransform out;
        // Checks the sort of transform based on the first line of the elastix file
        String firstLine = file.readLine();
        // Assert the first line contains the type of transformation
        Matcher m = p.matcher(firstLine);
        boolean match = m.matches();
        assert match;
        assert m.group(1).equals("Transform");

        switch (m.group(2)) {
            case "\"" + TRANSLATION_TRANSFORM + "\"":
                throw new UnsupportedOperationException();
            case "\"" + SPLINE_KERNEL_TRANSFORM + "\"":
                throw new UnsupportedOperationException();
            case  "\"" + EULER_TRANSFORM + "\"":
                out = new ElastixEulerTransform();
                out.Transform = EULER_TRANSFORM;
                break;
            case "\"" + AFFINE_TRANSFORM + "\"":
                out = new ElastixAffineTransform();
                out.Transform = AFFINE_TRANSFORM;
                break;
            case "\"" + BSPLINE_TRANSFORM + "\"":
                out = new ElastixBSplineTransform();
                out.Transform = BSPLINE_TRANSFORM;
                break;
            case "\"" + SIMILARITY_TRANSFORM + "\"":
                out = new ElastixSimilarityTransform();
                out.Transform = SIMILARITY_TRANSFORM;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        Class<?> elastixTransformClass = out.getClass();
        Field field;

        while ((line = file.readLine()) != null) {
            m = p.matcher(line);
            if (m.matches()) {
                try {
                    field = elastixTransformClass.getField(m.group(1));
                    if (field.isAnnotationPresent(Parameter.class)) { // save field only if it is annotated as a Scijava Parameter
                        fillField(out, field, m.group(2));
                    }
                } catch (NoSuchFieldException e) {
                    System.err.println("Field "+m.group(1)+" not found in "+elastixTransformClass.getName()+" class.");
                }
            }
        }

        // Casting to 2D or 3D transformation

        ElastixTransform dimensionCastET;

        if (!out.FixedImageDimension.equals(out.MovingImageDimension)) {
            // Do not handle 2D to 3D transformation
            System.err.println("fixed dimension = "+out.FixedImageDimension);
            System.err.println("moving dimension = "+out.MovingImageDimension);
            System.err.println("Unhandled case : non identical dimensions transformation");
            throw new UnsupportedOperationException();
        }

        switch (out.FixedImageDimension) {
            case 2:
                switch (out.getClass().getSimpleName()) {
                    case "ElastixAffineTransform":
                        dimensionCastET = upCast(out, ElastixAffineTransform2D.class);
                        break;
                    case "ElastixBSplineTransform":
                        dimensionCastET = upCast(out,ElastixBSplineTransform2D.class);
                        break;
                    case "ElastixSimilarityTransform":
                        dimensionCastET = upCast(out,ElastixSimilarityTransform2D.class);
                        break;
                    case "ElastixEulerTransform":
                        dimensionCastET = upCast(out,ElastixEulerTransform2D.class);
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
                break;
            case 3:
                switch (out.getClass().getSimpleName()) {
                    case "ElastixAffineTransform":
                        dimensionCastET = upCast(out, ElastixAffineTransform3D.class);
                        break;
                    case "ElastixBSplineTransform":
                        dimensionCastET = upCast(out,ElastixBSplineTransform3D.class);
                        break;
                    case "ElastixSimilarityTransform":
                        dimensionCastET = upCast(out,ElastixSimilarityTransform3D.class);
                        break;
                    case "ElastixEulerTransform":
                        dimensionCastET = upCast(out,ElastixEulerTransform3D.class);
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
                break;
            default:
                System.err.println(out.FixedImageDimension+"D transform unsupported.");
                throw new UnsupportedOperationException();
        }
        return dimensionCastET;
    }


    static ElastixTransform upCast(ElastixTransform et, Class classType) {
        // Upcasting
        ElastixTransform et_out = null;
        try {
            et_out = (ElastixTransform) classType.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field[] fields = et.getClass().getFields();
            for (int i=0;i<fields.length;i++) {
                Field f = fields[i];
                if (f.isAnnotationPresent(Parameter.class)) {
                    f.set(et_out,f.get(et));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return et_out;
    }


    /**
     * Inner class for file to object conversion of ElaslixTransform objects
     * @param et current object
     * @param f field ot be filled
     * @param s string representation of the object
     */
    static void fillField(ElastixTransform et, Field f, String s) {
        try {
            switch (f.getType().getSimpleName()) {
                case "String":
                        String stringValue = s.substring(1,s.length()-1); // Removes quotes on both sides
                        f.set(et,stringValue);
                    break;
                case "Integer":
                        f.set(et,new Integer(s));
                    break;
                case "Integer[]":
                        String[] integerList = s.split(" ");
                        Integer[] integers = new Integer[integerList.length];
                        for (int i=0;i<integers.length;i++) {
                            integers[i] = new Integer(integerList[i]);
                        }
                        f.set(et, integers);
                    break;
                case "Double":
                        f.set(et,new Double(s));
                    break;
                case "Double[]":
                        String[] doubleList = s.split(" ");
                        Double[] doubles = new Double[doubleList.length];
                        for (int i=0;i<doubles.length;i++) {
                            doubles[i] = new Double(doubleList[i]);
                        }
                        f.set(et, doubles);
                    break;
                case "Boolean":
                        f.set(et,new Boolean(s.substring(1,s.length()-1)));
                    break;
                default:
                    System.err.println("Unhandled class : "+ f.getType().getSimpleName());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
