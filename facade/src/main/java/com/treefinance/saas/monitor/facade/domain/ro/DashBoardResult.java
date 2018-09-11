package com.treefinance.saas.monitor.facade.domain.ro;

import com.treefinance.saas.monitor.facade.domain.base.BaseRO;
import lombok.Data;

import java.util.List;

/**
 * @author chengtong
 * @date 18/9/11 15:33
 */
@Data
public class DashBoardResult extends BaseRO {

    private AppTaskStatResult appTaskStatResult;

    private WholeConversionResult wholeConversionResult;

    private List<String> operators;

    private Integer count;

}
