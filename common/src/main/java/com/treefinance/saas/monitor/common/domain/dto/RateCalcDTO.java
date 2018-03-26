package com.treefinance.saas.monitor.common.domain.dto;

import java.math.BigDecimal;

import static java.math.BigDecimal.*;

/**
 * 在电商、邮箱、运营商的监控中许多参数都是一样的、比如 创建任务数、开始登陆数、登录成功数 etc.
 * 并且关于这方面计算的重复代码很多，抽象一个统一的计算类。便于后续的重构
 *
 * @author chengtong
 * @date 18/3/15 11:04
 */
public class RateCalcDTO {

    private Integer userCount = 0, taskCount = 0, entryCount = 0, confirmMobileCount = 0,
            startLoginCount = 0, loginSuccessCount = 0, crawlSuccessCount = 0,
            processSuccessCount = 0, callbackSuccessCount = 0;

    private BigDecimal previousLoginConversionRateCount = ZERO;
    private BigDecimal previousLoginSuccessRateCount = ZERO;
    private BigDecimal previousCrawlSuccessRateCount = ZERO;
    private BigDecimal previousProcessSuccessRateCount = ZERO;
    private BigDecimal previousCallbackSuccessRateCount = ZERO;
    private BigDecimal previousWholeConversionRateCount = ZERO;

    public void addUserCount(Integer target){
        this.userCount += target;
    }
    public void addTaskCount(Integer target){
        this.taskCount += target;
    }
    public void addEntryCount(Integer target){
        this.entryCount += target;
    }
    public void addConfirmMobileCount(Integer target){
        this.confirmMobileCount += target;
    }
    public void addStartLoginCount(Integer target){
        this.startLoginCount += target;
    }
    public void addLoginSuccessCount(Integer target){
        this.loginSuccessCount += target;
    }
    public void addCrawSuccessCount(Integer target){
        this.crawlSuccessCount += target;
    }
    public void addProcessSuccessCount(Integer target){
        this.processSuccessCount += target;
    }
    public void addCallbackSuccessCount(Integer target){
        this.callbackSuccessCount += target;
    }



    public void addLoginConvert(BigDecimal target){
        this.setPreviousLoginConversionRateCount(this.getPreviousLoginConversionRateCount().add(target));
    }
    public void addLoginSucc(BigDecimal target){
        this.setPreviousLoginSuccessRateCount(this.getPreviousLoginSuccessRateCount().add(target));
    }
    public void addCrawSucc(BigDecimal target){
        this.setPreviousCrawlSuccessRateCount(this.getPreviousCrawlSuccessRateCount().add(target));
    }
    public void addProcessSucc(BigDecimal target){
        this.setPreviousProcessSuccessRateCount(this.getPreviousProcessSuccessRateCount().add(target));
    }
    public void addCallBackSucc(BigDecimal target){
        this.setPreviousCallbackSuccessRateCount(this.getPreviousCallbackSuccessRateCount().add(target));
    }
    public void addWholeConversion(BigDecimal target){
        this.setPreviousWholeConversionRateCount(this.getPreviousWholeConversionRateCount().add(target));
    }







    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public Integer getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(Integer entryCount) {
        this.entryCount = entryCount;
    }

    public Integer getConfirmMobileCount() {
        return confirmMobileCount;
    }

    public void setConfirmMobileCount(Integer confirmMobileCount) {
        this.confirmMobileCount = confirmMobileCount;
    }

    public Integer getStartLoginCount() {
        return startLoginCount;
    }

    public void setStartLoginCount(Integer startLoginCount) {
        this.startLoginCount = startLoginCount;
    }

    public Integer getLoginSuccessCount() {
        return loginSuccessCount;
    }

    public void setLoginSuccessCount(Integer loginSuccessCount) {
        this.loginSuccessCount = loginSuccessCount;
    }

    public Integer getCrawlSuccessCount() {
        return crawlSuccessCount;
    }

    public void setCrawlSuccessCount(Integer crawlSuccessCount) {
        this.crawlSuccessCount = crawlSuccessCount;
    }

    public Integer getProcessSuccessCount() {
        return processSuccessCount;
    }

    public void setProcessSuccessCount(Integer processSuccessCount) {
        this.processSuccessCount = processSuccessCount;
    }

    public Integer getCallbackSuccessCount() {
        return callbackSuccessCount;
    }

    public void setCallbackSuccessCount(Integer callbackSuccessCount) {
        this.callbackSuccessCount = callbackSuccessCount;
    }

    public BigDecimal getPreviousLoginConversionRateCount() {
        return previousLoginConversionRateCount;
    }

    public void setPreviousLoginConversionRateCount(BigDecimal previousLoginConversionRateCount) {
        this.previousLoginConversionRateCount = previousLoginConversionRateCount;
    }

    public BigDecimal getPreviousLoginSuccessRateCount() {
        return previousLoginSuccessRateCount;
    }

    public void setPreviousLoginSuccessRateCount(BigDecimal previousLoginSuccessRateCount) {
        this.previousLoginSuccessRateCount = previousLoginSuccessRateCount;
    }

    public BigDecimal getPreviousCrawlSuccessRateCount() {
        return previousCrawlSuccessRateCount;
    }

    public void setPreviousCrawlSuccessRateCount(BigDecimal previousCrawlSuccessRateCount) {
        this.previousCrawlSuccessRateCount = previousCrawlSuccessRateCount;
    }

    public BigDecimal getPreviousProcessSuccessRateCount() {
        return previousProcessSuccessRateCount;
    }

    public void setPreviousProcessSuccessRateCount(BigDecimal previousProcessSuccessRateCount) {
        this.previousProcessSuccessRateCount = previousProcessSuccessRateCount;
    }

    public BigDecimal getPreviousCallbackSuccessRateCount() {
        return previousCallbackSuccessRateCount;
    }

    public void setPreviousCallbackSuccessRateCount(BigDecimal previousCallbackSuccessRateCount) {
        this.previousCallbackSuccessRateCount = previousCallbackSuccessRateCount;
    }

    public BigDecimal getPreviousWholeConversionRateCount() {
        return previousWholeConversionRateCount;
    }

    public void setPreviousWholeConversionRateCount(BigDecimal previousWholeConversionRateCount) {
        this.previousWholeConversionRateCount = previousWholeConversionRateCount;
    }
}
