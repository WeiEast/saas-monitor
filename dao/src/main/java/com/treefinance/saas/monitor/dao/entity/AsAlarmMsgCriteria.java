package com.treefinance.saas.monitor.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AsAlarmMsgCriteria {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    protected int limit;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    protected int offset;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public AsAlarmMsgCriteria() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public int getLimit() {
        return limit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public int getOffset() {
        return offset;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andAlarmIdIsNull() {
            addCriterion("alarmId is null");
            return (Criteria) this;
        }

        public Criteria andAlarmIdIsNotNull() {
            addCriterion("alarmId is not null");
            return (Criteria) this;
        }

        public Criteria andAlarmIdEqualTo(Long value) {
            addCriterion("alarmId =", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdNotEqualTo(Long value) {
            addCriterion("alarmId <>", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdGreaterThan(Long value) {
            addCriterion("alarmId >", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdGreaterThanOrEqualTo(Long value) {
            addCriterion("alarmId >=", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdLessThan(Long value) {
            addCriterion("alarmId <", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdLessThanOrEqualTo(Long value) {
            addCriterion("alarmId <=", value, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdIn(List<Long> values) {
            addCriterion("alarmId in", values, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdNotIn(List<Long> values) {
            addCriterion("alarmId not in", values, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdBetween(Long value1, Long value2) {
            addCriterion("alarmId between", value1, value2, "alarmId");
            return (Criteria) this;
        }

        public Criteria andAlarmIdNotBetween(Long value1, Long value2) {
            addCriterion("alarmId not between", value1, value2, "alarmId");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateIsNull() {
            addCriterion("titleTemplate is null");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateIsNotNull() {
            addCriterion("titleTemplate is not null");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateEqualTo(String value) {
            addCriterion("titleTemplate =", value, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateNotEqualTo(String value) {
            addCriterion("titleTemplate <>", value, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateGreaterThan(String value) {
            addCriterion("titleTemplate >", value, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateGreaterThanOrEqualTo(String value) {
            addCriterion("titleTemplate >=", value, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateLessThan(String value) {
            addCriterion("titleTemplate <", value, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateLessThanOrEqualTo(String value) {
            addCriterion("titleTemplate <=", value, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateLike(String value) {
            addCriterion("titleTemplate like", value, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateNotLike(String value) {
            addCriterion("titleTemplate not like", value, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateIn(List<String> values) {
            addCriterion("titleTemplate in", values, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateNotIn(List<String> values) {
            addCriterion("titleTemplate not in", values, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateBetween(String value1, String value2) {
            addCriterion("titleTemplate between", value1, value2, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andTitleTemplateNotBetween(String value1, String value2) {
            addCriterion("titleTemplate not between", value1, value2, "titleTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateIsNull() {
            addCriterion("bodyTemplate is null");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateIsNotNull() {
            addCriterion("bodyTemplate is not null");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateEqualTo(String value) {
            addCriterion("bodyTemplate =", value, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateNotEqualTo(String value) {
            addCriterion("bodyTemplate <>", value, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateGreaterThan(String value) {
            addCriterion("bodyTemplate >", value, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateGreaterThanOrEqualTo(String value) {
            addCriterion("bodyTemplate >=", value, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateLessThan(String value) {
            addCriterion("bodyTemplate <", value, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateLessThanOrEqualTo(String value) {
            addCriterion("bodyTemplate <=", value, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateLike(String value) {
            addCriterion("bodyTemplate like", value, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateNotLike(String value) {
            addCriterion("bodyTemplate not like", value, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateIn(List<String> values) {
            addCriterion("bodyTemplate in", values, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateNotIn(List<String> values) {
            addCriterion("bodyTemplate not in", values, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateBetween(String value1, String value2) {
            addCriterion("bodyTemplate between", value1, value2, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andBodyTemplateNotBetween(String value1, String value2) {
            addCriterion("bodyTemplate not between", value1, value2, "bodyTemplate");
            return (Criteria) this;
        }

        public Criteria andMsgTypeIsNull() {
            addCriterion("msgType is null");
            return (Criteria) this;
        }

        public Criteria andMsgTypeIsNotNull() {
            addCriterion("msgType is not null");
            return (Criteria) this;
        }

        public Criteria andMsgTypeEqualTo(Byte value) {
            addCriterion("msgType =", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeNotEqualTo(Byte value) {
            addCriterion("msgType <>", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeGreaterThan(Byte value) {
            addCriterion("msgType >", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("msgType >=", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeLessThan(Byte value) {
            addCriterion("msgType <", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeLessThanOrEqualTo(Byte value) {
            addCriterion("msgType <=", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeIn(List<Byte> values) {
            addCriterion("msgType in", values, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeNotIn(List<Byte> values) {
            addCriterion("msgType not in", values, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeBetween(Byte value1, Byte value2) {
            addCriterion("msgType between", value1, value2, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("msgType not between", value1, value2, "msgType");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeIsNull() {
            addCriterion("analysisType is null");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeIsNotNull() {
            addCriterion("analysisType is not null");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeEqualTo(Byte value) {
            addCriterion("analysisType =", value, "analysisType");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeNotEqualTo(Byte value) {
            addCriterion("analysisType <>", value, "analysisType");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeGreaterThan(Byte value) {
            addCriterion("analysisType >", value, "analysisType");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("analysisType >=", value, "analysisType");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeLessThan(Byte value) {
            addCriterion("analysisType <", value, "analysisType");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeLessThanOrEqualTo(Byte value) {
            addCriterion("analysisType <=", value, "analysisType");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeIn(List<Byte> values) {
            addCriterion("analysisType in", values, "analysisType");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeNotIn(List<Byte> values) {
            addCriterion("analysisType not in", values, "analysisType");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeBetween(Byte value1, Byte value2) {
            addCriterion("analysisType between", value1, value2, "analysisType");
            return (Criteria) this;
        }

        public Criteria andAnalysisTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("analysisType not between", value1, value2, "analysisType");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelIsNull() {
            addCriterion("notifyChannel is null");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelIsNotNull() {
            addCriterion("notifyChannel is not null");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelEqualTo(String value) {
            addCriterion("notifyChannel =", value, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelNotEqualTo(String value) {
            addCriterion("notifyChannel <>", value, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelGreaterThan(String value) {
            addCriterion("notifyChannel >", value, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelGreaterThanOrEqualTo(String value) {
            addCriterion("notifyChannel >=", value, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelLessThan(String value) {
            addCriterion("notifyChannel <", value, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelLessThanOrEqualTo(String value) {
            addCriterion("notifyChannel <=", value, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelLike(String value) {
            addCriterion("notifyChannel like", value, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelNotLike(String value) {
            addCriterion("notifyChannel not like", value, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelIn(List<String> values) {
            addCriterion("notifyChannel in", values, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelNotIn(List<String> values) {
            addCriterion("notifyChannel not in", values, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelBetween(String value1, String value2) {
            addCriterion("notifyChannel between", value1, value2, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andNotifyChannelNotBetween(String value1, String value2) {
            addCriterion("notifyChannel not between", value1, value2, "notifyChannel");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("createTime is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("createTime is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("createTime =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("createTime <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("createTime >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("createTime >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("createTime <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("createTime <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("createTime in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("createTime not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("createTime between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("createTime not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeIsNull() {
            addCriterion("lastUpdateTime is null");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeIsNotNull() {
            addCriterion("lastUpdateTime is not null");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeEqualTo(Date value) {
            addCriterion("lastUpdateTime =", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeNotEqualTo(Date value) {
            addCriterion("lastUpdateTime <>", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeGreaterThan(Date value) {
            addCriterion("lastUpdateTime >", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("lastUpdateTime >=", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeLessThan(Date value) {
            addCriterion("lastUpdateTime <", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("lastUpdateTime <=", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeIn(List<Date> values) {
            addCriterion("lastUpdateTime in", values, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeNotIn(List<Date> values) {
            addCriterion("lastUpdateTime not in", values, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("lastUpdateTime between", value1, value2, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("lastUpdateTime not between", value1, value2, "lastUpdateTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table as_alarm_msg
     *
     * @mbg.generated do_not_delete_during_merge Wed Aug 22 16:38:46 CST 2018
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table as_alarm_msg
     *
     * @mbg.generated Wed Aug 22 16:38:46 CST 2018
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}