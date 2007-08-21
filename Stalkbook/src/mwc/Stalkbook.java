/**
 * 
 */
package mwc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.facebook.api.FacebookRestClient;

/**
 * @author bergerwill
 * 
 */
public class Stalkbook extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String CONFIG_FILE = "../settings.conf";
	

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// super.doGet(request, response);
		
		response.setContentType("text/html");
		try {
			FacebookRestClient client = getClient(request);
			// uncomment the line below to get details of each request and
			// response printed to System.out.
			client.setDebug(true);
			int clientId = client.users_getLoggedInUser();
			
			PrintWriter writer = response.getWriter();
			writer.printf("<h2>Hi <fb:name firstnameonly=\"true\" uid=\"%d\" useyou=\"false\"/>!</h2>", clientId);
			writer.printf("<fb:iframe smartsize=\"true\" src=\"%s\"/>", "http://facebook.interface.org.nz/stalkbook/map.jsp");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req,resp);
	}
	
	public static FacebookRestClient getClient(HttpServletRequest request){
		try {
		URI config = Stalkbook.class.getResource(CONFIG_FILE).toURI();
		
		FileInputStream fis = new FileInputStream(new File(config));
		Properties props = new Properties();
		props.load(fis);

		String api_key = props.getProperty("api_key");
		String secret = props.getProperty("secret");
		String session = String.valueOf(request.getParameter("fb_sig_session_key"));		
		
		return new FacebookRestClient(api_key, secret, session);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		}
		return null;
	}

}
