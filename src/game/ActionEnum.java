package game;

public enum ActionEnum {
	FOLD, BET, CALL, CHECK, RAISE;
	
	public static boolean needsAmount(ActionEnum action)
	{
		switch (action) {
			case FOLD: case CHECK:
				return false;
			default: 
				return true;
		}
	}
}
