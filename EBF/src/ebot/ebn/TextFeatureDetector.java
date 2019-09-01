package ebot.ebn;

import java.io.IOException;

public interface TextFeatureDetector {
	public static final int NODES_PER_CHARACTER = 16 * 6 - 1 + 1;
	public static int genInputCountForTextSize(int length) {
		return NODES_PER_CHARACTER * length + 1;
	}
	public static double[] encodeText(String s, int count) {
		double[] data = new double[count];
		
		int dl = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			
			int cid;
			if (c >= 0x20 && c < 0x75) {
				cid = s.charAt(i) - ' ';
			} else {
				cid = NODES_PER_CHARACTER - 1;
			}
			
			data[dl + cid] = 1;
			
			dl += NODES_PER_CHARACTER;
			
			if(dl >= count - 1) {
				break;
			}
		}
		
		data[data.length - 1] = 1;
		
		return data;
	}
	public static String decodeText(double[] data, int count) {
		char[] out = new char[(count - 1) / NODES_PER_CHARACTER];
		
		int dl = 0;
		for (int i = 0; i < out.length; i++) {
			char c = '\u0000';
			double max = Double.NEGATIVE_INFINITY;
			
			for (int j = 0; j < NODES_PER_CHARACTER; j++) {
				if (data[dl] > max) {
					c = (char) (j + ' ');
					max = data[dl];
				}
				
				dl += 1;
			}
			
			out[i] = c;
		}
		
		return new String(out).replace("\u0000", "");
	}
	
	public boolean hasFeature(String in);
	
	public void train() throws IOException;
	public void quickTrain() throws IOException;
	
	public static void main(String[] args) {
		System.out.println(decodeText(encodeText("Hi", genInputCountForTextSize(2)), genInputCountForTextSize(2)));
	}
}
