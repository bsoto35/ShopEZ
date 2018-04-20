package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import edu.ycp.cs320.ShopEZ.controller.InsertItemController;
import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.Item;
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;

public class InsertItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Account login= new Account();
	//private GroceryList grocerys;
	private Item item= new Item();
	//private InsertItemController controller;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("check1");
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
		System.out.println("check2");
		String user = (String) req.getSession().getAttribute("user");
		DerbyDatabase db= new DerbyDatabase();
		System.out.println("\nInsertItemServlet: doPost");		
		String errorMessage = null;
		String successMessage = null;
		login= new Account();
		//controller= new InsertItemController();
		int amount=1;
		boolean passed;
		//login=controller.getAccountbyUser(user);
		try {
			//item=controller.findItemByName(name);
			login=db.findAccountByUsername(user);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage="Invalid Item";
		}
		req.setAttribute("quantityA", amount); 
		req.setAttribute("quantityR", amount); 
		if(req.getParameter("add") !=null) {
			if(req.getParameter("itemA")!= null) {				
				amount = getIntFromParameter(req.getParameter("quantityA"));
				item.setItemName(req.getParameter("itemA"));
				try {
					//item=controller.findItemByName(name);
					item=db.findItemByItemName(item.getItemName());
					System.out.println(""+item.getItemName()+" "+ item.getItemID()+ " " +item.getItemPrice()+" ");
					passed=db.insertItemIntoGroceryListTable(login.getAccountID(), item, amount);//use database method
				} catch (SQLException e) {
					e.printStackTrace();
					errorMessage="Invalid Item";
				}
				System.out.println("item inserted into list: "+item.getItemName()+" "+ item.getItemID()+ " " +item.getItemPrice()+" ");
				double sum=item.getItemPrice();
			}	
		}
		else if(req.getParameter("rem") !=null) {	
			amount = getIntFromParameter(req.getParameter("quantityR"));
			item.setItemName(req.getParameter("itemR"));
			try {
				item=db.findItemByItemName(item.getItemName());
				System.out.println(""+item.getItemName()+" "+ item.getItemID()+ " " +item.getItemPrice()+" ");
				passed=db.removeItemFromGroceryListTable(login.getAccountID(), item, amount);
				//successMessage=db.removeItemFromTheList(login, remItem, amount);;
				System.out.println("test1");
			} catch (SQLException e) {
				e.printStackTrace();
				errorMessage="Invalid Item";
			}
			System.out.println("item removed into list: "+item.getItemName()+" "+ item.getItemID()+ " " +item.getItemPrice()+" ");

			//passed=controller.removeItem(login, item, amount);
			// Forward to view to render the result HTML document
		}	
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("successMessage", successMessage);

		req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp);
		
		req.setAttribute("app", item);
		// Add parameters as request attributes
		req.setAttribute("Username", req.getParameter("inUsername"));

		// Add result objects as request attributes
		req.setAttribute("errorMessage", errorMessage);
		
	}

	// Decode form parameters and dispatch to controller

	// Add parameters as request attributes
	private Integer getIntFromParameter(String s) {
		if (s == null || s.equals("")) {
			return null;
		} else {
			return Integer.parseInt(s);
		}
	}


}
