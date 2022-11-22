-- ----------------------------
-- View structure for home_doing_bill
-- ----------------------------
DROP VIEW IF EXISTS `home_doing_bill`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `home_doing_bill` AS 
SELECT `name`, IFNULL(edit,0) edit, IFNULL(appr,0) appr, IFNULL(exec,0) exec
  FROM
	 (SELECT '销售报价' `name`, SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END) edit, 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END) appr,
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END) exec
			FROM sal_quot b
		 WHERE b.is_closed = 0 AND b.is_voided = 0
		UNION
		SELECT '销售订单', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM sal_order b
		 WHERE b.is_closed = 0 AND b.is_voided = 0
		UNION
		SELECT '出库单', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM stk_io b
		 WHERE b.is_closed = 0 AND b.is_voided = 0 AND b.stock_io_type LIKE '2%'
		UNION
		SELECT '应收单', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM fin_receivable b
		 WHERE b.is_closed = 0 AND b.is_voided = 0 
		UNION
		SELECT '收款单', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM fin_receipt b
		 WHERE b.is_closed = 0 AND b.is_voided = 0 
		UNION
		SELECT '销售发票', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM fin_sal_invoice b
		 WHERE b.is_closed = 0 AND b.is_voided = 0 
		UNION
		SELECT '采购申请', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END) 
			FROM pur_req b
		 WHERE b.is_closed = 0 AND b.is_voided = 0
		UNION
		SELECT '采购询价', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END) 
			FROM pur_inquiry b
		 WHERE b.is_closed = 0 AND b.is_voided = 0
		UNION
		SELECT '供应报价', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END) 
			FROM pur_quot b
		 WHERE b.is_closed = 0 AND b.is_voided = 0
		UNION
		SELECT '采购比价', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END) 
			FROM pur_compare b
		 WHERE b.is_closed = 0 AND b.is_voided = 0
		UNION
		SELECT '采购订单', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END) 
			FROM pur_order b
		 WHERE b.is_closed = 0 AND b.is_voided = 0
		UNION
		SELECT '入库单', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM stk_io b
		 WHERE b.is_closed = 0 AND b.is_voided = 0 AND b.stock_io_type LIKE '1%'
		UNION
		SELECT '应付单', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM fin_payable b
		 WHERE b.is_closed = 0 AND b.is_voided = 0 
		UNION
		SELECT '付款申请', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM fin_payment_req b
		 WHERE b.is_closed = 0 AND b.is_voided = 0
		UNION
		SELECT '付款单', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM fin_payment b
		 WHERE b.is_closed = 0 AND b.is_voided = 0 
		UNION
		SELECT '采购发票', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM fin_pur_invoice b
		 WHERE b.is_closed = 0 AND b.is_voided = 0 
		UNION
		SELECT '库存盘点', SUM(CASE b.bill_stage WHEN '12' THEN 1 ELSE 0 END), 
					 SUM(CASE WHEN b.bill_stage IN('14', '22') AND b.is_effective = 0 THEN 1 ELSE 0 END),
					 SUM(CASE WHEN b.bill_stage IN('24', '32') AND b.is_effective = 1 THEN 1 ELSE 0 END)
			FROM stk_check b
		 WHERE b.is_closed = 0 AND b.is_voided = 0) t;
