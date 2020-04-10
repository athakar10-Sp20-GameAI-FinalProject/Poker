package game;

public class Move {
	public ActionEnum action;
	private int amount;
	
	public Move(ActionEnum action)
	{
		this.action = action;
		this.amount = 0;
	}
	
	public Move(ActionEnum action, int amount)
	{
		this.action = action;
		this.amount = amount;
	}
	
	public String getAction()
	{
		return this.action.toString() + this.amount;
	}
}
