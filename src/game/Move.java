package game;

public class Move {
	private String action;
	private int amount;
	
	public Move(String action)
	{
		this.action = action;
		this.amount = 0;
	}
	
	public Move(String action, int amount)
	{
		this.action = action;
		this.amount = amount;
	}
	
	public String getAction()
	{
		return this.action + this.amount;
	}
}
