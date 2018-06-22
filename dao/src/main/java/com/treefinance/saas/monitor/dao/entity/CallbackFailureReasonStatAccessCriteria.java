package com.treefinance.saas.monitor.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallbackFailureReasonStatAccessCriteria {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    protected int limit;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    protected int offset;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public CallbackFailureReasonStatAccessCriteria() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public int getLimit() {
        return limit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public int getOffset() {
        return offset;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
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
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
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

        public Criteria andAppIdIsNull() {
            addCriterion("appId is null");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNotNull() {
            addCriterion("appId is not null");
            return (Criteria) this;
        }

        public Criteria andAppIdEqualTo(String value) {
            addCriterion("appId =", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotEqualTo(String value) {
            addCriterion("appId <>", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThan(String value) {
            addCriterion("appId >", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThanOrEqualTo(String value) {
            addCriterion("appId >=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThan(String value) {
            addCriterion("appId <", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThanOrEqualTo(String value) {
            addCriterion("appId <=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLike(String value) {
            addCriterion("appId like", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotLike(String value) {
            addCriterion("appId not like", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdIn(List<String> values) {
            addCriterion("appId in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotIn(List<String> values) {
            addCriterion("appId not in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdBetween(String value1, String value2) {
            addCriterion("appId between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotBetween(String value1, String value2) {
            addCriterion("appId not between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andGroupCodeIsNull() {
            addCriterion("groupCode is null");
            return (Criteria) this;
        }

        public Criteria andGroupCodeIsNotNull() {
            addCriterion("groupCode is not null");
            return (Criteria) this;
        }

        public Criteria andGroupCodeEqualTo(String value) {
            addCriterion("groupCode =", value, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeNotEqualTo(String value) {
            addCriterion("groupCode <>", value, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeGreaterThan(String value) {
            addCriterion("groupCode >", value, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeGreaterThanOrEqualTo(String value) {
            addCriterion("groupCode >=", value, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeLessThan(String value) {
            addCriterion("groupCode <", value, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeLessThanOrEqualTo(String value) {
            addCriterion("groupCode <=", value, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeLike(String value) {
            addCriterion("groupCode like", value, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeNotLike(String value) {
            addCriterion("groupCode not like", value, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeIn(List<String> values) {
            addCriterion("groupCode in", values, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeNotIn(List<String> values) {
            addCriterion("groupCode not in", values, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeBetween(String value1, String value2) {
            addCriterion("groupCode between", value1, value2, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupCodeNotBetween(String value1, String value2) {
            addCriterion("groupCode not between", value1, value2, "groupCode");
            return (Criteria) this;
        }

        public Criteria andGroupNameIsNull() {
            addCriterion("groupName is null");
            return (Criteria) this;
        }

        public Criteria andGroupNameIsNotNull() {
            addCriterion("groupName is not null");
            return (Criteria) this;
        }

        public Criteria andGroupNameEqualTo(String value) {
            addCriterion("groupName =", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameNotEqualTo(String value) {
            addCriterion("groupName <>", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameGreaterThan(String value) {
            addCriterion("groupName >", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameGreaterThanOrEqualTo(String value) {
            addCriterion("groupName >=", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameLessThan(String value) {
            addCriterion("groupName <", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameLessThanOrEqualTo(String value) {
            addCriterion("groupName <=", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameLike(String value) {
            addCriterion("groupName like", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameNotLike(String value) {
            addCriterion("groupName not like", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameIn(List<String> values) {
            addCriterion("groupName in", values, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameNotIn(List<String> values) {
            addCriterion("groupName not in", values, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameBetween(String value1, String value2) {
            addCriterion("groupName between", value1, value2, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameNotBetween(String value1, String value2) {
            addCriterion("groupName not between", value1, value2, "groupName");
            return (Criteria) this;
        }

        public Criteria andDataTimeIsNull() {
            addCriterion("dataTime is null");
            return (Criteria) this;
        }

        public Criteria andDataTimeIsNotNull() {
            addCriterion("dataTime is not null");
            return (Criteria) this;
        }

        public Criteria andDataTimeEqualTo(Date value) {
            addCriterion("dataTime =", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeNotEqualTo(Date value) {
            addCriterion("dataTime <>", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeGreaterThan(Date value) {
            addCriterion("dataTime >", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("dataTime >=", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeLessThan(Date value) {
            addCriterion("dataTime <", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeLessThanOrEqualTo(Date value) {
            addCriterion("dataTime <=", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeIn(List<Date> values) {
            addCriterion("dataTime in", values, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeNotIn(List<Date> values) {
            addCriterion("dataTime not in", values, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeBetween(Date value1, Date value2) {
            addCriterion("dataTime between", value1, value2, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeNotBetween(Date value1, Date value2) {
            addCriterion("dataTime not between", value1, value2, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTypeIsNull() {
            addCriterion("dataType is null");
            return (Criteria) this;
        }

        public Criteria andDataTypeIsNotNull() {
            addCriterion("dataType is not null");
            return (Criteria) this;
        }

        public Criteria andDataTypeEqualTo(Byte value) {
            addCriterion("dataType =", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeNotEqualTo(Byte value) {
            addCriterion("dataType <>", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeGreaterThan(Byte value) {
            addCriterion("dataType >", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("dataType >=", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeLessThan(Byte value) {
            addCriterion("dataType <", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeLessThanOrEqualTo(Byte value) {
            addCriterion("dataType <=", value, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeIn(List<Byte> values) {
            addCriterion("dataType in", values, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeNotIn(List<Byte> values) {
            addCriterion("dataType not in", values, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeBetween(Byte value1, Byte value2) {
            addCriterion("dataType between", value1, value2, "dataType");
            return (Criteria) this;
        }

        public Criteria andDataTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("dataType not between", value1, value2, "dataType");
            return (Criteria) this;
        }

        public Criteria andBizTypeIsNull() {
            addCriterion("bizType is null");
            return (Criteria) this;
        }

        public Criteria andBizTypeIsNotNull() {
            addCriterion("bizType is not null");
            return (Criteria) this;
        }

        public Criteria andBizTypeEqualTo(Byte value) {
            addCriterion("bizType =", value, "bizType");
            return (Criteria) this;
        }

        public Criteria andBizTypeNotEqualTo(Byte value) {
            addCriterion("bizType <>", value, "bizType");
            return (Criteria) this;
        }

        public Criteria andBizTypeGreaterThan(Byte value) {
            addCriterion("bizType >", value, "bizType");
            return (Criteria) this;
        }

        public Criteria andBizTypeGreaterThanOrEqualTo(Byte value) {
            addCriterion("bizType >=", value, "bizType");
            return (Criteria) this;
        }

        public Criteria andBizTypeLessThan(Byte value) {
            addCriterion("bizType <", value, "bizType");
            return (Criteria) this;
        }

        public Criteria andBizTypeLessThanOrEqualTo(Byte value) {
            addCriterion("bizType <=", value, "bizType");
            return (Criteria) this;
        }

        public Criteria andBizTypeIn(List<Byte> values) {
            addCriterion("bizType in", values, "bizType");
            return (Criteria) this;
        }

        public Criteria andBizTypeNotIn(List<Byte> values) {
            addCriterion("bizType not in", values, "bizType");
            return (Criteria) this;
        }

        public Criteria andBizTypeBetween(Byte value1, Byte value2) {
            addCriterion("bizType between", value1, value2, "bizType");
            return (Criteria) this;
        }

        public Criteria andBizTypeNotBetween(Byte value1, Byte value2) {
            addCriterion("bizType not between", value1, value2, "bizType");
            return (Criteria) this;
        }

        public Criteria andSaasEnvIsNull() {
            addCriterion("saasEnv is null");
            return (Criteria) this;
        }

        public Criteria andSaasEnvIsNotNull() {
            addCriterion("saasEnv is not null");
            return (Criteria) this;
        }

        public Criteria andSaasEnvEqualTo(Byte value) {
            addCriterion("saasEnv =", value, "saasEnv");
            return (Criteria) this;
        }

        public Criteria andSaasEnvNotEqualTo(Byte value) {
            addCriterion("saasEnv <>", value, "saasEnv");
            return (Criteria) this;
        }

        public Criteria andSaasEnvGreaterThan(Byte value) {
            addCriterion("saasEnv >", value, "saasEnv");
            return (Criteria) this;
        }

        public Criteria andSaasEnvGreaterThanOrEqualTo(Byte value) {
            addCriterion("saasEnv >=", value, "saasEnv");
            return (Criteria) this;
        }

        public Criteria andSaasEnvLessThan(Byte value) {
            addCriterion("saasEnv <", value, "saasEnv");
            return (Criteria) this;
        }

        public Criteria andSaasEnvLessThanOrEqualTo(Byte value) {
            addCriterion("saasEnv <=", value, "saasEnv");
            return (Criteria) this;
        }

        public Criteria andSaasEnvIn(List<Byte> values) {
            addCriterion("saasEnv in", values, "saasEnv");
            return (Criteria) this;
        }

        public Criteria andSaasEnvNotIn(List<Byte> values) {
            addCriterion("saasEnv not in", values, "saasEnv");
            return (Criteria) this;
        }

        public Criteria andSaasEnvBetween(Byte value1, Byte value2) {
            addCriterion("saasEnv between", value1, value2, "saasEnv");
            return (Criteria) this;
        }

        public Criteria andSaasEnvNotBetween(Byte value1, Byte value2) {
            addCriterion("saasEnv not between", value1, value2, "saasEnv");
            return (Criteria) this;
        }

        public Criteria andTotalCountIsNull() {
            addCriterion("totalCount is null");
            return (Criteria) this;
        }

        public Criteria andTotalCountIsNotNull() {
            addCriterion("totalCount is not null");
            return (Criteria) this;
        }

        public Criteria andTotalCountEqualTo(Integer value) {
            addCriterion("totalCount =", value, "totalCount");
            return (Criteria) this;
        }

        public Criteria andTotalCountNotEqualTo(Integer value) {
            addCriterion("totalCount <>", value, "totalCount");
            return (Criteria) this;
        }

        public Criteria andTotalCountGreaterThan(Integer value) {
            addCriterion("totalCount >", value, "totalCount");
            return (Criteria) this;
        }

        public Criteria andTotalCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("totalCount >=", value, "totalCount");
            return (Criteria) this;
        }

        public Criteria andTotalCountLessThan(Integer value) {
            addCriterion("totalCount <", value, "totalCount");
            return (Criteria) this;
        }

        public Criteria andTotalCountLessThanOrEqualTo(Integer value) {
            addCriterion("totalCount <=", value, "totalCount");
            return (Criteria) this;
        }

        public Criteria andTotalCountIn(List<Integer> values) {
            addCriterion("totalCount in", values, "totalCount");
            return (Criteria) this;
        }

        public Criteria andTotalCountNotIn(List<Integer> values) {
            addCriterion("totalCount not in", values, "totalCount");
            return (Criteria) this;
        }

        public Criteria andTotalCountBetween(Integer value1, Integer value2) {
            addCriterion("totalCount between", value1, value2, "totalCount");
            return (Criteria) this;
        }

        public Criteria andTotalCountNotBetween(Integer value1, Integer value2) {
            addCriterion("totalCount not between", value1, value2, "totalCount");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountIsNull() {
            addCriterion("unKnownReasonCount is null");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountIsNotNull() {
            addCriterion("unKnownReasonCount is not null");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountEqualTo(Integer value) {
            addCriterion("unKnownReasonCount =", value, "unKnownReasonCount");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountNotEqualTo(Integer value) {
            addCriterion("unKnownReasonCount <>", value, "unKnownReasonCount");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountGreaterThan(Integer value) {
            addCriterion("unKnownReasonCount >", value, "unKnownReasonCount");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("unKnownReasonCount >=", value, "unKnownReasonCount");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountLessThan(Integer value) {
            addCriterion("unKnownReasonCount <", value, "unKnownReasonCount");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountLessThanOrEqualTo(Integer value) {
            addCriterion("unKnownReasonCount <=", value, "unKnownReasonCount");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountIn(List<Integer> values) {
            addCriterion("unKnownReasonCount in", values, "unKnownReasonCount");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountNotIn(List<Integer> values) {
            addCriterion("unKnownReasonCount not in", values, "unKnownReasonCount");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountBetween(Integer value1, Integer value2) {
            addCriterion("unKnownReasonCount between", value1, value2, "unKnownReasonCount");
            return (Criteria) this;
        }

        public Criteria andUnKnownReasonCountNotBetween(Integer value1, Integer value2) {
            addCriterion("unKnownReasonCount not between", value1, value2, "unKnownReasonCount");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountIsNull() {
            addCriterion("personalReasonCount is null");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountIsNotNull() {
            addCriterion("personalReasonCount is not null");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountEqualTo(Integer value) {
            addCriterion("personalReasonCount =", value, "personalReasonCount");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountNotEqualTo(Integer value) {
            addCriterion("personalReasonCount <>", value, "personalReasonCount");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountGreaterThan(Integer value) {
            addCriterion("personalReasonCount >", value, "personalReasonCount");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("personalReasonCount >=", value, "personalReasonCount");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountLessThan(Integer value) {
            addCriterion("personalReasonCount <", value, "personalReasonCount");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountLessThanOrEqualTo(Integer value) {
            addCriterion("personalReasonCount <=", value, "personalReasonCount");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountIn(List<Integer> values) {
            addCriterion("personalReasonCount in", values, "personalReasonCount");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountNotIn(List<Integer> values) {
            addCriterion("personalReasonCount not in", values, "personalReasonCount");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountBetween(Integer value1, Integer value2) {
            addCriterion("personalReasonCount between", value1, value2, "personalReasonCount");
            return (Criteria) this;
        }

        public Criteria andPersonalReasonCountNotBetween(Integer value1, Integer value2) {
            addCriterion("personalReasonCount not between", value1, value2, "personalReasonCount");
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
     * This class corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated do_not_delete_during_merge Tue Jun 12 15:02:06 CST 2018
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table callback_failure_reason_stat_access
     *
     * @mbg.generated Tue Jun 12 15:02:06 CST 2018
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