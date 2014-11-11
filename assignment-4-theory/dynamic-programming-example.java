/* Dynamic programming is useful here. */

public class SubsetSum {

    public boolean subset_sum(int[] A, int i, int current_sum) {
        if (i == A.length) {
            if (current_sum == 0) {
                return true;
            } else {
                return false;
            }
        }
        return subset_sum(A, i+1, current_sum) || subset_sum(A, i+1, current_sum + A[i]);
    }

    public static void main(String[] args) {
        int[] s = {1, 2, -2, -3, 4};

    }
}
N
