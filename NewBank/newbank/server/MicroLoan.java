package newbank.server;

public class MicroLoan {
	
	//properties
	private Integer principle; //The amount of the loan
	private Integer interestRate; //The annual interest rate
	private CustomerID lender;
	private CustomerID borrower;
	private boolean isWired;
	private boolean isReimbursed;
	
	//constructor
	/*
	public MicroLoan(Integer principle, Integer interestRate) {
		super();
		this.principle = principle;
		this.interestRate = interestRate;
		
	}*/
	
	public MicroLoan(Integer principle, Integer interestRate, CustomerID lender) {
		super();
		this.principle = principle;
		this.interestRate = interestRate;
		this.lender = lender;
	}
	
	
	//Getters and setters
	public Integer getPrinciple() {
		return principle;
	}
	
	public void setPrinciple(Integer principle) {
		this.principle = principle;
	}

	public Integer getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Integer interestRate) {
		this.interestRate = interestRate;
	}

	public CustomerID getLender() {
		return lender;
	}
	
	public void setLender(CustomerID lender) {
		this.lender = lender;
	}
	
	public CustomerID getBorrower() {
		return borrower;
	}

	public void setBorrower(CustomerID borrower) {
		this.borrower = borrower;
	}
	
	
	
	public boolean isWired() {
		return isWired;
	}


	public void setWired(boolean isWired) {
		this.isWired = isWired;
	}


	public boolean isReimbursed() {
		return isReimbursed;
	}


	public void setReimbursed(boolean isReimbursed) {
		this.isReimbursed = isReimbursed;
	}


	
	
	
	
	public Integer calculateInterestDue() {
		Integer interestDue;
		interestDue =(int)Math.round(getInterestRate() * getPrinciple() /12/100);//rounding the amount to $
		return interestDue;
	}
	
	
	@Override
	public String toString() {
		if(this.borrower==null) {
			return "MicroLoan [principle=" + principle + ", interestRate=" + interestRate + ", lender=" + lender.getKey()
			+ ", borrower=" + borrower + ", isWired=" + isWired + ", isReimbursed="+ isReimbursed +"]";
		}else {
			return "MicroLoan [principle=" + principle + ", interestRate=" + interestRate + ", lender=" + lender.getKey()
			+ ", borrower=" + borrower.getKey() + ", isWired=" + isWired + ", isReimbursed="+ isReimbursed + "]";
		}
	}


	
	
	

	
}



