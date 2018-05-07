package edu.ycp.cs320.ShopEZ.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.ycp.cs320.ShopEZ.controller.InsertItemController;
//import edu.ycp.cs320.ShopEZ.controller.InsertItemController;
import edu.ycp.cs320.ShopEZ.model.Account;
import edu.ycp.cs320.ShopEZ.model.GroceryList;
import edu.ycp.cs320.ShopEZ.model.Item;

public class InsertItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Account login= new Account();
	private GroceryList groceries=new GroceryList();
	private Item item= new Item();
	private InsertItemController controller;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession(false);
		System.out.println("\nInsertItemServlet: doGet");
		req.setAttribute("quantityA", item.getQuantity()); 
		req.setAttribute("quantityR", item.getQuantity()); 
		login= (Account)session.getAttribute("user");
		String user = login.getUsername();
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
		HttpSession session=req.getSession(false);
		String errorMessage = null;
		String successMessage = null;
		login= (Account)session.getAttribute("user");
		if(session.getAttribute("list") != null)
			groceries=((GroceryList)session.getAttribute("grocerylist"));
		controller=new InsertItemController(groceries, login);
		String user = login.getUsername();
		successMessage = "logged in as " +user;
		System.out.println("   User: <" + user + "> logged in");
		System.out.println("\nInsertItemServlet: doPost");		
		boolean passed=false;

		req.setAttribute("quantityA", item.getQuantity()); 
		req.setAttribute("quantityR", item.getQuantity());
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("successMessage", successMessage);

		if(req.getParameter("add") !=null) {
			if(req.getParameter("itemA")!= null) {	
				try {
					item=controller.findItemByName(req.getParameter("itemA"));
					if(getIntFromParameter(req.getParameter("quantityA"))<10 && getIntFromParameter(req.getParameter("quantityA"))>0)
						item.setQuantity(getIntFromParameter(req.getParameter("quantityA")));
					else 
						item.setQuantity(1);
					System.out.println("Name: "+item.getItemName()+" Item ID: "+ item.getItemID()+ " price: " +item.getItemPrice()+" ");
					passed=controller.addItem(login.getAccountID(), item, item.getQuantity());
				} catch (SQLException e) {
					e.printStackTrace();
					errorMessage="Invalid Item";
				}
				if(passed) {
					System.out.println("item inserted into list: "+item.getItemName()+", "+ item.getItemID()+ ", " +item.getItemPrice()+", "+item.getQuantity());
					double sum=item.getItemPrice();
					controller.setTotalPrice(sum);
				}
			}
			else {
				errorMessage="invalid item";
			}
		}
		else if(req.getParameter("rem") !=null) {	
			try {
			if(getIntFromParameter(req.getParameter("quantityR"))<controller.getIdList().size() && getIntFromParameter(req.getParameter("quantityR"))>0)
				item.setQuantity(getIntFromParameter(req.getParameter("quantityR")));
			else 
				item.setQuantity(1);
				item=controller.findItemByName(item.getItemName());
				System.out.println("Name: "+item.getItemName()+" Item ID: "+ item.getItemID()+ " price: " +item.getItemPrice()+" ");
				passed=controller.removeItem(login.getAccountID(), item, item.getQuantity());
			} catch (SQLException e) {
				e.printStackTrace();
				errorMessage="Invalid Item";
			}

			System.out.println("item removed from list: "+item.getItemName()+", "+ item.getItemID()+ ", " +item.getItemPrice()+", "+item.getQuantity());

			// Forward to view to render the result HTML document
		}
		else if(req.getParameter("submit") !=null) {
			try {
				req.getSession().setAttribute("list", controller.findAllItems());
			} catch (SQLException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			req.getSession().setAttribute("user", login);
			resp.sendRedirect(req.getContextPath() + "/review");
			req.getRequestDispatcher("/_view/reviewList.jsp").forward(req, resp);
		}


		req.setAttribute("app", item);
		try {
			req.getSession().setAttribute("list", controller.findItemsByID(controller.getIdList()));
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		// Add parameters as request attributes
		req.setAttribute("Username", req.getParameter("inUsername"));

		// Add result objects as request attributes
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("quantityA", req.getParameter("quantityA")); 
		req.setAttribute("quantityR", item.getQuantity());
		req.getSession().setAttribute("user", login);
		req.getSession().setAttribute("cont", controller);
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("successMessage", successMessage);
		//req.getSession().setAttribute("grocerylist", controller.getGroceryList());
		resp.sendRedirect(req.getContextPath() + "/insertItem");
		req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp);
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
