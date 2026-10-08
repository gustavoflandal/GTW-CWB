//package com.consilux.rest.resources;
//
//import java.io.IOException;
//import java.sql.SQLException;
//
//import javax.naming.AuthenticationException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//
//import com.consilux.infra.exception.ConexaoException;
//import com.consilux.model.Acesso;
//import com.consilux.model.Infracao;
//import com.consilux.model.beans.InfoInfracaoBean;
//import com.sun.jersey.api.NotFoundException;
//import com.sun.jersey.api.Responses;
//
//@Path("/InfoInfracao")
//public class InfoInfracaoResource {
//
//	@Context
//	HttpServletRequest request;
//	
//	@Context
//	HttpServletResponse response;
//	
//	@QueryParam("id_infracao" )
//	int idInfracao;	
//	
//	@GET
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//	public InfoInfracaoBean getInfoInfracao() {
//	
//
//		InfoInfracaoBean infoInfracao = null;
//		
//		try {
//
//			if (!new Acesso(request, response, false).verificaAcesso(false))
//				throw new WebApplicationException(new AuthenticationException("Usuário não atenticado!"), 401); //401 Unauthorized 			
//
//			if (idInfracao <= 0)
//				throw new WebApplicationException(Responses.CLIENT_ERROR);
//			
//			Infracao infracao = Infracao.buscaInfracaoPorId(idInfracao);
//
//			if (infracao == null) {
//				throw new NotFoundException("Infração não encontrada");
//			}
//			infoInfracao = new InfoInfracaoBean(infracao);
//		}
//		catch (ConexaoException ce)
//		{
//			throw new WebApplicationException(ce, 500); // 500 Internal Server Error
//		}
//		catch (SQLException se)
//		{
//			throw new WebApplicationException(se, 500); // 500 Internal Server Error
//		}
//		catch (IOException ie) {
//			throw new WebApplicationException(ie, 500); // 500 Internal Server Error
//		}
//		return infoInfracao;
//		
//	}
//	
//	
//}
