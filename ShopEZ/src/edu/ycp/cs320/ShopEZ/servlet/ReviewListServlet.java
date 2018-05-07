package edu.ycp.cs320.ShopEZ.servlet;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.ShopEZ.controller.InsertItemController;
import edu.ycp.cs320.ShopEZ.controller.ReviewListController;
import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;

public class ReviewListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Account login= new Account();
	private GroceryList groceries=new GroceryList();
	private Item item= new Item();
	private InsertItemController controller;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession(false);
		System.out.println("Review List Servlet: doGet");
		login= (Account)session.getAttribute("user");
		controller=(InsertItemController)session.getAttribute("cont");
		String user = login.getUsername();
		if (user == null) {
			System.out.println("   User: <" + user + "> not logged in or session timed out");

			// user is not logged in, or the session expired
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		try {
			req.getSession().setAttribute("list", controller.findItemsByID(controller.getIdList()));
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}

		// now we have the user's User object,
		// proceed to handle request...

		System.out.println("   User: <" + user + "> logged in");

		req.getRequestDispatcher("/_view/reviewList.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession(false);
		login= (Account)session.getAttribute("user");
		controller=(InsertItemController)session.getAttribute("cont");
		if(req.getParameter("create") !=null) {
			try {
				req.getSession().setAttribute("list", controller.findAllItems());
			} catch (SQLException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			req.getSession().setAttribute("user", login);
			resp.sendRedirect(req.getContextPath() + "/route");
			req.getRequestDispatcher("/_view/Route.jsp").forward(req, resp);
		}
		for(int i=0; i<controller.getIdList().size(); i++)
			if(req.getParameter(""+controller.getIdList().get(i)) !=null) {
				try {
					
					controller.removeItem(login.getAccountID(), controller.findItemByID(controller.getIdList().get(i)), 1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				req.getRequestDispatcher("/_view/reviewList.jsp").forward(req, resp);
			}

	}

}
