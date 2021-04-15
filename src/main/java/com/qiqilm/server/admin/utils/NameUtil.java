package com.qiqilm.server.admin.utils;

import com.qiqilm.server.admin.domain.ConfigEnvironment;
import com.qiqilm.server.admin.mapper.ConfigEnvironmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@Component
public class NameUtil {
    @Autowired
    private ConfigEnvironmentMapper configEnvironmentMapper;

	private static final List<String> INIT_NICK_NAME_LIST = Arrays.asList( "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"
			, "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4",
			"5", "6", "7", "8", "9" );

	public static synchronized List<String> getInitNickNameList() {
		Collections.shuffle( INIT_NICK_NAME_LIST );
		return INIT_NICK_NAME_LIST;
	}

	public String nickNameRandom() {
        ConfigEnvironment member_first_name = configEnvironmentMapper.selectConfigEnvironmentById("member_first_name");
        return member_first_name.getEnvValue().concat( String.join( "", getInitNickNameList().subList( 0, 5 ) ) );
	}

	public static String random6String() {
		return String.join( "", getInitNickNameList().subList( 0, 6 ) );
	}

	public static void main( String[] args ) {
//		System.out.println( nickNameRandom() );
		System.out.println( random6String() );
	}
}
