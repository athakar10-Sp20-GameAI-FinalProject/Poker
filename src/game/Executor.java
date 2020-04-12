package game;

import java.util.ArrayList;
import java.util.List;

import agent.Agent;
import agent.HumanAgent;

public class Executor {
	public static final int STARTING_CHIPS = 1000;
	
	
	public static void main(String[] args) {
		Executor exec = new Executor();
		List<Agent> players = new ArrayList<Agent>();
		HumanAgent p1 = new HumanAgent(STARTING_CHIPS);
		HumanAgent p2 = new HumanAgent(STARTING_CHIPS);
		HumanAgent p3 = new HumanAgent(STARTING_CHIPS);
		HumanAgent p4 = new HumanAgent(STARTING_CHIPS);
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		//Agent[] ps = players.toArray(new Agent[players.size()]);
		exec.runGame(players);
	}
	
	public void runGame(List<Agent> players)
	{
		Dealer dealer = Dealer.getInstance(players);
		dealer.deal();
		dealer.playRound();
		while(!dealer.isGameOver())
		{
			for(Agent player : players)
			{
				player.printHand();
				player.printBetAmount();
			}
			
			// TODO: remove this when done testing
			dealer.playRound();
		}
		
	}
}
