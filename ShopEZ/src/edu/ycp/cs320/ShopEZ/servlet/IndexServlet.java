package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdk.nashorn.internal.ir.RuntimeNode.Request;

public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		System.out.println("Index Servlet: doGet");
		req.getRequestDispatcher("/_view/Shopeze.jsp").forward(req, resp);
		
		if(req.getParameter("Login") !=null)
			req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
		else if(req.getParameter("Continue as Guest") !=null)
			
			req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp);
	}
}
