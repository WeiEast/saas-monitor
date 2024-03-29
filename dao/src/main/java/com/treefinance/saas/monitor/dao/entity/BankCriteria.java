package com.treefinance.saas.monitor.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BankCriteria {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public BankCriteria() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
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
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
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
            addCriterion("Id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("Id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Short value) {
            addCriterion("Id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Short value) {
            addCriterion("Id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Short value) {
            addCriterion("Id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Short value) {
            addCriterion("Id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Short value) {
            addCriterion("Id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Short value) {
            addCriterion("Id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Short> values) {
            addCriterion("Id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Short> values) {
            addCriterion("Id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Short value1, Short value2) {
            addCriterion("Id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Short value1, Short value2) {
            addCriterion("Id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdIsNull() {
            addCriterion("WebsiteId is null");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdIsNotNull() {
            addCriterion("WebsiteId is not null");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdEqualTo(Integer value) {
            addCriterion("WebsiteId =", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdNotEqualTo(Integer value) {
            addCriterion("WebsiteId <>", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdGreaterThan(Integer value) {
            addCriterion("WebsiteId >", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("WebsiteId >=", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdLessThan(Integer value) {
            addCriterion("WebsiteId <", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdLessThanOrEqualTo(Integer value) {
            addCriterion("WebsiteId <=", value, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdIn(List<Integer> values) {
            addCriterion("WebsiteId in", values, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdNotIn(List<Integer> values) {
            addCriterion("WebsiteId not in", values, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdBetween(Integer value1, Integer value2) {
            addCriterion("WebsiteId between", value1, value2, "websiteId");
            return (Criteria) this;
        }

        public Criteria andWebsiteIdNotBetween(Integer value1, Integer value2) {
            addCriterion("WebsiteId not between", value1, value2, "websiteId");
            return (Criteria) this;
        }

        public Criteria andBankMarkIsNull() {
            addCriterion("BankMark is null");
            return (Criteria) this;
        }

        public Criteria andBankMarkIsNotNull() {
            addCriterion("BankMark is not null");
            return (Criteria) this;
        }

        public Criteria andBankMarkEqualTo(String value) {
            addCriterion("BankMark =", value, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkNotEqualTo(String value) {
            addCriterion("BankMark <>", value, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkGreaterThan(String value) {
            addCriterion("BankMark >", value, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkGreaterThanOrEqualTo(String value) {
            addCriterion("BankMark >=", value, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkLessThan(String value) {
            addCriterion("BankMark <", value, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkLessThanOrEqualTo(String value) {
            addCriterion("BankMark <=", value, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkLike(String value) {
            addCriterion("BankMark like", value, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkNotLike(String value) {
            addCriterion("BankMark not like", value, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkIn(List<String> values) {
            addCriterion("BankMark in", values, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkNotIn(List<String> values) {
            addCriterion("BankMark not in", values, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkBetween(String value1, String value2) {
            addCriterion("BankMark between", value1, value2, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankMarkNotBetween(String value1, String value2) {
            addCriterion("BankMark not between", value1, value2, "bankMark");
            return (Criteria) this;
        }

        public Criteria andBankNameIsNull() {
            addCriterion("BankName is null");
            return (Criteria) this;
        }

        public Criteria andBankNameIsNotNull() {
            addCriterion("BankName is not null");
            return (Criteria) this;
        }

        public Criteria andBankNameEqualTo(String value) {
            addCriterion("BankName =", value, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameNotEqualTo(String value) {
            addCriterion("BankName <>", value, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameGreaterThan(String value) {
            addCriterion("BankName >", value, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameGreaterThanOrEqualTo(String value) {
            addCriterion("BankName >=", value, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameLessThan(String value) {
            addCriterion("BankName <", value, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameLessThanOrEqualTo(String value) {
            addCriterion("BankName <=", value, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameLike(String value) {
            addCriterion("BankName like", value, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameNotLike(String value) {
            addCriterion("BankName not like", value, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameIn(List<String> values) {
            addCriterion("BankName in", values, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameNotIn(List<String> values) {
            addCriterion("BankName not in", values, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameBetween(String value1, String value2) {
            addCriterion("BankName between", value1, value2, "bankName");
            return (Criteria) this;
        }

        public Criteria andBankNameNotBetween(String value1, String value2) {
            addCriterion("BankName not between", value1, value2, "bankName");
            return (Criteria) this;
        }

        public Criteria andOrderIndexIsNull() {
            addCriterion("OrderIndex is null");
            return (Criteria) this;
        }

        public Criteria andOrderIndexIsNotNull() {
            addCriterion("OrderIndex is not null");
            return (Criteria) this;
        }

        public Criteria andOrderIndexEqualTo(Short value) {
            addCriterion("OrderIndex =", value, "orderIndex");
            return (Criteria) this;
        }

        public Criteria andOrderIndexNotEqualTo(Short value) {
            addCriterion("OrderIndex <>", value, "orderIndex");
            return (Criteria) this;
        }

        public Criteria andOrderIndexGreaterThan(Short value) {
            addCriterion("OrderIndex >", value, "orderIndex");
            return (Criteria) this;
        }

        public Criteria andOrderIndexGreaterThanOrEqualTo(Short value) {
            addCriterion("OrderIndex >=", value, "orderIndex");
            return (Criteria) this;
        }

        public Criteria andOrderIndexLessThan(Short value) {
            addCriterion("OrderIndex <", value, "orderIndex");
            return (Criteria) this;
        }

        public Criteria andOrderIndexLessThanOrEqualTo(Short value) {
            addCriterion("OrderIndex <=", value, "orderIndex");
            return (Criteria) this;
        }

        public Criteria andOrderIndexIn(List<Short> values) {
            addCriterion("OrderIndex in", values, "orderIndex");
            return (Criteria) this;
        }

        public Criteria andOrderIndexNotIn(List<Short> values) {
            addCriterion("OrderIndex not in", values, "orderIndex");
            return (Criteria) this;
        }

        public Criteria andOrderIndexBetween(Short value1, Short value2) {
            addCriterion("OrderIndex between", value1, value2, "orderIndex");
            return (Criteria) this;
        }

        public Criteria andOrderIndexNotBetween(Short value1, Short value2) {
            addCriterion("OrderIndex not between", value1, value2, "orderIndex");
            return (Criteria) this;
        }

        public Criteria andMatchTextIsNull() {
            addCriterion("MatchText is null");
            return (Criteria) this;
        }

        public Criteria andMatchTextIsNotNull() {
            addCriterion("MatchText is not null");
            return (Criteria) this;
        }

        public Criteria andMatchTextEqualTo(String value) {
            addCriterion("MatchText =", value, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextNotEqualTo(String value) {
            addCriterion("MatchText <>", value, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextGreaterThan(String value) {
            addCriterion("MatchText >", value, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextGreaterThanOrEqualTo(String value) {
            addCriterion("MatchText >=", value, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextLessThan(String value) {
            addCriterion("MatchText <", value, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextLessThanOrEqualTo(String value) {
            addCriterion("MatchText <=", value, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextLike(String value) {
            addCriterion("MatchText like", value, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextNotLike(String value) {
            addCriterion("MatchText not like", value, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextIn(List<String> values) {
            addCriterion("MatchText in", values, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextNotIn(List<String> values) {
            addCriterion("MatchText not in", values, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextBetween(String value1, String value2) {
            addCriterion("MatchText between", value1, value2, "matchText");
            return (Criteria) this;
        }

        public Criteria andMatchTextNotBetween(String value1, String value2) {
            addCriterion("MatchText not between", value1, value2, "matchText");
            return (Criteria) this;
        }

        public Criteria andIsEnabledIsNull() {
            addCriterion("IsEnabled is null");
            return (Criteria) this;
        }

        public Criteria andIsEnabledIsNotNull() {
            addCriterion("IsEnabled is not null");
            return (Criteria) this;
        }

        public Criteria andIsEnabledEqualTo(String value) {
            addCriterion("IsEnabled =", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledNotEqualTo(String value) {
            addCriterion("IsEnabled <>", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledGreaterThan(String value) {
            addCriterion("IsEnabled >", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledGreaterThanOrEqualTo(String value) {
            addCriterion("IsEnabled >=", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledLessThan(String value) {
            addCriterion("IsEnabled <", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledLessThanOrEqualTo(String value) {
            addCriterion("IsEnabled <=", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledLike(String value) {
            addCriterion("IsEnabled like", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledNotLike(String value) {
            addCriterion("IsEnabled not like", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledIn(List<String> values) {
            addCriterion("IsEnabled in", values, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledNotIn(List<String> values) {
            addCriterion("IsEnabled not in", values, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledBetween(String value1, String value2) {
            addCriterion("IsEnabled between", value1, value2, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledNotBetween(String value1, String value2) {
            addCriterion("IsEnabled not between", value1, value2, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNull() {
            addCriterion("CreatedAt is null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNotNull() {
            addCriterion("CreatedAt is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtEqualTo(Date value) {
            addCriterion("CreatedAt =", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotEqualTo(Date value) {
            addCriterion("CreatedAt <>", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThan(Date value) {
            addCriterion("CreatedAt >", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("CreatedAt >=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThan(Date value) {
            addCriterion("CreatedAt <", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThanOrEqualTo(Date value) {
            addCriterion("CreatedAt <=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIn(List<Date> values) {
            addCriterion("CreatedAt in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotIn(List<Date> values) {
            addCriterion("CreatedAt not in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtBetween(Date value1, Date value2) {
            addCriterion("CreatedAt between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotBetween(Date value1, Date value2) {
            addCriterion("CreatedAt not between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNull() {
            addCriterion("UpdatedAt is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNotNull() {
            addCriterion("UpdatedAt is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtEqualTo(Date value) {
            addCriterion("UpdatedAt =", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotEqualTo(Date value) {
            addCriterion("UpdatedAt <>", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThan(Date value) {
            addCriterion("UpdatedAt >", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThanOrEqualTo(Date value) {
            addCriterion("UpdatedAt >=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThan(Date value) {
            addCriterion("UpdatedAt <", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThanOrEqualTo(Date value) {
            addCriterion("UpdatedAt <=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIn(List<Date> values) {
            addCriterion("UpdatedAt in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotIn(List<Date> values) {
            addCriterion("UpdatedAt not in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtBetween(Date value1, Date value2) {
            addCriterion("UpdatedAt between", value1, value2, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotBetween(Date value1, Date value2) {
            addCriterion("UpdatedAt not between", value1, value2, "updatedAt");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table t_bank
     *
     * @mbggenerated do_not_delete_during_merge Wed Jun 14 17:16:27 CST 2017
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table t_bank
     *
     * @mbggenerated Wed Jun 14 17:16:27 CST 2017
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