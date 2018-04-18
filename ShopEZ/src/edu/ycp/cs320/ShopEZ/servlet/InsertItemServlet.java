package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.persist.DatabaseProvider;
import edu.ycp.cs320.ShopEZ.persist.IDatabase;

public class InsertItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Account login= new Account();
	private GroceryList grocerys;
	private Item newItem; 

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("\nInsertItemServlet: doGet");

		String user = (String) req.getSession().getAttribute("user");
		if (user == null) {
			System.out.println("   User: <" + user + "> not logged in or session timed out");

			// user is not logged in, or the session expired
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		// now we have the user's User object,
		// proceed to handle request...

		System.out.println("   User: <" + user + "> logged in");

		req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("\nInsertItemServlet: doPost");		
		IDatabase db = DatabaseProvider.getInstance();
		String errorMessage = null;
		boolean validLogin  = false;
		login= new Account();
		req.setAttribute("quantity", 1); 
		double amount=1;
		req.setAttribute("quantityA", amount); 
		if(req.getParameter("Add") !=null) {
			if(req.getParameter("Item")!= null) {				
				int amount = req.getParameter("quantity");
				amount = getDoubleFromParameter(req.getParameter("quantityA"));
				newItem.setItemName(req.getParameter("Add"));
				try {
					public class InsertItemServlet extends HttpServlet {
					} catch (SQLException e) {
						e.printStackTrace();
						errorMessage="Invalid Item";


					}
					grocerys.addItem(newItem, amount);
					grocerys.setAccountID(login.getAccountID());
					double sum= grocerys.getTotalPrice(); 
					grocerys.setListPrice(sum);
				}	
			}
			if(req.getParameter("Remove") !=null) {


				removeItemFromTheList(remItem, amount);
			}

			String successMessage = null;
			public class InsertItemServlet extends HttpServlet {
				// Forward to view to render the result HTML document
				req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp);
			}	
			private Double getDoubleFromParameter(String s) {
				if (s == null || s.equals("")) {
					return null;
				} else {
					return Double.parseDouble(s);
				}
			}
		}else {

		}
	}
}