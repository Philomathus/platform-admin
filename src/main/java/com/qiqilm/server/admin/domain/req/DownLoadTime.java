package com.qiqilm.server.admin.domain.req;

import com.qiqilm.server.admin.core.vo.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author axing
 * @version 1.0
 * @date 2021/5/26/026 14:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class DownLoadTime extends BaseEntity {
    private String[] downLoadDate = new String[2];
    public static int downLoadLimit;
    @Value("${downLoadLimit}")
    public void setDownLoadLimit(int downLoadLimit) {
        DownLoadTime.downLoadLimit = downLoadLimit;
    }
}
