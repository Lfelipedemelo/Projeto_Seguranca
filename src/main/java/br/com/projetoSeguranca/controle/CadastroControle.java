package br.com.projetoSeguranca.controle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.projetoSeguranca.asymetric.RSA;
import br.com.projetoSeguranca.utils.DataSourceMySQL;

@WebServlet("/CadastroControle")
public class CadastroControle extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public CadastroControle() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userLogin = request.getParameter("userLogin");
		String userPassword = request.getParameter("senha");
		
		DataSourceMySQL ds = new DataSourceMySQL();
		String sql = "INSERT INTO USERS(user_login, user_password, user_public_key_path)"
				+ " values (?,?,?)";
		Connection con = ds.getCon();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(sql);
			ps.setString(1, userLogin);
			ps.setString(2, userPassword);
			ps.setString(3, RSA.generateKey(userLogin));
			int result = ps.executeUpdate();
			if (result > 0) {
				// TERMINAR ESTA PARTE
			} else {
				 response.setStatus(500);
				 response.getOutputStream().print("Some error happens");
			}
			ps.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
