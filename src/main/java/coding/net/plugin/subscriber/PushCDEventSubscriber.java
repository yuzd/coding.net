package coding.net.plugin.subscriber;

import coding.net.common.XSSApi;
import coding.net.plugin.CodingWebHook;
import coding.net.plugin.webhook.CDEvent;
import hudson.Extension;
import hudson.model.Job;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
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
        LOGGER.debug("{} webhook received from repo <{}>!", event, payload);
        try {
            excute(payload);
        } catch (Exception e) {
            LOGGER.error("{} webhook error :<{}>!", event,e);
        }
    }

    /**
     * 1 获取coding传过来的token(其他的字段暂时没有需要)
     * 2 从plugin配置文件中获取用户名和密码
     * 3 遍历job下的config文件获取成功匹配到job配置的Token
     * 4 如果一致那么启动job编译
     * @param payload
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private void excute(String payload) throws ParserConfigurationException, SAXException, IOException {
        JSONObject parsedPayload = fromObject(payload);
        String jobToken = parsedPayload.getString("token");//从coding传过来的数据中拿到Token
        String login = null;
        String password = null;
        String workSpace = Jenkins.getInstance().getRootDir().getPath();

        //从plugin配置文件中获取用户名和密码
        String pluginSpace = FilenameUtils.concat(workSpace, CodingWebHook.PLUGIN_XML_NAME);
        File fXmlFile = new File(pluginSpace);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(CodingWebHook.PLUGIN_XML_NODE_NAME);
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            Element eElement = (Element) nNode;
            login = eElement.getElementsByTagName("login").item(0).getTextContent();
            password = eElement.getElementsByTagName("password").item(0).getTextContent();
            break;
        }

        //根据jobs目录找到所有的job名称 然后遍历job下的config文件获取job配置的Token
        //这里要说明一点：用jenkins提供的获取job名称集合的方法 调试时可以获取，发布到生产获取为空。
        //所以采取遍历文件夹的方式
        String jobSpace = FilenameUtils.concat(workSpace, "jobs");
        Collection<String> jobNames = new ArrayList<String>();
        File jobFolder=new File(jobSpace);
        File[] tempList = jobFolder.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()) {
                jobNames.add(tempList[i].getName());
            }
        }
        String jobName = null ;
        for (String job : jobNames){
            String jobPath = FilenameUtils.concat(FilenameUtils.concat(jobSpace , job ), "config.xml");
            LOGGER.debug("jobPath <{}>!", jobPath);
            doc = dBuilder.parse(jobPath);
            doc.getDocumentElement().normalize();
            nList = doc.getElementsByTagName("authToken");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Element eElement = (Element) nNode;
                String auth = eElement.getTextContent();
                if (auth.equals(jobToken)){
                    //成功匹配一个之后就不再继续匹配下去。
                    //需要注意的是 如果不同的job 配置同样的 token 会出问题 所以生产上使用要避免不同的job 配置同样的 token
                    jobName = job;
                    LOGGER.debug("job token match success <{} - {}>!", jobName,jobToken);
                    break;
                }
            }
        }

        if (login != null && password != null && jobName !=null){
            //String remoteJobUrl = Jenkins.getInstance().getRootUrl() + "/job/aaaaaa/build?token=" + jobToken;
            String remoteJobUrl = Jenkins.getInstance().getRootUrl() + "/job/"+jobName+"/build?token=" + jobToken;
            XSSApi.load(remoteJobUrl, login, password, false);
            LOGGER.debug("job start success <{}>!", jobName);
        }
    }
}