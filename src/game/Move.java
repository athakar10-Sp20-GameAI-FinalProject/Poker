package game;

public class Move {
	private Action action;
	private int amount;
	
	public Move(Action action)
	{
		this.action = action;
		this.amount = 0;
	}
	
	public Move(Action action, int amount)
	{
		this.action = action;
		this.amount = amount;
	}
	
	public String getAction()
	{
		return this.action.toString() + this.amount;
	}
}
