package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import agent.Agent;
import agent.HumanAgent;
import agent.MCTS;

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
		exec.runGame(players);
	}

	public void runGame(List<Agent> players) {
		MCTS bot = new MCTS(STARTING_CHIPS);
		players.add(bot);
		Dealer dealer = new Dealer(players);
		bot.setDealer(dealer);

//		Card[] botHand = new Card[2];
//		botHand[0] = new Card(CardEnum.KING, SuitEnum.CLUB);
//		botHand[1] = new Card(CardEnum.NINE, SuitEnum.CLUB);
		
//		for(int i = 0; i < 10; i++) {
//			System.out.println();
//			bot.root.setProbabilities(new Card[5], botHand);
//		}
//		Card[] community = new Card[5];
//		community[0] = new Card(CardEnum.QUEEN, SuitEnum.CLUB);
//		community[1] = new Card(CardEnum.JACK, SuitEnum.CLUB);
//		community[2] = new Card(CardEnum.TEN, SuitEnum.CLUB);
//		community[3] = new Card(CardEnum.FIVE, SuitEnum.CLUB);
//		community[4] = new Card(CardEnum.FOUR, SuitEnum.CLUB);
//		for(int i = 0; i < 10; i++) {
//			System.out.println();
//			bot.root.setProbabilities(community, botHand);
//		}
		
		while (!dealer.isGameOver()) {
			dealer.deal();
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
