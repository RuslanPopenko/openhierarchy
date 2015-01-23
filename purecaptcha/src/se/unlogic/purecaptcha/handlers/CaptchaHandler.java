package se.unlogic.purecaptcha.handlers;

import java.io.IOException;
import java.net.SocketException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import se.unlogic.purecaptcha.Captcha;
import se.unlogic.purecaptcha.CaptchaImage;

public class CaptchaHandler {

	protected Captcha captcha;
	protected String sessionAttribute;
	protected long validationTimeout;
	protected boolean caseSensitive;

	public CaptchaHandler(Captcha captcha, String sessionAttribute, long validationTimeout, boolean caseSensitive) {

		super();
		this.captcha = captcha;
		this.sessionAttribute = sessionAttribute;
		this.validationTimeout = validationTimeout;
		this.caseSensitive = caseSensitive;
	}

	public void getCaptchaImage(HttpServletRequest req, HttpServletResponse res) {

		try {
			CaptchaImage captchaImage = captcha.generateCaptchaImage();

			req.getSession(true).setAttribute(sessionAttribute, captchaImage.getCode());
			
			res.setHeader("Cache-Control", "no-store");
			res.setHeader("Pragma", "no-cache");
			res.setDateHeader("Expires", 0);
			res.setContentType("image/jpeg");

			ImageIO.write(captchaImage.getBufferedImage(), "jpg", res.getOutputStream());

			res.getOutputStream().flush();
			res.getOutputStream().close();

		} catch (IllegalArgumentException e) {
			return;
		} catch (SocketException e) {
			return;
		} catch (IOException e) {
			return;
		} catch (IllegalStateException e) {
			return;
		}
	}
	
	public boolean isValidCode(HttpServletRequest req, String code){
		
		try{
			
			HttpSession session = req.getSession();
			
			if(session == null){
				
				return false;
			}
			
			String correctCode = (String)session.getAttribute(sessionAttribute);
			
			if(correctCode == null){
				
				return false;
			}
			
			if(caseSensitive && correctCode.equals(code)){
				
				return true;
				
			}else if(!caseSensitive && correctCode.equalsIgnoreCase(code)){
				
				return true;
			}
			
			session.removeAttribute(sessionAttribute);
			
		} catch (IllegalStateException e) {}
		
		return false;
	}
}
