/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: com.consilux.lib

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 30/03/2011

  Descricao: XXX

  Historico:

    $Log$

*********************************************************************************/
package com.consilux.lib;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import de.tu_darmstadt.informatik.rbg.mhartle.sabre.DataReference;
import de.tu_darmstadt.informatik.rbg.mhartle.sabre.Element;
import de.tu_darmstadt.informatik.rbg.mhartle.sabre.Fixup;
import de.tu_darmstadt.informatik.rbg.mhartle.sabre.HandlerException;
import de.tu_darmstadt.informatik.rbg.mhartle.sabre.StreamHandler;
import de.tu_darmstadt.informatik.rbg.mhartle.sabre.impl.FileFixup;

/**
 * XXX
 * @author fos
 * @version $Revision$ $Date$ $Author$
 */

public class CSXISOImageFileHandler implements StreamHandler {

    private File arqParc = null;
    private File arqOrig = null;
    private RandomAccessFile raFile = null;
    private DataOutputStream dataOutputStream = null;
    private long position = 0;

    /**
     * ISO Image File Handler
     *
     * @param file ISO image output file
     *
     * @throws FileNotFoundException File not found
     */
    public CSXISOImageFileHandler(File file) throws FileNotFoundException {
    	this.arqOrig = file;
        this.arqParc = new File(file.getPath()+".parc");
        this.raFile = new RandomAccessFile(arqParc, "rw");
        this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.arqParc)));
    }

    public void startDocument() throws HandlerException {
        // nothing to do here
    }

    public void startElement(Element element) throws HandlerException {
        // nothing to do here
    }

    public void data(DataReference reference) throws HandlerException {
        InputStream inputStream = null;
        byte[] buffer = null;
        int bytesToRead = 0;
        int bytesHandled = 0;
        int bufferLength = 65535;
        long lengthToWrite = 0;
        long length = 0;

        try {
            buffer = new byte[bufferLength];
            length = reference.getLength();
            lengthToWrite = length;
            inputStream = reference.createInputStream();
            while (lengthToWrite > 0) {
                if (lengthToWrite > bufferLength) {
                    bytesToRead = bufferLength;
                } else {
                    bytesToRead = (int) lengthToWrite;
                }

                bytesHandled = inputStream.read(buffer, 0, bytesToRead);
                if (bytesHandled == -1) {
                    throw new HandlerException("Cannot read all data from reference.");
                }

                dataOutputStream.write(buffer, 0, bytesHandled);
                lengthToWrite -= bytesHandled;
                position += bytesHandled;
            }
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new HandlerException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
            } catch (IOException e) {
            }
        }
    }

    public Fixup fixup(DataReference reference) throws HandlerException {
        Fixup fixup = null;
        fixup = new FileFixup(raFile, position, reference.getLength());
        data(reference);
        return fixup;
    }

    public long mark() throws HandlerException {
        return position;
    }

    public void endElement() throws HandlerException {
        // nothing to do here
    }

    public void endDocument() throws HandlerException {
        try {
            this.dataOutputStream.close();
        	this.raFile.close();
        	this.arqParc.renameTo(this.arqOrig);
        } catch (IOException e) {
            throw new HandlerException(e);
        }
    }
}
