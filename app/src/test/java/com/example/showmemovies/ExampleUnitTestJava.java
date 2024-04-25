package com.example.showmemovies;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ExampleUnitTestJava {

    @Test
    public void addition_isCorrect() {
//        int result = removeElement(new int[]{0, 1, 2, 2, 3, 0, 4, 2}, 2);
//        System.out.println(result);
//        TreeNode root = new TreeNode(0);
//        root.left = new TreeNode(1);
//        root.right = new TreeNode(2);
//        root.left.left = new TreeNode(3);
//        root.left.right = new TreeNode(4);
//        System.out.println(smallestFromLeaf(root));
//        int snakesAndLadders = snakesAndLadders(
//                new int[][]{
//                        {-1, -1, -1, -1, -1, -1},
//                        {-1, -1, -1, -1, -1, -1},
//                        {-1, -1, -1, -1, -1, -1},
//                        {-1, 35, -1, -1, 13, -1},
//                        {-1, -1, -1, -1, -1, -1},
//                        {-1, -1, -1, -1, -1, -1}
//                }
//        );
//        int snakesAndLadders = snakesAndLadders(
//                new int[][]{
//                        {-1, -1, 30, 14, 15, -1},
//                        {23, 9, -1, -1, -1, 9},
//                        {12, 5, 7, 24, -1, 30},
//                        {10, -1, -1, -1, 25, 17},
//                        {32, -1, 28, -1, -1, 32},
//                        {-1, -1, 23, -1, 13, 19}
//                }
//        );
//        int snakesAndLadders = snakesAndLadders(
//                new int[][]{
//                        {-1,-1,-1},
//                        {-1,9,8},
//                        {-1,8,9}
//                }
//        );
//        assertEquals(2, snakesAndLadders);
//        int lock = openLock(new String[]{"0201", "0101", "0102", "1212", "2002"}, "0202");
//        int lock2 = openLock(new String[]{"8888"}, "0009");
//        int lock3 = openLock(new String[]{"8887","8889","8878","8898","8788","8988","7888","9888"}, "8888");
//        int lock4 = openLock(new String[]{"5557","5553","5575","5535","5755","5355","7555","3555","6655","6455","4655","4455","5665","5445","5645","5465","5566","5544","5564","5546","6565","4545","6545","4565","5656","5454","5654","5456","6556","4554","4556","6554"}, "5555");
//        System.out.println(lock);
//        System.out.println(lock2);
//        System.out.println(lock3);
//        System.out.println(lock4);
//        findMinHeightTrees2(6, new int[][]{{3, 0}, {3, 1}, {3, 2}, {3, 4}, {5, 4}});
        System.out.println(tribonacci(24));
    }
    int[] tribonacciArr = new int[40];
    public int tribonacci(int n) {
        if (n <= 0) return 0;
        tribonacciArr[1] = 1;
        tribonacciArr[2] = 1;
        if (tribonacciArr[n] > 0) {
            return tribonacciArr[n];
        }
        tribonacciArr[n] = tribonacci(n - 1) + tribonacci(n - 2) + tribonacci(n - 3);
        return tribonacciArr[n];
    }

    int maxDiameter = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        if (root == null) return maxDiameter;
        Map<TreeNode, Integer> heightMap = new HashMap<>();
        diameterOfBinaryTreeUtil(root, heightMap);
        return maxDiameter - 1;
    }

    private void diameterOfBinaryTreeUtil(TreeNode node, Map<TreeNode, Integer> heightMap) {
        if (node == null) return;
        int maxLeftHeight = maxHeight(node.left, heightMap);
        int maxRightHeight = maxHeight(node.right, heightMap);
        if (1 + maxRightHeight + maxLeftHeight > maxDiameter) {
            maxDiameter = 1 + maxRightHeight + maxLeftHeight;
        }
        diameterOfBinaryTreeUtil(node.left, heightMap);
        diameterOfBinaryTreeUtil(node.right, heightMap);
    }

    public int maxHeight(TreeNode node, Map<TreeNode, Integer> map) {
        if (node == null) return 0;
        if (map.containsKey(node)) return map.get(node);
        int height = 1 + Math.max(maxHeight(node.left, map), maxHeight(node.right, map));
        map.put(node, height);
        return height;
    }

    public List<Integer> findMinHeightTrees2(int n, int[][] edges) {
        List<Integer> result = new ArrayList<>();
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int[] edge : edges) {
            if (graph.containsKey(edge[0])) {
                graph.get(edge[0]).add(edge[1]);
            } else {
                ArrayList<Integer> edgesList = new ArrayList<>();
                edgesList.add(edge[1]);
                graph.put(edge[0], edgesList);
            }
            if (graph.containsKey(edge[1])) {
                graph.get(edge[1]).add(edge[0]);
            } else {
                ArrayList<Integer> edgesList = new ArrayList<>();
                edgesList.add(edge[0]);
                graph.put(edge[1], edgesList);
            }
        }
        while (graph.keySet().size() > 2) {
            Iterator<Map.Entry<Integer, List<Integer>>> iterator = graph.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, List<Integer>> next = iterator.next();
                List<Integer> curVertexEdges = next.getValue();
                if (curVertexEdges.size() == 1) {
                    graph.get(curVertexEdges.get(0)).remove(next.getKey());
                    iterator.remove();
                }
            }
        }
        return new ArrayList<>(graph.keySet());
    }

    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        List<Integer> result = new ArrayList<>();
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < edges.length; i++) {
            if (graph.containsKey(edges[i][0])) {
                graph.get(edges[i][0]).add(edges[i][1]);
            } else {
                ArrayList<Integer> edgesList = new ArrayList<>();
                edgesList.add(edges[i][1]);
                graph.put(edges[i][0], edgesList);
            }
            if (graph.containsKey(edges[i][1])) {
                graph.get(edges[i][1]).add(edges[i][0]);
            } else {
                ArrayList<Integer> edgesList = new ArrayList<>();
                edgesList.add(edges[i][0]);
                graph.put(edges[i][1], edgesList);
            }
        }
        int[] maxDepth = new int[n];
        graph.keySet().forEach(vertex -> {
            boolean[] visited = new boolean[n];
            int maxDepthForCurrentVertex = doDFS(vertex, graph, 0, visited);
            maxDepth[vertex] = maxDepthForCurrentVertex;
        });
        int minDepth = Integer.MAX_VALUE;
        for (int i = 0; i < maxDepth.length; i++) {
            int j = maxDepth[i];
            if (j < minDepth) {
                result.clear();
                minDepth = j;
                result.add(i);
            } else if (j == minDepth) {
                result.add(i);
            }
        }
        return result;
    }

    private int doDFS(Integer curVertex, Map<Integer, List<Integer>> graph, int curDepth, boolean[] visited) {
        visited[curVertex] = true;
        int maxDepth = curDepth;
        for (Integer abc : graph.get(curVertex)) {
            if (!visited[abc]) {
                int depth = doDFS(abc, graph, curDepth + 1, visited);
                maxDepth = Math.max(maxDepth, depth);
            }
        }
        return maxDepth;
    }

    public int openLock(String[] deadends, String target) {
        String init = "0000";
        if (target.equals(init)) {
            return 0;
        }
        if (Arrays.asList(deadends).contains("0000")) {
            return -1;
        }
        if (Arrays.asList(deadends).contains(target)) {
            return -1;
        }
        Set<String> deadEndSet = new HashSet<>(Arrays.asList(deadends));
        Queue<String> nodes = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        nodes.add(init);
        visited.add(init);
        int distance = 0;
        while (!nodes.isEmpty()) {
            int n = nodes.size();
            distance++;
            for (int nod = 0; nod < n; nod++) {
                String poll = nodes.poll();
                StringBuilder builder = new StringBuilder(poll);
                for (int i = 0; i < 4; i++) {
                    for (int delta : new int[]{-1, 1}) {
                        char cache = builder.charAt(i);
                        char c = (char) ((builder.charAt(i) - '0' + delta + 10) % 10 + '0');
                        builder.setCharAt(i, c);
                        String string = builder.toString();
                        if (string.equals(target)) return distance;
                        if (!deadEndSet.contains(string) && !visited.contains(string)) {
                            nodes.offer(string);
                            visited.add(string);
                        }
                        builder.setCharAt(i, cache);
                    }
                }
            }
        }
        return -1;
    }

    public int snakesAndLadders(int[][] board) {
        int vertices = board.length * board[0].length;
        int[] distance = new int[vertices];
        Arrays.fill(distance, 1000);
        distance[0] = 0;
        boolean[] visited = new boolean[vertices];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(0);
        while (!queue.isEmpty()) {
            int curVertex = queue.poll();
            for (int i = 1; i <= 6 && curVertex + i < vertices; i++) {
                int nextVertex = curVertex + i;
                if (!visited[nextVertex]) {
                    visited[nextVertex] = true;
                    int row = board.length - (nextVertex / (board.length)) - 1;
                    int col = (board.length - row) % 2 == 1 ? nextVertex % board.length : board.length - (nextVertex % board.length) - 1;
                    int snakeLadderVertex = board[row][col];
                    if (snakeLadderVertex != -1) {
                        nextVertex = snakeLadderVertex - 1;
                    }
                    if (distance[nextVertex] == -1) {
                        distance[nextVertex] = distance[curVertex] + 1;
                    }
                    queue.offer(nextVertex);
                }
            }
        }
        return distance[distance.length - 1] == 1000 ? -1 : distance[distance.length - 1];
    }

    public int islandPerimeter(int[][] grid) {
        int[] rowDir = {0, -1, 0, 1};
        int[] colDir = {1, 0, 1, 0};
        int perimeter = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    int surroundedDirection = 0;
                    for (int k = 0; k < colDir.length; k++) {
                        if (
                                i + rowDir[k] >= 0 &&
                                        i + rowDir[k] < grid.length &&
                                        j + colDir[k] >= 0 &&
                                        j + colDir[k] < grid[0].length &&
                                        grid[i + rowDir[k]][j + colDir[k]] == 1
                        ) {
                            surroundedDirection++;
                        }
                    }
                    perimeter += (4 - surroundedDirection);
                }
            }
        }
        return perimeter;
    }

    StringBuilder result = new StringBuilder();

    public String smallestFromLeaf(TreeNode root) {
        if (root == null) return "";
        StringBuilder builder = new StringBuilder();
        smallestFromLeafUtil(root, builder);
        return result.reverse().toString();
    }


    private void smallestFromLeafUtil(TreeNode curNode, StringBuilder curBuilder) {
        curBuilder.append((char) (curNode.val + 'a'));
        if (curNode.left == null && curNode.right == null) {
            if (result.length() == 0 || new StringBuilder(curBuilder).reverse().compareTo(new StringBuilder(result).reverse()) < 0) {
                result = new StringBuilder(curBuilder);
            }
        } else {
            if (curNode.left != null) {
                smallestFromLeafUtil(curNode.left, curBuilder);
            }
            if (curNode.right != null) {
                smallestFromLeafUtil(curNode.right, curBuilder);
            }
        }
        curBuilder.deleteCharAt(curBuilder.length() - 1);
    }

    public int removeDuplicates(int[] nums) {
        int startReplacementIndex = 0, curNumStartingIndex = 0, curNumLastIndex = 0, allowedRepetitions = 2;
        while (curNumStartingIndex < nums.length) {
            while (curNumLastIndex < nums.length && nums[curNumStartingIndex] == nums[curNumLastIndex]) {
                curNumLastIndex++;
            }
            for (int i = 0; i < Math.min(allowedRepetitions, curNumLastIndex - curNumStartingIndex); i++) {
                nums[startReplacementIndex++] = nums[curNumStartingIndex];
            }
            curNumStartingIndex = curNumLastIndex;
        }
        return startReplacementIndex;
    }

    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int n = queue.size();
            for (int i = 0; i < n; i++) {
                TreeNode poll = queue.poll();
                if (i == 0) result.add(poll.val);
                if (poll.right != null) queue.add(poll.right);
                if (poll.left != null) queue.add(poll.left);
            }
        }
        return result;
    }


    public List<Integer> rightSideView2(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        rightSideView2Util(root, 0, result);
        return result;
    }

    private void rightSideView2Util(TreeNode curRoot, int curDepth, List<Integer> result) {
        if (curRoot == null) return;
        if (result.size() == curDepth) {
            result.add(curRoot.val);
        }
        rightSideView2Util(curRoot.right, curDepth + 1, result);
        rightSideView2Util(curRoot.left, curDepth + 1, result);
    }


    public TreeNode addOneRow(TreeNode root, int val, int depth) {
        if (depth == 1) {
            return new TreeNode(val, root, null);
        }
        addOneRowUtil(root, 1, val, depth);
        return root;
    }

    private void addOneRowUtil(TreeNode curRoot, int curDepth, int val, int depth) {
        if (curRoot == null || curDepth >= depth) return;
        if (curDepth == depth - 1) {
            curRoot.left = new TreeNode(val, curRoot.left, null);
            curRoot.right = new TreeNode(val, null, curRoot.right);
        }
        addOneRowUtil(curRoot.left, curDepth + 1, val, depth);
        addOneRowUtil(curRoot.right, curDepth + 1, val, depth);
    }


    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int[] result = new int[n + m];
        int i = 0, j = 0, k = 0;
        while (i < m && j < n) {
            result[k] = Math.min(nums1[i], nums2[j]);
            if (nums1[i] <= nums2[j]) {
                i++;
            } else {
                j++;
            }
            k++;
        }
        if (i == m) {
            for (; j < n; j++) {
                result[k++] = nums2[j];
            }
        } else if (j == n) {
            for (; i < n; i++) {
                result[k++] = nums1[i];
            }
        }
        for (int l = 0; l < n + m; l++) {
            nums1[l] = result[l];
        }
    }

    public int removeElement(int[] nums, int val) {
        if (nums.length == 0) return 0;
        int i = 0, j = nums.length - 1;
        while (i < j) {
            if (nums[i] != val) {
                i++;
            } else if (nums[i] == val && nums[j] == val) {
                j--;
            } else {
                swap(nums, i, j);
                i++;
                j--;
            }
        }
        return nums[i] != val ? i + 1 : i;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
