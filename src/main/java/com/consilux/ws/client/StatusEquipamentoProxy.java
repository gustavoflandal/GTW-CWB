package com.consilux.ws.client;

public class StatusEquipamentoProxy implements com.consilux.ws.client.StatusEquipamento {
  private String _endpoint = null;
  private com.consilux.ws.client.StatusEquipamento statusEquipamento = null;
  
  public StatusEquipamentoProxy() {
    _initStatusEquipamentoProxy();
  }
  
  public StatusEquipamentoProxy(String endpoint) {
    _endpoint = endpoint;
    _initStatusEquipamentoProxy();
  }
  
  private void _initStatusEquipamentoProxy() {
    try {
      statusEquipamento = (new com.consilux.ws.client.StatusEquipamentoserviceLocator()).getStatusEquipamentoPort();
      if (statusEquipamento != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)statusEquipamento)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)statusEquipamento)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (statusEquipamento != null)
      ((javax.xml.rpc.Stub)statusEquipamento)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.consilux.ws.client.StatusEquipamento getStatusEquipamento() {
    if (statusEquipamento == null)
      _initStatusEquipamentoProxy();
    return statusEquipamento;
  }
  
  public com.consilux.ws.TConnectionStatusEnum getStatusConexao(int idEquipamento) throws java.rmi.RemoteException{
    if (statusEquipamento == null)
      _initStatusEquipamentoProxy();
    return statusEquipamento.getStatusConexao(idEquipamento);
  }
  
  public com.consilux.ws.TPowerStatusEnum getStatusEnergia(int idEquipamento) throws java.rmi.RemoteException{
    if (statusEquipamento == null)
      _initStatusEquipamentoProxy();
    return statusEquipamento.getStatusEnergia(idEquipamento);
  }
  
  public com.consilux.ws.TDIVStatusEnum getStatusDIV(int idEquipamento) throws java.rmi.RemoteException{
    if (statusEquipamento == null)
      _initStatusEquipamentoProxy();
    return statusEquipamento.getStatusDIV(idEquipamento);
  }
  
  
}