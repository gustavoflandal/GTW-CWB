package com.consilux.ws.client;

public class IConfigEquipChangeProxy implements com.consilux.ws.client.IConfigEquipChange {
  private String _endpoint = null;
  private com.consilux.ws.client.IConfigEquipChange iConfigEquipChange = null;
  
  public IConfigEquipChangeProxy() {
    _initIConfigEquipChangeProxy();
  }
  
  public IConfigEquipChangeProxy(String endpoint) {
    _endpoint = endpoint;
    _initIConfigEquipChangeProxy();
  }
  
  private void _initIConfigEquipChangeProxy() {
    try {
      iConfigEquipChange = (new com.consilux.ws.client.IConfigEquipChangeserviceLocator()).getIConfigEquipChangePort();
      if (iConfigEquipChange != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iConfigEquipChange)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iConfigEquipChange)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iConfigEquipChange != null)
      ((javax.xml.rpc.Stub)iConfigEquipChange)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.consilux.ws.client.IConfigEquipChange getIConfigEquipChange() {
    if (iConfigEquipChange == null)
      _initIConfigEquipChangeProxy();
    return iConfigEquipChange;
  }
  
  public void notifyChange(int idEquipamento) throws java.rmi.RemoteException{
    if (iConfigEquipChange == null)
      _initIConfigEquipChangeProxy();
    iConfigEquipChange.notifyChange(idEquipamento);
  }
  
  
}