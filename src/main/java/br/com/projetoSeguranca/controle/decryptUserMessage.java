/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.projetoSeguranca.controle;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;

import br.com.projetoSeguranca.asymetric.RSA;
import br.com.projetoSeguranca.utils.DataSourceMySQL;

@WebServlet("/decryptUserMessage")
public class decryptUserMessage extends HttpServlet {

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet decryptUserMessage</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet decryptUserMessage at " + request.getContextPath() + "</h1>");
			out.println("</body>");
			out.println("</html>");
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
	// + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (request.getSession().getAttribute("user_id") == null) {
			response.setStatus(500);
			response.getOutputStream().print("This funciton requires authentication...");
			return;
		}
		long userId = (long) request.getSession().getAttribute("user_id");

		byte[] message = null;
		PrivateKey privateKey = null;
		response.setContentType("text/html;charset=UTF-8");
		if (FileUpload.isMultipartContent(request)) {
			try {
				DiskFileUpload upload = new DiskFileUpload();
				upload.setSizeMax(50 * 1024 * 1024);// 50Mb
				List items = upload.parseRequest(request);
				Iterator it = items.iterator();
				while (it.hasNext()) {
					FileItem fitem = (FileItem) it.next();

					String fileName = fitem.getName();
//					if (!fitem.isFormField()) {
//						File f = new File(fileName);
//						FileOutputStream fo = new FileOutputStream(f);
//						DataOutputStream dados = new DataOutputStream(fo);
//						byte[] b = fitem.get();
//						dados.write(b, 0, (int) fitem.getSize());
//						dados.close();
//						fo.close();
//						// response.getOutputStream().print("Uploaded file: "+f.getAbsolutePath());
//						// System.out.println("Uploaded file: "+f.getAbsolutePath());
//
//						if (fitem.getFieldName().equals("message")) {
//							message = b;
//						} else if (fitem.getFieldName().equals("private_key")) {
//							ObjectInputStream inputStream = new ObjectInputStream(
//									new FileInputStream(f.getAbsolutePath()));
//							privateKey = (PrivateKey) inputStream.readObject();
//
//						}
//
//						// response.getOutputStream().write(originalContent);
//					}
				}

				byte[] originalContent = null;

				DataSourceMySQL ds = new DataSourceMySQL();
				String sql = "SELECT content FROM messages where `to`=?";
				Connection con = ds.getCon();
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setLong(1, userId);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					// String content=rs.getString(1);
					byte[] content = rs.getBytes("content");// varbinary or blob columns
					try {
						originalContent = RSA.decrypt(content, privateKey);
						response.getOutputStream().print("<br/>");
						response.getOutputStream().print(new String(originalContent));
					} catch (Exception e) {
						response.getOutputStream().print("Not possile to decript:" + new String(content));
						e.printStackTrace();
					}
				}

			} catch (FileUploadException e) {
				e.printStackTrace();
				System.out.println("Erro: " + e.getMessage());
			} /*catch (ClassNotFoundException ex) {
				Logger.getLogger(decryptUserMessage.class.getName()).log(Level.SEVERE, null, ex);
			}*/ catch (SQLException ex) {
				Logger.getLogger(decryptUserMessage.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
