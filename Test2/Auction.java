public class Auction{
	
	private String item;
	private double reserve, highestBid;
	private boolean sold;
	private int buyer;
	
	public Auction(String item, double reserve, double highestBid){
		this.item =item;
		this.reserve=reserve;
		this.highestBid=highestBid;
		sold=false;
		buyer = -999;
	}
	
	public String getItem(){
		return item;
	}
	
	public double getReserve(){
		return reserve;
	}
	
	public double getHighestBid(){
		return highestBid;
	}
	
	public void setHighestBid(double x){
		highestBid = x;
	}
	
	public void setSold(boolean b){
		sold =b;
	}
	
	public boolean getSold(){
		return sold;
	}
	
	public void setBuyer(int x){
		buyer = x;
	}
	
	public int getBuyer(){
		return buyer;
	}
	
	public String toString(){
		return ("Item: "+getItem() 
		+ "\nReserve: "+getReserve()
		+"\nHighestBid: "+getHighestBid()
		+"\nSold: "+getSold()
		+"\nBuyer: "+getBuyer()
		+"\n\n");
	}
	
	
}