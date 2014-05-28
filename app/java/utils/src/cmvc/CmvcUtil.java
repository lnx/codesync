package cmvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import util.Log;

import com.ibm.sdwb.cmvc.client.api.Authentication;
import com.ibm.sdwb.cmvc.client.api.ClientDefaults;
import com.ibm.sdwb.cmvc.client.api.Command;
import com.ibm.sdwb.cmvc.client.api.CommandFactory;
import com.ibm.sdwb.cmvc.client.api.CommandResults;
import com.ibm.sdwb.cmvc.client.api.FamilyInfo;
import com.ibm.sdwb.cmvc.client.api.PasswordAuthentication;
import com.ibm.sdwb.cmvc.client.api.SessionData;

public class CmvcUtil {

	public static final int INIT_RET_CODE = -1709;

	private final String hostName;
	private final int portNumber;
	private final String userName;
	private final String password;
	private final String workspace;

	private final FamilyInfo familyInfo = new FamilyInfo();
	private final SessionData sessionData = new SessionData();
	private final String sessionId = String.valueOf(new Random().nextInt());

	public CmvcUtil(String hostName, int portNumber, String userName, String password, String workspace) {
		this.hostName = hostName == null ? "" : hostName;
		this.portNumber = portNumber;
		this.userName = userName == null ? "" : userName;
		this.password = password == null ? "" : password;
		this.workspace = workspace == null ? "" : workspace;
	}

	public int login() {
		int ret = INIT_RET_CODE;
		try {
			familyInfo.setHostName(hostName);
			familyInfo.setPortNumber(portNumber);
			familyInfo.retrieveServerVersion();
			ClientDefaults clientDefaults = new ClientDefaults();
			clientDefaults.setProperty(ClientDefaults.CMVC_ROOT, workspace);
			Authentication authentication = new PasswordAuthentication(userName, userName, password, sessionId);
			sessionData.setClientDefaults(clientDefaults);
			sessionData.setAuthentication(authentication);
			CommandResults commandResults = ((PasswordAuthentication) authentication).login(familyInfo, sessionData);
			logCommandResutls(commandResults);
			ret = commandResults.getReturnCode();
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
		Log.log("cmvc login " + (ret == CommandResults.SUCCESS ? "success" : "fail"));
		return ret;
	}

	public int execute(String commandName) {
		return execute(commandName, new HashMap<String, String>());
	}

	public int execute(String commandName, Map<String, String> commandParameter) {
		int ret = INIT_RET_CODE;
		try {
			Command command = CommandFactory.getInstance().getCommand(commandName);
			command.setFamilyInfo(familyInfo);
			command.setSessionData(sessionData);
			for (String paramName : commandParameter.keySet()) {
				command.addParameterValue(paramName, commandParameter.get(paramName));
			}
			CommandResults commandResults = command.exec();
			ret = commandResults.getReturnCode();
			logCommandResutls(commandResults);
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
		Log.log("cmvc execute " + commandName + " " + (ret == CommandResults.SUCCESS ? "success" : "fail"));
		return ret;
	}

	public int logout() {
		int ret = INIT_RET_CODE;
		try {
			CommandResults commandResults = ((PasswordAuthentication) sessionData.getAuthentication()).logout(familyInfo, sessionData);
			ret = commandResults.getReturnCode();
			logCommandResutls(commandResults);
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
		Log.log("cmvc logout " + (ret == CommandResults.SUCCESS ? "success" : "fail"));
		return ret;
	}

	private void logCommandResutls(CommandResults commandResults) {
		String[] messages = commandResults.getMessages();
		for (int i = 0; i < messages.length; ++i) {
			Log.log(messages[i]);
		}
	}

	public static void main(String[] args) {
		CmvcUtil cu = new CmvcUtil("", -1, "", "", "");
		cu.login();
		String commandName = "FileExtract";
		Map<String, String> commandParameter = new HashMap<String, String>();
		commandParameter.put("-release", "v7r2m0f.dgo");
		commandParameter.put("-extract", "java.pgm/zui.dgo/com/ibm/as400/httpsvr/wpa/gui/WPABase.java");
		cu.execute(commandName, commandParameter);
		cu.logout();
	}

}
