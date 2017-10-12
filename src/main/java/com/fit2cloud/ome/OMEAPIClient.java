package com.fit2cloud.ome;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.CookieRestrictionViolationException;

import com.fit2cloud.ome.XmlUitl.CollectionWrapper;
import com.fit2cloud.ome.model.ArrayDisk;
import com.fit2cloud.ome.model.Device;
import com.fit2cloud.ome.model.DeviceInventoryResponse;
import com.fit2cloud.ome.model.DeviceInventoryResult;
import com.fit2cloud.ome.model.DevicesResponse;
import com.fit2cloud.ome.model.DocumentElement;
import com.fit2cloud.ome.model.VirtualDisk;



public class OMEAPIClient {

    private String username;
    private String password;
    private String port;
    private String host;
    
    /**
     * https://172.16.160.21:2607/api/OME.svc/Devices        设备list
    	 * https://172.16.160.21:2607/api/OME.svc/Devices/24     查单个带id
    	 * https://172.16.160.21:2607/api/OME.svc/Devices/68
    	 * https://172.16.160.21:2607/api/OME.svc/Devices/TableInventory/3      物理卷固定UIR
     * https://172.16.160.21:2607/api/OME.svc/Devices/TableInventory/36     虚拟卷固定UIR
     */
    private static final String DEVICES_ENPOINT = "/api/OME.svc/Devices";
    private static final String ARRAY_DISK_ENPOINT="/api/OME.svc/Devices/TableInventory/3";
    private static final String VIRTUAL_DISK_ENPOINT="/api/OME.svc/Devices/TableInventory/36";
    
   
    public OMEAPIClient(String username, String password, String port, String host) {
		this.username = username;
		this.password = password;
		this.port = port;
		this.host = host;
	}

	/**
     * 统一请求方法
     * @return
	 * @throws LoginException 
	 * @throws IOException 
	 * @throws CookieRestrictionViolationException 
     */
    public String ome_action(RestClient.RequestMethod method,String enpoint,String... params) throws LoginException, IOException, CookieRestrictionViolationException {
    		String URL= "https://" + host + ":" + port + enpoint;
    		if (null!=params) {
    			for (int i = 0; i < params.length; i++) {
        			if (StringUtils.isNotBlank(params[i])) {
        				URL+="/"+params[i];
        			}	
    			}	
		}
    		RestClient client = new RestClient(URL);
    		client.addBasicAuthentication(username, password);
        try {
			client.execute(method);
		} catch (Exception e) {
			throw new IOException(e);
		}
        if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {
			// Successfully connected
			return client.getResponse();
		} else if (client.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
			throw new LoginException(client.getErrorMessage());
		} else if (client.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
			// TODO: find a better exception
			throw new CookieRestrictionViolationException(client.getErrorMessage());
		} else {
			throw new IOException(client.getErrorMessage());
			// error connecting to server, lets just return an error
		}
    }
    /**
     * 获取设备列表
     * @return
     * @throws CookieRestrictionViolationException
     * @throws LoginException
     * @throws IOException
     */
    public List<Device> getDeviceList() throws CookieRestrictionViolationException, LoginException, IOException{
    		XmlUitl xmlUitl=new XmlUitl(DevicesResponse.class,CollectionWrapper.class);
    		String xmlStr=ome_action(RestClient.RequestMethod.GET, DEVICES_ENPOINT);
    		DevicesResponse response = xmlUitl.fromXml(xmlStr);
    		return response.getDevicesResult().getDevices();
    }
    /**
     * 根据Id获取设备
     * @param Id
     * @return
     * @throws CookieRestrictionViolationException
     * @throws LoginException
     * @throws IOException
     */
    public DeviceInventoryResult getDevice(String Id) throws CookieRestrictionViolationException, LoginException, IOException{
		XmlUitl xmlUitl=new XmlUitl(DeviceInventoryResponse.class,CollectionWrapper.class);
		String xmlStr=ome_action(RestClient.RequestMethod.GET, DEVICES_ENPOINT, Id);
		DeviceInventoryResponse response = xmlUitl.fromXml(xmlStr);
		return response.getDeviceInventoryResult();
    }
    /**
     * 获取物理卷
     * @return
     * @throws CookieRestrictionViolationException
     * @throws LoginException
     * @throws IOException
     */
    public List<ArrayDisk> getArrayDiskList() throws CookieRestrictionViolationException, LoginException, IOException{
    		XmlUitl xmlUitl=new XmlUitl(DocumentElement.class,CollectionWrapper.class);
		String xmlStr=ome_action(RestClient.RequestMethod.GET, ARRAY_DISK_ENPOINT);
		DocumentElement response = xmlUitl.fromXml(xmlStr);
		return response.getArrayDisk();
    }
    /**
     * 获取虚拟卷
     * @return
     * @throws CookieRestrictionViolationException
     * @throws LoginException
     * @throws IOException
     */
    public List<VirtualDisk> getVirtualDiskList() throws CookieRestrictionViolationException, LoginException, IOException{
    		XmlUitl xmlUitl=new XmlUitl(DocumentElement.class,CollectionWrapper.class);
		String xmlStr=ome_action(RestClient.RequestMethod.GET, VIRTUAL_DISK_ENPOINT);
		DocumentElement response = xmlUitl.fromXml(xmlStr);
		return response.getVirtualDisk();
    }
    

}