package game;

public enum ActionEnum {
	FOLD, BET, CALL, CHECK, RAISE, INVALID;
	
	public static boolean needsAmount(ActionEnum action) {
		switch (action) {
			case FOLD: case CHECK: case CALL: case INVALID:
				return false;
			default: 
				return true;
		}
	}
	
	public static String toString(ActionEnum action) {
		String ret = "";
		switch (action) {
			case FOLD: ret = "Fold";
			break;
			case BET: ret = "Bet X amount"; 
			break;
			case CALL: ret = "Call the current bet";
			break;
			case CHECK: ret = "Check";
			break;
			case RAISE: ret = "Raise X amount";
			break;
			case INVALID: ret = "Invalid action";
			break;
			default: ret = "Error in ActionEnum toString()";
		}
		return ret;
	}
}
