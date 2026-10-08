package com.consilux.servlet.log;

import java.io.PrintStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.consilux.infra.LoggingOutputStream;

/**
 * Servlet implementation class LogServlet
 */
public class LogServlet extends HttpServlet {
	
	private static final long serialVersionUID = 4366688945848104051L;

	public void init(ServletConfig config) throws ServletException {
		//Ajustando variável de sistema, para se tornar disponível para o log4j.
		String localName = System.getenv("COMPUTERNAME");
		
		if (!"".equals(System.getenv("USERDOMAIN"))) {
			localName += "."+System.getenv("USERDOMAIN");
		}
		
		System.setProperty("local.name", localName);		
		
		// make sure everything sent to System.err is logged
		System.setErr(new PrintStream(new LoggingOutputStream(
			Logger.getRootLogger(),
			Level.WARN),
			true));
		
		// make sure everything sent to System.out is also logged
		System.setOut(new PrintStream(new LoggingOutputStream(
			Logger.getRootLogger(),
			Level.DEBUG),
			true));
	}

}
