package im.actor.sdk.controllers.auth;

import android.content.Context;

import java.io.Serializable;
import java.util.HashMap;

import im.actor.sdk.util.Files;

public class LoginIPBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static HashMap<String, LoginIPBean> IPLoginMap;// 根据单位名称，存储该单位最后一个登录的用户名

	private String company;
	private String ip;
	private Boolean isCheck;
	private String userName;
	private String password;
	private Boolean isRemember = true;// 是否记住密码

	public LoginIPBean() {
		// TODO Auto-generated constructor stub
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Boolean getIsCheck() {
		if (isCheck == null) {
			isCheck = false;
		}
		return isCheck;
	}

	public void setIsCheck(Boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getIsRemember() {
		return isRemember;
	}

	public void setIsRemember(Boolean isRemember) {
		this.isRemember = isRemember;
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, LoginIPBean> getIPLoginMap(Context con) {
		IPLoginMap = (HashMap<String, LoginIPBean>) Files.readLoginInfo(con,
				"IPLastLogin");
		if (IPLoginMap == null) {
			IPLoginMap = new HashMap<String, LoginIPBean>();
		}
		return IPLoginMap;
	}

	public static void setIPLoginMap(Context con, String ipName,
			LoginIPBean bean) {
		IPLoginMap = getIPLoginMap(con);
		IPLoginMap.put(ipName, bean);
		Files.saveLoginInfo(LoginIPBean.IPLoginMap, con, "IPLastLogin");
	}

	@SuppressWarnings("unchecked")
	public static LoginIPBean getIPLoginBean(Context con, String ipname) {
		IPLoginMap = (HashMap<String, LoginIPBean>) Files.readLoginInfo(con,
				"IPLastLogin");
		if (IPLoginMap == null) {
			return null;
		} else {
			LoginIPBean bean = IPLoginMap.get(ipname);
			return bean;
		}
	}

	public static void clearAllIPLoginBean(Context con) {
		IPLoginMap = null;
		Files.saveLoginInfo(LoginIPBean.IPLoginMap, con, "IPLastLogin");
	}

	@SuppressWarnings("unchecked")
	public static void clearIPLoginBean(Context con, String ipName) {
		IPLoginMap = (HashMap<String, LoginIPBean>) Files.readLoginInfo(con,
				"IPLastLogin");
		if (IPLoginMap == null) {
			return;
		}
		IPLoginMap.put(ipName, null);
		Files.saveLoginInfo(LoginIPBean.IPLoginMap, con, "IPLastLogin");
	}

}
