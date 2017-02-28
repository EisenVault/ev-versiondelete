package com.eisenvault.versiondelete.evaluator;

import javax.servlet.http.HttpSession;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Response;

public class DeleteVersionsActionEvaluator extends BaseEvaluator{

	private static Log logger = LogFactory
			.getLog(DeleteVersionsActionEvaluator.class);

	protected static final String SITE = "site";

	public void debug(String debugMessage) {
		if (logger.isDebugEnabled()) {
			logger.debug("*******" + debugMessage + "*******");
		}
	}
	
	/**
	 * 
	 * Returns the current site id OR null if we aren't in a site
	 * 
	 * 
	 * 
	 * @param context
	 * 
	 * @return The current site id OR null if we aren't in a site
	 */

	/*protected String getSite(RequestContext context) {
		debug("Inside getSite()");
		// Look for siteId in url path & parameters
		String site = context.getUriTokens().get(SITE);
		debug("Getting site from Uri tokens " + site);
		if (site == null) {
			site = context.getParameter(SITE);
			debug("Getting site from context param " + site);
		}
		if (site == null) {
			String[] pathNames = context.getUri()
					.substring(context.getContextPath().length()).split("/");
			for (int i = 0; i < pathNames.length; i++) {
				if (pathNames[i].equals(SITE) && (i + 1 < pathNames.length)) {
					site = pathNames[i + 1];
					debug("Getting site from context pathNames " + site);
					break;
				}
			}
		}
		return site;
	}*/
	
	@Override
	public boolean evaluate(JSONObject arg0) {

		try {
			
			debug("Inside evaluate()");
			
			System.out.println("*******Printing JSONObject " + arg0);
			
			final RequestContext rc = ThreadLocalRequestContext
					.getRequestContext();
			HttpSession session = ServletUtil.getSession();
			CredentialVault cv = rc.getCredentialVault();
			
			if (cv != null) {
				String userName = (String) session
						.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
				Connector connector = rc.getServiceRegistry()
						.getConnectorService()
						.getConnector("alfresco", userName, session);
				
				String membershipUri = "/api/sites/" + getSiteId(arg0)
						+ "/memberships/" + rc.getUserId();
				JSONObject membership = (JSONObject) getResponse(connector ,membershipUri);
				System.out.println("Printing membership object " + membership);
				
				String versionUri = "/api/version?nodeRef="+arg0.get("nodeRef");
				JSONArray version = (JSONArray) getResponse(connector ,versionUri);
				System.out.println("Printing version object " + version);
				
				if(("SiteManager".equals((String) membership.get("role"))) && version.size() > 1.0){
					return true;
				}
			}
		} catch (ConnectorServiceException | ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Object getResponse(Connector connector, String uri) throws ParseException {
		
		Response res = connector.call(uri);
		JSONObject jsonRes = null;
		JSONArray jsonArray = null;
		if (res.getStatus().getCode() == Status.STATUS_OK) {
			String response = res.getResponse();
			JSONParser p = new JSONParser();
			Object obj = p.parse(response);
			System.out.println(">> Object obj value :: " + obj);
			if (obj instanceof JSONObject) {
				jsonRes = (JSONObject) obj;
				return jsonRes;
			}
			if (obj instanceof JSONArray) {
				jsonArray = (JSONArray) obj;
				return jsonArray;
			}
		}
		return null;
	}

}
