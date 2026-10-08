/**
 * StatusEquipamentoserviceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.consilux.ws.client;

@SuppressWarnings("unchecked")
public class StatusEquipamentoserviceLocator extends org.apache.axis.client.Service implements com.consilux.ws.client.StatusEquipamentoservice {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2362318397556507754L;

	public StatusEquipamentoserviceLocator() {
    }


    public StatusEquipamentoserviceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public StatusEquipamentoserviceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for StatusEquipamentoPort
    private java.lang.String StatusEquipamentoPort_address = "http://localhost:8080/soap/StatusEquipamento";

    public java.lang.String getStatusEquipamentoPortAddress() {
        return StatusEquipamentoPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String StatusEquipamentoPortWSDDServiceName = "StatusEquipamentoPort";

    public java.lang.String getStatusEquipamentoPortWSDDServiceName() {
        return StatusEquipamentoPortWSDDServiceName;
    }

    public void setStatusEquipamentoPortWSDDServiceName(java.lang.String name) {
        StatusEquipamentoPortWSDDServiceName = name;
    }

    public com.consilux.ws.client.StatusEquipamento getStatusEquipamentoPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(StatusEquipamentoPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getStatusEquipamentoPort(endpoint);
    }

    public com.consilux.ws.client.StatusEquipamento getStatusEquipamentoPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.consilux.ws.client.StatusEquipamentobindingStub _stub = new com.consilux.ws.client.StatusEquipamentobindingStub(portAddress, this);
            _stub.setPortName(getStatusEquipamentoPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setStatusEquipamentoPortEndpointAddress(java.lang.String address) {
        StatusEquipamentoPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.consilux.ws.client.StatusEquipamento.class.isAssignableFrom(serviceEndpointInterface)) {
                com.consilux.ws.client.StatusEquipamentobindingStub _stub = new com.consilux.ws.client.StatusEquipamentobindingStub(new java.net.URL(StatusEquipamentoPort_address), this);
                _stub.setPortName(getStatusEquipamentoPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("StatusEquipamentoPort".equals(inputPortName)) {
            return getStatusEquipamentoPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://consilux.com", "StatusEquipamentoservice");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://consilux.com", "StatusEquipamentoPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("StatusEquipamentoPort".equals(portName)) {
            setStatusEquipamentoPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
