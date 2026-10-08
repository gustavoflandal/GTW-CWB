/**
 * IConfigEquipChangeserviceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.consilux.ws.client;

public class IConfigEquipChangeserviceLocator extends org.apache.axis.client.Service implements com.consilux.ws.client.IConfigEquipChangeservice {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2026347975883493383L;

	public IConfigEquipChangeserviceLocator() {
    }


    public IConfigEquipChangeserviceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public IConfigEquipChangeserviceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for IConfigEquipChangePort
    private java.lang.String IConfigEquipChangePort_address = "http://localhost:8080/soap/IConfigEquipChange";

    public java.lang.String getIConfigEquipChangePortAddress() {
        return IConfigEquipChangePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String IConfigEquipChangePortWSDDServiceName = "IConfigEquipChangePort";

    public java.lang.String getIConfigEquipChangePortWSDDServiceName() {
        return IConfigEquipChangePortWSDDServiceName;
    }

    public void setIConfigEquipChangePortWSDDServiceName(java.lang.String name) {
        IConfigEquipChangePortWSDDServiceName = name;
    }

    public com.consilux.ws.client.IConfigEquipChange getIConfigEquipChangePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(IConfigEquipChangePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getIConfigEquipChangePort(endpoint);
    }

    public com.consilux.ws.client.IConfigEquipChange getIConfigEquipChangePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.consilux.ws.client.IConfigEquipChangebindingStub _stub = new com.consilux.ws.client.IConfigEquipChangebindingStub(portAddress, this);
            _stub.setPortName(getIConfigEquipChangePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setIConfigEquipChangePortEndpointAddress(java.lang.String address) {
        IConfigEquipChangePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.consilux.ws.client.IConfigEquipChange.class.isAssignableFrom(serviceEndpointInterface)) {
                com.consilux.ws.client.IConfigEquipChangebindingStub _stub = new com.consilux.ws.client.IConfigEquipChangebindingStub(new java.net.URL(IConfigEquipChangePort_address), this);
                _stub.setPortName(getIConfigEquipChangePortWSDDServiceName());
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
        if ("IConfigEquipChangePort".equals(inputPortName)) {
            return getIConfigEquipChangePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://consilux.com", "IConfigEquipChangeservice");
    }

    private java.util.HashSet ports = null;

    @SuppressWarnings("unchecked")
	public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://consilux.com", "IConfigEquipChangePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("IConfigEquipChangePort".equals(portName)) {
            setIConfigEquipChangePortEndpointAddress(address);
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
