package com.bryjamin.dancedungeon.utils.pathing;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

import java.util.Comparator;

/**
 * Created by BB on 28/10/2017.
 */

public class AStarPathCalculator {


    public OrderedMap<CoordinateComponent, Node> componentNodeOrderedMap = new OrderedMap<CoordinateComponent, Node>();

    public Array<Coordinates> unavailableCoordinates = new Array<Coordinates>();

    public Array<Coordinates> availableCoordinates = new Array<Coordinates>();

    private static final int HORIZONTAL_COST = 10;

    private static final Comparator<Node> nodeFValueComparator = new Comparator<Node>() {
        @Override
        public int compare(Node node, Node t1) {
            Integer n = node.fValue;
            Integer n2 = t1.fValue;

            return n.compareTo(n2);
        }
    };



    public AStarPathCalculator(Array<Coordinates> availableCoordinates, Array<Coordinates> unavailableCoordinates){
        this.availableCoordinates = availableCoordinates;
        this.unavailableCoordinates = unavailableCoordinates;
    }




    public boolean findShortestPath(Coordinates start, Coordinates end, Queue<Coordinates> fillQueue){

        Array<Node> openList = new Array<Node>();
        Array<Node> closedList = new Array<Node>();

        OrderedMap<Coordinates, Node> allNodeMap = setUpNodes(new OrderedMap<Coordinates, Node>(), start);

        Node firstNode = allNodeMap.get(start);

        closedList.add(allNodeMap.get(start));

        //Could place this inside the Node set up.
        for(Node n: allNodeMap.values().toArray()) n.setHeuristic(n.coordinates, end);

        for(Coordinates c : returnSurroundingCoordinates(firstNode.coordinates)){

            if(c.equals(end)) return true;

            //TODO test what happens if null
            Node potentialOpenListNode = allNodeMap.get(c);
            if(!closedList.contains(potentialOpenListNode, false) && potentialOpenListNode != null) {
                potentialOpenListNode.parent = firstNode;
                potentialOpenListNode.gValue = HORIZONTAL_COST;
                openList.add(potentialOpenListNode);
                potentialOpenListNode.calcF();
            }
        }


        while(openList.size != 0){

            openList.sort(nodeFValueComparator);
            Node nextNode = openList.first();

            closedList.add(nextNode);
            openList.removeValue(nextNode, false);

            Array<Coordinates> surroundingCoordinates = returnSurroundingCoordinates(nextNode.coordinates);

            if(surroundingCoordinates.contains(end, false)) {
                createCoordinateSequence(nextNode, fillQueue);
                return true;
            }

            for (Coordinates c : returnSurroundingCoordinates(nextNode.coordinates)) {

                //This is to prevent a stack over flow error (Most likely due to the way return surrounding coordinates
                //Does not account for nodes of the closed list. A way to prevent this would be to
                //check if the coordinate selected belongs to any node on the closed list possibly?

                /*               if(c.equals(start)){
                    continue;
                }
*/
                Node potentialOpenListNode = allNodeMap.get(c);
                if(closedList.contains(potentialOpenListNode, true)) continue;


                if (potentialOpenListNode != null) {
                    if (potentialOpenListNode.gValue == 0) {
                        potentialOpenListNode.gValue = nextNode.gValue + HORIZONTAL_COST;
                        potentialOpenListNode.parent = nextNode;
                        openList.add(potentialOpenListNode);
                    } else if(openList.contains(potentialOpenListNode, false)){
                        //  System.out.println(potentialOpenListNode.gValue);

                        if (potentialOpenListNode.gValue > nextNode.gValue + HORIZONTAL_COST) {
                            potentialOpenListNode.gValue = nextNode.gValue + HORIZONTAL_COST;
                            //   potentialOpenListNode.parent = nextNode;
                        }
                    }
                    potentialOpenListNode.calcF();

                }

            }
        }


        return false;
    }



    private OrderedMap<Coordinates, Node> setUpNodes(OrderedMap<Coordinates, Node> fillNodeMap, Coordinates start){

        for(Coordinates coordinates : availableCoordinates) {
            if(!unavailableCoordinates.contains(coordinates, false) || coordinates.equals(start)) {
                fillNodeMap.put(coordinates, new Node(coordinates));
            }
        }

        return fillNodeMap;

    }


    public Queue<Coordinates> createCoordinateSequence(Node node, Queue<Coordinates> coordinatesQueue){

        if(node.parent != null){
            coordinatesQueue.addFirst(node.coordinates);
            return createCoordinateSequence(node.parent, coordinatesQueue);
        }

        return coordinatesQueue;

    }



    private Array<Coordinates> returnSurroundingCoordinates(Coordinates coordinates){

        Array<Coordinates> array = new Array<Coordinates>();

        array.add(new Coordinates(coordinates.getX(), coordinates.getY() + 1));
        array.add(new Coordinates(coordinates.getX(), coordinates.getY() - 1));
        array.add(new Coordinates(coordinates.getX() + 1, coordinates.getY()));
        array.add(new Coordinates(coordinates.getX() - 1, coordinates.getY()));

        return array;

    }









    private class Node {


        public Node(Coordinates coordinates){
            this.coordinates = coordinates;
        }

        public Node parent;

        public Coordinates coordinates;

        public int hValue;
        public int gValue;
        public int fValue;

        public void calcF(){
            fValue = gValue + hValue;
        }

        private void setHeuristic(Coordinates start, Coordinates goal) {

            int dx = Math.abs(start.getX() - goal.getX());
            int dy = Math.abs(start.getY() - goal.getY());

            int D = 1;

            this.hValue = D * (dx + dy);

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            return coordinates.equals(node.coordinates);
        }

        @Override
        public int hashCode() {
            return coordinates.hashCode();
        }


    }



}