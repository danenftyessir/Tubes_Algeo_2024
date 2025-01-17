import java.util.Scanner;

public class GaussJordanElimination {
    private int n;
    private double[][] augmentedMatrix;
    private Scanner scanner;
    public GaussJordanElimination() {
        scanner = new Scanner(System.in);
    }

    public void run() {
        inputData();
        processAndOutputResults();
        scanner.close();
    }
    
    private void inputData() {
        System.out.print("Masukkan jumlah variabel n: ");
        n = scanner.nextInt();
        System.out.println("Masukkan koefisien matriks augmented:");
        augmentedMatrix = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n + 1; j++) {
                System.out.printf("Masukkan elemen matriks[%d][%d]: ", i, j);
                augmentedMatrix[i][j] = scanner.nextDouble();
            }
        }
    }

    private void processAndOutputResults() {
        double[][] reducedRowEchelonForm = gaussJordanElimination(augmentedMatrix);
        System.out.println("\nMatriks eselon tereduksi:");
        printMatrix(reducedRowEchelonForm);  // Cetak matriks setelah tereduksi
        boolean hasUniqueSolution = true;
        for (int i = 0; i < n; i++) {
            if (reducedRowEchelonForm[i][i] == 0) {
                hasUniqueSolution = false;
                break;
            }
        }
        if (hasUniqueSolution) {
            System.out.println("\nHasil SPL:");
            for (int i = 0; i < n; i++) {
                System.out.printf("x%d = %.2f\n", i + 1, reducedRowEchelonForm[i][n]);
            }
        } else {
            System.out.println("\nMatriks adalah singular atau tidak memiliki solusi unik.");
        }
    }

    public static double[][] gaussJordanElimination(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int i = 0; // Baris
        int j = 0; // Kolom
        
        while (i < rows && j < cols) {
            // Jika elemen pivot (matrix[i][j]) adalah 0, coba tukar dengan baris di bawahnya
            if (matrix[i][j] == 0) {
                boolean swapped = false;
                for (int k = i + 1; k < rows; k++) {
                    if (matrix[k][j] != 0) {
                        swapRows(matrix, i, k);
                        swapped = true;
                        break;
                    }
                }
                // Jika tidak ada baris yang dapat ditukar, lanjutkan ke kolom berikutnya
                if (!swapped) {
                    j++;
                    continue;
                }
            }
    
            // Bagi seluruh baris dengan nilai pivot untuk memastikan pivot menjadi 1
            double pivot = matrix[i][j];
            for (int k = 0; k < cols; k++) {
                matrix[i][k] /= pivot;
            }
    
            // Eliminasi di semua baris selain baris pivot
            for (int k = 0; k < rows; k++) {
                if (k != i) {
                    double factor = matrix[k][j];
                    for (int l = 0; l < cols; l++) {
                        matrix[k][l] -= factor * matrix[i][l];
                    }
                }
            }
    
            // Lanjutkan ke baris dan kolom berikutnya
            i++;
            j++;
        }
    
        return matrix;
    }
    

    public static int calculateRank(double[][] matrix) {
        int rank = 0;
        int rows = matrix.length;
        int cols = matrix[0].length - 1;

        for (int i = 0; i < rows; i++) {
            boolean nonZeroRow = false;
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    nonZeroRow = true;
                    break;
                }
            }
            if (nonZeroRow) {
                rank++;
            }
        }

        return rank;
    }

    public static boolean hasInconsistentSystem(double[][] matrix, int rank) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        for (int i = rank; i < rows; i++) {
            boolean allZeros = true;
            for (int j = 0; j < cols - 1; j++) {
                if (matrix[i][j] != 0) {
                    allZeros = false;
                    break;
                }
            }
            if (allZeros && matrix[i][cols - 1] != 0) {
                return true;
            }
        }

        return false;
    }

    public static String[] parametricSolutions(double[][] matrix, int rank) {
        int rows = matrix.length;
        int cols = matrix[0].length - 1;  // kolom terakhir adalah hasil (right-hand side)
        String[] solutions = new String[rows];
        int[] freeVariables = new int[rows];
        double[] solution = new double[rows];

        // Inisialisasi semua variabel bebas
        for (int i = 0; i < cols; i++) {
            freeVariables[i] = 1;
        }

        // Identifikasi variabel terikat dan keluarkan dari daftar variabel bebas
        for (int i = 0; i < rank; i++) {
            int pivotCol = -1;
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    pivotCol = j;
                    break;
                }
            }
            if (pivotCol != -1) {
                freeVariables[pivotCol] = 0; // Hapus variabel terikat dari daftar variabel bebas
            }
        }

        // Bangun solusi parametrik untuk variabel terikat
        for (int i = rank - 1; i >= 0; i--) {
            int pivotCol = -1;
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    pivotCol = j;
                    break;
                }
            }

            if (pivotCol != -1) {
                StringBuilder equation = new StringBuilder("x" + (pivotCol + 1) + " = ");
                double constant = matrix[i][cols]; // Nilai di kolom paling kanan

                // Kurangi kontribusi dari variabel bebas pada baris ini
                for (int j = pivotCol + 1; j < cols; j++) {
                    if (matrix[i][j] != 0) {
                        equation.append(String.format("%.2f", -matrix[i][j]) + " * t" + (j + 1) + " ");
                    }
                }
                equation.append(String.format("+ %.2f", constant));
                solutions[pivotCol] = equation.toString();
                solution[pivotCol] = constant;
            }
        }

        // Tambahkan solusi untuk variabel bebas
        for (int i = 0; i < cols; i++) {
            if (freeVariables[i] == 1) {
                solutions[i] = "x" + (i + 1) + " = t" + (i + 1);
            }
        }

        return solutions;
    }
    
    

    private static void swapRows(double[][] matrix, int row1, int row2) {
        double[] temp = matrix[row1];
        matrix[row1] = matrix[row2];
        matrix[row2] = temp;
    }

    private static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double element : row) {
                System.out.printf("%.2f\t", element);
            }
            System.out.println();
        }
    }
}
