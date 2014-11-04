package com.trutech.calculall;


import java.util.ArrayList;

/**
 * Used as an unit within a tree.
 *
 * @author Alston Lin
 * @param E The type of Object the tree will store
 */
public class Node<E> {

    private ArrayList<Node> children;
    private E content;
    private Node parent;

    /**
     * Constructs a new Node for a tree.
     *
     * @param content The contents of the node
     */
    public Node(E content) {
        this.content = content;
        children = new ArrayList();
    }

    /**
     * Adds the given child to this Node.
     *
     * @param child the child Node
     */
    public void addChild(Node child) {
        children.add(child);
        child.parent = this;
    }

    /**
     * Adds the given child to this Node.
     *
     * @param newChildren a list of all the children to be added to this Node
     */
    public void addChildren(ArrayList<Node> newChildren) {
        for (int i = 0; i < newChildren.size(); i++) {
            this.addChild(newChildren.get(i));
        }
    }

    /**
     *
     * @return The children of this Node
     */
    public ArrayList<Node> getChildren() {
        return children;
    }

    /**
     *
     * @return The number of children in this Node
     */
    public int getNumOfChildren() {
        return children.size();
    }

    /**
     * @return The data that this Node stores
     */
    public E getContent() {
        return content;
    }

    /**
     * @return The parent of this Node
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Prints the tree that this node is the root of
     * @param indent String used to indent the subtrees
     * @param last Is this the root's last child?
     */
    public void printTree(String indent, boolean last) {
        System.out.print(indent);
        if (last) {
            System.out.print("\\>");
            indent += "  ";
        } else {
            System.out.print("|>");
            indent += "| ";
        }
        if (this.content instanceof Token) {
            if (this.content instanceof Number) {
                if (((Number) this.content).getValue() % 1 != 0) {
                    System.out.println(" "+((Number) this.content).getValue());
                } else {
                    System.out.println(" "+(int) (((Number) this.content).getValue()));
                }
            } else {
                System.out.println(" "+((Token)this.content).getSymbol());
            }
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).printTree(indent, i == children.size() - 1);
        }
    }
}