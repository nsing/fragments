package com.exercise;


import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Solution class
 */
public class Fragments {

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try (BufferedReader in = new BufferedReader(new FileReader(args[0]))) {
			String fragmentProblem;
			while ((fragmentProblem = in.readLine()) != null) {
				System.out.println(reassemble(fragmentProblem));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method reassembles the input String according to the requirements
	 * Logic:
	 * For each input line, search the collection of fragments to locate
	 * the pair with the maximal overlap match then merge those two fragments.
	 * This operation will decrease the total number of fragments by one.
	 * Repeat until there is only one fragment remaining in the collection
	 * This is the defragmented line / reassembled document.
	 * If there is more than one pair of maximally overlapping fragments
	 * in any iteration then just merge one of them.
	 * When comparing for overlaps, compare case sensitively.
  	 * Examples:
	 * - "ABCDEF" and "DEFG" overlap with overlap length 3
	 * - "ABCDEF" and "XYZABC" overlap with overlap length 3
	 * - "ABCDEF" and "BCDE" overlap with overlap length 4
	 * - "ABCDEF" and "XCDEZ" do *not* overlap (they have matching characters
	 *   in the middle, but the overlap does not extend to the end of either string).
	 * @param fragmentProblem
	 * @return
	 */
	private static String reassemble(String fragmentProblem) {
		//Get an array of fragments
		String[] fragments = fragmentProblem.split(";");
		while(fragments.length > 1) {
			//Get maximum overlap
			Overlap maxOverlap = getMaxOverlap(fragments);
			if(maxOverlap.overlapStr.length() != 0) { //Max overlap
				//Merge two overlapping fragments into one in the array
				fragments = mergeFragments(maxOverlap, fragments);
			}
			else { //No match in fragments
				//Get a new array after merging all fragments into one
				String newFragment = "";
				String[] newFragments = new String[1];
				for(int i=0; i < fragments.length; i++) {
					newFragment = newFragment + fragments[i];
				}
				newFragments[0] = newFragment;
				fragments = newFragments;
			}
		}
		return fragments[0];
	}

	/**
	 * This method merges two overlapping fragments into one in the array
	 *
	 * @param maxOverlap
	 * @param fragments
	 * @return
	 */
	private static String[] mergeFragments(Overlap maxOverlap, String[] fragments) {
		String mergedFragment = null; //Merged fragment
		if(maxOverlap.frag1.contains(maxOverlap.frag2)) {
			//When first fragment contains second fragment
			mergedFragment = maxOverlap.frag1;
		}
		else if(maxOverlap.frag2.contains(maxOverlap.frag1)) {
			//When second fragment contains first fragment
			mergedFragment = maxOverlap.frag2;
		}
		else if(maxOverlap.frag1.indexOf(maxOverlap.overlapStr) == 0) {
			//When overlap string is prefix of first fragment
			mergedFragment = maxOverlap.frag2.replace(maxOverlap.overlapStr,"").concat(maxOverlap.frag1);
		}
		else {
			//When overlap string is not prefix of first fragment
			mergedFragment = maxOverlap.frag1.replace(maxOverlap.overlapStr,"").concat(maxOverlap.frag2);
		}
		String[] newFragments = new String[fragments.length-1]; //New array
		//Set new array after merge
		int counter = 0;
		for(int i=0; i < fragments.length; i++) {
			if(i != maxOverlap.frag1Index && i != maxOverlap.frag2Index) {
				//Index is not the index of the two overlapping fragments
				newFragments[counter] = fragments[i];
				counter++;
			}
		}
		//Set the merged fragment
		newFragments[counter] = mergedFragment;
		return newFragments;
	}


	/**
	 * This method returns Overlap object for those two fragments who have maximum overlap
	 *
	 * @param fragments
	 * @return
	 */
	private static Overlap getMaxOverlap (String[] fragments) {
		//Find maximum overlap
		Overlap maxOverlap = null;
		for(int i=0; i < fragments.length; i++) {
			String fragment1 = fragments[i];
			String fragment2 = "";
			for(int j=i+1; j < fragments.length; j++){
				fragment2 = fragments[j];
				Overlap newOverlap = getOverlap(fragment1, fragment2, i, j); //Get overlap
				//Compare maxOverlap with newOverlap
				if(maxOverlap == null ||
						(newOverlap.overlapStr.length() > 0 &&
								maxOverlap.overlapStr.length() < newOverlap.overlapStr.length()) ){
					//New value of maxOverlap is newOverlap
					maxOverlap = newOverlap;
				}
			}
		}
		return maxOverlap;
	}


	/**
	 * This method returns Overlap object having overlap information between two fragments
	 * Please note: If Overlap object's overlapString value is "" then that means two
	 * fragments don't overlap
	 *
	 * @param frag1
	 * @param frag2
	 * @param frag1Index
	 * @param frag2Index
	 * @return
	 */
	private static Overlap getOverlap(String frag1, String frag2, int frag1Index, int frag2Index) {
		String overlapStr = "";
		if(frag2.contains(frag1)) { //1st fragment is overlap string
			overlapStr = frag1;
		}
		else if (frag1.contains(frag2)) { //2nd fragment is overlap string
			overlapStr = frag2;
		}
		else {
			//Overlap string can be 1st fragment's suffix and 2nd fragment's prefix
			int count1 = frag1.length();
			int count2 = 0;
			while(count1 > 0 && count2 < frag2.length()) {
				if (frag1.substring(count1-1, frag1.length()).equals(frag2.substring(0, count2+1))) {
					overlapStr = frag1.substring(count1-1, frag1.length());
				}
				count1--;
				count2++;
			}
			if("".equals(overlapStr)) {
				//Overlap string can be 1st fragment's prefix and 2nd fragment's suffix
				count1 = 0;
				count2 = frag2.length();
				while(count2 > 0 && count1 < frag1.length()) {
					if (frag2.substring(count2-1, frag2.length()).equals(frag1.substring(0, count1+1))) {
						overlapStr = frag2.substring(count2-1, frag2.length());
					}
					count2--;
					count1++;
				}
			}
		}

		//Instantiate and set overlap object
		Overlap overlap = new Overlap();
		overlap.frag1 = frag1;
		overlap.frag2 = frag2;
		overlap.overlapStr = overlapStr;
		overlap.frag1Index = frag1Index;
		overlap.frag2Index = frag2Index;

		//Return overlap
		return overlap;
	}

	/**
	 * Inner class representing overlap
	 */
	private static class Overlap {

		/**
		 * Constructor
		 */
		private Overlap() {
			overlapStr = "";
		}

		/*
		 * First fragment
		 */
		private String frag1;

		/*
		 * Second fragment
		 */
		private String frag2;

		/*
		 * Index of first fragment in the array
		 */
		private int frag1Index;

		/*
		 * Index of second fragment in the array
		 */
		private int frag2Index;

		/*
		 * Overlap string between fragment1 and fragment2
		 */
		private String overlapStr;

	}

}
