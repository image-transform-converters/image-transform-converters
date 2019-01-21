package itc.transforms.elastix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ElastixTransform {

    public String Transform;
    public Integer NumberOfParameters;
    public Double[] TransformParameters;

    public String InitialTransformParametersFileName;
    public String HowToCombineTransforms;

    // Image specific
    public Integer FixedImageDimension;
    public Integer MovingImageDimension;
    public String FixedInternalImagePixelType; // "float", "unsigned char"
    public String MovingInternalImagePixelType;
    public Integer[] Size;
    public Integer[] Index;
    public Double[] Spacing;
    public Double[] Origin;
    public Double[] Direction;
    public Boolean UseDirectionCosines;

    // Transform specific
    //  -> ElastixTransform subclasses

    // ResampleInterpolator specific
    public String ResampleInterpolator;
    public Integer FinalBSplineInterpolationOrder;

    // Resampler specific
    public String Resampler;
    public Double DefaultPixelValue;
    public String ResultImageFormat;
    public String ResultImagePixelType;
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
                if (f.get(this)!=null) { // Provided the field has a value
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
                            String doublesList = Arrays.stream((Double[]) f.get(this))
                                    .map(Object::toString)
                                    .collect(Collectors.joining(" "));
                            str += "(" + f.getName() + " " + doublesList + ")\n";
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
    public static ElastixTransform load(File f) throws IOException, UnsupportedOperationException {

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
            case "\"TranslationTransform\"": case "\"SplineKernelTransform\"":
                throw new UnsupportedOperationException();
            case "\"EulerTransform\"":
                out = new ElastixEulerTransform();
                out.Transform = "EulerTransform";
                break;
            case "\"AffineTransform\"":
                out = new ElastixAffineTransform();
                out.Transform = "AffineTransform";
                break;
            case "\"BSplineTransform\"":
                out = new ElastixBSplineTransform();
                out.Transform = "BSplineTransform";
                break;
            case "\"SimilarityTransform\"":
                out = new ElastixSimilarityTransform();
                out.Transform = "SimilarityTransform";
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
                    fillField(out, field, m.group(2));
                } catch (NoSuchFieldException e) {
                    System.err.println("Field "+m.group(1)+" not found in "+elastixTransformClass.getName()+" class.");
                }
            }
        }
        return out;
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

    public static void main( String[] args )
    {
        try {
            String path = "/home/nico/Dropbox/BIOP/image-transform-converters/src/test/resources/elastix/";
            String fName;
            fName = "TransformParameters.0.txt";
            fName = "TransformParameters.1.txt";
            fName = "TransformParameters.2.txt";

            fName = "TransformParameters.Affine.txt";
            fName = "TransformParameters.BSpline.txt";
            fName = "TransformParameters.Similarity.txt";
            ElastixTransform et =  ElastixTransform.load(new File(path+fName));
            System.out.println(et);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
