package newbank.server;

import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private String tempLoanAmount="0"; 
		
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer();
		christina.addAccount(new Account("Main", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer();
		john.addAccount(new Account("Main", 250.0));
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String [] request) {
		if(customers.containsKey(customer.getKey())) {
			switch(request [0]) {
			
			//Show the Menu at request (RT)
			case "MENU" : return Menu.printMenu();
			
			//Showing the accounts
			case "1" : return showMyAccounts(customer);
			
			//create new account
			case "2" : try { return newAccount(customer, request[1]);}
						//error is caught if user doesn't specify a name for the new account
						catch (ArrayIndexOutOfBoundsException e) {return "Please enter the NEWACCOUNT command in the form: NEWACCOUNT <name>.\n";}
			
			//create new account (bis) - so that typing "NEWACCOUNT" also works.(This is so that we don't have to amend previous code -RT)
			case "NEWACCOUNT" : try { return newAccount(customer, request[1]);}
			//error is caught if user doesn't specify a name for the new account
			catch (ArrayIndexOutOfBoundsException e) {return "Please enter the NEWACCOUNT command in the form: NEWACCOUNT <name>.\n";}
			
			
			//External Money Transfer FR1.5 Added by Abhinav
			case "3" : return payOthers(customer, request);
			
			//Keeping "PAY" so that we don't have to amend previous code (RT)
			case "PAY" : return payOthers(customer, request);
			
			
			//Adding MicroLoan functionality (added by Raymond (RT))
			
			//Create a MicroLoan account
			case "4":  try { return openMicroLoanAccount(customer);}
			//error is caught if user doesn't specify a name for the new account
			catch (ArrayIndexOutOfBoundsException e) {return "Please enter the OpenMicroLoanAccount command in the form: OpenMicroLoanAccount.\n";}
			//Create a MicroLoan A/C
			case "OpenMicroLoanAccount":  try { return openMicroLoanAccount(customer);}
			//error is caught if user doesn't specify a name for the new account
			catch (ArrayIndexOutOfBoundsException e) {return "Please enter the OpenMicroLoanAccount command in the form: OpenMicroLoanAccount.\n";}
			
			
			case "5": return "To create a MicroLoan, please enter command in the form:\n "
						+ "PRINCIPLE <amount> INTEREST RATE <amount> \n";
			case "PRINCIPLE": try {
				tempLoanAmount=request[1];
				return customers.get(customer.getKey()).createMicroLoan(Integer.parseInt(request[1]), Integer.parseInt(request[4])) ;
			}catch(ArrayIndexOutOfBoundsException e) {return "To create a MicroLoan, please enter command in the form: \n "
					+ "PRINCIPLE <amount> INTEREST RATE <amount> \n";	
			}
			
			case "6": 
			String[] request1 = {"PAY", "FROM", "Main", "TO", customer.getKey() , "MicroLoan",tempLoanAmount};	
			return payOthers(customer, request1);
			
			case "7": try{return MicroLoanMarket.showMicroLoansAvailable();
			}catch(ArrayIndexOutOfBoundsException e) {
				return "No available MicroLoans at this Moment";
			}
			
			
			case "8": return "Acquiring a MicroLoan...";
			
			case "TEST": return  tempLoanAmount ;
						
			
			default : return "FAIL - Please enter a number from the Menu or Type 'Menu' to see the Menu again.\n";
			}
			
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return "Available accounts:\n" + (customers.get(customer.getKey())).accountsToString();
	}

	private String newAccount (CustomerID customer, String name) {
		customers.get(customer.getKey()).addAccount(new Account (name, 0.00));
		return "SUCCESS- New account created.\n";
	}
	
	//Method when "PAY" Keyword is used
	private String payOthers (CustomerID customer, String[] request) {
		if(request.length==1) { //only PAY mentioned
			return "You have following accounts" + "\n" + showMyAccounts(customer)+ "Please select the account type for payment in the form:" +
				"PAY FROM <YourAccountType> TO <Person/Company> <RecepientAccountType> <Amount>";
		} else if (request.length==7 && request[1].equals("FROM") && request[3].equals("TO")) { //if the length of request matches the format
			return makePayment(customer, request);			
		} else { // for all other cases
			return "You have following accounts" + "\n" + showMyAccounts(customer)+ "Please select the account type for payment in the form:" +
				   "PAY FROM <AccountType> TO <Person/Company> <RecepientAccountType>  <Amount>";
		}
		
	}
	
	//Method when "PAY FROM <AccountType> TO <Person/Company> <RecepientAccountType> <Amount>" is used
	private String makePayment (CustomerID customer, String[] request) {
		//check if donor's account type is correct
		Customer donorCustomer  = customers.get(customer.getKey());
		
		if(!donorCustomer.checkAccountType(request[2])) {
			return "Entered <YourAccountType> " + request[2] + "is incorrect"+ "\n" + "You have following accounts" + "\n" + showMyAccounts(customer);
		}
		
		//check if the <Amount> entered in number
		double amountToTransfer;
		try {
			amountToTransfer =  Double.parseDouble(request[6]);	
			//check for negative numbers
			if(amountToTransfer <=0) {
				return "Please enter <Amount> greater than zero";
			}
			
		}catch (NumberFormatException e) {
			return "Please enter numbers only for <Amount>";
		}
		
		//check if the amount can be parsed as double & it is less than the amount in the balance
		double donorAccountBalance = donorCustomer.accountType(request[2]).getBalance(); 
		if(amountToTransfer > donorAccountBalance) {
			return "Entered transfered amount " + amountToTransfer +",  is greater than the balance in your selected account";
		}
		
		//check if the person/company to pay is in the bank database 
		Customer recepientCustomer;
		try {
			recepientCustomer = customers.get(request[4]);	
			if (recepientCustomer ==null) {
				return "Entered Customer: " + request[4] + " does not have an account in the bank.";
			}
		}catch(Exception e) {
			return "Entered Customer: " + request[4] + " does not have an account in the bank.";
		}
		
		//check if the account type of person/company to pay is in the database
		if(!recepientCustomer.checkAccountType(request[5])) {
			return "Entered <RecepientAccountType> " + request[5] + " is incorrect";
		}
		
		//if all the checks passed transfer the money
		donorCustomer.accountType(request[2]).changeBalance(-amountToTransfer);
		recepientCustomer.accountType(request[5]).changeBalance(amountToTransfer);
		
		return "SUCCESS - " + "Your account balance:" + "\n" + showMyAccounts(customer);
	}
	
	
	//Methods related to MicroLoan
	
				
		private String openMicroLoanAccount (CustomerID customer) {
			String name="MicroLoan";
			customers.get(customer.getKey()).addAccount(new Account (name, 0.00));
			return "SUCCESS- New MicroLoan account created.\n";
		}
		
		
		
		
		
		
		
		
		
		

}
