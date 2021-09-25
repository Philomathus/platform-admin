package com.qiqilm.server.admin.controller;

import java.util.List;

import com.qiqilm.server.admin.service.impl.TokenService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.domain.PayAgentCard;
import com.qiqilm.server.admin.service.IPayAgentCardService;
import com.qiqilm.server.admin.utils.ExportExcelUtil;
import com.qiqilm.server.admin.core.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 代充人银行卡列表Controller
 *
 * @author 77tv
 * @date 2021-09-24
 */
@RestController
@RequestMapping("/admin/payAgentCard")
public class PayAgentCardController extends BaseController {
    @Autowired
    private IPayAgentCardService payAgentCardService;
    @Autowired
    private TokenService tokenService;
    /**
     * 查询代充人银行卡列表
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentCard:list')")
    @GetMapping("/list")
    public TableDataInfo list(PayAgentCard payAgentCard) {
        startPage();
        List<PayAgentCard> list = payAgentCardService.selectPayAgentCardList(payAgentCard);
        return getDataTable(list);
    }

    /**
     * 导出代充人银行卡列表
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentCard:export')")
    @Log(title = "代充人银行卡列表", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(PayAgentCard payAgentCard, HttpServletResponse response) {
        List<PayAgentCard> list = payAgentCardService.selectPayAgentCardList(payAgentCard);
        ExportExcelUtil.exportExcel(list, "代充人银行卡列", "代充人银行卡列表", PayAgentCard.class, response);
    }

    /**
     * 获取代充人银行卡详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentCard:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(payAgentCardService.selectPayAgentCardById(id));
    }

    /**
     * 新增代充人银行卡列表
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentCard:add')")
    @Log(title = "代充人银行卡列表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PayAgentCard payAgentCard) {
        return toAjax(payAgentCardService.insertPayAgentCard(payAgentCard));
    }

    /**
     * 修改代充人银行卡列表
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentCard:edit')")
    @Log(title = "代充人银行卡列表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PayAgentCard payAgentCard) {
        return toAjax(payAgentCardService.updatePayAgentCard(payAgentCard));
    }

    /**
     * 删除代充人银行卡列表
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentCard:remove')")
    @Log(title = "代充人银行卡列表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(payAgentCardService.deletePayAgentCardByIds(ids));
    }

    /**
     * 支付状态修改
     */
    @PreAuthorize("@ss.hasPermi('pay:payAgentCard:edit')")
    @Log(title = "支付类型", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody PayAgentCard payAgentCard) {
        return toAjax(payAgentCardService.updatePayAgentCard(payAgentCard));
    }

}
