package com.qiqilm.server.admin.service.impl;

import java.time.LocalDate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qiqilm.server.admin.mapper.MemberOnlineMapper;
import com.qiqilm.server.admin.domain.MemberOnline;
import com.qiqilm.server.admin.service.IMemberOnlineService;

import javax.annotation.Resource;

/**
 * 在线会员列表Service业务层处理
 *
 * @author 77tv
 * @date 2021-03-22
 */
@Service
public class MemberOnlineServiceImpl implements IMemberOnlineService {
    @Resource
    private MemberOnlineMapper memberOnlineMapper;

    /**
     * 查询在线会员列表列表
     *
     * @param memberOnline 在线会员列表
     * @return 在线会员列表
     */
    @Override
    public List<MemberOnline> selectMemberOnlineList(MemberOnline memberOnline) {
        long now_time=System.currentTimeMillis()/1000 - 300;
        memberOnline.setOnlineTime(now_time);
        memberOnline.setTableLast(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        //判斷是否在零點後5分鐘
        long zero=System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
        long nowTime = System.currentTimeMillis();
        if(nowTime - zero < 300000){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            String systemNowDate = sdf.format(calendar.getTime());
            String tableLastTwo = systemNowDate.replaceAll("-","");
            memberOnline.setTableLastTwo(tableLastTwo);
            memberOnlineMapper.selectMemberOnlineListTwo(memberOnline);
        }
        return memberOnlineMapper.selectMemberOnlineList(memberOnline);
    }

    @Override
    public MemberOnline selectMemberOnlineListCountTotal() {
        MemberOnline memberOnline =new MemberOnline();
        long now_time=System.currentTimeMillis()/1000 - 300;
        memberOnline.setOnlineTime(now_time);
        memberOnline.setTableLast(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        MemberOnline memberOnline1 = memberOnlineMapper.selectMemberOnlineListCountTotal(memberOnline);

        //判斷是否在零點後5分鐘
        long zero=System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
        long nowTime = System.currentTimeMillis();
        if(nowTime - zero < 300000){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            String systemNowDate = sdf.format(calendar.getTime());
            String tableLastTwo = systemNowDate.replaceAll("-","");
            memberOnline.setTableLastTwo(tableLastTwo);
            MemberOnline memberOnline2 = memberOnlineMapper.selectMemberOnlineListCountTotalTwo(memberOnline);
            memberOnline1.setTotal(memberOnline1.getTotal() + memberOnline2.getTotal());
        }

        return memberOnline1;
    }

    @Override
    public void cutTableOnline(int num) {
        LocalDate l = LocalDate.now();
        String day  ;
        for(int i=0;i<num;i++){
            day = l.plusDays(i).toString().replace("-","");
            memberOnlineMapper.cutTableOnline("member_online".concat(day));
        }


        for(int i=5;i<num+5;i++){
            day = l.plusDays(-i).toString().replace("-","");
            memberOnlineMapper.dropTableOnline("member_online".concat(day));
        }
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String systemNowDate = sdf.format(calendar.getTime());
        String tableLastTwo = systemNowDate.replaceAll("-","");
        System.out.println(tableLastTwo);
    }
}
