# Poker
Code by Matthew Barton & Arjun Thakar.

Done as the final project for Game AI (CSCI-3395-1 Spring 2020) at Trinity University (TX) with Dr. Britton Horn.

# Executive Summary

Poker implemented using Texas (No Limit) Hold'em rules. AI agent implemented with a Monte-Carlo Tree Search (MCTS) and some rule-based system behavior.  Blinds and the button are incremented at the end of each pot as it would in a normal poker game. The game runs with at least one human player and a single agent (need to change Executor to specify players).

All game logic classes exist in the game package. All player and AI agent classes exist in the agent package. The MCTS calculates distributed probabilites in parallel for its nodes to estimate the value of its hands and the board in order to make intelligent decisions.  Java Standard Input and Standard Output handle I/O for all players. Anyone is welcome to fork this project and put in a graphical display. 

Deck/Card classes and some enumerations were implemented using [this Stack Exchange question's design](https://codereview.stackexchange.com/questions/10583/basic-poker-draw). Dr. Horn's sample MCTS solution was used as a basis for our implementation.

No other preexisting frameworks or packages were used in this repository.
