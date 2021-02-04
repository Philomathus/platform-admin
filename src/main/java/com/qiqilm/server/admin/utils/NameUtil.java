package com.qiqilm.server.admin.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NameUtil {
	private static final List<String> INIT_NICK_NAME_LIST = Arrays.asList( "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"
			, "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4",
			"5", "6", "7", "8", "9" );

	public static synchronized List<String> getInitNickNameList() {
		Collections.shuffle( INIT_NICK_NAME_LIST );
		return INIT_NICK_NAME_LIST;
	}

	public static String nickNameRandom() {
		return "柒柒".concat( String.join( "", getInitNickNameList().subList( 0, 5 ) ) );
	}

	public static String random6String() {
		return String.join( "", getInitNickNameList().subList( 0, 6 ) );
	}

	public static void main( String[] args ) {
		System.out.println( nickNameRandom() );
		System.out.println( random6String() );
	}
}
