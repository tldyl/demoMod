package temp;

public class Temp {

    static class Node {
        boolean visited = false;
        Node left;
        Node right;
        Node up;
        Node down;
    }

    public static void main(String[] args) {
        Node[][] nodes = new Node[5][5];
        for (int i=0;i<5;i++) {
            for (int j=0;j<5;j++) {
                nodes[i][j] = new Node();
            }
        }
        for (int i=0;i<5;i++) {
            for (int j=0;j<5;j++) {
                if (i > 0) {
                    nodes[i][j].up = nodes[i - 1][j];
                }
                if (i < 4) {
                    nodes[i][j].down = nodes[i + 1][j];
                }
                if (j > 0) {
                    nodes[i][j].left = nodes[i][j - 1];
                }
                if (j < 4) {
                    nodes[i][j].right = nodes[i][j + 1];
                }
            }
        }
        nodes[0][1].visited = true;
        dfs(nodes[0][0], 1);
    }

    private static void dfs(Node node, int ctr) {
        node.visited = true;
        if (node.up != null && !node.up.visited) {
            dfs(node.up, ctr + 1);
        }
        if (node.down != null && !node.down.visited) {
            dfs(node.down, ctr + 1);
        }
        if (node.left != null && !node.left.visited) {
            dfs(node.left, ctr + 1);
        }
        if (node.right != null && !node.right.visited) {
            dfs(node.right, ctr + 1);
        }
        System.out.println(ctr);
    }
}
