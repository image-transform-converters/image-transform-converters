package itc.utilities;

public class ParsingUtils
{
	public static double[] delimitedStringToDoubleArray( String s, String delimiter )
	{
		String[] strings = s.split(delimiter);
		double[] nums = new double[strings.length];
		for (int i = 0; i < nums.length; i++) {
			nums[i] = Double.parseDouble(strings[i]);
		}

		return nums;
	}
}
