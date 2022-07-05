package jp.sagalab.splineInterpolation;

import jp.sagalab.Point;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

public final class SplineCurveInterpolation {
  public static SplineCurveInterpolation create(List<Point> _passPoints, int _degree) {
    return new SplineCurveInterpolation(_passPoints, _degree);
  }

  /**
   * ���֐��iB-Spline�j
   *
   * @param _i ����_����̑Ώۂ̐���_�̃C���f�b�N�X
   * @param _k ����
   * @param _t ��`����̃p�����[�^�[t
   * @return ���֐��̒l
   */
  private Double basisFunction(Integer _i, Integer _k, Double _t) {
    { // ���ʂȏꍇ�̏����i���[�E�E�[�̊��֐���������A�ߓ_��̏I�[��n�d�ߓ_��������j
      int knotsSize = m_knots.size();
      int controlPointsSize = m_knots.size() - m_degree + 1;
      int n = m_degree;

      // ���[�ł� u(-1) �͍l�����Ȃ��ip.48 �}3.14�Ap.50 �}3.16�A�}3.17�j
      if (_i == 0) {
        double coeff = (m_knots.get(_i + _k) - _t) / (m_knots.get(_i + _k) - m_knots.get(_i));
        return coeff * basisFunction(_i + 1, _k - 1, _t);
      }

      // �E�[�ł� u(knotsSize) �͍l�����Ȃ��ip.48 �}3.14�Ap.50 �}3.16�A�}3.17�j
      if (_i + _k == knotsSize) {
        double coeff = (_t - m_knots.get(_i - 1)) / (m_knots.get(_i + _k - 1) - m_knots.get(_i - 1));
        return coeff * basisFunction(_i, _k - 1, _t);
      }

      if (_k == 0) {
        // �ߓ_��̏I�[��n�d�ߓ_�ɂȂ��Ă��邩
        boolean isN_Overlapped = m_knots.get(m_knots.size() - 1).equals(m_knots.get(m_knots.size() - n));

        if (_i == controlPointsSize - 1 && m_knots.get(knotsSize - 1).equals(_t) && isN_Overlapped) {
          return 1.0;
        }

        return (m_knots.get(_i - 1) <= _t && _t < m_knots.get(_i)) ? 1.0 : 0.0;
      }
    }

    // �ʏ폈��
    // ������Ɍv�Z����0�ɂȂ�����i0���Z�������������Ȃ�j���̍��̌W����0�Ƃ���
    double denom1 = m_knots.get(_i + _k - 1) - m_knots.get(_i - 1);
    double denom2 = m_knots.get(_i + _k) - m_knots.get(_i);
    double coeff1 = (denom1 != 0.0) ? (_t - m_knots.get(_i - 1)) / denom1 : 0.0;
    double coeff2 = (denom2 != 0.0) ? (m_knots.get(_i + _k) - _t) / denom2 : 0.0;

    return coeff1 * basisFunction(_i, _k - 1, _t)
            + coeff2 * basisFunction(_i + 1, _k - 1, _t);
  }

  private List<Double> generateKnots() {
    double timeFirst = m_normalizedTimes.get(0);
    double timeLast = m_normalizedTimes.get(m_normalizedTimes.size() - 1);
    int knotIntervalNum = (int)Math.ceil((timeLast - timeFirst) / TIME_INTERVAL);
    int knotsSize = knotIntervalNum + 2 * m_degree - 1;

    List<Double> result = new ArrayList<>();

    for (int i = 0; i < knotsSize; ++i) {
      double w = (i - m_degree + 1) / (double) knotIntervalNum;
      result.add((1.0 - w) * timeFirst + w * timeLast);
    }

    return result;
  }

  private List<Double> normalizedTimes() {
    double timeLast = m_passPoints.get(m_passPoints.size() - 1).getTime();
    double lastFloored = (Math.floor(timeLast * 10)) / 10;
    double ratio = lastFloored / timeLast;
    List<Double> result = new ArrayList<>();

    for (Point p : m_passPoints) {
      result.add(p.getTime() * ratio);
    }

    return result;
  }

  public List<Point> getControlPoints() {
    int size = m_passPoints.size();
    int controlPointsSize = m_knots.size() - m_degree + 1;
    double[][] passXMatrixRaw = new double[size][1];
    double[][] passYMatrixRaw = new double[size][1];

    for (int i = 0; i < size; ++i) {
      passXMatrixRaw[i][0] = m_passPoints.get(i).getX();
      passYMatrixRaw[i][0] = m_passPoints.get(i).getY();
    }

    RealMatrix passXMatrix = MatrixUtils.createRealMatrix(passXMatrixRaw);
    RealMatrix passYMatrix = MatrixUtils.createRealMatrix(passYMatrixRaw);

    double[][] basisMatrixRaw = new double[size][controlPointsSize];

    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < controlPointsSize; ++j) {
        double basis = basisFunction(j, m_degree, m_normalizedTimes.get(i));
        basisMatrixRaw[i][j] = basis;
      }
    }

    RealMatrix N = MatrixUtils.createRealMatrix(basisMatrixRaw);
    RealMatrix N_T = N.copy().transpose();
    RealMatrix N_TN = N_T.copy().multiply(N);

    RealMatrix N_Tp_x = N_T.copy().multiply(passXMatrix);
    RealMatrix N_Tp_y = N_T.copy().multiply(passYMatrix);

    LUDecomposition LU_Decomposition = new LUDecomposition(N_TN);
    RealMatrix resultXMatrix = LU_Decomposition.getSolver().solve(N_Tp_x);
    RealMatrix resultYMatrix = LU_Decomposition.getSolver().solve(N_Tp_y);

    List<Point> result = new ArrayList<>();

    for (int i = 0; i < controlPointsSize; ++i) {
      double x = resultXMatrix.getEntry(i, 0);
      double y = resultYMatrix.getEntry(i, 0);

      result.add(Point.create(x, y));
    }

    return result;
  }

  public List<Double> getKnots() {
    return new ArrayList<>(m_knots);
  }

  private SplineCurveInterpolation(List<Point> _passPoints, int _degree) {
    m_degree = _degree;
    m_passPoints = new ArrayList<>(_passPoints);
    m_normalizedTimes = normalizedTimes();
    m_knots = generateKnots();
  }

  private final int m_degree;
  private final List<Point> m_passPoints;
  private final List<Double> m_normalizedTimes;
  private final List<Double> m_knots;
  private static final double TIME_INTERVAL = 0.05;
}
