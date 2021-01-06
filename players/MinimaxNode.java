package players;

import java.util.ArrayList;
import java.util.List;
import snake.GameState;

//basic class to implement nodes to store gamestates in

public class MinimaxNode<GameState>{
	private MinimaxNode<GameState> parent = null;
	private List<MinimaxNode<GameState>> children = new ArrayList<>();
	private GameState eState = null;
	private int numberOfChildren = 0;
	private int nodeIndex;
	private boolean chanceNode = false;
	private double probability;

	public MinimaxNode(GameState eState){
		this.eState = eState;
	}

	public void addChild(MinimaxNode<GameState> child){
		child.setParent(this);
		this.children.add(child);
		numberOfChildren += 1;
	}

	public List<MinimaxNode<GameState>> getChildren(){
		return children;
	}

	public GameState getState(){
		return eState;
	}

	public void setState(GameState eState){
		this.eState = eState;
	}

	private void setParent(MinimaxNode<GameState> parent){
		this.parent = parent;
	}

	public MinimaxNode<GameState> getParent(){
		return parent;
	}

	public int getNumberOfChildren(){
		return numberOfChildren;
	}

	public void setNodeIndex(int nodeIndex){
		this.nodeIndex = nodeIndex;
	}

	public int getNodeIndex(){
		return nodeIndex;
	}

	public void setChanceNode(){
		chanceNode = true;
	}

	public boolean getIfChanceNode(){
		return chanceNode;
	}

	public void setProbability(double probability){
		this.probability = probability;
	}

	public double getProbability(){
		return probability;
	}

}
