
import java.io.*;
import java.net.*;
import java.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.*;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringEscapeUtils;



String Query ="SELECT Nodes.Caption,CPULoad.datetime,CPULoad.AvgLoad,CPULoad.AvgPercentMemoryUsed FROM Orion.Nodes INNER JOIN Orion.CPULoad ON (Nodes.NodeID = CPULoad.NodeID) INNER JOIN Orion.NodesCustomProperties ON (Nodes.NodeID = NodesCustomProperties.NodeID) where NodesCustomProperties.Owner = 'NXP' and Nodes.Caption = 'somehost' and CPULoad.datetime > '2020-01-12T15:00:00.0000000' order by CPULoad.datetime desc"

// Please update the IP address below
String AXLURL = "https://x.x.x.x:17778/SolarWinds/InformationService/v3/Json/Query?query="+URLEncoder.encode(Query, "UTF-8");  
//Please update username and password below
String login = "username" + ":" + "password";
String base64login = new String(Base64.getEncoder().encode(login.getBytes()));


// To ignore SSL errors
SSLContextBuilder builder = new SSLContextBuilder();
builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

HttpGet httpget = new HttpGet(AXLURL);
httpget.setHeader("Authorization", "Basic " + base64login);
httpget.setHeader("Content-Type", "application/json");

HttpResponse response = httpClient.execute(httpget);
StatusLine statusLine = response.getStatusLine();
int statusCode = statusLine.getStatusCode();
HttpEntity entity1 = response.getEntity();
String cidetails = EntityUtils.toString(entity1);

if (statusCode == 200) 
{
    def jsonSlurper = new groovy.json.JsonSlurper()
def loginresp = jsonSlurper.parseText(cidetails)



for (int i=0;i< loginresp.results.size();i++)
{

String x = ""
for (ix in loginresp.results.get(i).keySet())
{

if(ix.toLowerCase().contains("datetime"))
{

x+=Date.parse("yyyy-MM-dd'T'HH:mm:ss", loginresp.results.get(i).get(ix)).format('MM/dd/yyyy hh:mm:ss a')+",";
}
else{
x+=loginresp.results.get(i).get(ix).toString().replaceAll("(^\\h*)|(\\h*\$)","").trim()+","
}


}

println(x)
}
   
} else {
    return ["1", "Failed with error : \nStatus Code : " + statusCode + "\nStatus : " + statusLine+"\nError:"+cidetails];
}