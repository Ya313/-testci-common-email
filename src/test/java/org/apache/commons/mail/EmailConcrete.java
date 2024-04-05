package org.apache.commons.mail;

import java.util.Map;

import javax.mail.Session;

public class EmailConcrete extends Email{
	
	private Session session;

	@Override
	public Email setMsg(String msg) throws EmailException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return headers
	 */
	public Map<String, String> getHeaders()
	{
		return this.headers;
	}
	
	public String getContentType()
	{
		return this.contentType;
	}
	
	public void setMailSession(Session session) {
	    this.session = session;
	}
	
}
