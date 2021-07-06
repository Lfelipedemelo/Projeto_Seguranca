package br.com.projetoSeguranca.controle;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.projetoSeguranca.asymetric.RSA;
import br.com.projetoSeguranca.utils.DataSourceMySQL;

@WebServlet("/sendMessage")
public class sendMessageToUser extends HttpServlet {

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet sendMessageToUser</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet sendMessageToUser at " + request.getContextPath() + "</h1>");
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
//		if (request.getSession().getAttribute("user_id") == null) {
//			response.setStatus(500);
//			response.getOutputStream().print("This funciton requires authentication...");
//			return;
//		}

		try {
			String userId = request.getParameter("userId");
			String message = request.getParameter("message");

			DataSourceMySQL ds = new DataSourceMySQL();
			String sql = "SELECT user_public_key_path FROM users where id=?";
			Connection con = ds.getCon();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, Integer.valueOf(userId));
			ResultSet rs = ps.executeQuery();
			String publicKeyPath = null;
			while (rs.next()) {
				publicKeyPath = rs.getString(1);
			}
			if (publicKeyPath != null) {
				ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(publicKeyPath));
				try {
					final PublicKey publicKey = (PublicKey) inputStream.readObject();
					byte[] messageCyphered = RSA.encrypt(message.getBytes(), publicKey);

					File f = new File("message_cyphered");
					FileOutputStream fos = new FileOutputStream(f);
					DataOutputStream dos = new DataOutputStream(fos);
					dos.write(messageCyphered);
					dos.close();
					fos.close();
					response.getOutputStream().print(f.getAbsolutePath());

//					// store in database
//					long from = (long) request.getSession().getAttribute("user_id");
//					long to = Integer.valueOf(userId);
//					String content = new String(messageCyphered);
//
//					ds = new DataSourceMySQL();
//					sql = "insert into messages (`from`,`to`,content) values(?,?,?)";
//					con = ds.getCon();
//					ps = con.prepareStatement(sql);
//					ps.setLong(1, from);
//					ps.setLong(2, to);
//					// ps.setString(3, content);
//					ps.setBytes(3, messageCyphered);// VARBINARY OR BLOB ON MySQL
//					boolean result = ps.execute();
//					if (result) {
//						response.setStatus(200);
//						response.getOutputStream().print("Message sent");
//					} else {
//						// response.setStatus(500);
//						// response.getOutputStream().print("Some error happens");
//					}

				} catch (ClassNotFoundException ex) {
					Logger.getLogger(sendMessageToUser.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		} catch (SQLException ex) {
			Logger.getLogger(sendMessageToUser.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}
