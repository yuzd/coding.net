package coding.net.plugin.subscriber;

import coding.net.common.XSSApi;
import coding.net.plugin.webhook.CDEvent;
import hudson.Extension;
import hudson.model.Job;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static coding.net.plugin.webhook.CDEvent.PUSH;
import static com.google.common.collect.Sets.immutableEnumSet;
import static net.sf.json.JSONObject.fromObject;

@Extension
@SuppressWarnings("unused")
public class PushCDEventSubscriber extends CDEventsSubscriber {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushCDEventSubscriber.class);

    @Override
    protected boolean isApplicable(Job<?, ?> project) {
        return false;
    }


    @Override
    protected Set<CDEvent> events() {
        return immutableEnumSet(PUSH);
    }

    @Override
    protected void onEvent(CDEvent event, String payload) {
        LOGGER.info("{} webhook received from repo <{}>!", event, payload);
        try {
            JSONObject parsedPayload = fromObject(payload);
            String jobToken = parsedPayload.getString("token");
            String login = null;
            String password = null;
            String workSpace = Jenkins.getInstance().getRootDir().getPath();

            String pluginSpace = workSpace + "\\coding.net.plugin.CodingProjectProperty.xml";
            File fXmlFile = new File(pluginSpace);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("coding.net.plugin.CodingProjectProperty_-DescriptorImpl");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Element eElement = (Element) nNode;
                login = eElement.getElementsByTagName("login").item(0).getTextContent();
                password = eElement.getElementsByTagName("password").item(0).getTextContent();
                break;
            }

            String jobSpace = workSpace + "\\jobs\\";
            Collection<String> jobNames = new ArrayList<String>();
            File jobFolder=new File(jobSpace);
            File[] tempList = jobFolder.listFiles();
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isDirectory()) {
                    jobNames.add(tempList[i].getName());
                }
            }
            LOGGER.info("jobNames Count <{}>!", jobNames.size());
            String jobName = null ;
            //Map<String, String> jobs = new HashMap<String, String>();
            for (String job : jobNames){

                String jobPath = jobSpace + "\\" + job + "\\config.xml";
                LOGGER.info("jobPath <{}>!", jobPath);

                //dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(jobPath);
                doc.getDocumentElement().normalize();
                nList = doc.getElementsByTagName("authToken");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    Element eElement = (Element) nNode;
                    String auth = eElement.getTextContent();
                    if (auth.equals(jobToken)){
                        jobName = job;
                        break;
                    }
                    //jobs.put(job,auth);
                }
            }
            LOGGER.info("{} || {}  <{}>!", login, password,jobName);
            if (login != null && password != null && jobName !=null){
                //String remoteJobUrl = Jenkins.getInstance().getRootUrl() + "/job/aaaaaa/build?token=" + jobToken;
                String remoteJobUrl = Jenkins.getInstance().getRootUrl() + "/job/"+jobName+"/build?token=" + jobToken;
                XSSApi.load(remoteJobUrl, login, password, false);
            }
        } catch (Exception e) {
            LOGGER.error("{} webhook error !", event);
        }
    }
}