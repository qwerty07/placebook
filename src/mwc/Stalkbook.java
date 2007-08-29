/**
 * 
 */
package mwc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mwc.facebook.ObjectManager;
import mwc.facebook.data.DataStore;
import mwc.facebook.data.Point;
import mwc.facebook.data.User;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
	
	private static String SECRET_KEY = null;
	private static String API_KEY = null;
	private static String INFINITE_SESSION_KEY = null;
	
	static {
		try {
			URI config = Stalkbook.class.getResource(CONFIG_FILE).toURI();
			FileInputStream fis = new FileInputStream(new File(config));
			Properties props = new Properties();
			props.load(fis);

			SECRET_KEY = props.getProperty("secret");
			API_KEY = props.getProperty("api_key");
			INFINITE_SESSION_KEY = props.getProperty("infinite_session_key");
			
			System.out
					.printf(
							"Keys initialized:\n secret: %s\n api:%s\n infinite session: %s\n",
							SECRET_KEY, API_KEY, INFINITE_SESSION_KEY);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("API_KEY and SECRET_KEY not initialized.\n"
					+ "Facebook client WILL NOT WORK.");
		}
	}
	

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
			
			Collection<Integer> ids = new Vector<Integer>();
			ids.add(clientId);
			
			DataStore store = ObjectManager.instance().store();
			User user = store.getUserById(""+clientId);
			
			if (user == null) {
				Set<CharSequence> fields = new TreeSet<CharSequence>();
				fields.add("name");
				Document document = Stalkbook.getClient(request).users_getInfo(ids, fields);
				NodeList list = document.getElementsByTagName("name");
				String name = list.item(0).getTextContent();
				
				user = new User(""+clientId, name, new Point(0,0));
				store.addUser(user);
			}
			
			String iframeLocation = "http://facebook.interface.org.nz/placebook/map.jsp";
			String queryString = request.getQueryString();
			if (queryString != null) iframeLocation += "?" + queryString;

			PrintWriter writer = response.getWriter();
			//writer.printf("<h2>Hi <fb:name firstnameonly=\"true\" uid=\"%d\" useyou=\"false\"/>!</h2>", clientId);
			//writer.printf("<h2>Hi %s!</h2>", user.getName());
			writer.printf("<fb:iframe frameborder=\"0\" smartsize=\"true\" src=\"%s\"/>", iframeLocation);
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
	
	/** Get a FacebookRestClient from the static infinite session key */
	public static FacebookRestClient getClient() {
		return getClient(INFINITE_SESSION_KEY);
	}
	
	/** Get a FacebookRestClient by getting the session key from the given request */
	public static FacebookRestClient getClient(HttpServletRequest request) {
		String session = request.getParameter("fb_sig_session_key");

		// Steal a session key, ugh.
		// http://wiki.developers.facebook.com/index.php/Infinite_session_keys
		String expires = String.valueOf(request.getParameter("fb_sig_expires"));
		if (expires.equals("0")) {
			System.out.println("Infinite-session-key:" + session);
		}

		return getClient(session);
	}
	

	/** Get a FacbookRestClient from the given session key */
	public static FacebookRestClient getClient(String session) {
		System.out.printf("Creating REST client\n secret: %s\n api:%s\n session: %s\n",
							SECRET_KEY, API_KEY, session);
		return new FacebookRestClient(API_KEY, SECRET_KEY, session);
	}

}
