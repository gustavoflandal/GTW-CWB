/**
 * StatusEquipamento.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.consilux.ws.client;

public interface StatusEquipamento extends java.rmi.Remote {
    public com.consilux.ws.TConnectionStatusEnum getStatusConexao(int idEquipamento) throws java.rmi.RemoteException;
    public com.consilux.ws.TPowerStatusEnum getStatusEnergia(int idEquipamento) throws java.rmi.RemoteException;
    public com.consilux.ws.TDIVStatusEnum getStatusDIV(int idEquipamento) throws java.rmi.RemoteException;
}
