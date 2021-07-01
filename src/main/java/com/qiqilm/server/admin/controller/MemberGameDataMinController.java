package com.qiqilm.server.admin.controller;

import com.qiqilm.server.admin.annotation.AccessLimit;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.domain.rsp.RspMemberGameData;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.GamePlatformMapper;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RequestParamData;
import com.qiqilm.server.admin.utils.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 会员注单数据Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@RestController
@RequestMapping( "/member/memberGameDataMin" )
public class MemberGameDataMinController extends BaseController {


	@Resource
	private GamePlatformMapper gamePlatformMapper;

	/** 注单列表 **/
	@GetMapping( value = "/orderBetStateList" )
	public AjaxResult orderBetStateList(){
		return AjaxResult.success(defaultOrderBetState());
	}

	/**
	 * 查询会员注单数据列表
	 */
	@AccessLimit(seconds = 5, maxCount = 0)
	@PreAuthorize( "@ss.hasPermi('member:memberGameDataMin:list')" )
	@GetMapping( "/list" )
	public AjaxResult list(MemberGameData memberGameData ) {
		//查询注单
		List<Map> listMap = null;
		try {
			listMap = handlyGameData((req) ->{
				List<Map<String,String>> list = new ArrayList<>();
				req.setGameStartTime(Optional.ofNullable(req.getGameStartTime()).orElseGet(() -> {
					if (StringUtils.isEmpty(req.getGameEndTime())){
						Date nowTime = new Date();
						Date startDate = null;
						try {
							startDate = DateFormatUtils.addMin(nowTime, -5);
						} catch (Exception e) {
							e.printStackTrace();
						}
						req.setGameEndTime(DateFormatUtils.formate(nowTime));
						return DateFormatUtils.formate(startDate);
					}
					String endTime = req.getGameEndTime();
					try {
						Date startDate = DateFormatUtils.addMin(DateFormatUtils.parse(endTime), -5);
						return DateFormatUtils.formate(startDate);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}));
				req.setGameEndTime(Optional.ofNullable(req.getGameEndTime()).orElseGet(() ->{
					String startTime = req.getGameStartTime();
					try {
						Date endDate = DateFormatUtils.addMin(DateFormatUtils.parse(startTime), 5);
						return DateFormatUtils.formate(endDate);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}));
				try {
					Optional.ofNullable(memberGameData.getPlatformId()).orElseThrow(()-> new Exception("游戏平台不存在，请检查!"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( memberGameData.getPlatformId() );
				if (gamePlatform != null){
					List<Map<String, String>> mapList = RequestParamData.requestBBINSportBetRecord(memberGameData,gamePlatform);
					mapList.stream().forEach(n ->{
						int count = 0;
						if (StringUtils.isNotEmpty(memberGameData.getBetState())){
							if (memberGameData.getBetState().equals(n.get("Result"))) count ++;
						}else {
							count ++;
						}
						if (StringUtils.isNotEmpty(memberGameData.getAccount())){
							String account = n.get("UserName").replace("bbin","_").toUpperCase();
							if (account.equals(memberGameData.getAccount())) count ++;
						}else {
							count ++;
						}
						if (StringUtils.isNotEmpty(memberGameData.getGameId()) ){
							if (memberGameData.getGameId().equals(n.get("WagersID"))) count ++;
						}else {
							count ++;
						}
						if (count == 3){
							list.add(n);
						}
					});
				}
				return list;
			},memberGameData);
		}catch (Exception ex){
			if (ex instanceof BusinessException){
				Integer code = Integer.valueOf(ex.getMessage());
				return AjaxResult.error(code, "查询投注记录失败!");
			}else
				ex.printStackTrace();
		}
		return AjaxResult.success(listMap);
	}

}
