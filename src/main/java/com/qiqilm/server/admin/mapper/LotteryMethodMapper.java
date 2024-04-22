package com.qiqilm.server.admin.mapper;

import com.qiqilm.server.admin.domain.LotteryMethod;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 彩票种类Mapper接口
 *
 * @author 77tv
 * @date 2021-02-23
 */
public interface LotteryMethodMapper {

    /**
     * 查询彩票种类列表
     *
     * @param lotteryMethod 彩票种类
     *
     * @return 彩票种类集合
     */
    public List<LotteryMethod> selectLotteryMethodList( LotteryMethod lotteryMethod );

    List<LotteryMethod> selectByBatchId( @Param( "array" ) Collection<String> methodIds );
}
