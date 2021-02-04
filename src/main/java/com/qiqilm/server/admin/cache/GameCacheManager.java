package com.qiqilm.server.admin.cache;


import com.fasterxml.jackson.core.type.TypeReference;
import com.qiqilm.server.admin.constant.Constants;
import com.qiqilm.server.admin.domain.GameInfo;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.GameType;
import com.qiqilm.server.admin.domain.rsp.RspGameInfo;
import com.qiqilm.server.admin.domain.rsp.RspGameType;
import com.qiqilm.server.admin.domain.rsp.RspVipSet;
import com.qiqilm.server.admin.mapper.ConfigVipMapper;
import com.qiqilm.server.admin.mapper.GameInfoMapper;
import com.qiqilm.server.admin.mapper.GamePlatformMapper;
import com.qiqilm.server.admin.mapper.GameTypeMapper;
import com.qiqilm.server.admin.utils.JsonUtil;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 游戏缓存
 */
@Component
@Slf4j
public class GameCacheManager {

    @Resource
    StringRedisTemplate stringRedisTemplate ;

    @Resource
    GameInfoMapper gameInfoMapper ;

    @Resource
    GameTypeMapper gameTypeMapper ;

    @Resource
    GamePlatformMapper gamePlatformMapper ;

    @Resource
    ConfigVipMapper configVipMapper ;



    @PostConstruct
    public void init(){
        log.info("初始化游戏redis高速缓存开始");
        long bTime = System.currentTimeMillis();
        reSetGames();
        log.info("初始化游戏redis缓存结束,用时 {}毫秒",System.currentTimeMillis()-bTime);
        initGamePlatforms();
        initVip();

    }

    public void initVip(){
        List<RspVipSet> list =  configVipMapper.findListForCache();
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(Constants.CX_VIP.concat("list"), JsonUtil.object2Json(list));
        for(RspVipSet p:list){
            operations.set(Constants.CX_VIP.concat(String.valueOf(p.getLevel_flag())), JsonUtil.object2Json(p));
        }

    }

    public void reSetGames(){
        //初始化游戏 id:game
        initGames();
       // 初始化游戏类型
        initGamesTypes();
        //初始化游戏类型和游戏的关联 typeId:gamesid
        initGameGroup();
    }


    //初始化游戏
    public void initGames(){

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();

        for(RspGameInfo game : gameInfoMapper.findTypeList()){
            operations.set(Constants.CX_GAME.concat("id:").concat(game.getId()), JsonUtil.object2Json(game));
            if(game.getPlatformId()==3){
                operations.set(Constants.CX_GAME.concat("liveId"), game.getId());
            }
        }
    }

    public void setGameInfo(GameInfo gameInfo ){
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        RspGameInfo game = new RspGameInfo();
        BeanUtils.copyProperties(gameInfo,game);
        operations.set(Constants.CX_GAME.concat("id:").concat(game.getId()), JsonUtil.object2Json(game));
        if(game.getPlatformId()==3){
            operations.set(Constants.CX_GAME.concat("liveId"), game.getId());
        }
    }

    public RspGameInfo getGameInfo(String id){
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String val = operations.get(Constants.CX_GAME.concat("id:")+id);
        if(StringUtils.isEmpty(val)){
            return null;
        }
        return JsonUtil.json2Object(val, RspGameInfo.class);
    }
    public void initGamesTypes(){
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        List<GameType> typeList =  gameTypeMapper.selectGameTypeList(new GameType());
        operations.set(Constants.CX_GAME.concat("type:list"), JsonUtil.object2Json(typeList.stream().filter(s->Integer.parseInt(s.getStatus())>0).collect(Collectors.toList())));
    }

    public List<RspGameType> getGamesTypes(){
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String val = operations.get(Constants.CX_GAME.concat("type:list"));
        return JsonUtil.json2Array(val, new TypeReference<List<RspGameType>>() {});
    }
//
    /**
     * 初始化游戏类型和游戏的关联
     */
    public void initGameGroup(){
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        for(RspGameType gameType : getGamesTypes() ){
            List<String> gameList =  gameInfoMapper.findListByType(gameType.getId()).stream().map(RspGameInfo::getId).collect(Collectors.toList());
            operations.set(Constants.CX_GAME.concat("group:").concat(gameType.getId()), JsonUtil.object2Json(gameList));
        }
    }



    public GamePlatform getGamePlatform(Integer id){
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String val = operations.get(Constants.CX_GAME.concat("platformId:").concat(String.valueOf(id)));
        return JsonUtil.json2Object(val, GamePlatform.class);
    }

    public void initGamePlatforms(){

        List<String> allIds = new ArrayList<>();
        Map<String,List<String>> groupMap = new HashMap<>();

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        for(GamePlatform p: gamePlatformMapper.findSimpleList()){
            if(p.getGameTypeid()==null){
                continue;
            }
            allIds.add(String.valueOf(p.getId()));
            operations.set(Constants.CX_GAME.concat("platformId:").concat(String.valueOf(p.getId())), JsonUtil.object2Json(p));
            if(!groupMap.containsKey(p.getGameTypeid())){
                groupMap.put(p.getGameTypeid(), new ArrayList<>() );
            }
            groupMap.get(p.getGameTypeid()).add(String.valueOf(p.getId()));
        }

        operations.set(Constants.CX_GAME.concat("platformIds:list"), JsonUtil.object2Json(allIds));
        for(String typeId : groupMap.keySet()){
            operations.set(Constants.CX_GAME.concat("platformGroups:").concat(typeId), JsonUtil.object2Json(groupMap.get(typeId)));
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
