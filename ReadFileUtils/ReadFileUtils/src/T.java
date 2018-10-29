import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SingleSelectionModel;

public class T {
	public  static void main (String [] args){
		ArrayList<String> val = new ArrayList<String>( Arrays.asList("a","b","c"));
		for (int i = 0; i < val.size(); i++) {
			System.out.println(val.get(i));
		}
		String s = val.get(0);
		s="d";
		for (int i = 0; i < val.size(); i++) {
			System.out.println(val.get(i));
		}
	}
}
