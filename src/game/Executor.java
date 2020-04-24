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
		HumanAgent p1 = new HumanAgent(STARTING_CHIPS, "P1");
		HumanAgent p2 = new HumanAgent(STARTING_CHIPS, "P2");
		// HumanAgent p3 = new HumanAgent(STARTING_CHIPS);
		// HumanAgent p4 = new HumanAgent(STARTING_CHIPS);
		players.add(p1);
		players.add(p2);
		// players.add(p3);
		// players.add(p4);
		// Agent[] ps = players.toArray(new Agent[players.size()]);
		exec.runGame(players);
	}

	public void runGame(List<Agent> players) {
		Dealer dealer = new Dealer(players);

		while (!dealer.isGameOver()) {
			dealer.deal();
			//players.get(0).setHand(new Card(CardEnum.KING, SuitEnum.HEART), new Card(CardEnum.QUEEN, SuitEnum.HEART));
			//players.get(1).setHand(new Card(CardEnum.ACE, SuitEnum.CLUB), new Card(CardEnum.ACE, SuitEnum.SPADE));
			do {
				dealer.playRound();
			} while (!dealer.isPotOver());

			Agent winner = dealer.findWinner();
			//System.out.println(winner.getName());
			dealer.dollPot(winner);
			for (Agent player : players) {
				if (player.getChips() == 0) {
					dealer.removePlayerFromGame(player);
				}
			}
		}

	}
}
