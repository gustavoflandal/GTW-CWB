package com.consilux.ws.client;

public class IWantedPlateListWSProxy implements com.consilux.ws.client.IWantedPlateListWS {
  private String _endpoint = null;
  private com.consilux.ws.client.IWantedPlateListWS iWantedPlateListWS = null;
  
  public IWantedPlateListWSProxy() {
    _initIWantedPlateListWSProxy();
  }
  
  public IWantedPlateListWSProxy(String endpoint) {
    _endpoint = endpoint;
    _initIWantedPlateListWSProxy();
  }
  
  private void _initIWantedPlateListWSProxy() {
    try {
      iWantedPlateListWS = (new com.consilux.ws.client.IWantedPlateListWSserviceLocator()).getIWantedPlateListWSPort();
      if (iWantedPlateListWS != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iWantedPlateListWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iWantedPlateListWS)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iWantedPlateListWS != null)
      ((javax.xml.rpc.Stub)iWantedPlateListWS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.consilux.ws.client.IWantedPlateListWS getIWantedPlateListWS() {
    if (iWantedPlateListWS == null)
      _initIWantedPlateListWSProxy();
    return iWantedPlateListWS;
  }
  
  public void refresh() throws java.rmi.RemoteException{
    if (iWantedPlateListWS == null)
      _initIWantedPlateListWSProxy();
    iWantedPlateListWS.refresh();
  }
  
  
}