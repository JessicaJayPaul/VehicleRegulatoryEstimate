package com.cjt_pc.vehicleregulatoryestimate.sortlistview;

import java.util.Comparator;

public class PinyinComparator implements Comparator<SortModel> {

	@Override
	public int compare(SortModel lhs, SortModel rhs) {
		if (lhs.getSortLetters().equals("@")
				|| rhs.getSortLetters().equals("#")) {
			return -1;
		} else if (lhs.getSortLetters().equals("#")
				|| rhs.getSortLetters().equals("@")) {
			return 1;
		} else {
			return lhs.getSortLetters().compareTo(rhs.getSortLetters());
		}
	}
}
