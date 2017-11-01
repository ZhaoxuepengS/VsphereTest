package com.vmware.vm;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.xml.ws.BindingProvider;

import com.vmware.connection.ConnectedVimServiceBase;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;
public class VMSnapshotTest extends ConnectedVimServiceBase{
	
		private static class TrustAllTrustManager implements javax.net.ssl.TrustManager,
		javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
		}
		public Boolean isServerTrusted (X509Certificate[] certss) {
		return true;
		}
		public boolean isClientTrusted(X509Certificate[] certs) {
		return true;
		}
		public void checkServerTrusted(java.security.cert.X509Certificate[] certs,String
		authType) throws java.security.cert.CertificateException {
		return;
		}
		public void checkClientTrusted(java.security.cert.X509Certificate[] certs,String
		authType) throws java.security.cert.CertificateException {
		return;
		}
		}

			String vmMor;
		    String snapshotName;
		    String desc;
	    
		    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException{
		    try{
		    	String servername = "10.24.15.15";
		    	String username = "auto";
		    	String password = "Test6530";
		    	String url = "https://"+servername+"/sdk/vimService";
		    	
		    	ManagedObjectReference SVC_INST_REF = new ManagedObjectReference();
		    	VimService vimService;
		    	VimPortType vimPort;
		    	ServiceContent serviceContent;
	    	
		    	HostnameVerifier hv = new HostnameVerifier() {
		    		public boolean verify(String urlHostName, SSLSession session){
		    		return true;
		    		}
		    		};
	    		
	    		TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
	    		TrustManager tm = new TrustAllTrustManager();
	    		trustAllCerts[0] = tm;
	    		SSLContext sc = SSLContext.getInstance("SSL");
	    		SSLSessionContext sslsc = sc.getServerSessionContext();
	    		
	    		sslsc.setSessionTimeout(0);
	    		sc.init(null, trustAllCerts, null);
	    		
	    		
	    		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    		//Set the default host name verifier to enable the connection
	    		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	    		//Set up the manufactured managed object reference for the ServiceInstance
	    		SVC_INST_REF.setType("ServiceInstance");
	    		SVC_INST_REF.setValue("ServiceInstance");
	    		//Create a VimService object to obtain a VimPort binding provider
	    		//The BindingProvider provides access to the protocol fields
	    		//in request/response messages.Retrieve the request context
	    		//which will be used for processing message requests.
	    		vimService = new VimService();
	    		vimPort = vimService.getVimPort();
	    		Map<String, Object> ctxt = ((BindingProvider) vimPort).getRequestContext();
	    				
	    		ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
	    		ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
	    		//Retrieve the ServiceContent object and login
	    		serviceContent = vimPort.retrieveServiceContent(SVC_INST_REF);
	    		vimPort.login(serviceContent.getSessionManager(), username, password, null);
	    		//print out the product name,server type ,and product version
	    		System.out.println(serviceContent.getAbout().getFullName());
	    		System.out.println("Server type is " + serviceContent.getAbout().getApiType());
	    		System.out.println("API version is " + serviceContent.getAbout().getVersion());
	    		//close the connection
	    		vimPort.logout(serviceContent.getSessionManager());
	    		} catch( Exception e) {
	    		System.out.println("Conncetion Failed");
	    		e.printStackTrace();
	    		}
	    				
		    }
			
}


