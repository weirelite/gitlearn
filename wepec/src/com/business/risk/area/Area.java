package com.business.risk.area;

import org.apache.log4j.Logger;
import com.swdp3.core.action.AbstractBM;

public class Area extends AbstractBM {
	static Logger logger = Logger.getLogger(Area.class);
	/**
	 * ȡ���б�-���ύ
	 * @throws Exception
	 */
	public void getListEdit() throws Exception {
		try {
			String json = this.queryBySql2Json(getPermission(null,0, "SELECT  *  FROM  T_RISK_AREA WHERE (SHBZ=0 OR SHBZ=-1) ? "),"ORDER BY SYSID DESC");
			this.responseRender(json);
		} catch (Exception e) {
			this.responseExceptionRender();
		}
	}

	/**
	 * ȡ���б�-�����
	 * @throws Exception
	 */
	public void getListAudit() throws Exception {
		try {
			String json = this.queryBySql2Json(getPermission(null,1, "SELECT * FROM T_RISK_AREA a  WHERE SHBZ=1 AND EXISTS (SELECT 1 FROM v_jbpm_usertask b WHERE  a.WORKFLOWID=b.PROCINST_ AND b.ACTORID_='"+this.getUserInfo().getUserID()+"') "),"ORDER BY SYSID DESC");
			this.responseRender(json);
		} catch (Exception e) {
			this.responseExceptionRender();
		}
	}
	/**
	 * ȡ���б�-����ע��
	 * @throws Exception
	 */
	public void getListRoll() throws Exception {
		try {
			String json = this.queryBySql2Json(getPermission(null,2, "SELECT  *  FROM  T_RISK_AREA WHERE SHBZ>=2 ? "),"ORDER BY SYSID DESC");
			this.responseRender(json);
		} catch (Exception e) {
			this.responseExceptionRender();
		}
	}
	/**
	 * ȡ���б�-���ڲ�ѯ
	 * @throws Exception
	 */
	public void getListChoose() throws Exception {
		try {
			String json = this.queryBySql2Json(getPermission(null,3, "SELECT  *  FROM  T_RISK_AREA WHERE SHBZ=2 ? "),"ORDER BY SYSID DESC");
			this.responseRender(json);
		} catch (Exception e) {
			this.responseExceptionRender();
		}
	}
	/**
	 * ȡ���б�-���ύ
	 * @throws Exception
	 */
	public void getListQuery() throws Exception {
		try {
			String json = this.queryBySql2Json(getPermission(null,4, "SELECT  *  FROM  T_RISK_AREA WHERE SHBZ>0 ? "),"ORDER BY SYSID DESC");
			this.responseRender(json);
		} catch (Exception e) {
			this.responseExceptionRender();
		}
	}
	/**
	 * ȡ���б�
	 * 
	 * @throws Exception
	 */
	public void getnullList() throws Exception {
		String unflag = this.getParameterValue("unflag");
		try {
			String json = this.queryBySql2Json("SELECT  * FROM  T_RISK_FX WHERE unflag=" + unflag + " ? ","ORDER BY SYSID ");
			this.responseRender(json);
		} catch (Exception e) {
			this.responseExceptionRender();
		}
	}

	/**
	 * ȡ���б�
	 * 
	 * @throws Exception
	 */
	public void getnullList() throws Exception {
		String unflag = this.getParameterValue("unflag");
		try {
			String json = this.queryBySql2Json("SELECT  * FROM  T_RISK_MEASURES WHERE unflag=" + unflag + " ? ","ORDER BY SYSID ");
			this.responseRender(json);
		} catch (Exception e) {
			this.responseExceptionRender();
		}
	}

	/**
	 * ������Ϣ
	 * 
	 * @throws Exception
	 */
	public void save() throws Exception {
		if(!this.getUserInfo().getOpPermission2(this.getClass().getName(), "saveop")){
			throw new Exception("����Ȩ�ޱ�������!");
		}
		this.setBillUserInfo("t_risk_area");
		super.save();
		this.responseRender("ok");
	}
	/**
	 * ɾ������
	 * 
	 * @throws Exception
	 */
	public void del() throws Exception {
		if(!this.getUserInfo().getOpPermission2(this.getClass().getName(), "delop")){
			throw new Exception("����Ȩ��ɾ������!");
		}
		this.executeUpdate("DELETE FROM T_RISK_AREA WHERE SYSID IN " + this.getParameterValue("formdata"));//����
		this.executeUpdate("DELETE FROM T_RISK_FX WHERE  UNFLAG IN " + this.getParameterValue("formdata"));//�ֱ�
		this.executeUpdate("DELETE FROM T_RISK_MEASURES WHERE  UNFLAG IN " + this.getParameterValue("formdata"));//�ֱ�
		this.executeUpdate("DELETE FROM T_AUDIT_COMMENT WHERE TABLENAME='T_RISK_AREA' AND  BILLID IN " + this.getParameterValue("formdata"));//���������
		this.responseOK("ɾ���ɹ�");
	}
	/**
	 * �ύ����
	 * 
	 * @throws Exception
	 */
	public void commit() throws Exception {
		if(!this.getUserInfo().getOpPermission2(this.getClass().getName(), "commitop")){
			throw new Exception("����Ȩ���ύ����!");
		}
		String sysids =this.getParameterValue("formdata");
		String[] sysidArray = sysids.split(",");
		for(String s : sysidArray){
			this.startWorkFlow("T_RISK_AREA", null, s);
		}
		this.responseOK("�ύ���");
	}

	/**
	 * �������
	 * @throws Exception
	 */
	public void audit() throws Exception {
		if(!this.getUserInfo().getOpPermission2(this.getClass().getName(), "auditop")){
			throw new Exception("����Ȩ���������!");
		}
		String sysids =this.getParameterValue("formdata");
		String msg = this.getParameterValue("msg");
		int pass = Integer.parseInt(this.getParameterValue("pass"));
		logger.info(msg);
		String[] sysidArray = sysids.split(",");
		for(String s : sysidArray){
			this.continueWorkFlow("T_RISK_AREA", null, pass, msg, s);
		}
		this.responseOK("������");
	}
	/**
	 * ��˺��������
	 * @throws Exception
	 */
	public void rollback() throws Exception {
		if(!this.getUserInfo().getOpPermission2(this.getClass().getName(), "rollbackop")){
			throw new Exception("����Ȩ�޻�������!");
		}
		String sysids =this.getParameterValue("formdata");
		String[] sysidArray = sysids.split(",");
		for(String s : sysidArray){
			this.executeUpdate("UPDATE   T_RISK_AREA SET STATUS='���˻�',SHBZ=-1 WHERE   SHBZ>=2 AND  SYSID= " + s);
			com.swdp3.core.action.ActionAssistant aa = new com.swdp3.core.action.ActionAssistant();
			aa.saveAuditComments("T_RISK_AREA", "T_RISK_AREA", this.getUserInfo(), 0, "����", s, 0, 0, 0, this.getConnection());
		}
		this.responseOK("�������");
	}
	

	/**
	 * ��˺�ע������
	 * @throws Exception
	 */
	public void block() throws Exception {
		if(!this.getUserInfo().getOpPermission2(this.getClass().getName(), "blockop")){
			throw new Exception("����Ȩע������!");
		}
		String sysids =this.getParameterValue("formdata");
		String[] sysidArray = sysids.split(",");
		for(String s : sysidArray){
			this.executeUpdate("UPDATE   T_RISK_AREA SET STATUS='��ע��',SHBZ=3 WHERE SHBZ=2 AND SYSID= " + s);
			com.swdp3.core.action.ActionAssistant aa = new com.swdp3.core.action.ActionAssistant();
			aa.saveAuditComments("T_RISK_AREA", "T_RISK_AREA", this.getUserInfo(), 0, "ע��", s, 0, 0, 0, this.getConnection());
		}
		this.responseOK("ע�����");
	}
	

	/**
	 * ȡ��ע������
	 * @throws Exception
	 */
	public void cancleBlock() throws Exception {
		if(!this.getUserInfo().getOpPermission2(this.getClass().getName(), "blockop")){
			throw new Exception("����Ȩȡ��ע������!");
		}
		String sysids =this.getParameterValue("formdata");
		String[] sysidArray = sysids.split(",");
		for(String s : sysidArray){
			this.executeUpdate("UPDATE   T_RISK_AREA SET STATUS='������',SHBZ=2 WHERE SHBZ=3 AND SYSID= " + s);
			com.swdp3.core.action.ActionAssistant aa = new com.swdp3.core.action.ActionAssistant();
			aa.saveAuditComments("T_RISK_AREA", "T_RISK_AREA", this.getUserInfo(), 0, "ȡ��ע��", s, 0, 0, 0, this.getConnection());
		}
		this.responseOK("ȡ��ע�����");
	}
	

public void render() throws Exception {
	this.setLocalRequestParameter("tableName", "T_RISK_AREA");
	super.render();
}
	public void initForm() throws Exception {
		java.util.List userList = this.queryBySql("SELECT  *  FROM  T_RISK_AREA WHERE SYSID=" + this.getParameterValue("sysid"), 1, 1).getList();
		if (userList.size() == 1) {
			this.setFormValues(userList.get(0));
		} else {
			java.util.Map<String, String> mainMap = new java.util.HashMap<String, String>();
			mainMap.put("status", "δ�ύ");
			String sysid = this.getUserSEQ();
			mainMap.put("sysid", sysid);
			mainMap.put("unflag", sysid);
			mainMap.put("shbz", "0");
			this.setFormValues(mainMap);
		}
	}
}
