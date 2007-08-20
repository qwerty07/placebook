/**
 * 
 */
package mwc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import com.facebook.api.FacebookException;
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
			/*URI config = Stalkbook.class.getResource(CONFIG_FILE).toURI();
			
			FileInputStream fis = new FileInputStream(new File(config));
			Properties props = new Properties();
			props.load(fis);

			String api_key = props.getProperty("api_key");
			String secret = props.getProperty("secret");
*/
			String session = String.valueOf(request.getParameter("fb_sig_session_key"));
			String api = String.valueOf(request.getParameter("fb_sig_api_key"));
			
			System.out.println("Session key: " + session);
			System.out.println("API key: " + api);
			
			FacebookRestClient client = new FacebookRestClient(api, "603eaef90de13abbacd6ae2701d92877", session);

			// uncomment the line below to get details of each request and
			// respnse
			// printed to System.out.
			client.setDebug(true);
			//Document friends = client.friends_get();
			int clientId = client.users_getLoggedInUser();
			
			PrintWriter writer = response.getWriter();
			writer.printf("<h2>Hi <fb:name firstnameonly=\"true\" uid=\"%d\" useyou=\"false\"/>!</h2>", clientId);
			//String auth = client.auth_createToken();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		}
	}
}
