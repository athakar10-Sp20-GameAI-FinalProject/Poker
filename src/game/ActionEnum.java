package game;

public enum ActionEnum {
	FOLD, BET, CALL, CHECK, RAISE, INVALID;
	
	public static boolean needsAmount(ActionEnum action)
	{
		switch (action) {
			case FOLD: case CHECK: case CALL:
				return false;
			default: 
				return true;
		}
	}
}
