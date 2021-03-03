package com.qiqilm.server.admin.service.impl;

import com.qiqilm.server.admin.cache.LiveCacheUtil;
import com.qiqilm.server.admin.cache.SysConfigCacheUtil;
import com.qiqilm.server.admin.domain.LiveHostWageNote;
import com.qiqilm.server.admin.mapper.LiveHostWageNoteMapper;
import com.qiqilm.server.admin.service.ILiveHostWageNoteService;
import com.qiqilm.server.admin.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-27
 */
@Service
public class LiveHostWageNoteServiceImpl implements ILiveHostWageNoteService {
    @Autowired
    private LiveHostWageNoteMapper liveHostWageNoteMapper;
    @Autowired
    private SysConfigCacheUtil     sysConfigCacheUtil;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public LiveHostWageNote selectLiveHostWageNoteById(Long id) {
        return liveHostWageNoteMapper.selectLiveHostWageNoteById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param liveHostWageNote 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<LiveHostWageNote> selectLiveHostWageNoteList(LiveHostWageNote liveHostWageNote) {
        return liveHostWageNoteMapper.selectLiveHostWageNoteList(liveHostWageNote);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param liveHostWageNote 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertLiveHostWageNote(LiveHostWageNote liveHostWageNote) {
        liveHostWageNote.setCreateTime(DateUtils.getNowDate());
        return liveHostWageNoteMapper.insertLiveHostWageNote(liveHostWageNote);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param liveHostWageNote 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateLiveHostWageNote(LiveHostWageNote liveHostWageNote) {
        return liveHostWageNoteMapper.updateLiveHostWageNote(liveHostWageNote);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveHostWageNoteByIds(Long[] ids) {
        return liveHostWageNoteMapper.deleteLiveHostWageNoteByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteLiveHostWageNoteById(Long id) {
        return liveHostWageNoteMapper.deleteLiveHostWageNoteById(id);
    }

    @Override
    public List<LiveHostWageNote> familyPage(LiveHostWageNote dto) {
        BigDecimal ticketCattyRatio = sysConfigCacheUtil.getConfBd("ticket_catty_ratio");
        List<LiveHostWageNote> liveHostWageNotes = liveHostWageNoteMapper.familyPage(dto.getSelectDate()[0] + "-" + dto.getSelectDate()[1],dto );
        for (LiveHostWageNote liveHostWageNote : liveHostWageNotes) {
            if (liveHostWageNote.getAllticket()!=null) {
                BigDecimal allTicket=new BigDecimal(liveHostWageNote.getAllticket());
                //判断是否是散户
                if (liveHostWageNote.getFamilyId()==0  && dto.getSettlementRate()!=null){
                    liveHostWageNote.setAllticketRes(allTicket.multiply(dto.getSettlementRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    liveHostWageNote.setSettlementRate(dto.getSettlementRate());
                }else {
                    liveHostWageNote.setAllticketRes(allTicket.multiply(ticketCattyRatio).setScale(2, BigDecimal.ROUND_HALF_UP));
                    liveHostWageNote.setSettlementRate(ticketCattyRatio);
                }
            }
        }
        return liveHostWageNotes;
    }

    @Override
    public List<LiveHostWageNote> getPage(LiveHostWageNote dto) {
        BigDecimal ticketCattyRatio = sysConfigCacheUtil.getConfBd("ticket_catty_ratio");
        List<LiveHostWageNote> liveHostWageNotes = liveHostWageNoteMapper.selectListMt(dto);
        List<Map<String, Object>> mapLists = liveHostWageNoteMapper.selectFamilyName();
        for (LiveHostWageNote liveHostWageNote : liveHostWageNotes) {
            if (liveHostWageNote.getAllticket()!=null) {
                BigDecimal allTicket=new BigDecimal(liveHostWageNote.getAllticket());
                //判断是否是散户
                if (liveHostWageNote.getFamilyId()==0  && dto.getSettlementRate()!=null){
                    liveHostWageNote.setAllticketRes(allTicket.multiply(dto.getSettlementRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
                    liveHostWageNote.setSettlementRate(dto.getSettlementRate());
                }else {
                    liveHostWageNote.setAllticketRes(allTicket.multiply(ticketCattyRatio).setScale(2, BigDecimal.ROUND_HALF_UP));
                    liveHostWageNote.setSettlementRate(ticketCattyRatio);

                }
            }

            for (Map<String, Object> mapList : mapLists) {
                if(mapList.get("id").equals(liveHostWageNote.getFamilyId())){
                    liveHostWageNote.setFamilyName(mapList.get("name").toString());
                }
            }
        }
        return liveHostWageNotes;
    }
}
