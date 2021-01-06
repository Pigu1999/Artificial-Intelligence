
package players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import snake.GameState;
import snake.Snake;
import static players.RandomPlayer.rand;

public class MinimaxPlayer extends SnakePlayer {

    public static int optimalMoves;
   
    public MinimaxPlayer(GameState state, int index, Snake game) {
        super(state, index, game);
    }

    public void doMove(){
        try{
            GameState extract = (GameState)state.clone();//clones the current gamestate
            MinimaxNode<GameState> root = new MinimaxNode(extract); //sets the clone of the current gamestate as the root node
            List<Double> value;
            gameTree(root, 7, index);//generates game tree for the next two moves of each player
            value = MinimaxAlgorithm(root, 0);// works out best moves through the game tree    
        }
        catch (CloneNotSupportedException e){
            System.out.println("Clone not excepted");
        }
        state.setOrientation(index, MinimaxPlayer.optimalMoves);// sets the next move from the list of moves
    }
    
    //overall minimax algorithm that'll step go through the game tree I have created.
    //Used pseudocode from https://en.wikipedia.org/wiki/Minimax#Pseudocode 
    private List<Double> MinimaxAlgorithm(MinimaxNode<GameState> node, int dataCount){
        if(node.getNumberOfChildren() == 0){ //checks to see if terminal node
            List<Double> heuristics = new ArrayList();
            heuristics.addAll(calculateHeuristic(node.getState()));
            return heuristics;
        }
        else{ //will maximise all the scores for each player.
            double value = Double.NEGATIVE_INFINITY;
            double checkValue;
            int bestMove;
            List<Double> heuristics = new ArrayList();
            if(node.getIfChanceNode()){ //checks if current node is a chance node, if so, times 
                for (int i = 0; i < node.getChildren().size(); i++){
                    List<Double> tempHeuristics = new ArrayList();
                    tempHeuristics.addAll(MinimaxAlgorithm(node.getChildren().get(i),dataCount+1));
                    checkValue = node.getChildren().get(i).getProbability() * tempHeuristics.get(node.getNodeIndex());
                    value = Math.max(value, checkValue);//maximises the score.
                    if(checkValue == value){
                        heuristics.addAll(tempHeuristics);   
                    }
                    if(dataCount == 0){//checks if this is the root node
                        if(checkValue == value){
                            MinimaxPlayer.optimalMoves = node.getChildren().get(i).getState().getOrientation(node.getNodeIndex());   
                        }
                    }    
                }
            }
            else{
                for (int i = 0; i < node.getChildren().size(); i++){
                    List<Double> tempHeuristics = new ArrayList();
                    tempHeuristics.addAll(MinimaxAlgorithm(node.getChildren().get(i),dataCount+1));
                    checkValue = tempHeuristics.get(node.getNodeIndex());
                    value = Math.max(value, checkValue);//maximises the score.
                    if(checkValue == value){
                        heuristics.addAll(tempHeuristics);   
                    }
                    if(dataCount == 0){//checks if this is the root node
                        if(checkValue == value){
                            MinimaxPlayer.optimalMoves = node.getChildren().get(i).getState().getOrientation(node.getNodeIndex());   
                        }
                    }    
                }  
            }
            return heuristics;
        }      
    }

    //function to generate a game tree
    private void gameTree(MinimaxNode<GameState> currentNode, int depth, int oIndex){
        currentNode.setNodeIndex(oIndex); 
        if (depth == 0) {
            return;
        }
        else{
            if (currentNode.getState().isDead(oIndex)){ //checks to see if current player is dead, if so, just skips their moves
                int newIndex = (oIndex + 1) % currentNode.getState().getNrPlayers();
/*                if(oIndex == 3){
                    newIndex = 0;
                }
                else{
                    newIndex = oIndex + 1;
                }*/
                gameTree(currentNode, depth-1, newIndex);
            }
            else{
                //this if statement sets up chance nodes, if the target has been met it will create 5 children nodes with randomly generated new target postitions 
                if(!currentNode.getState().hasTarget()){
                    currentNode.setChanceNode();
                    for(int i = 1; i <= 5; i++){
                        try{
                            GameState newState = (GameState)currentNode.getState().clone();
                            newState.chooseNextTarget();
                            MinimaxNode<GameState> childNode = new MinimaxNode(newState);
                            childNode.setProbability(0.2);
                            currentNode.addChild(childNode); 
                        }
                        catch (CloneNotSupportedException e){
                            System.out.println("Clone not excepted");
                        }
                        
                    }
                    for (int i = 0; i < currentNode.getNumberOfChildren(); i++){
                        gameTree(currentNode.getChildren().get(i), depth, oIndex);
                    }

                }
                else{
                    List<Integer> options = new ArrayList();
                    for (int i = 1; i <= 4; i++) {
                        if (currentNode.getState().isLegalMove(oIndex, i)){
                            options.add(i);
                        }
                    }
                    for (int i = 0; i < options.size(); i++){
                        try{
                            GameState newState = (GameState)currentNode.getState().clone();
                            newState.setOrientation(oIndex, options.get(i));
                            newState.updatePlayerPosition(oIndex);
                            MinimaxNode<GameState> childNode = new MinimaxNode(newState);
                            currentNode.addChild(childNode);
                        }
                        catch (CloneNotSupportedException e){
                            System.out.println("Clone not excepted");
                        }
                    }
                    int newIndex = (oIndex + 1) % currentNode.getState().getNrPlayers();
    /*              if(oIndex == 3){
                        newIndex = 0;
                    }
                    else{
                        newIndex = oIndex + 1;
                    }*/
                    for (int i = 0; i < currentNode.getNumberOfChildren(); i++){
                        gameTree(currentNode.getChildren().get(i), depth-1, newIndex);
                    }
                }     
            }     
        }
    }

    //function for calculating a heuristic for how good a state is for the AI to be in
    private List<Double> calculateHeuristic(GameState misState){
        List<Double> heuristics = new ArrayList();
        for (int i = 0; i < misState.getNrPlayers(); i++){
            if (misState.isDead(i)){
            heuristics.add(-1000.0); //minimax player is dead, i.e. really bad
            }
            else{
                double score = 500.0;
                //score returned euals 500 - distance to food.
                heuristics.add(score - calculateDistance(misState.getPlayerX(i).get(0), misState.getPlayerY(i).get(0), misState.getTargetX(), misState.getTargetY()));
            }   
        }
        return heuristics;
    }

    //function to calculate distance between two points on the Graph
    private double calculateDistance(int sourceX, int sourceY, int targetX, int targetY){
        int xLength = targetX - sourceX;
        int yLength = targetY - sourceY;
        return Math.sqrt((xLength*xLength)+(yLength*yLength)); 
    }


}
