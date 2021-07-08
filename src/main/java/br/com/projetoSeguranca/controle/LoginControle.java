package br.com.projetoSeguranca.controle;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.projetoSeguranca.utils.DataSourceMySQL;


@WebServlet("/LoginControle")
public class LoginControle extends HttpServlet {
	private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet authentication</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet authentication at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
	
    protected void logOut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
			request.getSession().removeAttribute("user_id");
		} catch (Exception e) {
			e.getMessage();
		}
    
    }
    
    public LoginControle() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String login = request.getParameter("login");
			String password = request.getParameter("senha");
			long id = -1;
			
			DataSourceMySQL ds = new DataSourceMySQL();
			String sql = "SELECT id FROM users where user_login=? and user_password=sha2(?,512)";
			Connection con = ds.getCon();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, login);
			ps.setInt(2, Integer.parseInt(password));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("id");
			}		
			if(id == -1) {
				response.setStatus(500);
				response.getOutputStream().print("Error: Credentials not valid.");
			} else {
				response.sendRedirect("home.html");
				request.getSession().setAttribute("user_id", id);
			}
		} catch (Exception e) {
			
		}
	}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
	
}
