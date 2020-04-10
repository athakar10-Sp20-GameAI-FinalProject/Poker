package game;

public enum Action {
	FOLD, BET, CALL, CHECK, RAISE;
	
	public static boolean needsAmount(Action action)
	{
		switch (action) {
			case FOLD: case CHECK: case CALL:
				return false;
			default: return true;
		}
	}
}
