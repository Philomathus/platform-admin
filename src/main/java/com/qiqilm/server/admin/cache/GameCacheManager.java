package com.qiqilm.server.admin.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.rsp.RspGameInfo;
import com.qiqilm.server.admin.domain.rsp.RspGameType;
import com.qiqilm.server.admin.mapper.GameInfoMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 游戏缓存
 */
@Component
@Log4j2
public class GameCacheManager {

	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private GameInfoMapper      gameInfoMapper;

	public List<RspGameType> getGamesTypes() {
		ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
		String                          val        = operations.get( Constants.CX_GAME.concat( "type:list" ) );
		return JsonUtil.json2Array( val, new TypeReference<List<RspGameType>>() {} );
	}

	/**
	 * 初始化游戏类型和游戏的关联
	 */
	public void initGameGroup() {
		ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
		for ( RspGameType gameType : getGamesTypes() ) {
			List<String> gameList =
					gameInfoMapper.findListByType( gameType.getId() ).stream().map( RspGameInfo::getId ).collect( Collectors.toList() );
			operations.set( Constants.CX_GAME.concat( "group:" ).concat( gameType.getId() ), JsonUtil.object2Json( gameList ) );
		}
	}

	public String getICGToken() {
		ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
		return operations.get( Constants.PLATFORM_TOKEN.concat( "icg" ) );
	}

	public Long add( String useID, String game ) {
		return stringRedisTemplate.opsForSet().add( Constants.PLATFORM_TOKEN.concat( game ).concat( ":users" ), useID );
	}
}
