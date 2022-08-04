package com.qiqilm.server.admin.controller.live;

import com.qiqilm.server.admin.annotation.Log;
import com.qiqilm.server.admin.core.controller.BaseController;
import com.qiqilm.server.admin.core.page.TableDataInfo;
import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.WheelPool;
import com.qiqilm.server.admin.enums.BusinessType;
import com.qiqilm.server.admin.service.WheelPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【wheel_pool】Controller
 *
 * @author Rajesh
 * @date 2022-07-29
 */

@RestController
@RequestMapping("/admin/wheelPool")
public class WheelPoolController  extends BaseController {

    @Autowired
    private WheelPoolService wheelPoolService;


    /**
     * 查询 wheel pool信息列表 -  query to get all wheel pool
     */
    @PreAuthorize( "@ss.hasPermi('admin:wheelPool:list')")
    @GetMapping("/list")
    public TableDataInfo wheelPools(WheelPool wheelPool){
        startPage();
        List<WheelPool> wheelPoolList =  wheelPoolService.selectAllWheelPool(wheelPool);
        for (WheelPool wheelPoolTest : wheelPoolList){
            System.out.println(wheelPoolTest.getName());
        }
        return getDataTable( wheelPoolList );
    }

    /**
     * 查询通过id获取数据 -  query to get data by id
     */
    @GetMapping("/{id}")
    @PreAuthorize( "@ss.hasPermi('admin:wheelPool:query')" )
    public AjaxResult findById(@PathVariable("id") Long id){
        WheelPool wheelPool =  wheelPoolService.findWheelPoolById(id);
        return AjaxResult.success(wheelPool);
    }

    /**
     * 更新轮池 update wheel pool
     */
    @PreAuthorize( "@ss.hasPermi('admin:wheelPool:edit')" )
    @Log( title = "漩涡", businessType = BusinessType.UPDATE )
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody WheelPool wheelPool ) {
        return toAjax(wheelPoolService.updateWheelPool(wheelPool));
    }

    

}
