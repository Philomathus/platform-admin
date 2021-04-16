package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.core.vo.LoginUser;
import com.qiqilm.server.admin.domain.LiveUserWithdrawNewlog;
import com.qiqilm.server.admin.enums.EnumLock;
import com.qiqilm.server.admin.mapper.LiveHostWageDayMapper;
import com.qiqilm.server.admin.mapper.LiveUserWithdrawNewlogMapper;
import com.qiqilm.server.admin.service.ILiveUserWithdrawNewlogService;
import com.qiqilm.server.admin.utils.DateFormatUtils;
import com.qiqilm.server.admin.utils.DateUtils;
import com.qiqilm.server.admin.utils.RedisUtil;
import com.qiqilm.server.admin.utils.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 主播提现管理Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-23
 */
@Service
public class LiveUserWithdrawNewlogServiceImpl implements ILiveUserWithdrawNewlogService {
    @Autowired
    private LiveUserWithdrawNewlogMapper liveUserWithdrawNewlogMapper;
    @Autowired
    private TokenService            tokenService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LiveHostWageDayMapper liveHostWageDayMapper;

    /**
     * 查询主播提现管理
     *
     * @param id 主播提现管理ID
     * @return 主播提现管理
     */
    @Override
    public LiveUserWithdrawNewlog selectLiveUserWithdrawNewlogById(String id) {
        return liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(id);
    }

    /**
     * 查询主播提现管理列表
     *
     * @param liveUserWithdrawNewlog 主播提现管理
     * @return 主播提现管理
     */
    @Override
    public List<LiveUserWithdrawNewlog> selectLiveUserWithdrawNewlogList(LiveUserWithdrawNewlog liveUserWithdrawNewlog) {
        String[] searchTime = liveUserWithdrawNewlog.getSearchTime();
        if ( searchTime != null && searchTime.length > 0 ) {
            liveUserWithdrawNewlog.setStartTime(searchTime[ 0 ]);
            liveUserWithdrawNewlog.setEndTime(searchTime[ 1 ]);
        }
        return liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogList(liveUserWithdrawNewlog);
    }

    /**
     * 新增主播提现管理
     *
     * @param liveUserWithdrawNewlog 主播提现管理
     * @return 结果
     */
    @Override
    public int insertLiveUserWithdrawNewlog(LiveUserWithdrawNewlog liveUserWithdrawNewlog) {
        liveUserWithdrawNewlog.setCreateTime(DateUtils.getNowDate());
        return liveUserWithdrawNewlogMapper.insertLiveUserWithdrawNewlog(liveUserWithdrawNewlog);
    }

    /**
     * 修改主播提现管理
     *
     * @param liveUserWithdrawNewlog 主播提现管理
     * @return 结果
     */
    @Override
    public int updateLiveUserWithdrawNewlog(LiveUserWithdrawNewlog liveUserWithdrawNewlog) {
        liveUserWithdrawNewlog.setUpdateTime(DateUtils.getNowDate());
        return liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(liveUserWithdrawNewlog);
    }

    /**
     * 批量删除主播提现管理
     *
     * @param ids 需要删除的主播提现管理ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserWithdrawNewlogByIds(String[] ids) {
        return liveUserWithdrawNewlogMapper.deleteLiveUserWithdrawNewlogByIds(ids);
    }

    /**
     * 删除主播提现管理信息
     *
     * @param id 主播提现管理ID
     * @return 结果
     */
    @Override
    public int deleteLiveUserWithdrawNewlogById(String id) {
        return liveUserWithdrawNewlogMapper.deleteLiveUserWithdrawNewlogById(id);
    }

    @Override
    public AjaxResult unlock(LiveUserWithdrawNewlog req) {
        LiveUserWithdrawNewlog liveUserWithdrawNewlog = liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(req.getId());
        if ( liveUserWithdrawNewlog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        if ( !StringUtils.isEmpty( liveUserWithdrawNewlog.getOpName() ) && !userName.equals( liveUserWithdrawNewlog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + liveUserWithdrawNewlog.getOpName() + "处理" );
        }
        if ( !redisUtil.lock( EnumLock.Anchor, liveUserWithdrawNewlog.getUserId().toString(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }
        LiveUserWithdrawNewlog update = new LiveUserWithdrawNewlog();
        update.setId(liveUserWithdrawNewlog.getId());
        update.setRemark( "取消锁定人：" + userName );
        update.setOpName( "" );
        update.setWstatus(Long.valueOf(3));//审核通过
        update.setUpdateTime( new Date() );
        int i = liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(update);
        return i > 0 ? AjaxResult.success() : AjaxResult.error("解锁订单状态失败");
    }

    @Override
    public AjaxResult refused(LiveUserWithdrawNewlog req) {
        LiveUserWithdrawNewlog liveUserWithdrawNewlog = liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(req.getId());
        if ( liveUserWithdrawNewlog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( liveUserWithdrawNewlog.getWstatus() == 2 ) {
            return AjaxResult.error( "订单重复处理" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        if ( !StringUtils.isEmpty( liveUserWithdrawNewlog.getOpName() ) && !userName.equals( liveUserWithdrawNewlog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + liveUserWithdrawNewlog.getOpName() + "处理" );
        }
        if ( !redisUtil.lock( EnumLock.Anchor, liveUserWithdrawNewlog.getUserId().toString(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }

        LiveUserWithdrawNewlog update = new LiveUserWithdrawNewlog();
        update.setId(liveUserWithdrawNewlog.getId());
        update.setRemark( req.getRemark() );
        update.setWstatus(Long.valueOf(2));//审核不通过
        update.setOpName( "" );
        update.setUpdateTime( new Date() );
        int i = liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(update);
        return i > 0 ? AjaxResult.success() : AjaxResult.error("更新订单拒绝状态失败");
    }

    @Override
    public AjaxResult artificial(LiveUserWithdrawNewlog req) {
        LiveUserWithdrawNewlog liveUserWithdrawNewlog = liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(req.getId());
        if ( liveUserWithdrawNewlog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( liveUserWithdrawNewlog.getWstatus() == 2 ) {
            return AjaxResult.error( "该订单已被拒绝" );
        }
        if ( liveUserWithdrawNewlog.getWstatus() == 4 ) {
            return AjaxResult.error( "订单状态有误，请刷新数据后重试" );
        }
        if ( liveUserWithdrawNewlog.getWstatus() != 3 && 3 < liveUserWithdrawNewlog.getWstatus() ) {
            return AjaxResult.error( "审核流程非法" );
        }
        if ( !redisUtil.lock( EnumLock.Anchor, liveUserWithdrawNewlog.getUserId().toString(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        if ( StringUtils.hasText( liveUserWithdrawNewlog.getOpName() ) && !userName.equals( liveUserWithdrawNewlog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + liveUserWithdrawNewlog.getOpName() + "处理" );
        }

        LiveUserWithdrawNewlog update = new LiveUserWithdrawNewlog();
        update.setId(liveUserWithdrawNewlog.getId());
        update.setWstatus(Long.valueOf(4));//出款
        update.setOpName( userName );
        update.setUpdateTime( new Date() );
        int i = liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(update);
        return i > 0 ? AjaxResult.success() : AjaxResult.error("更新订单出款状态失败");

    }

    @Override
    public AjaxResult recoverAudit(LiveUserWithdrawNewlog req) {
        LiveUserWithdrawNewlog liveUserWithdrawNewlog = liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(req.getId());
        if ( liveUserWithdrawNewlog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( liveUserWithdrawNewlog.getWstatus() != 2 && liveUserWithdrawNewlog.getWstatus() != 3 ) {
            return AjaxResult.error( "只有拒绝和审核通过才能恢复提交申请" );
        }

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        if ( StringUtils.hasText( liveUserWithdrawNewlog.getOpName() ) && !userName.equals( liveUserWithdrawNewlog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + liveUserWithdrawNewlog.getOpName() + "处理" );
        }
        if ( !redisUtil.lock( EnumLock.Anchor, liveUserWithdrawNewlog.getUserId().toString(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }

        LiveUserWithdrawNewlog update = new LiveUserWithdrawNewlog();
        update.setId( liveUserWithdrawNewlog.getId() );
        update.setWstatus( Long.valueOf(1) );
        update.setOpName( userName );
        update.setUpdateTime( new Date() );
        int i = liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog( update );
        return i > 0 ? AjaxResult.success() : AjaxResult.error("恢复订单状态失败");
    }

    @Override
    public AjaxResult finalAudit(LiveUserWithdrawNewlog req) {
        LiveUserWithdrawNewlog liveUserWithdrawNewlog = liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(req.getId());
        if ( liveUserWithdrawNewlog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( liveUserWithdrawNewlog.getWstatus() != 1 ) {
            return AjaxResult.error( "订单状态有误，请刷新数据后重试" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        if ( StringUtils.hasText( liveUserWithdrawNewlog.getOpName() ) && !userName.equals( liveUserWithdrawNewlog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + liveUserWithdrawNewlog.getOpName() + "处理" );
        }
        if ( !redisUtil.lock( EnumLock.Anchor, liveUserWithdrawNewlog.getUserId().toString(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }
        LiveUserWithdrawNewlog update = new LiveUserWithdrawNewlog();
        update.setId(liveUserWithdrawNewlog.getId());
        update.setWstatus(Long.valueOf(3));//审核通过
        update.setOpName( "" );
        update.setRemark("");
        update.setUpdateTime( new Date() );
        int i = liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(update);
        return i > 0 ? AjaxResult.success() : AjaxResult.error("更新订单审核通过状态失败");

    }

    @Override
    public AjaxResult getTotal(LiveUserWithdrawNewlog req) {
        String[] searchTime = req.getSearchTime();
        if ( searchTime != null && searchTime.length > 0 ) {
            req.setStartTime(searchTime[ 0 ]);
            req.setEndTime(searchTime[ 1 ]);
        }
        return AjaxResult.success( liveUserWithdrawNewlogMapper.getTotal( req ) );
    }

    @Override
    public AjaxResult withdrawSucc(LiveUserWithdrawNewlog req) {
        LiveUserWithdrawNewlog liveUserWithdrawNewlog = liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(req.getId());
        if ( liveUserWithdrawNewlog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( liveUserWithdrawNewlog.getWstatus()!=4 ) {
            return AjaxResult.error( "该订单未提交出款" );
        }
        if ( liveUserWithdrawNewlog.getWstatus() == 5 ) {
            return AjaxResult.error( "订单状态有误，请刷新数据后重试" );
        }
        if ( !redisUtil.lock( EnumLock.Anchor, liveUserWithdrawNewlog.getUserId().toString(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }

        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();
        if ( StringUtils.hasText( liveUserWithdrawNewlog.getOpName() ) && !userName.equals( liveUserWithdrawNewlog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + liveUserWithdrawNewlog.getOpName() + "处理" );
        }

        LiveUserWithdrawNewlog update = new LiveUserWithdrawNewlog();
        update.setId(liveUserWithdrawNewlog.getId());
        update.setWstatus(Long.valueOf(5));//出款
        update.setOpName( userName );
        update.setUpdateTime( new Date() );
        int i = liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(update);
        return i > 0 ? AjaxResult.success() : AjaxResult.error("更新订单出款成功状态失败");
    }

    @Override
    public AjaxResult withdrawRefused(LiveUserWithdrawNewlog req) {
        LiveUserWithdrawNewlog liveUserWithdrawNewlog = liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(req.getId());
        if ( liveUserWithdrawNewlog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        if ( liveUserWithdrawNewlog.getWstatus() == 1 ) {
            return AjaxResult.error( "订单重复处理" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        if ( !StringUtils.isEmpty( liveUserWithdrawNewlog.getOpName() ) && !userName.equals( liveUserWithdrawNewlog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + liveUserWithdrawNewlog.getOpName() + "处理" );
        }
        if ( !redisUtil.lock( EnumLock.Anchor, liveUserWithdrawNewlog.getUserId().toString(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }
        LiveUserWithdrawNewlog update = new LiveUserWithdrawNewlog();
        update.setId(liveUserWithdrawNewlog.getId());
        update.setRemark( req.getRemark() );
        update.setWstatus(Long.valueOf(1));//审核不通过
        update.setOpName( userName );
        update.setUpdateTime( new Date() );
        int i = liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(update);
        return i > 0 ? AjaxResult.success() : AjaxResult.error("更新订单拒绝出款状态失败");
    }

    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult updateOrder(LiveUserWithdrawNewlog req) {
        LiveUserWithdrawNewlog liveUserWithdrawNewlog = liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(req.getId());
        if ( liveUserWithdrawNewlog == null ) {
            return AjaxResult.error( "订单不存在" );
        }
        LoginUser loginUser = tokenService.getLoginUser( ServletUtil.getHttpServletRequest() );
        String    userName  = loginUser.getUser().getUserName();

        if ( !StringUtils.isEmpty( liveUserWithdrawNewlog.getOpName() ) && !userName.equals( liveUserWithdrawNewlog.getOpName() ) ) {
            return AjaxResult.error( "该订单只能由" + liveUserWithdrawNewlog.getOpName() + "处理" );
        }
        if ( !redisUtil.lock( EnumLock.Anchor, liveUserWithdrawNewlog.getUserId().toString(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }
        //判断事家族还是成员
        Long type = liveUserWithdrawNewlog.getType();
        Date createTime = liveUserWithdrawNewlog.getCreateTime();
        String time = DateFormatUtils.formate( createTime, "yyyy-MM-dd" );
        if (type==1){
            //1家族
            List<String> strings = liveHostWageDayMapper.getliveHostWageDay(time, liveUserWithdrawNewlog.getFamilyId());
            //调存贮过程
            for (String hostid:strings){
                liveUserWithdrawNewlogMapper.calldataProrepLiveTixianorder(time,hostid);
            }
        }else {
            //2个人
            liveUserWithdrawNewlogMapper.calldataProrepLiveTixianorder(time,liveUserWithdrawNewlog.getUserId().toString());
        }
        LiveUserWithdrawNewlog update = new LiveUserWithdrawNewlog();
        update.setId(liveUserWithdrawNewlog.getId());
        update.setUpdateTime( new Date() );
        int i = liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(update);
        return i > 0 ? AjaxResult.success() : AjaxResult.error("重置订单状态失败");

    }

    @Override
    @Transactional( rollbackFor = Exception.class )
    public AjaxResult fixOrder(String[] ids) {
        List<LiveUserWithdrawNewlog> lists=new ArrayList<>();
        for (int i = 0; i < ids.length; i++){
            String id = ids[i];
            LiveUserWithdrawNewlog liveUserWithdrawNewlog = liveUserWithdrawNewlogMapper.selectLiveUserWithdrawNewlogById(id);
            if (liveUserWithdrawNewlog.getWstatus()!=1){
                return AjaxResult.error(liveUserWithdrawNewlog.getOrderNo()+"状态有误不能合并订单");
            }
            lists.add(liveUserWithdrawNewlog);
        }
        if ( !redisUtil.lock( EnumLock.Anchor, lists.get(0).getUserId().toString(), "1", 5 ) ) {
            return AjaxResult.error( "请勿重复提交" );
        }
        //时间倒序
        Collections.sort(lists, (a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        BigDecimal sumMoney = lists.stream()
                // 将user对象的age取出来map为Bigdecimal
                .map(LiveUserWithdrawNewlog::getWithdrawMoney)
                // 使用reduce()聚合函数,实现累加器
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        for (LiveUserWithdrawNewlog log:lists){
            if (!lists.get(0).getBankAccount().equals(log.getBankAccount())){
                return AjaxResult.error("不同的银行账号不能合并订单");
            }
            if (!lists.get(0).getUserId().equals(log.getUserId())){
                return AjaxResult.error("不同的账号不能合并订单");
            }
            if (lists.get(0).getId().equals(log.getId())){
                LiveUserWithdrawNewlog liveUserWithdrawNewlog=new LiveUserWithdrawNewlog();
                liveUserWithdrawNewlog.setWithdrawMoney(sumMoney);
                liveUserWithdrawNewlog.setId(log.getId());
                liveUserWithdrawNewlog.setRemark("合并订单，请勿重置订单");
                liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(liveUserWithdrawNewlog);
            }else {
                LiveUserWithdrawNewlog WithdrawNewlog=new LiveUserWithdrawNewlog();
                WithdrawNewlog.setId(log.getId());
                WithdrawNewlog.setWstatus(Long.valueOf(6));//订单合并已销毁
                WithdrawNewlog.setRemark( "订单合并到：" + lists.get(0).getOrderNo());
                liveUserWithdrawNewlogMapper.updateLiveUserWithdrawNewlog(WithdrawNewlog);
            }
        }
        return AjaxResult.success();
    }
}
