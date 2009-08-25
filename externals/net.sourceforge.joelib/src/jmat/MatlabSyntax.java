package jmat;

import jmat.data.Matrix;
import jmat.data.RandomMatrix;

import jmat.io.gui.Plot2D;
import jmat.io.gui.Plot3D;


/**
 * <p>Description : Enables an easy to use Matlab-like syntax for invokation of main features.</p>
 * <p>Copyright : GPL</p>
 * @.author Yann RICHET
 * @version 1.0
 */
public class MatlabSyntax
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**Element-by-element inverse
    @param A    Matrix.
    @return     |A|
     */

    /*M
    <syntax>
     <matlab>X = abs(A);</matlab>
     <java>X = abs(A);</java>
    </syntax>
    */
    public static Matrix abs(Matrix A)
    {
        return A.ebeAbs();
    }

    /** Cholesky Decomposition
    @param A    Matrix.
    @return     L
    */

    /*M
    <syntax>
     <matlab>L = chol(A);</matlab>
     <java>L = chol(A);</java>
    </syntax>
    */
    public static Matrix chol(Matrix A)
    {
        return A.chol().getL();
    }

    /** Matrix condition (2 norm)
    @param A    Matrix.
    @return     ratio of largest to smallest singular value.
    */

    /*M
    <syntax>
     <matlab>d = cond(A);</matlab>
     <java>d = cond(A);</java>
    </syntax>
    */
    public static double cond(Matrix A)
    {
        return A.cond();
    }

    /** Generate a correlation matrix.
    @param A    Matrix.
    @return     An m-by-m matrix.
    */

    /*M
    <syntax>
     <matlab>X = corrcoef(A);</matlab>
     <java>X = corrcoef(A);</java>
    </syntax>
    */
    public static Matrix corrcoef(Matrix A)
    {
        return new RandomMatrix(A).cor();
    }

    /**Element-by-element cosinus
    @param A    Matrix.
    @return     cos.(A)
     */

    /*M
    <syntax>
     <matlab>X = cos(A);</matlab>
     <java>X = cos(A);</java>
    </syntax>
    */
    public static Matrix cos(Matrix A)
    {
        return A.ebeCos();
    }

    /** Generate a covariance matrix.
    @param A    Matrix.
    @return     An m-by-m matrix.
    */

    /*M
    <syntax>
     <matlab>X = cov(A);</matlab>
     <java>X = cov(A);</java>
    </syntax>
    */
    public static Matrix cov(Matrix A)
    {
        return new RandomMatrix(A).cov();
    }

    /** Matrix determinant
    @param A    Matrix.
    @return     determinant
    */

    /*M
    <syntax>
     <matlab>d = det(A);</matlab>
     <java>d = det(A);</java>
    </syntax>
    */
    public static double det(Matrix A)
    {
        return A.det();
    }

    /** Matrix diagonal extraction.
    @param A    Matrix.
    @return     An d*1 Matrix of diagonal elements, d = min(m,n).
    */

    /*M
    <syntax>
     <matlab>X = diag(A);</matlab>
     <java>X = diag(A);</java>
    </syntax>
    */
    public static Matrix diag(Matrix A)
    {
        return A.diag();
    }

    /** Matrix diagonal extraction.
    @param A    Matrix.
    @param num    diagonal number.
    @return     Matrix of the n-th diagonal elements.
    */

    /*M
    <syntax>
     <matlab>X = diag(A,n);</matlab>
     <java>X = diag(A,n);</java>
    </syntax>
    */
    public static Matrix diag(Matrix A, int num)
    {
        return A.diag(num);
    }

    /////////////////////////////////////////////////////////
    //Matrix io methods, in panels, frames or command line.//
    /////////////////////////////////////////////////////////

    /** Print the Matrix in the Command Line.
    @param A    Matrix.
    */

    /*M
    <syntax>
     <matlab>disp(A);</matlab>
     <java>disp(A);</java>
    </syntax>
    */
    public static void disp(Matrix A)
    {
        A.toCommandLine("");
    }

    /** Print the Matrix in the Command Line.
    @param text    String.
    */

    /*M
    <syntax>
     <matlab>disp('abcdef');</matlab>
     <java>disp('abcdef');</java>
    </syntax>
    */
    public static void disp(String text)
    {
        System.out.println(text);
    }

    /** Divide a matrix by a scalar, C = A/s
    @param A    Matrix.
    @param s    scalar
    @return     A/s
    */

    /*M
    <syntax>
     <matlab>X = A/s;</matlab>
     <java>X = divide(A,s);</java>
    </syntax>
    */
    public static Matrix divide(Matrix A, double s)
    {
        return A.divide(s);
    }

    /** Linear algebraic matrix division, A / B
    @param A    Matrix.
    @param B    another matrix
    @return     Matrix division, A / B
    */

    /*M
    <syntax>
     <matlab>X = A/B;</matlab>
     <java>X = divide(A,B);</java>
    </syntax>
    */
    public static Matrix divide(Matrix A, Matrix B)
    {
        return A.divide(B);
    }

    /** Element-by-element right division, C = A./B
    @param A    Matrix.
    @param B    another matrix
    @return     A./B
    */

    /*M
    <syntax>
     <matlab>X = A./B;</matlab>
     <java>X = ebeDivide(A,B);</java>
    </syntax>
    */
    public static Matrix ebeDivide(Matrix A, Matrix B)
    {
        return A.ebeDivide(B);
    }

    /////////////////////////
    //Functions for Matrix.//
    /////////////////////////

    /** Element-by-element multiplication, C = A.*B
    @param A    Matrix.
    @param B    another matrix
    @return     A.*B
    */

    /*M
    <syntax>
     <matlab>X = A.*B;</matlab>
     <java>X = ebeTimes(A,B);</java>
    </syntax>
    */
    public static Matrix ebeTimes(Matrix A, Matrix B)
    {
        return A.ebeTimes(B);
    }

    /** Eigenvalue Decomposition
    @param A    Matrix.
    @return     D
    */

    /*M
    <syntax>
     <matlab>[V,D] = eig(A);</matlab>
     <java>D = eig_D(A);</java>
    </syntax>
    */
    public static Matrix eig_D(Matrix A)
    {
        return A.eig().getD();
    }

    /** Eigenvalue Decomposition
    @param A    Matrix.
    @return     V
    */

    /*M
    <syntax>
     <matlab>[V,D] = eig(A);</matlab>
     <java>V = eig_V(A);</java>
    </syntax>
    */
    public static Matrix eig_V(Matrix A)
    {
        return A.eig().getV();
    }

    /**Element-by-element exponential
    @param A    Matrix.
    @return     exp.(A)
     */

    /*M
    <syntax>
     <matlab>X = exp(A);</matlab>
     <java>X = exp(A);</java>
    </syntax>
    */
    public static Matrix exp(Matrix A)
    {
        return A.ebeExp();
    }

    /** Construct an m-by-n matrix of random numbers from an exponantial random variable.
    @param m    Number of rows.
    @param n    Number of columns.
    @param mu    Parmaeter of the exponential random variable.
    @return      A RandomMatrix.
    */

    /*M
    <syntax>
     <matlab>X = exprnd(mu,m,n);</matlab>
     <java>X = expRnd(mu,m,n);</java>
    </syntax>
    */
    public static RandomMatrix expRnd(double mu, int m, int n)
    {
        return RandomMatrix.exponential(m, n, mu);
    }

    /** Generate identity matrix
    @param m    Number of rows.
    @param n    Number of colums.
    @return     An m-by-n matrix with ones on the diagonal and zeros elsewhere.
    */

    /*M
    <syntax>
     <matlab>X = eye(m,n);</matlab>
     <java>X = eye(m,n);</java>
    </syntax>
    */
    public static Matrix eye(int m, int n)
    {
        return Matrix.identity(m, n);
    }

    /** Find elements verifying a boolean test
    @param A    Matrix.
    @param test    Test to apply: < > =...
    @param e    Element (value) to compare
    @return     A list of indices where this element is found.
    */

    /*M
    <syntax>
     <matlab>[I,J] = find(A<3);</matlab>
     <java>IJ = find(A,'<',3);</java>
    </syntax>
    */
    public static Matrix find(Matrix A, String test, double e)
    {
        int[][] I = A.find(test, e);
        Matrix X = new Matrix(I.length, 2);

        for (int i = 0; i < I.length; i++)
        {
            X.set(i, 0, I[i][0] + 1);
            X.set(i, 1, I[i][1] + 1);
        }

        return X;
    }

    ////////////////////////////////////////////////////////////
    //Basic and advanced Get methods for matrix and submatrix.//
    ////////////////////////////////////////////////////////////

    /** Get a single element.
    @param A    Matrix.
    @param i    Row index.
    @param j    Column index.
    @return     A(i,j)
    @exception  ArrayIndexOutOfBoundsException
    */

    /*M
    <syntax>
     <matlab>d = A(i,j);</matlab>
     <java>d = get(A,i,j);</java>
    </syntax>
    */
    public static double get(Matrix A, int i, int j)
    {
        return A.get(i - 1, j - 1);
    }

    /** Get a submatrix.
    @param A    Matrix.
    @param i0   Initial row index
    @param i1   Final row index
    @param j0   Initial column index
    @param j1   Final column index
    @return     A(i0:i1,j0:j1)
    */

    /*M
    <syntax>
     <matlab>d = A(i0:i1,j0:j1);</matlab>
     <java>d = get(A,i0,i1,j0,j1);</java>
    </syntax>
    */
    public static Matrix get(Matrix A, int i0, int i1, int j0, int j1)
    {
        return A.getMatrix(i0 - 1, i1 - 1, j0 - 1, j1 - 1);
    }

    /** Copy an internal one-dimensional array from a column.
    @param A    Matrix.
    @param c    Column index
    @return     one-dimensional array copy of matrix elements.
    */

    /*M
    <syntax>
     <matlab>X = A(:,c);</matlab>
     <java>X = getColumn(A,c);</java>
    </syntax>
    */
    public static Matrix getColumn(Matrix A, int c)
    {
        return A.getColumn(c - 1);
    }

    /** Copy an internal one-dimensional array from a row.
    @param A    Matrix.
    @param l    Row index
    @return     one-dimensional array copy of matrix elements.
    */

    /*M
    <syntax>
     <matlab>X = A(i,:);</matlab>
     <java>X = getRow(A,i);</java>
    </syntax>
    */
    public static Matrix getRow(Matrix A, int l)
    {
        return A.getRow(l - 1);
    }

    /** Plot the Matrix in a JFrame
    @param X    Matrix.
    @param n    Number of slices.
    @return      A MatrixPlot2D (extends a Swing JPanel)
    */

    /*M
    <syntax>
     <matlab>figure;hist(X,20);</matlab>
     <java>hist(X,20);</java>
    </syntax>
    */
    public static Plot2D hist(Matrix X, int n)
    {
        return new RandomMatrix(X).toFrameHist2D("", n);
    }

    /** Plot the Matrix in a JFrame
    @param X    Matrix.
    @param n    Number of slices.
    @param title Title of the JFrame.
    @return      A MatrixPlot2D (extends a Swing JPanel)
    */

    /*M
    <syntax>
     <matlab>figure(title);hist(X,20);</matlab>
     <java>hist(title,X,20);</java>
    </syntax>
    */
    public static Plot2D hist(String title, Matrix X, int n)
    {
        return new RandomMatrix(X).toFrameHist2D(title, n);
    }

    /** Generate a matrix with a constant pitch beetwen each row
    @param m    Number of rows.
    @param n    Number of colums.
    @return     An m-by-n matrix.
    */
    public static Matrix inc(int m, int n)
    {
        return Matrix.increment(m, n, 1, n);
    }

    /** Generate a matrix with a constant pitch beetwen each row
    @param m    Number of rows.
    @param n    Number of colums.
    @param begin    begining value to increment.
    @param pitch    pitch to add.
    @return     An m-by-n matrix.
    */
    public static Matrix inc(int m, int n, double begin, double pitch)
    {
        return Matrix.increment(m, n, begin, pitch);
    }

    /** Matrix inverse or pseudoinverse
    @param A    Matrix.
    @return     inverse(A) if A is square, pseudoinverse otherwise.
    */

    /*M
    <syntax>
     <matlab>X = inv(A);</matlab>
     <java>X = inv(A);</java>
    </syntax>
    */
    public static Matrix inv(Matrix A)
    {
        return A.inverse();
    }

    /** Load the Matrix from an ASCII file.
    @param fileName    filename of the file to load.
    @return      A matrix.
    */

    /*M
    <syntax>
     <matlab>X = load('A.dat');</matlab>
     <java>X = load('A.dat');</java>
    </syntax>
    */
    public static Matrix load(String fileName)
    {
        return Matrix.fromFile(fileName);
    }

    /**Element-by-element neperian logarithm
    @param A    Matrix.
    @return     log.(A)
     */

    /*M
    <syntax>
     <matlab>X = log(A);</matlab>
     <java>X = log(A);</java>
    </syntax>
    */
    public static Matrix log(Matrix A)
    {
        return A.ebeLog();
    }

    ///////////////////////////////////////////////
    //Advanced Decompositions methods for Matrix.//
    ///////////////////////////////////////////////

    /** LU Decomposition
    @param A    Matrix.
    @return     L
    */

    /*M
    <syntax>
     <matlab>[L,U,P] = lu(A);</matlab>
     <java>L = lu_L(A);</java>
    </syntax>
    */
    public static Matrix lu_L(Matrix A)
    {
        return A.lu().getL();
    }

    /** LU Decomposition
    @param A    Matrix.
    @return     P
    */

    /*M
    <syntax>
     <matlab>[L,U,P] = lu(A);</matlab>
     <java>P = lu_P(A);</java>
    </syntax>
    */
    public static Matrix lu_P(Matrix A)
    {
        return A.lu().getP();
    }

    /** LU Decomposition
    @param A    Matrix.
    @return     U
    */

    /*M
    <syntax>
     <matlab>[L,U,P] = lu(A);</matlab>
     <java>U = lu_U(A);</java>
    </syntax>
    */
    public static Matrix lu_U(Matrix A)
    {
        return A.lu().getU();
    }

    /** Construct a matrix from a 2D-array.
    @param B    Two-dimensional array of doubles.
    @exception  IllegalArgumentException All rows must have the same length
    @return     An m-by-n matrix.
    */
    public static Matrix matrix(double[][] B)
    {
        return new Matrix(B);
    }

    /** Construct an m-by-n constant matrix.
    @param m    Number of rows.
    @param n    Number of colums.
    @param s    Fill the matrix with this scalar value.
    @return     An m-by-n matrix of scalar.
    */
    public static Matrix matrix(int m, int n, double s)
    {
        return new Matrix(m, n, s);
    }

    /** Generate a row matrix, each column contents the maximum value of the columns.
    @param A    Matrix.
    @return     An 1-by-n matrix.
    */

    /*M
    <syntax>
     <matlab>X = max(A);</matlab>
     <java>X = max(A);</java>
    </syntax>
    */
    public static Matrix max(Matrix A)
    {
        return A.max();
    }

    /** Generate a row matrix, each column contents the mean value of the columns.
    @param A    Matrix.
    @return     An 1-by-n matrix.
    */

    /*M
    <syntax>
     <matlab>X = mean(A);</matlab>
     <java>X = mean(A);</java>
    </syntax>
    */
    public static Matrix mean(Matrix A)
    {
        return new RandomMatrix(A).mean();
    }

    /** Generate a matrix from other matrix.
    @param A    1st Matrix to merge.
    @param B    2nd Matrix to merge.
    @param n    dimension to merge.
    @return     An m1+m2+...-by-n matrix.
    */

    /*M
    <syntax>
     <matlab>X = [A,B];</matlab>
     <java>X = merge(A,B,2);</java>
    </syntax>
    */
    public static Matrix merge(Matrix A, Matrix B, int n)
    {
        if (n == 1)
        {
            return A.mergeRows(B);
        }
        else if (n == 2)
        {
            return A.mergeColumns(B);
        }
        else
        {
            return A;
        }
    }

    /** Generate a row matrix, each column contents the minimum value of the columns.
    @param A    Matrix.
    @return     An 1-by-n matrix.
    */

    /*M
    <syntax>
     <matlab>X = min(A);</matlab>
     <java>X = min(A);</java>
    </syntax>
    */
    public static Matrix min(Matrix A)
    {
        return A.min();
    }

    /** C = A - B
    @param A    Matrix.
    @param B    another matrix
    @return     A - B
    */

    /*M
    <syntax>
     <matlab>X = A-B;</matlab>
     <java>X = minus(A,B);</java>
    </syntax>
    */
    public static Matrix minus(Matrix A, Matrix B)
    {
        return A.minus(B);
    }

    ////////////////////////////////////////
    //Norms and characteristics of Matrix.//
    ////////////////////////////////////////

    /** Norm
    @param A    Matrix.
    @param n    Norm indice.
    @return    norm value.
    */

    /*M
    <syntax>
     <matlab>d = norm(A,2);</matlab>
     <java>d = norm(A,'2');</java>
    </syntax>
    */
    public static double norm(Matrix A, String n)
    {
        if (n.equals("1"))
        {
            return A.norm1();
        }
        else if (n.equals("2"))
        {
            return A.norm2();
        }
        else if (n.equals("Inf"))
        {
            return A.normInf();
        }
        else if (n.equals("Fro"))
        {
            return A.normF();
        }
        else
        {
            return Double.NaN;
        }
    }

    /** Construct an m-by-n matrix of random numbers from an normal random variable.
    @param m    Number of rows.
    @param n    Number of columns.
    @param mu    Mean of the normal random variable.
    @param sigma    Variance of the normal random variable.
    @return      A RandomMatrix.
    */

    /*M
    <syntax>
     <matlab>X = normrnd(mu,sigma,m,n);</matlab>
     <java>X = normRnd(mu,sigma,m,n);</java>
    </syntax>
    */
    public static RandomMatrix normRnd(double mu, double sigma, int m, int n)
    {
        return RandomMatrix.normal(m, n, mu, sigma);
    }

    /** Construct an m-by-n matrix of ones.
    @param m    Number of rows.
    @param n    Number of colums.
    @return     An m-by-n matrix of ones.
    */

    /*M
    <syntax>
     <matlab>X = ones(m,n);</matlab>
     <java>X = ones(m,n);</java>
    </syntax>
    */
    public static Matrix ones(int m, int n)
    {
        return new Matrix(m, n, 1);
    }

    /** Plot the Matrix in a JFrame
    @param X    Matrix.
    @param Y    Matrix.
    @return      A MatrixPlot2D (extends a Swing JPanel)
    */

    /*M
    <syntax>
     <matlab>figure;plot(X,Y,'.');</matlab>
     <java>plot(X,Y);</java>
    </syntax>
    */
    public static Plot2D plot(Matrix X, Matrix Y)
    {
        return Y.toFramePlot2D("", X);
    }

    /** Plot the Matrix in a JFrame
    @param title Title of the JFrame.
    @param X    Matrix.
    @param Y    Matrix.
    @return      A MatrixPlot2D (extends a Swing JPanel)
    */

    /*M
    <syntax>
     <matlab>figure(title);plot(X,Y,'.');</matlab>
     <java>plot(title,X,Y);</java>
    </syntax>
    */
    public static Plot2D plot(String title, Matrix X, Matrix Y)
    {
        return Y.toFramePlot2D(title, X);
    }

    /** Plot the Matrix in a Window in a JFrame
    @param X    Matrix
    @param Y    Matrix
    @param Z    Matrix
    @return      A MatrixPlot3D (extends a Swing JPanel)
    */

    /*M
    <syntax>
     <matlab>figure;plot3(X,Y,Z,'.');</matlab>
     <java>plot3(X,Y,Z);</java>
    </syntax>
    */
    public static Plot3D plot3(Matrix X, Matrix Y, Matrix Z)
    {
        return Z.toFramePlot3D("", X, Y);
    }

    /** Plot the Matrix in a Window in a JFrame
    @param title Title of the JFrame.
    @param X    Matrix
    @param Y    Matrix
    @param Z    Matrix
    @return      A MatrixPlot3D (extends a Swing JPanel)
    */

    /*M
    <syntax>
     <matlab>figure(title);plot3(X,Y,Z,'.');</matlab>
     <java>plot3(title,X,Y,Z);</java>
    </syntax>
    */
    public static Plot3D plot3(String title, Matrix X, Matrix Y, Matrix Z)
    {
        return Z.toFramePlot3D(title, X, Y);
    }

    /** C = A + B
    @param A    Matrix.
    @param B    another matrix
    @return     A + B
    */

    /*M
    <syntax>
     <matlab>X = A+B;</matlab>
     <java>X = plus(A,B);</java>
    </syntax>
    */
    public static Matrix plus(Matrix A, Matrix B)
    {
        return A.plus(B);
    }

    /**Element-by-element power
    @param A    Matrix.
    @param p    double
    @return     A.^p
     */

    /*M
    <syntax>
     <matlab>X = power(A,p);</matlab>
     <java>X = power(A,p);</java>
    </syntax>
    */
    public static Matrix power(Matrix A, double p)
    {
        return A.ebePow(p);
    }

    /**Element-by-element power
    @param A    Matrix.
    @param B    another matrix
    @return     A.^B
     */

    /*M
    <syntax>
     <matlab>X = power(A,B);</matlab>
     <java>X = power(A,B);</java>
    </syntax>
    */
    public static Matrix power(Matrix A, Matrix B)
    {
        return A.ebePow(B);
    }

    /** Generate a row matrix, each column contents the product value of the columns.
    @param A    Matrix.
    @return     An 1-by-n matrix.
    */

    /*M
    <syntax>
     <matlab>X = prod(A);</matlab>
     <java>X = prod(A);</java>
    </syntax>
    */
    public static Matrix prod(Matrix A)
    {
        return A.prod();
    }

    /** QR Decomposition
    @param A    Matrix.
    @return     Q
    */

    /*M
    <syntax>
     <matlab>[Q,R] = qr(A);</matlab>
     <java>Q = qr_Q(A);</java>
    </syntax>
    */
    public static Matrix qr_Q(Matrix A)
    {
        return A.qr().getQ();
    }

    /** QR Decomposition
    @param A    Matrix.
    @return     R
    */

    /*M
    <syntax>
     <matlab>[Q,R] = qr(A);</matlab>
     <java>R = qr_R(A);</java>
    </syntax>
    */
    public static Matrix qr_R(Matrix A)
    {
        return A.qr().getR();
    }

    /* ----------------------
       Public Methods
     * ---------------------- */

    //////////////////////////////////////////
    //Static constructors for simple matrix.//
    //////////////////////////////////////////

    /** Generate matrix with random elements
    @param m    Number of rows.
    @param n    Number of colums.
    @return     An m-by-n matrix with uniformly distributed random elements.
    */

    /*M
    <syntax>
     <matlab>X = rand(m,n);</matlab>
     <java>X = rand(m,n);</java>
    </syntax>
    */
    public static Matrix rand(int m, int n)
    {
        return Matrix.random(m, n);
    }

    /** Matrix rank
    @param A    Matrix.
    @return     effective numerical rank, obtained from SVD.
    */

    /*M
    <syntax>
     <matlab>d = rank(A);</matlab>
     <java>d = rank(A);</java>
    </syntax>
    */
    public static int rank(Matrix A)
    {
        return A.rank();
    }

    /** Matrix reshape by Row.
    @param A    Matrix.
    @param m2    number of rows
    @param n2    number of columns
    @return    reshaped matrix
    */

    /*M
    <syntax>
     <matlab>X = reshape(A,m,n);</matlab>
     <java>X = reshape(A,m,n);</java>
    </syntax>
    */
    public static Matrix reshape(Matrix A, int m2, int n2)
    {
        return A.reshape(m2, n2);
    }

    ////////////////////////////////
    //Modify dimensions of matrix.//
    ////////////////////////////////

    /** Matrix resize.
    @param A    Matrix.
    @param m2    number of rows
    @param n2    number of columns
    @return    resized matrix
    */
    public static Matrix resize(Matrix A, int m2, int n2)
    {
        return A.resize(m2, n2);
    }

    /** Save the Matrix in an ASCII file.
    @param A    Matrix.
    @param fileName    filename to save in.
    */

    /*M
    <syntax>
     <matlab>save('A.dat','A','-ASCII');</matlab>
     <java>save('A.dat',A);</java>
    </syntax>
    */
    public static void save(String fileName, Matrix A)
    {
        A.toFile(fileName);
    }

    ////////////////////////////////////////////////////////////
    //Basic and advanced Set methods for matrix and submatrix.//
    ////////////////////////////////////////////////////////////

    /** Set a single element.
    @param A    Matrix.
    @param i    Row index.
    @param j    Column index.
    @param s    A(i,j).
    */

    /*M
    <syntax>
     <matlab>A(i,j) = s;</matlab>
     <java>set(A,i,j,s);</java>
    </syntax>
    */
    public static void set(Matrix A, int i, int j, double s)
    {
        A.set(i - 1, j - 1, s);
    }

    /** Set a submatrix.
    @param A    Matrix.
    @param i0   Initial row index
    @param i1   Final row index
    @param j0   Initial column index
    @param j1   Final column index
    @param X   subMatrix to add
    */

    /*M
    <syntax>
     <matlab>A(i0:i1,j0:i1) = X;</matlab>
     <java>set(A,i0,i1,j0,i1,X);</java>
    </syntax>
    */
    public static void set(Matrix A, int i0, int i1, int j0, int j1, Matrix X)
    {
        A.setMatrix(i0 - 1, i1 - 1, j0 - 1, j1 - 1, X);
    }

    /** Set a column to an internal one-dimensional Column.
    @param A    Matrix.
    @param c    Column index
    @param B    Column-matrix
    */
    public static void setColumn(Matrix A, int c, Matrix B)
    {
        A.setColumn(c - 1, B);
    }

    /** Copy an internal one-dimensional array from a row.
    @param A    Matrix.
    @param l    Row index
    @param B    Row-matrix
    */

    /*M
    <syntax>
     <matlab>A(l,:) = B;</matlab>
     <java>setRow(A,l,B);</java>
    </syntax>
    */
    public static void setRow(Matrix A, int l, Matrix B)
    {
        A.setRow(l - 1, B);
    }

    /**Element-by-element sinus
    @param A    Matrix.
    @return     sin.(A)
     */

    /*M
    <syntax>
     <matlab>X = sin(A);</matlab>
     <java>X = sin(A);</java>
    </syntax>
    */
    public static Matrix sin(Matrix A)
    {
        return A.ebeSin();
    }

    /** Get row dimension.
    @param A    Matrix.
    @param dim    Dimension to return (1 = number of rows, 2 = number of columns).
    @return     the number of rows or columns.
    */

    /*M
    <syntax>
     <matlab>i = size(A,dim);</matlab>
     <java>i = size(A,dim);</java>
    </syntax>
    */
    public static int size(Matrix A, int dim)
    {
        if (dim == 1)
        {
            return A.getRowDimension();
        }
        else if (dim == 2)
        {
            return A.getColumnDimension();
        }
        else
        {
            return 0;
        }
    }

    /** Solve A*X = B
    @param A    Matrix.
    @param B    right hand side
    @return     solution if A is square, least squares solution otherwise
    */

    /*M
    <syntax>
     <matlab>X = A\B;</matlab>
     <java>X = solve(A,B);</java>
    </syntax>
    */
    public static Matrix solve(Matrix A, Matrix B)
    {
        return A.solve(B);
    }

    /////////////////////////////////////////
    //Advanced functions like sort or find.//
    /////////////////////////////////////////

    /** Generate a column-permuted matrix, rows are permuted in order to sort the column 'c'
    @param A    Matrix.
    @return     An int[] Array.
    */

    /*M
    <syntax>
     <matlab>[Y,I] = sort(A);</matlab>
     <java>I = sort_I(A);</java>
    </syntax>
    */
    public static Matrix sort_I(Matrix A)
    {
        Matrix X = new Matrix(A.getRowDimension(), A.getColumnDimension());

        for (int i = 0; i < A.getColumnDimension(); i++)
        {
            int[] Ii = A.sort(i);

            for (int j = 0; j < Ii.length; j++)
            {
                X.set(i, j, Ii[j] + 1);
            }
        }

        return X;
    }

    /** Generate a column-permuted matrix, rows are permuted in order to sort the column 'c'
    @param A    Matrix.
    @return     An m-by-n matrix.
    */

    /*M
    <syntax>
     <matlab>[Y,I] = sort(A);</matlab>
     <java>Y = sort_Y(A);</java>
    </syntax>
    */
    public static Matrix sort_Y(Matrix A)
    {
        Matrix X = new Matrix(A.getRowDimension(), A.getColumnDimension());

        for (int i = 0; i < A.getColumnDimension(); i++)
        {
            Matrix C = A.sortedMatrix(i).getColumn(i);
            X.setColumn(i, C);
        }

        return X;
    }

    /**Element-by-element inverse
    @param A    Matrix.
    @return     sqrt.(A)
     */

    /*M
    <syntax>
     <matlab>X = sqrt(A);</matlab>
     <java>X = sqrt(A);</java>
    </syntax>
    */
    public static Matrix sqrt(Matrix A)
    {
        return A.ebeSqrt();
    }

    /** Generate a row matrix, each column contents the sum value of the columns.
    @param A    Matrix.
    @return     An 1-by-n matrix.
    */

    /*M
    <syntax>
     <matlab>X = sum(A);</matlab>
     <java>X = sum(A);</java>
    </syntax>
    */
    public static Matrix sum(Matrix A)
    {
        return A.sum();
    }

    /** Singular Value Decomposition
    @param A    Matrix.
    @return     S
    */

    /*M
    <syntax>
     <matlab>[U,S,V] = svd(A);</matlab>
     <java>S = svd_S(A);</java>
    </syntax>
    */
    public static Matrix svd_S(Matrix A)
    {
        return A.svd().getS();
    }

    /** Singular Value Decomposition
    @param A    Matrix.
    @return     U
    */

    /*M
    <syntax>
     <matlab>[U,S,V] = svd(A);</matlab>
     <java>U = svd_U(A);</java>
    </syntax>
    */
    public static Matrix svd_U(Matrix A)
    {
        return A.svd().getU();
    }

    /** Singular Value Decomposition
    @param A    Matrix.
    @return     V
    */

    /*M
    <syntax>
     <matlab>[U,S,V] = svd(A);</matlab>
     <java>V = svd_V(A);</java>
    </syntax>
    */
    public static Matrix svd_V(Matrix A)
    {
        return A.svd().getV();
    }

    /** Matrix transpose.
    @param A    Matrix.
    @return    A'
    */

    /*M
    <syntax>
     <matlab>X = A';</matlab>
     <java>X = t(A);</java>
    </syntax>
    */
    public static Matrix t(Matrix A)
    {
        return A.transpose();
    }

    /** Multiply a matrix by a scalar, C = s*A
    @param A    Matrix.
    @param s    scalar
    @return     s*A
    */

    /*M
    <syntax>
     <matlab>X = A*s;</matlab>
     <java>X = times(A,s);</java>
    </syntax>
    */
    public static Matrix times(Matrix A, double s)
    {
        return A.times(s);
    }

    /** Linear algebraic matrix multiplication, A * B
    @param A    Matrix.
    @param B    another matrix
    @return     Matrix product, A * B
    */

    /*M
    <syntax>
     <matlab>X = A*B;</matlab>
     <java>X = times(A,B);</java>
    </syntax>
    */
    public static Matrix times(Matrix A, Matrix B)
    {
        return A.times(B);
    }

    /** Matrix trace.
    @param A    Matrix.
    @return     sum of the diagonal elements.
    */

    /*M
    <syntax>
     <matlab>d = trace(A);</matlab>
     <java>d = trace(A);</java>
    </syntax>
    */
    public static double trace(Matrix A)
    {
        return A.trace();
    }

    /** Construct an m-by-n matrix of random numbers from an triangular random variable.
    @param m    Number of rows.
    @param n    Number of columns.
    @param min    Min of the uniform random variable.
    @param mod    Mode of the uniform random variable.
    @param max    Max of the uniform random variable.
    @return      A RandomMatrix.
    */
    public static RandomMatrix triangRnd(int m, int n, double min, double mod,
        double max)
    {
        return RandomMatrix.triangular(m, n, min, mod, max);
    }

    ///////////////////////////////////
    //Algebraic Functions for Matrix.//
    ///////////////////////////////////

    /**  Unary minus
    @param A    Matrix.
    @return    -A
    */

    /*M
    <syntax>
     <matlab>X = -A;</matlab>
     <java>X = uminus(A);</java>
    </syntax>
    */
    public static Matrix uminus(Matrix A)
    {
        return A.uminus();
    }

    /** Construct an m-by-n matrix of random numbers from an uniform random variable.
    @param m    Number of rows.
    @param n    Number of columns.
    @param min    Min of the uniform random variable.
    @param max    Max of the uniform random variable.
    @return      A RandomMatrix.
    */

    /*M
    <syntax>
     <matlab>X = unifrnd(min,max,m,n);</matlab>
     <java>X = unifRnd(min,max,m,n);</java>
    </syntax>
    */
    public static RandomMatrix unifRnd(int m, int n, double min, double max)
    {
        return RandomMatrix.uniform(m, n, min, max);
    }

    /** Generate a variance matrix.
    @param A    Matrix.
    @return     An 1-by-n matrix.
    */

    /*M
    <syntax>
     <matlab>X = var(A);</matlab>
     <java>X = var(A);</java>
    </syntax>
    */
    public static Matrix var(Matrix A)
    {
        return new RandomMatrix(A).var();
    }

    /* ------------------------
       Constructors
     * ------------------------ */

    /** Construct an m-by-n matrix of zeros.
    @param m    Number of rows.
    @param n    Number of colums.
    @return     An m-by-n matrix of zeros.
    */

    /*M
    <syntax>
     <matlab>X = zeros(m,n);</matlab>
     <java>X = zeros(m,n);</java>
    </syntax>
    */
    public static Matrix zeros(int m, int n)
    {
        return new Matrix(m, n);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
