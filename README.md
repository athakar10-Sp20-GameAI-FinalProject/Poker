# Poker
Code by Matthew Barton & Arjun Thakar.

Done as the final project for Game AI (CSCI-3395-1 Spring 2020) at Trinity University (TX) with Dr. Britton Horn.

# Executive Summary

Poker implemented using Texas (No Limit) Hold'em rules. AI agent implemented with a paralellized Monte-Carlo Tree Search (MCTS) and some rule-based system behavior. 

All game logic classes exist in the game package. All player and AI agent classes exist in the agent package. There is probably not enough time left to add graphics to this project (anyone is welcome to fork the project and put in graphics). The MCTS will use distributed probabilites in its nodes to estimate the value of its hands and the board in order to make intelligent decisions. The simulations rolled out in the tree will be parallelized on multiple threads in order to increase performance. We will see if we are able to add some form of reinforcement learning or vector math so that the agent can build statistical models of how certain other players play and adjust its decision-making accordingly.

Deck/Card classes and some enumerations were implemented using [this Stack Exchange question's design](https://codereview.stackexchange.com/questions/10583/basic-poker-draw).

No other preexisting frameworks or packages were used in this repository.
