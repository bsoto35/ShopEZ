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
import edu.ycp.cs320.ShopEZ.persist.DerbyDatabase;

public class InsertItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Account login= new Account();
	private GroceryList grocerys;
	private Item item= new Item();
	private InsertItemController controller;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession(false);
		System.out.println("check1");
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
		System.out.println("check2");
		HttpSession session=req.getSession(false);
		login= (Account)session.getAttribute("user");
		grocerys=(GroceryList)session.getAttribute("grocerylist");
		controller=new InsertItemController(grocerys, login);
		String user = login.getUsername();
		System.out.println("   User: <" + user + "> logged in");
		//DerbyDatabase db= new DerbyDatabase();
		System.out.println("\nInsertItemServlet: doPost");		
		String errorMessage = null;
		String successMessage = null;
		login= new Account();
		boolean passed=false;
		try {
			//item=controller.findItemByName(user);
			login=controller.getAccountByUser(user);
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage="Invalid Item";
		}
		req.setAttribute("quantityA", item.getQuantity()); 
		req.setAttribute("quantityR", item.getQuantity()); 
		if(req.getParameter("add") !=null) {
			if(req.getParameter("itemA")!= null) {	
				item.setItemName(req.getParameter("itemA"));
				try {
					item=controller.findItemByName(item.getItemName());
					item.setQuantity(getIntFromParameter(req.getParameter("quantityA")));
					System.out.println(""+item.getItemName()+" "+ item.getItemID()+ " " +item.getItemPrice()+", "+item.getQuantity());
					passed=controller.addItem(login.getAccountID(), item, item.getQuantity());
					System.out.println("passed3");
				} catch (SQLException e) {
					e.printStackTrace();
					errorMessage="Invalid Item";
				}
				if(passed)
					System.out.println("item inserted into list: "+item.getItemName()+", "+ item.getItemID()+ ", " +item.getItemPrice()+", "+item.getQuantity());
				double sum=item.getItemPrice();
				controller.setTotalPrice(sum);
			}
			else {
				
			}
		}
		else if(req.getParameter("rem") !=null) {	
			item.setQuantity(getIntFromParameter(req.getParameter("quantityR")));
			item.setItemName(req.getParameter("itemR"));
			try {
				item=controller.findItemByName(item.getItemName());
				System.out.println(""+item.getItemName()+" "+ item.getItemID()+ " " +item.getItemPrice()+" ");
				passed=controller.removeItem(login.getAccountID(), item, item.getQuantity());
				//successMessage=db.removeItemFromTheList(login, remItem, amount);
				System.out.println("test1");
			} catch (SQLException e) {
				e.printStackTrace();
				errorMessage="Invalid Item";
			}
			
			System.out.println("item removed into list: "+item.getItemName()+", "+ item.getItemID()+ ", " +item.getItemPrice()+", "+item.getQuantity());

			// Forward to view to render the result HTML document
		}	
		InsertItemController cont2=new InsertItemController(controller.getGroceryList(), login);
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("successMessage", successMessage);

		
		req.setAttribute("app", item);
		req.setAttribute("list", controller);
		// Add parameters as request attributes
		req.setAttribute("Username", req.getParameter("inUsername"));

		// Add result objects as request attributes
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("quantityA", req.getParameter("quantityA")); 
		req.setAttribute("quantityR", item.getQuantity());
		req.getSession().setAttribute("user", login);;
		req.getSession().setAttribute("grocerylist", cont2.getGroceryList());
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
