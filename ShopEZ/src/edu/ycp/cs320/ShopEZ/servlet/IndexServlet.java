package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;

public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Account guest =new Account(); 
	DerbyDatabase db= new DerbyDatabase();
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("Index Servlet: doGet");
		req.getRequestDispatcher("/_view/Shopeze.jsp").forward(req, resp);
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("\nindexServlet: doPost");
		if(req.getParameter("guest") !=null) {
			try {
				guest=db.findAccountByUsername("guest");
			} catch (SQLException e) {
				e.printStackTrace();
			}			
			req.getSession().setAttribute("user", guest);
			resp.sendRedirect(req.getContextPath() + "/insertItem");
			req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp); 
			}
	}
}
