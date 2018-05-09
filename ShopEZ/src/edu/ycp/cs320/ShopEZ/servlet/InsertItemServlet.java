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
	private Item item= new Item();
	private InsertItemController controller= new InsertItemController();
	String errorMessage = null;
	String successMessage = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession(false);
		System.out.println("\nInsertItemServlet: doGet");
		String user=null;
		errorMessage=(String)session.getAttribute("errorMessage");
		successMessage=(String)session.getAttribute("successMessage");
		if(session.getAttribute("user") != null) {
			login= (Account)session.getAttribute("user");
			user = login.getUsername();
		}
		if (user == null) {
			System.out.println("   User: <" + user + "> not logged in or session timed out");

			// user is not logged in, or the session expired
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		// now we have the user's User object,
		// proceed to handle request...

		System.out.println("   User: <" + user + "> logged in");
		try {
			req.getSession().setAttribute("entireList", controller.getAllItems());
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		req.getRequestDispatcher("/_view/insertItem.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession(false);
		login = (Account)session.getAttribute("user");
		if(session.getAttribute("groceryList") != null) {
			System.out.print("Grocery List is not empty");
			controller=new InsertItemController((GroceryList)session.getAttribute("groceryList"), login);
		}
		String user = login.getUsername();
		int userID=login.getAccountID();
		successMessage = "logged in as " +user;
		System.out.println("   User: <"+userID + ", "+user + "> logged in");
		System.out.println("\nInsertItemServlet: doPost");		
		boolean passed=false;


		errorMessage=(String)session.getAttribute("errorMessage");
		successMessage=(String)session.getAttribute("successMessage");

		//if the add button is pressed
		if(req.getParameter("add") !=null && req.getParameter("itemA")!= null) {	
			try {
				//gets the item by name and saves it
				item=controller.findItemByName(req.getParameter("itemA"));
				if(getIntFromParameter(req.getParameter("quantityA"))<10 && getIntFromParameter(req.getParameter("quantityA"))>0) {
					controller.setQuantity(getIntFromParameter(req.getParameter("quantityA")));
				}
				else {
					errorMessage="More than 10 items are being added or less than 0 items. Defaulted to 1";
					controller.setQuantity(1);
				}
				System.out.println("Name: "+item.getItemName()+" Item ID: "+ item.getItemID()+ " price: " +item.getItemPrice()+" ");
				passed=controller.addItem(login.getAccountID(), item, controller.getQuantity());
				successMessage="Added "+controller.getQuantity()+" "+item.getItemName();
			} catch (SQLException e) {
				e.printStackTrace();
				errorMessage="Invalid Item";
			}
			if(passed) {
				System.out.println("item inserted into list: "+item.getItemName()+", "+ item.getItemID()+ ", " +item.getItemPrice()+", "+controller.getQuantity());
				double sum=item.getItemPrice();
				controller.setTotalPrice(sum);
			}
			else {
				errorMessage="invalid item";
			}
		}
		else if(req.getParameter("rem") !=null && req.getParameter("itemR")!= null) {	
			try {
				if(getIntFromParameter(req.getParameter("quantityR"))<controller.getIdList().size() && getIntFromParameter(req.getParameter("quantityR"))>0)
					controller.setQuantity(getIntFromParameter(req.getParameter("quantityR")));
				else 
					controller.setQuantity(1);
				item=controller.findItemByName(req.getParameter("itemR"));
				System.out.println("Name: "+item.getItemName()+" Item ID: "+ item.getItemID()+ " price: " +item.getItemPrice()+" ");
				passed=controller.removeItem(login.getAccountID(), item, controller.getQuantity());
				successMessage="Removed "+controller.getQuantity()+" "+item.getItemName();
			} catch (SQLException e) {
				e.printStackTrace();
				errorMessage="Invalid Item";
			}

			//System.out.println("item removed from list: "+item.getItemName()+", "+ item.getItemID()+ ", " +item.getItemPrice()+", "+item\\.getQuantity());

			// Forward to view to render the result HTML document
		}
		else if(req.getParameter("submit") !=null) {
			try {
				req.getSession().setAttribute("list", controller.findAllItems());
			} catch (SQLException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			req.getSession().setAttribute("groceryList", controller.getGroceryList());
			req.getSession().setAttribute("user", login);
			resp.sendRedirect(req.getContextPath() + "/review");
			req.getRequestDispatcher("/_view/reviewList.jsp").forward(req, resp);
		}


		req.setAttribute("app", item);
		try {
			req.getSession().setAttribute("entireList", controller.getAllItems());
			req.getSession().setAttribute("list", controller.findItemsByID(controller.getIdList()));
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		// Add parameters as request attributes
		req.setAttribute("Username", req.getParameter("inUsername"));

		// Add result objects as request attributes
		req.getSession().setAttribute("user", login);
		req.getSession().setAttribute("groceryList", controller.getGroceryList());
		req.getSession().setAttribute("cont", controller);
		req.getSession().setAttribute("errorMessage", errorMessage);
		req.getSession().setAttribute("successMessage", successMessage);
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
