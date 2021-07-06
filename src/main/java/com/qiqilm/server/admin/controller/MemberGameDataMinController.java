package com.qiqilm.server.admin.controller;

import com.alibaba.fastjson.JSON;
import com.qiqilm.server.admin.annotation.AccessLimit;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.domain.MemberGameData;
import com.qiqilm.server.admin.enums.EnumGamePlatform;
import com.qiqilm.server.admin.exception.BusinessException;
import com.qiqilm.server.admin.mapper.GamePlatformMapper;
import com.qiqilm.server.admin.service.IMemberGameDataMinService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.RequestParamData;
import com.qiqilm.server.admin.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * 会员注单数据Controller
 *
 * @author 77tv
 * @date 2021-01-29
 */
@Log4j2
@RestController
@RequestMapping( "/member/memberGameDataMin" )
public class MemberGameDataMinController extends BaseController {

	@Resource
	private GamePlatformMapper gamePlatformMapper;
	@Value( "${spring.profiles.active}" )
	private String profile;

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
				String[] dates = Optional.ofNullable(req.getSelectDate()).orElseGet(() ->{
					Date nowTime = new Date();
					String stringDate = "";
					try {
						stringDate = DateFormatUtils.formate(nowTime,DateFormatUtils.SPLIT_PATTERN_DATE);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return new String[]{stringDate+" 00:00:00",stringDate+" 23:59:59"};
				});
				req.setGameStartTime(dates[0]);
				req.setGameEndTime(dates[1]);
				try {
					Optional.ofNullable(memberGameData.getPlatformId()).orElseThrow(()-> new Exception("游戏平台不存在，请检查!"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( memberGameData.getPlatformId() );
				if (gamePlatform != null){
					RequestParamData.requestBBINSportBetRecord(memberGameData,gamePlatform).stream().forEach(element ->{
						//初始化数据
						String account = element.get("UserName").replace("bbin","_").toUpperCase();
						String agentId = account.split("_")[0];
						element.put("UserName",account);
						element.put("agent",gamePlatform.getAgent());
						element.put("platformId",gamePlatform.getId()+"");
						element.put("platformName",gamePlatform.getName());
						try {
							element.put("WagersDate",DateFormatUtils.convertMeidongTOBeijing(element.get("WagersDate")));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						IMemberGameDataMinService.BooleanAgent booleanAgent = () -> agentId.equals(profile);
						if (booleanAgent.getBoolean()){
							int count = 0;
							if (StringUtils.isNotEmpty(memberGameData.getBetState()) && !memberGameData.getBetState().equals("ALL")){
								if (memberGameData.getBetState().equals(element.get("Result"))) count ++;
							}else {
								count ++;
							}
							if (StringUtils.isNotEmpty(memberGameData.getAccount())){
								if (account.equals(memberGameData.getAccount())) count ++;
							}else {
								count ++;
							}
							if (StringUtils.isNotEmpty(memberGameData.getGameId()) ){
								if (memberGameData.getGameId().equals(element.get("WagersID"))) count ++;
							}else {
								count ++;
							}
							element.put("Result", (String) defaultOrderBetState().get(element.get("Result")));
							if (count == 3){
								list.add(element);
							}
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

	/**
	 * 查询会员注单数据列表
	 */
	@AccessLimit(seconds = 5, maxCount = 0)
	@PreAuthorize( "@ss.hasPermi('member:memberGameDataMin:detail')" )
	@GetMapping( "/detail" )
	public AjaxResult detail(MemberGameData memberGameData ) {
		//校验游戏种类
		if (memberGameData == null || memberGameData.getPlatformId() == 0){
			return AjaxResult.error("查询游戏局号失败，请输入正确的查询参数");
		}
		try {
			GamePlatform gamePlatform = gamePlatformMapper.selectGamePlatformById( memberGameData.getPlatformId() );
			if (gamePlatform != null){
				String result = RequestParamData.requestBBINSportBetDetail(memberGameData,gamePlatform);
				log.info(EnumGamePlatform.BBIN_SPORT.getName()+"获取局明细返回结果数据:"+JSON.toJSONString(result));
				return RequestParamData.gameBBINSportDetailDataWrapper(result);
			}
		}catch (Exception e) {
			log.error( "查询游戏局号明细失败，参数:{},错误信息:",JSON.toJSONString(memberGameData),e);
			return AjaxResult.error("查询游戏局号明细失败Account:" + memberGameData.getAccount());
		}
		return AjaxResult.error("游戏未配置，请选择其他游戏!");
	}
}
