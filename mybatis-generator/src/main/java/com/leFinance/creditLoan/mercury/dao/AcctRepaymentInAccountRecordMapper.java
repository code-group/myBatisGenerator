package com.leFinance.creditLoan.mercury.dao;

import com.leFinance.creditLoan.mercury.domain.AcctRepaymentInAccountRecord;
import com.leFinance.creditLoan.mercury.domain.AcctRepaymentInAccountRecordWithBLOBs;

public interface AcctRepaymentInAccountRecordMapper {
    int deleteByPrimaryKey(Long pid);

    int insert(AcctRepaymentInAccountRecordWithBLOBs record);

    int insertSelective(AcctRepaymentInAccountRecordWithBLOBs record);

    AcctRepaymentInAccountRecordWithBLOBs selectByPrimaryKey(Long pid);

    int updateByPrimaryKeySelective(AcctRepaymentInAccountRecordWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(AcctRepaymentInAccountRecordWithBLOBs record);

    int updateByPrimaryKey(AcctRepaymentInAccountRecord record);
}