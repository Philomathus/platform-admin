package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.LotteryBet0;
import com.qiqilm.server.admin.domain.LotteryMethod;
import com.qiqilm.server.admin.enums.EnumGamePlatform;
import com.qiqilm.server.admin.mapper.LotteryMethodMapper;
import com.qiqilm.server.admin.service.IGameDataLogService;
import com.qiqilm.server.admin.service.IGamePlatformService;
import com.qiqilm.server.admin.service.ILotteryBet0Service;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户投资行为Controller
 *
 * @author 77tv
 * @date 2021-03-03
 */
@RestController
@RequestMapping( "/admin/lotteryBet0" )
public class LotteryBet0Controller extends BaseController {

    @Autowired
    private ILotteryBet0Service  lotteryBet0Service;
    @Autowired
    private IGameDataLogService  gameDataLogService;
    @Autowired
    private IGamePlatformService gamePlatformService;

    @Resource
    private LotteryMethodMapper lotteryMethodMapper;

    /**
     * 查询用户投资行为列表
     */
    @PreAuthorize( "@ss.hasPermi('admin:lotteryBet:list')" )
    @GetMapping( "/list" )
    public TableDataInfo list( LotteryBet0 lotteryBet0 ) {
        startPage();
        List<LotteryBet0>   list              = lotteryBet0Service.selectLotteryBet0List( lotteryBet0 );
        Set<String>         methodIds         = list.stream().map( LotteryBet0::getMethodId ).collect( Collectors.toSet() );
        List<LotteryMethod> lotteryMethodList = lotteryMethodMapper.selectByBatchId( methodIds );
        for ( LotteryBet0 bet : list ) {
            for ( LotteryMethod lotteryMethod : lotteryMethodList ) {
                if ( bet.getMethodId().equals( lotteryMethod.getId() ) ) {
                    bet.setMethodStr( lotteryMethod.getName() );
                }
            }
        }
        return getDataTable( list );
    }

    /**
     * 用户投资行为统计
     */
    @PreAuthorize( "@ss.hasPermi('admin:lotteryBet:list')" )
    @GetMapping( "/getCount" )
    public AjaxResult getCount( LotteryBet0 lotteryBet0 ) {
        return lotteryBet0Service.getCount( lotteryBet0 );
    }

    /**
     * 导出用户投资行为
     */
    @PreAuthorize( "@ss.hasPermi('admin:lotteryBet:list')" )
    @GetMapping( "/export" )
    public void export( LotteryBet0 lotteryBet0, HttpServletResponse response ) {
        List<LotteryBet0> list = lotteryBet0Service.selectLotteryBet0List( lotteryBet0 );
        ExportExcelUtil.exportExcel( list, "投注記錄", "投注記錄表", LotteryBet0.class, response );
    }

    /**
     * 导出用户投资行为
     */
    @PreAuthorize( "@ss.hasPermi('admin:lotteryBet:repairOrder')" )
    @GetMapping( "/repairOrder" )
    public void repairOrder( LotteryBet0 lotteryBet0 ) {
        GamePlatform gamePlatform   = gamePlatformService.selectGamePlatformById( EnumGamePlatform.CX_LOTTERY.getType() );
        String       platformTypeId = gamePlatform.getGameTypeid();
        BigDecimal   beatRate       = gamePlatform.getRateBeat();
        gameDataLogService.beatLotteryCode2( platformTypeId, beatRate, lotteryBet0 );
    }

}
