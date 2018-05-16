package com.treefinance.saas.monitor.common.domain.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author chengtong
 * @date 18/5/11 14:05
 * <p>
 * 任务成功率预警 的 比较用DTO
 */
public class TaskSuccRateCompareDTO {

    /**
     * 数据类型：0-合计，1:银行，2：电商，3:邮箱，4:运营商
     */
    private Byte dataType;

    /**
     * 访问用户数
     */
    private Integer userCount = 0;

    /**
     * 访问总数
     */
    private Integer totalCount = 0;

    /**
     * 访问成功数
     */
    private Integer successCount=0;

    /**
     * 访问失败数
     */
    private Integer failCount=0;

    /**
     * 访问取消数
     */
    private Integer cancelCount=0;

    private BigDecimal threshold;

    private String thresholdDecs;

    /**
     * 访问成功率
     */
    private BigDecimal successRate = null;

    /**
     * 访问失败率
     */
    private BigDecimal failRate;

    private String evn;

    /**当前的平均值*/
    private BigDecimal averSuccRate;

    /**
     * 转化率
     */
    private BigDecimal conversionRate;

    /**参与平均数计算的天数*/
    private int days;


    public void add(SaasStatAccessDTO saasStatAccessDTO) {
        this.totalCount +=(saasStatAccessDTO.getTotalCount() == null?0:saasStatAccessDTO
                .getTotalCount());
        this.userCount += saasStatAccessDTO.getUserCount() == null?0:saasStatAccessDTO.getUserCount();
        this.failCount += saasStatAccessDTO.getFailCount() == null?0:saasStatAccessDTO.getFailCount();
        this.cancelCount += saasStatAccessDTO.getCancelCount()== null?0:saasStatAccessDTO.getCancelCount();
        this.successCount += saasStatAccessDTO.getSuccessCount()==null?0:saasStatAccessDTO.getSuccessCount();
        this.dataType = saasStatAccessDTO.getDataType();
        days++;
    }

    public BigDecimal getSuccessRate() {

        if (successRate != null){
            return successRate;
        }
        if (totalCount == 0){
            successRate = BigDecimal.ZERO;
        }else {
            successRate =  new BigDecimal(successCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount),2,
                    RoundingMode
                            .HALF_UP);
        }

        return successRate;

    }

    public BigDecimal getFailRate() {
        return failRate;
    }

    public BigDecimal getConversionRate() {

        if (conversionRate != null){
            return conversionRate;
        }
        if (totalCount == 0){
            conversionRate = BigDecimal.ZERO;
        }else {
            conversionRate =  new BigDecimal(successCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount),2,
                    RoundingMode
                            .HALF_UP);
        }
        return conversionRate;
    }

    public Byte getDataType() {
        return dataType;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public Integer getCancelCount() {
        return cancelCount;
    }

    public int getDays() {
        return days;
    }

    @Override
    public String toString() {
        return "TaskSuccRateCompareDTO{" +
                "dataType=" + dataType +
                ", userCount=" + userCount +
                ", totalCount=" + totalCount +
                ", successCount=" + successCount +
                ", failCount=" + failCount +
                ", cancelCount=" + cancelCount +
                ", successRate=" + successRate +
                ", failRate=" + failRate +
                ", conversionRate=" + conversionRate +
                ", days=" + days +
                '}';
    }

    public BigDecimal getAverSuccRate() {
        return averSuccRate;
    }

    public void setAverSuccRate(BigDecimal averSuccRate) {
        this.averSuccRate = averSuccRate;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }

    public String getThresholdDecs() {
        return thresholdDecs;
    }

    public void setThresholdDecs(String thresholdDecs) {
        this.thresholdDecs = thresholdDecs;
    }

    public String getEvn() {
        return evn;
    }

    public void setEvn(String evn) {
        this.evn = evn;
    }
}
