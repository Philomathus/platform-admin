package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.ThirdPMCacheManager;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.MemberGameMoney;
import com.qiqilm.server.admin.domain.MemberInfo;
import com.qiqilm.server.admin.domain.rsp.RspGameBalance;
import com.qiqilm.server.admin.domain.vo.BalanceResult;
import com.qiqilm.server.admin.domain.vo.GameApiRes;
import com.qiqilm.server.admin.domain.vo.GameResult;
import com.qiqilm.server.admin.domain.vo.XiaFenResult;
import com.qiqilm.server.admin.enums.EnumGamePlatform;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.manager.LogAction;
import com.qiqilm.server.admin.manager.LogMoney;
import com.qiqilm.server.admin.mapper.GamePlatformMapper;
import com.qiqilm.server.admin.mapper.MemberGameMoneyMapper;
import com.qiqilm.server.admin.mapper.MemberInfoMapper;
import com.qiqilm.server.admin.service.IGameBaseService;
import com.qiqilm.server.admin.service.IMemberInfoService;
import com.qiqilm.server.admin.service.game.*;
import com.qiqilm.server.admin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Log4j2
@Service
public class GameBaseServiceImpl implements IGameBaseService {
    @Autowired
    public  LogAction             logAction;
    @Autowired
    public  LogMoney              logMoney;
    @Resource
    private ForkJoinPool          forkJoinPool;
    @Resource
    private RestTemplate          restTemplate;
    @Resource
    private RedisUtil             redisUtil;
    @Resource
    private ThirdPMCacheManager   thirdPMCacheManager;
    @Resource
    private GamePlatformMapper    gamePlatformMapper;
    @Resource
    private MemberInfoMapper      memberInfoMapper;
    @Resource
    private MemberGameMoneyMapper gameMoneyMapper;
    @Resource
    private IMemberInfoService    memberInfoService;
    @Resource
    private BbinService           bbinService;
    @Resource
    private ShabaService          shabaService;
    @Resource
    private IcgService            icgService;
    @Resource
    private MeiTianService        meiTianService;
    @Resource
    private KaiXuanService        kaiXuanService;
    @Resource
    private LegService            legService;
    @Resource
    private NewWorldService       newWorldService;
    @Resource
    private AFBService            afbService;
    @Resource
    private FanYSportService      fanYSportService;
    @Resource
    private BGService             bgService;
    @Resource
    private MemberGameMoneyMapper memberGameMoneyMapper;


    @Override
    public AjaxResult balance( String userId ) {
        final Date                    date          = new Date();
        List<Integer>                 lists         = memberGameMoneyMapper.lists( userId );
        Set<Callable<RspGameBalance>> forkJoinTasks = new HashSet<>();
        if ( lists.contains( EnumGamePlatform.AG_LIVE.getType() ) ) {
            forkJoinTasks.add( this.agBalanceTask( userId, date ) );
        }
        if ( lists.contains( EnumGamePlatform.OG_LIVE.getType() ) ) {
            forkJoinTasks.add( this.ogBalanceTask( userId ) );
        }
        if ( lists.contains( EnumGamePlatform.OG_NEW.getType() ) ) {
            forkJoinTasks.add( this.ogNewBalanceTask( userId ) );
        }
        if ( lists.contains( EnumGamePlatform.KY_CHESS.getType() ) ) {
            forkJoinTasks.add( this.kyBalanceTask( userId ) );
        }
        if ( lists.contains( EnumGamePlatform.KY_CHESS_NEW.getType() ) ) {
            forkJoinTasks.add( this.kyNewBalanceTask( userId ) );
        }
        if ( lists.contains( EnumGamePlatform.MG_LIVE.getType() ) ) {
            forkJoinTasks.add( this.mgBalanceTask( userId ) );
        }
        if ( lists.contains( EnumGamePlatform.NG_LIVE.getType() ) ) {
            forkJoinTasks.add( this.ngBalanceTask( userId ) );
        }
        if ( lists.contains( EnumGamePlatform.BBIN_LIVE.getType() ) ) {
            forkJoinTasks.add( this.bbinBalanceTask( userId, EnumGamePlatform.BBIN_LIVE.getType() ) );
        }
        if ( lists.contains( EnumGamePlatform.BBIN_SPORT.getType() ) ) {
            forkJoinTasks.add( this.bbinBalanceTask( userId, EnumGamePlatform.BBIN_SPORT.getType() ) );
        }
        if ( lists.contains( EnumGamePlatform.BBIN_DIANZI.getType() ) ) {
            forkJoinTasks.add( this.bbinBalanceTask( userId, EnumGamePlatform.BBIN_DIANZI.getType() ) );
        }
        if ( lists.contains( EnumGamePlatform.BBIN_FISH.getType() ) ) {
            forkJoinTasks.add( this.bbinBalanceTask( userId, EnumGamePlatform.BBIN_FISH.getType() ) );
        }
        if ( lists.contains( EnumGamePlatform.SHABA_SPORT.getType() ) ) {
            forkJoinTasks.add( this.shabaBalanceTask( userId ) );
        }
        if ( lists.contains( EnumGamePlatform.ICG_DIANZI.getType() ) ) {
            forkJoinTasks.add( this.icgBalanceTask( userId ) );
        }
        if ( lists.contains( EnumGamePlatform.MEITIAN_CHESS.getType() ) ) {
            forkJoinTasks.add( this.mtBalanceTask( userId ) );
        }
        if ( lists.contains( EnumGamePlatform.KAIXUAN_CHESS.getType() ) ) {
            forkJoinTasks.add( this.kxBalanceTask( userId, date ) );
        }
        if ( lists.contains( EnumGamePlatform.KAIXUAN_CHESS_NEW.getType() ) ) {
            forkJoinTasks.add( this.kxNewBalanceTask( userId, date ) );
        }
        if ( lists.contains( EnumGamePlatform.NEWWORLD_CHESS.getType() ) ) {
            forkJoinTasks.add( this.newWorldBalanceTask( userId, date ) );
        }
        if ( lists.contains( EnumGamePlatform.AFB.getType() ) ) {
            forkJoinTasks.add( this.afbBalanceTask( userId, date ) );
        }
        if ( lists.contains( EnumGamePlatform.FANY_SPORT.getType() ) ) {
            forkJoinTasks.add( this.fanyBalanceTask( userId, date ) );
        }
        if ( lists.contains( EnumGamePlatform.BG_LIVE.getType() ) ) {
            forkJoinTasks.add( this.bgBalanceTask( userId, EnumGamePlatform.BG_LIVE.getType() ) );
        }
        if ( lists.contains( EnumGamePlatform.BG_DIANZI.getType() ) ) {
            forkJoinTasks.add( this.bgBalanceTask( userId, EnumGamePlatform.BG_DIANZI.getType() ) );
        }
        if ( lists.contains( EnumGamePlatform.BG_FISH.getType() ) ) {
            forkJoinTasks.add( this.bgBalanceTask( userId, EnumGamePlatform.BG_FISH.getType() ) );
        }

        List<Future<RspGameBalance>> futureList = forkJoinPool.invokeAll( forkJoinTasks );
        Set<RspGameBalance> resultSet = futureList.stream().map( t -> {
            try {
                return t.get();
            } catch ( InterruptedException | ExecutionException e ) {
                throw new IllegalStateException( e );
            }
        } ).filter( Objects::nonNull ).collect( Collectors.toSet() );
        return AjaxResult.success( resultSet );
    }

    private Callable<RspGameBalance> fanyBalanceTask( final String userId, final Date date ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.FANY_SPORT.getType() );
                BigDecimal   backMoney    = fanYSportService.queryCoin( gamePlatform, userId, date );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.FANY_SPORT.getType() );
                rspGameBalance.setName( EnumGamePlatform.FANY_SPORT.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> bgBalanceTask( final String userId, Integer platformId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( platformId );
                BigDecimal   backMoney    = bgService.queryCoin( gamePlatform, userId, gamePlatform.getLinecode() );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( platformId );
                rspGameBalance.setName( gamePlatform.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }


    private Callable<RspGameBalance> afbBalanceTask( final String userId, final Date date ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.AFB.getType() );
                BigDecimal   backMoney    = afbService.queryCoin( gamePlatform, userId, date );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.AFB.getType() );
                rspGameBalance.setName( EnumGamePlatform.AFB.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> newWorldBalanceTask( final String userId, final Date date ) {
        return () -> {
            try {
                GamePlatform gamePlatform =
                        gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.NEWWORLD_CHESS.getType() );
                BigDecimal backMoney = newWorldService.queryCoin( gamePlatform, userId, date );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.NEWWORLD_CHESS.getType() );
                rspGameBalance.setName( EnumGamePlatform.NEWWORLD_CHESS.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> legBalanceTask( final String userId, final Date date ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.LEG_CHESS.getType() );
                BigDecimal   backMoney    = legService.queryCoin( gamePlatform, userId, date );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.LEG_CHESS.getType() );
                rspGameBalance.setName( EnumGamePlatform.LEG_CHESS.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> kxBalanceTask( final String userId, final Date date ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.KAIXUAN_CHESS.getType() );
                BigDecimal   backMoney    = kaiXuanService.queryCoin( gamePlatform, userId, date );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.KAIXUAN_CHESS.getType() );
                rspGameBalance.setName( EnumGamePlatform.KAIXUAN_CHESS.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> kxNewBalanceTask( final String userId, final Date date ) {
        return () -> {
            try {
                GamePlatform gamePlatform =
                        gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.KAIXUAN_CHESS_NEW.getType() );
                BigDecimal backMoney = kaiXuanService.queryCoin( gamePlatform, userId, date );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.KAIXUAN_CHESS_NEW.getType() );
                rspGameBalance.setName( EnumGamePlatform.KAIXUAN_CHESS_NEW.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> mtBalanceTask( final String userId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.MEITIAN_CHESS.getType() );
                BigDecimal   backMoney    = meiTianService.queryCoin( gamePlatform, userId );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.MEITIAN_CHESS.getType() );
                rspGameBalance.setName( EnumGamePlatform.MEITIAN_CHESS.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> icgBalanceTask( final String userId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.ICG_DIANZI.getType() );
                BigDecimal   backMoney    = icgService.queryCoin( gamePlatform, userId );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.ICG_DIANZI.getType() );
                rspGameBalance.setName( EnumGamePlatform.ICG_DIANZI.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> shabaBalanceTask( final String userId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.SHABA_SPORT.getType() );
                BigDecimal   backMoney    = shabaService.queryCoin( gamePlatform, userId );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.SHABA_SPORT.getType() );
                rspGameBalance.setName( EnumGamePlatform.SHABA_SPORT.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> bbinBalanceTask( final String userId, Integer platformId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( platformId );
                BigDecimal   backMoney    = bbinService.queryCoin( gamePlatform, userId.replace( "_", "BBIN" ) );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( platformId );
                rspGameBalance.setName( gamePlatform.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> ngBalanceTask( final String userId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.NG_LIVE.getType() );
                //获取token令牌
                String     token     = PostData.getMGToken( gamePlatform );
                BigDecimal backMoney = PostData.getMGBalance( token, userId, gamePlatform );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.NG_LIVE.getType() );
                rspGameBalance.setName( EnumGamePlatform.NG_LIVE.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> mgBalanceTask( final String userId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.MG_LIVE.getType() );
                //获取token令牌
                String     token     = PostData.getMGToken( gamePlatform );
                BigDecimal backMoney = PostData.getMGBalance( token, userId, gamePlatform );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.MG_LIVE.getType() );
                rspGameBalance.setName( EnumGamePlatform.MG_LIVE.getName() );
                rspGameBalance.setValue( backMoney.setScale( 2, BigDecimal.ROUND_HALF_UP ) );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> kyBalanceTask( final String userId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.KY_CHESS.getType() );
                String resAll = PostData.getAllBalance( gamePlatform.getAgent(), userId, gamePlatform.getDes(),
                        gamePlatform.getMd5(), gamePlatform.getApiUrl() );

                log.warn( resAll );
                GameApiRes gameApiResAll = JsonUtil.json2Object( resAll, GameApiRes.class );
                BigDecimal backMoney =
                        gameApiResAll.getD().getCode() != 0 ? BigDecimal.ZERO : BigDecimal.valueOf( gameApiResAll.getD()
                                .getFreeMoney() ).setScale( 2, BigDecimal.ROUND_HALF_UP );
                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.KY_CHESS.getType() );
                rspGameBalance.setName( EnumGamePlatform.KY_CHESS.getName() );
                rspGameBalance.setValue( backMoney );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> kyNewBalanceTask( final String userId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.KY_CHESS_NEW.getType() );
                String resAll = PostData.getAllBalance( gamePlatform.getAgent(), userId, gamePlatform.getDes(),
                        gamePlatform.getMd5(), gamePlatform.getApiUrl() );

                GameApiRes gameApiResAll = JsonUtil.json2Object( resAll, GameApiRes.class );
                BigDecimal backMoney =
                        gameApiResAll.getD().getCode() != 0 ? BigDecimal.ZERO : BigDecimal.valueOf( gameApiResAll.getD()
                                .getFreeMoney() ).setScale( 2, BigDecimal.ROUND_HALF_UP );
                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.KY_CHESS_NEW.getType() );
                rspGameBalance.setName( EnumGamePlatform.KY_CHESS_NEW.getName() );
                rspGameBalance.setValue( backMoney );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }


    private Callable<RspGameBalance> ogBalanceTask( final String userId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.OG_LIVE.getType() );
                HttpHeaders  headers      = new HttpHeaders();
                thirdPMCacheManager.pullOgToken( userId, gamePlatform.getApiUrl(), gamePlatform.getDes(), gamePlatform.getMd5() );
                headers.set( "X-Token", thirdPMCacheManager.getOgToken( userId ) );
                String url = gamePlatform.getApiUrl() + "/game-providers/30/balance?username=" + userId;

                HttpMethod method = HttpMethod.GET;
                // 以表单的方式提交
                headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
                //将请求头部和参数合成一个请求
                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( headers );
                ResponseEntity<BalanceResult> response = restTemplate.exchange( url, method, requestEntity, BalanceResult.class );
                BalanceResult resultOG = response.getBody();
                log.warn( "OG查询余额 - userId:{} - url:{} - header:{} - result:{}", userId, url, JsonUtil.object2Json( headers ),
                        JsonUtil.object2Json( resultOG ) );
                BigDecimal backMoney = "success".equals( resultOG.getStatus() ) ? resultOG.getData()
                        .getBalance() : BigDecimal.ZERO;
                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.OG_LIVE.getType() );
                rspGameBalance.setName( EnumGamePlatform.OG_LIVE.getName() );
                rspGameBalance.setValue( backMoney );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> ogNewBalanceTask( final String userId ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.OG_NEW.getType() );

                MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
                requestMap.add( "player_id", userId );

                HttpHeaders headers = new HttpHeaders();
                headers.set( "key", gamePlatform.getDes() );
                headers.set( "operator-name", gamePlatform.getAgent() );
                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( headers );

                UriComponents uriComponents = UriComponentsBuilder.fromUriString(
                                gamePlatform.getApiUrl() + "/api/v2/platform/transfer-wallet/get-balance" ).queryParams( requestMap )
                        .build( true );

                Map<String, Object> resultMap = restTemplate.execute( uriComponents.toUri(), HttpMethod.GET,
                        restTemplate.httpEntityCallback( requestEntity ), response -> {
                    InputStream bodyStream = response.getBody();
                    String      text;
                    try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                        text = IOUtils.toString( reader );
                    }
                    return JsonUtil.json2Map( text );
                } );

                BigDecimal backMoney;
                if ( !CollectionUtils.isEmpty( resultMap ) && "S-100".equals( resultMap.get( "rs_code" ).toString() ) ) {
                    backMoney = new BigDecimal( resultMap.getOrDefault( "current_balance", "0" ).toString() );
                } else {
                    backMoney = BigDecimal.ZERO;
                }

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.OG_NEW.getType() );
                rspGameBalance.setName( EnumGamePlatform.OG_NEW.getName() );
                rspGameBalance.setValue( backMoney );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    private Callable<RspGameBalance> agBalanceTask( final String userId, final Date date ) {
        return () -> {
            try {
                GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( EnumGamePlatform.AG_LIVE.getType() );

                String orderId = PostData.createOrderId( gamePlatform.getAgent(), userId, EnumGamePlatform.AG_LIVE.getType(),
                        date );
                String result = PostData.GetBalance( userId, gamePlatform, orderId );

                Document doc = DocumentUtil.getXml( result );
                String money = doc.getElementsByTagName( "result" ).item( 0 ).getAttributes().getNamedItem( "info" )
                        .getTextContent();
                String msg = doc.getElementsByTagName( "result" ).item( 0 ).getAttributes().getNamedItem( "msg" )
                        .getTextContent();
                BigDecimal backMoney = StringUtils.isNotBlank( msg ) ? BigDecimal.ZERO : new BigDecimal( money ).setScale( 2,
                        BigDecimal.ROUND_HALF_UP );

                RspGameBalance rspGameBalance = new RspGameBalance();
                rspGameBalance.setType( EnumGamePlatform.AG_LIVE.getType() );
                rspGameBalance.setName( EnumGamePlatform.AG_LIVE.getName() );
                rspGameBalance.setValue( backMoney );
                return rspGameBalance;
            } catch ( Exception e ) {
                log.error( e.getMessage(), e );
            }
            return null;
        };
    }

    @Override
    public AjaxResult esc( String userId, Integer platformId ) {
        if ( !redisUtil.lock( EnumLock.member, userId, "0", 5 ) ) {
            log.error( "管理后台忽略会员重复下分userId:{}", userId );
            return AjaxResult.error( "管理后台忽略会员重复下分userId:" + userId );
        }
        MemberGameMoney gameMoney = getMemberGameMoney( userId, platformId );
        if ( gameMoney == null ) {
            return AjaxResult.error( "用户未登陆此游戏" );
        }
        GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( platformId );
        Date         date         = new Date();
        String       orderId      = PostData.createOrderId( gamePlatform.getAgent(), userId, platformId, date );
        String       name         = gamePlatform.getName() + "人工下分";
        BigDecimal   old          = memberInfoMapper.getMemberMoney( userId );

        EnumGamePlatform enumGamePlatform = EnumGamePlatform.getByType( platformId );
        log.warn( "UserId: {} enumGamePlatform: {}", userId, enumGamePlatform.getName() );
        try {
            XiaFenResult xiaFenResult;
            switch ( enumGamePlatform ) {
            case OG_LIVE:
                thirdPMCacheManager.pullOgToken( userId, gamePlatform.getApiUrl(), gamePlatform.getDes(), gamePlatform.getMd5() );
                xiaFenResult = outGameOG( userId, gamePlatform.getApiUrl(), orderId );
                break;
            case OG_NEW:
                xiaFenResult = outGameOGNew( userId, gamePlatform, orderId );
                break;
            case KY_CHESS:
            case KAIXUAN_CHESS_NEW:
                xiaFenResult = outGameKY( userId, gamePlatform, orderId );
                break;
            case AG_LIVE:
                xiaFenResult = outGameAG( userId, gamePlatform, orderId );
                break;
            case MG_LIVE:
            case NG_LIVE:
                xiaFenResult = outGameMG( userId, gamePlatform, orderId );
                break;
            case BBIN_LIVE:
            case BBIN_SPORT:
            case BBIN_FISH:
            case BBIN_DIANZI:
                xiaFenResult = bbinService.transferOUT( gamePlatform, userId, orderId );
                break;
            case SHABA_SPORT:
                xiaFenResult = shabaService.transfer( gamePlatform, userId, orderId );
                break;
            case ICG_DIANZI:
                xiaFenResult = icgService.transfer( gamePlatform, userId, orderId );
                break;
            case MEITIAN_CHESS:
                xiaFenResult = meiTianService.transfer( gamePlatform, userId, orderId );
                break;
            case KAIXUAN_CHESS:
                xiaFenResult = kaiXuanService.transfer( gamePlatform, userId, orderId, date );
                break;
            case LEG_CHESS:
                xiaFenResult = legService.transfer( gamePlatform, userId, orderId, date );
                break;
            case NEWWORLD_CHESS:
                xiaFenResult = newWorldService.transfer( gamePlatform, userId, orderId, date );
                break;
            case AFB:
                xiaFenResult = afbService.transfer( gamePlatform, userId, orderId, date );
                break;
            case FANY_SPORT:
                xiaFenResult = fanYSportService.transfer( gamePlatform, userId, orderId, date );
                break;
            case BG_LIVE:
            case BG_DIANZI:
            case BG_FISH:
                xiaFenResult = bgService.transfer( gamePlatform, userId, gamePlatform.getLinecode() );
                break;
            default:
                xiaFenResult = null;
                break;
            }
            if ( xiaFenResult == null ) {
                throw new BusinessException( "获取游戏下分记录异常" );
            }
            if ( xiaFenResult.getBackMoney().compareTo( BigDecimal.ZERO ) == 0 ) {
                return AjaxResult.error( "下分金额为0！" );
            }
            if ( xiaFenResult.isOk() ) {
                MemberInfo member = memberInfoService.selectMemberInfoById( userId );

                this.xiafen( member, gamePlatform, xiaFenResult, orderId, old, name, gameMoney );
            } else {
                memberInfoService.outGameFail( orderId, userId, platformId );
                log.info( "人工下分失败：会员ID:{},下分平台:{},金额:{},result:{}", userId, gamePlatform.getName(), xiaFenResult.getBackMoney()
                        , xiaFenResult.isOk() );
                return AjaxResult.error();
            }
        } catch ( Exception e ) {
            memberInfoService.outGameFail( orderId, userId, platformId );
            log.error( "退出游戏失败userId：{}, platformId:{},orderId:{},msg:{} ", userId, platformId, orderId, e.getMessage() );
            return AjaxResult.error();
        }
        return AjaxResult.success();
    }

    @Transactional( rollbackFor = Exception.class )
    public void xiafen( MemberInfo member, GamePlatform gamePlatform, XiaFenResult xiaFenResult, String orderId, BigDecimal old
            , String name, MemberGameMoney gameMoney ) {
        memberInfoService.outGMGameSucess( orderId, member.getId(), gamePlatform.getId()
                .intValue(), xiaFenResult.getBackMoney(), member.getUserName() );
        logMoney.logPlatformSwitch( member.getId(), member.getUserName(), xiaFenResult.getBackMoney(), old,
                gamePlatform.getAgent(), name, orderId );
        logAction.logXiafen( ServletUtil.getHttpServletRequest(), member.getId(), member.getUserName(), name, orderId,
                xiaFenResult.getBackMoney()
                .subtract( gameMoney.getMoney() ), member.getTotalAccount(), xiaFenResult.getBackMoney() );
        log.info( "人工下分成功：会员ID:{},下分平台:{},金额:{},result:{}", member.getId(), gamePlatform.getName(), xiaFenResult.getBackMoney()
                , xiaFenResult.isOk() );
    }

    private MemberGameMoney getMemberGameMoney( String userId, Integer platformId ) {
        MemberGameMoney query = new MemberGameMoney();
        query.setMemberId( userId );
        List<MemberGameMoney> list      = gameMoneyMapper.selectMemberGameMoneyList( query );
        int[]                 platforms = { 8, 9, 10, 11 };
        Set<Integer>          set       = new HashSet( Arrays.asList( platforms ) );
        if ( set.contains( platformId ) ) {
            for ( MemberGameMoney m : list ) {
                for ( Integer in : set ) {
                    if ( m.getId().endsWith( in.toString() ) && m.getMoney().compareTo( BigDecimal.ZERO ) >= 0 ) {
                        return m;
                    }
                }
            }
            return new MemberGameMoney();
        }
        return gameMoneyMapper.selectMemberGameMoneyById( userId + "_" + platformId );
    }

    public XiaFenResult outGameOG( String account, String gameUrl, String orderId ) {
        XiaFenResult xiaFenResult = new XiaFenResult();

        //取得余额
        HttpHeaders headers = new HttpHeaders();
        headers.set( "X-Token", thirdPMCacheManager.getOgToken( account ) );
        String url = gameUrl + "/game-providers/30/balance?username=" + account;

        HttpMethod method = HttpMethod.GET;
        // 以表单的方式提交
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED );
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( headers );
        ResponseEntity<BalanceResult> response = restTemplate.exchange( url, method, requestEntity, BalanceResult.class );
        BalanceResult result = response.getBody();
        if ( !result.getStatus().equals( "success" ) ) {
            log.error( "取得余额失败失败 userId:" + account );
            return xiaFenResult;
        }
        if ( result.getData().getBalance().compareTo( BigDecimal.ZERO ) == 0 ) {
            xiaFenResult.setOk( true );
            return xiaFenResult;
        }

        //转出余额
        url = gameUrl + "/game-providers/30/balance";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add( "username", account );
        params.add( "transferId", orderId );

        params.add( "balance", String.valueOf( result.getData().getBalance().floatValue() ) );
        params.add( "action", "out" );

        method = HttpMethod.POST;

        //将请求头部和参数合成一个请求
        requestEntity = new HttpEntity<>( params, headers );

        ResponseEntity<GameResult> responseGameResult = restTemplate.exchange( url, method, requestEntity, GameResult.class );
        GameResult                 gameResult         = responseGameResult.getBody();
        if ( !gameResult.getStatus().equals( "success" ) ) {
            log.error( "转出余额失败 userId:" + account );
            return xiaFenResult;
        }
        xiaFenResult.setBackMoney( result.getData().getBalance() );
        xiaFenResult.setOk( true );
        return xiaFenResult;
    }

    public XiaFenResult outGameKY( String account, GamePlatform gamePlatform, String orderId ) {
        XiaFenResult xiaFenResult = new XiaFenResult();
        String       resAll;
        try {
            resAll = PostData.getAllBalance( gamePlatform.getAgent(), account, gamePlatform.getDes(), gamePlatform.getMd5(),
                    gamePlatform.getApiUrl() );
        } catch ( Exception e ) {
            log.error( "获取总分失败，memId=" + account );
            return xiaFenResult;
        }
        log.warn( resAll );
        GameApiRes gameApiResAll = JsonUtil.json2Object( resAll, GameApiRes.class );
        if ( gameApiResAll.getD().getCode() != 0 ) {
            log.error( "获取总分失败code：" + gameApiResAll.getD().getCode() );
            return xiaFenResult;
        }
        BigDecimal backMoney = new BigDecimal( gameApiResAll.getD().getFreeMoney() );
        if ( backMoney.compareTo( BigDecimal.ZERO ) == 0 ) {
            xiaFenResult.setOk( true );
            return xiaFenResult;
        }
        String resXF;
        try {
            resXF = PostData.xiafen( gamePlatform.getAgent(), account, backMoney.toString(), orderId, gamePlatform.getDes(),
                    gamePlatform.getMd5(), gamePlatform.getApiUrl() );
        } catch ( Exception e ) {
            log.error( "下分失败，，account=" + account );
            return xiaFenResult;
        }
        log.warn( resXF );
        GameApiRes gameApiResXF = JsonUtil.json2Object( resXF, GameApiRes.class );

        if ( gameApiResXF.getD().getCode() != 0 ) {
            log.error( "EEEEEE下分异常，可能正在结算,资金暂时保存在：{}中,code:{},memberId:{}，下分金额：{}", gamePlatform.getName(), gameApiResXF.getD()
                    .getCode(), account, backMoney );
            return xiaFenResult;
        }
        xiaFenResult.setOk( true );
        xiaFenResult.setBackMoney( backMoney );
        return xiaFenResult;

    }

    /**
     * AG下分
     *
     * @throws Exception
     */
    public XiaFenResult outGameAG( String account, GamePlatform gamePlatform, String orderId ) throws Exception {
        XiaFenResult xiaFenResult = new XiaFenResult();
        String       money;
        //查询余额
        String result = PostData.GetBalance( account, gamePlatform, orderId );
        if ( result == null ) {
            log.error( "查询AG余额失败account:{}", account );
            throw new BusinessException( "查询余额失败!" );
        } else {
            Document doc = DocumentUtil.getXml( result );
            money = doc.getElementsByTagName( "result" ).item( 0 ).getAttributes().getNamedItem( "info" ).getTextContent();
            String msg = doc.getElementsByTagName( "result" ).item( 0 ).getAttributes().getNamedItem( "msg" ).getTextContent();
            if ( !StringUtils.isEmpty( msg ) ) {
                log.error( "查询余额失败!" + msg );
                throw new BusinessException( "下分失败!" );
            }
        }
        BigDecimal backMoney = new BigDecimal( money );
        if ( backMoney.compareTo( BigDecimal.ZERO ) == 0 ) {
            xiaFenResult.setOk( true );
            return xiaFenResult;
        }
        String type = "OUT";
        String res  = PostData.PrepareTransferCredit( account, gamePlatform, backMoney, orderId, type );
        if ( res == null ) {
            throw new BusinessException( "预转账失败!" );
        } else {
            Document doc = DocumentUtil.getXml( res );
            String   msg = doc.getElementsByTagName( "result" ).item( 0 ).getAttributes().getNamedItem( "msg" ).getTextContent();
            if ( !StringUtils.isEmpty( msg ) ) {
                log.error( "预转账失败!" + msg );
                throw new BusinessException( "下分失败!" );
            }
        }
        String resAll = PostData.confirmMoney( account, gamePlatform, backMoney, orderId, type );
        if ( resAll == null ) {
            throw new BusinessException( "确认转账失败!" );
        } else {
            Document doc = DocumentUtil.getXml( resAll );
            String   msg = doc.getElementsByTagName( "result" ).item( 0 ).getAttributes().getNamedItem( "msg" ).getTextContent();
            if ( !StringUtils.isEmpty( msg ) ) {
                log.error( "确认转账失败!" + msg );
                throw new BusinessException( "进入游戏失败!" );
            }
        }
        xiaFenResult.setOk( true );
        xiaFenResult.setBackMoney( backMoney );
        return xiaFenResult;
    }

    private XiaFenResult outGameMG( String userId, GamePlatform gamePlatform, String orderId ) {
        try {
            //获取token令牌
            String token = PostData.getMGToken( gamePlatform );

            BigDecimal balance = PostData.getMGBalance( token, userId, gamePlatform );

            boolean flag = true;
            if ( balance.compareTo( BigDecimal.ZERO ) > 0 ) {
                String type = "Withdraw";
                flag = PostData.WalletTransactions( token, userId, balance, type, gamePlatform );
            }
            XiaFenResult xiaFenResult = new XiaFenResult();
            xiaFenResult.setOk( flag );
            xiaFenResult.setBackMoney( balance );
            return xiaFenResult;
        } catch ( Exception e ) {
            throw new BusinessException( "MG下分游戏失败!" + e.getMessage() );
        }
    }

    public XiaFenResult outGameOGNew( String account, GamePlatform gamePlatform, String orderId ) {
        XiaFenResult xiaFenResult = new XiaFenResult();

        //取得余额
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add( "player_id", account );

        HttpHeaders headers = new HttpHeaders();
        headers.set( "key", gamePlatform.getDes() );
        headers.set( "operator-name", gamePlatform.getAgent() );
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( headers );

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(
                        gamePlatform.getApiUrl() + "/api/v2/platform/transfer-wallet/get-balance" ).queryParams( requestMap )
                .build( true );

        Map<String, Object> resultMap = restTemplate.execute( uriComponents.toUri(), HttpMethod.GET,
                restTemplate.httpEntityCallback( requestEntity ), response -> {
            InputStream bodyStream = response.getBody();
            String      text;
            try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                text = IOUtils.toString( reader );
            }
            return JsonUtil.json2Map( text );
        } );
        if ( !CollectionUtils.isEmpty( resultMap ) && "S-100".equals( resultMap.get( "rs_code" ).toString() ) ) {
            BigDecimal balance = new BigDecimal( resultMap.getOrDefault( "current_balance", "0" ).toString() );
            xiaFenResult.setBackMoney( balance );
            if ( balance.compareTo( BigDecimal.ZERO ) == 0 ) {
                xiaFenResult.setOk( true );
                return xiaFenResult;
            }
        } else {
            log.error( "取得余额失败失败 userId:" + account );
            return xiaFenResult;
        }

        //转出余额
        Map<String, String> params = new TreeMap<>();
        params.put( "player_id", account );
        params.put( "transaction_id", orderId );
        params.put( "transfer_amount", xiaFenResult.getBackMoney().stripTrailingZeros().toPlainString() );
        params.put( "timestamp", String.valueOf( System.currentTimeMillis() / 1000 ) );

        StringBuilder sb = new StringBuilder();
        params.forEach( ( k, v ) -> sb.append( k ).append( "=" ).append( v ).append( "&" ) );
        String sign = sb.substring( 0, sb.length() - 1 ) + gamePlatform.getMd5();
        params.put( "signature", DigestUtils.md5Hex( sign ) );

        headers.setContentType( MediaType.APPLICATION_JSON );

        resultMap = restTemplate.execute( gamePlatform.getApiUrl()
                + "/api/v2/platform/transfer-wallet/withdraw", HttpMethod.POST,
                restTemplate.httpEntityCallback( new HttpEntity<>( params, headers ) ), response -> {
            InputStream bodyStream = response.getBody();
            String      text;
            try ( Reader reader = new InputStreamReader( bodyStream ) ) {
                text = IOUtils.toString( reader );
            }
            return JsonUtil.json2Map( text );
        } );

        if ( !CollectionUtils.isEmpty( resultMap ) && "S-100".equals( resultMap.get( "rs_code" ).toString() ) ) {
            xiaFenResult.setOk( true );
            return xiaFenResult;
        }

        log.error( "转出余额失败 userId:" + account );
        return xiaFenResult;
    }
}
